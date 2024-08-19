package com.nexus.aws.cloud;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.nexus.aws.cloud.client.S3Client;
import com.nexus.aws.exception.FileAlreadyExistsException;
import com.nexus.aws.exception.FileNotExists;
import com.nexus.aws.exception.FolderEmptyException;
import com.nexus.aws.model.FileEmpty;
import com.nexus.aws.model.S3File;
import com.nexus.aws.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Component
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class S3Impl implements S3 {

    private final S3Client s3Client;
    private static final ObjectMetadata metadata = new ObjectMetadata();
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB

    private void createFolder(String folderName){
        if (!folderName.endsWith("/")) { folderName += "/"; }
        this.verifyBucketExistsOrElseCreate();
        this.putObjectV2(folderName);
    }

    @Override
    public void putObject(MultipartFile file, String folder, String filename){

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds the maximum limit of 5MB");
        }

        String key = String.format("%s/%s", folder, filename);
        this.fileExists(key);
        try {
            this.verifyBucketExistsOrElseCreate();
            this.verifyFolderExistsOrElseCreate(folder);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            PutObjectRequest request =
                    new PutObjectRequest(
                            s3Client.getAwsProperties().getBucketName(),
                            key,
                            file.getInputStream(),
                            metadata
                    );

            s3Client.getClient().putObject(request);
        } catch (SdkClientException e){
            strangeError(e);
        } catch (Exception e) {
            log.info("Error in put file:".concat(e.getMessage()));
        }
    }

    @Override
    public List<S3File> listObjectsInFolder(String folderPath) {
        List<S3File> files;
        ListObjectsV2Request request = this.listFiles(folderPath);
        ListObjectsV2Result result;

        do {
            result = s3Client.getClient().listObjectsV2(request);
            files = this.formateList(result, folderPath);
            request.setContinuationToken(result.getNextContinuationToken());
        } while (result.isTruncated());

        return files;
    }

    @Override
    public void deleteFile(String folderName, String fileName) {
        String key = String.format("%s/%s", folderName, fileName);
        boolean exists = s3Client.getClient().doesObjectExist(s3Client.getAwsProperties().getBucketName(), key);
        Objects.throwIfFalse(exists, new FileNotExists("O arquivo não existe"));
        s3Client.getClient().deleteObject(new DeleteObjectRequest(s3Client.getAwsProperties().getBucketName(), key));
    }

    @Override
    public void updateObject(MultipartFile file, String folder, String filename) {
        this.deleteFile(folder, filename);
        this.putObject(file, folder, filename);
    }

    @Override
    public byte[] getFile(String folder, String fileName) {
        String key = String.format("%s/%s", folder, fileName);
        boolean exists = s3Client.getClient().doesObjectExist(s3Client.getAwsProperties().getBucketName(), key);
        Objects.throwIfFalse(exists, new FileNotExists("O arquivo não existe"));

        GetObjectRequest getObjectRequest = new GetObjectRequest(s3Client.getAwsProperties().getBucketName(), key);
        S3Object object = s3Client.getClient().getObject(getObjectRequest);

        try {
            return IOUtils.toByteArray(object.getObjectContent());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<S3File> formateList(ListObjectsV2Result result, String folderPath) {

        result.getObjectSummaries().removeIf(objectSummary -> folderPath.concat("/").equals(objectSummary.getKey()));

        Objects.requireNonEmpty(result.getObjectSummaries(), new FolderEmptyException("Lista vazia, você não possui contraCheques"));

        return result.getObjectSummaries().stream()
                .filter(s3ObjectSummary -> !s3ObjectSummary.getKey().equals(folderPath + "/"))
                .map(s3ObjectSummary -> S3File.builder()
                        .size(s3ObjectSummary.getSize())
                        .filename(s3ObjectSummary.getKey()
                                                 .substring(s3ObjectSummary.getKey().lastIndexOf("/") + 1))
                        .build()).collect(Collectors.toList());
    }

    private void putObjectV2(String folderName){
        metadata.setContentLength(0);
        try {
            s3Client.getClient().putObject(s3Client.getAwsProperties().getBucketName(), folderName, FileEmpty.inputStream, metadata);
        } catch (Exception ignored){

        }
    }

    private void strangeError(SdkClientException e){
        String estrangeMessage = "Unable to verify integrity of data upload. Client calculated content hash (contentMD5:";
        if (!e.getMessage().contains(estrangeMessage)) {
            log.error("Erro ao enviar para o Amazon S3: {}", e.getMessage());
        }
    }

    private ListObjectsV2Request listFiles(String folderPath){
        return new ListObjectsV2Request()
                .withBucketName(this.s3Client.getAwsProperties().getBucketName())
                .withPrefix(folderPath + "/");
    }


    private void fileExists(String key) {
        try {
            ObjectMetadata metadata = s3Client.getClient().getObjectMetadata(s3Client.getAwsProperties().getBucketName(), key);
            boolean isNotNull = metadata != null;
            Objects.throwIfTrue(isNotNull, new FileAlreadyExistsException("Arquivo já existe na base de dados"));
        } catch (AmazonS3Exception ignored){}
    }

    private void verifyBucketExistsOrElseCreate() {
        if (s3Client.getClient().listBuckets().isEmpty()) {
            s3Client.getClient().createBucket(s3Client.getAwsProperties().getBucketName());
        }
    }

    private void verifyFolderExistsOrElseCreate(String folder) {
        ListObjectsV2Request request = new ListObjectsV2Request()
                .withBucketName(this.s3Client.getAwsProperties().getBucketName())
                .withPrefix(folder)
                .withDelimiter("/");

        ListObjectsV2Result result = this.s3Client.getClient().listObjectsV2(request);

        if (result.getCommonPrefixes().isEmpty()){
            this.createFolder(folder.concat("/"));
        }
    }

}
