package com.nexus.aws.cloud;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.model.*;
import com.nexus.aws.cloud.client.S3Client;
import com.nexus.aws.exception.FileAlreadyExistsException;
import com.nexus.aws.exception.FileNotExists;
import com.nexus.aws.exception.FolderEmptyException;
import com.nexus.aws.model.S3File;
import com.nexus.utils.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Component
@RequiredArgsConstructor
public class S3Impl implements S3 {

    private final S3Client s3Client;
    private static final ObjectMetadata metadata = new ObjectMetadata();

    private static final InputStream inputStream = new ByteArrayInputStream(new byte[0]);
    private static final FolderEmptyException FOLDER_EMPTY_EXCEPTION = new FolderEmptyException("Lista vazia, você não possui contraCheques");
    private static final FileNotExists FILE_NOT_EXISTS = new FileNotExists("O arquivo não existe");
    private static final FileAlreadyExistsException FILE_ALREADY_EXISTS_EXCEPTION = new FileAlreadyExistsException("Arquivo já existe na base de dados");

    @Override
    public void verifyBucketExistsOrElseCreate() {
        if (s3Client.getClient().listBuckets().isEmpty()) {
            s3Client.getClient().createBucket(s3Client.getAwsProperties().getBucketName());
        }
    }

    @Override
    public void verifyFolderExistsOrElseCreate(String folder) {
        ListObjectsV2Request request = new ListObjectsV2Request()
                .withBucketName(this.s3Client.getAwsProperties().getBucketName())
                .withPrefix(folder)
                .withDelimiter("/");

        ListObjectsV2Result result = this.s3Client.getClient().listObjectsV2(request);

        if (result.getCommonPrefixes().isEmpty()){
            this.createFolder(folder.concat("/"));
        }
    }

    @Override
    public void createFolder(String folderName){
        if (!folderName.endsWith("/")) { folderName += "/"; }
        this.verifyBucketExistsOrElseCreate();
        this.putObjectV2(folderName);
    }

    @Override
    public void putObject(InputStream file, String folder, String filename){
        String key = String.format("%s/%s", folder, filename);

        this.fileExists(key);

        metadata.setContentType("application/pdf");
        PutObjectRequest request = new PutObjectRequest(
                s3Client.getAwsProperties().getBucketName(),
                key,
                file,
                metadata);

        request.getRequestClientOptions().setReadLimit(1024 * 1024);

        try {

            this.verifyBucketExistsOrElseCreate();
            this.verifyFolderExistsOrElseCreate(folder);
            this.s3Client.getClient().putObject(request);

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
    public List<S3File> formateList(ListObjectsV2Result result, String folderPath) {
        Objects.requireNonEmpty(result.getObjectSummaries(), FOLDER_EMPTY_EXCEPTION);

        return result.getObjectSummaries().stream()
                .filter(s3ObjectSummary -> !s3ObjectSummary.getKey().equals(folderPath + "/"))
                .map(s3ObjectSummary -> S3File.builder()
                        .size(s3ObjectSummary.getSize())
                        .filename(s3ObjectSummary.getKey().concat(".pdf")
                                .substring(s3ObjectSummary.getKey().lastIndexOf("/") + 1))
                        .build()).collect(Collectors.toList());
    }

    @Override
    public void deleteFile(String folderName, String fileName) {
        String key = String.format("%s/%s", folderName, fileName);
        boolean exists = s3Client.getClient().doesObjectExist(s3Client.getAwsProperties().getBucketName(), key);
        Objects.throwIfFalse(exists, FILE_NOT_EXISTS);
        s3Client.getClient().deleteObject(new DeleteObjectRequest(s3Client.getAwsProperties().getBucketName(), key));
    }


    private void putObjectV2(String folderName){
        metadata.setContentLength(0);
        try {
            s3Client.getClient().putObject(s3Client.getAwsProperties().getBucketName(), folderName, inputStream, metadata);
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
            Objects.throwIfTrue(isNotNull, FILE_ALREADY_EXISTS_EXCEPTION);
        } catch (AmazonS3Exception ignored){}
    }
}
