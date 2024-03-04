package com.nexus.aws.service;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.model.*;
import com.nexus.aws.client.AWSS3Client;
import com.nexus.aws.exception.FolderEmptyException;
import com.nexus.aws.model.S3File;
import com.nexus.aws.properties.AwsProperties;
import com.nexus.utils.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Component
@RequiredArgsConstructor
public class S3Uploader {

    private final AWSS3Client awss3Client;
    private final AwsProperties awsProperties;
    private static final ObjectMetadata metadata = new ObjectMetadata();
    private static final InputStream inputStream = new ByteArrayInputStream(new byte[0]);
    private static final FolderEmptyException FOLDER_EMPTY_EXCEPTION = new FolderEmptyException("Lista vazia, você não possui contraCheques");


    private void putObject(File file,String folder, String filename){
        String key = String.format("%s/%s", folder, filename);

        try {
            this.verifyBucketExistsOrElseCreate();
            this.verifyFolderExistsOrElseCreate(folder);
            this.awss3Client.getClient().putObject(new PutObjectRequest(awsProperties.getBucketName(), key, file));
        } catch (SdkClientException e){
            strangeError(e);
        } catch (Exception e) {
            log.info("Error in put file:".concat(e.getMessage()));
        }
    }

    private void verifyFolderExistsOrElseCreate(String folder) {
        ListObjectsV2Request request = new ListObjectsV2Request()
                .withBucketName(this.awsProperties.getBucketName())
                .withPrefix(folder)
                .withDelimiter("/");

        ListObjectsV2Result result = this.awss3Client.getClient().listObjectsV2(request);

        if (result.getCommonPrefixes().isEmpty()){
            this.createFolder(folder.concat("/"));
        }

    }

    private void putObjectV2(String folderName){
        metadata.setContentLength(0);
        try {
            awss3Client.getClient().putObject(awsProperties.getBucketName(), folderName, inputStream, metadata);
        } catch (Exception ignored){}
    }

    private void strangeError(SdkClientException e){
        String estrangeMessage = "Unable to verify integrity of data upload. Client calculated content hash (contentMD5:";
        if (!e.getMessage().contains(estrangeMessage)) {
            log.error("Erro ao enviar para o Amazon S3: {}", e.getMessage());
        }
    }

    private void verifyBucketExistsOrElseCreate() {
        if (awss3Client.getClient().listBuckets().isEmpty()) {
            awss3Client.getClient().createBucket(awsProperties.getBucketName());
        }
    }

    private void createFolder(String folderName){
        if (!folderName.endsWith("/")) { folderName += "/"; }
        this.verifyBucketExistsOrElseCreate();
        this.putObjectV2(folderName);
    }

    public List<S3File> listObjectsInFolder(String folderPath) {
        List<S3File> files;
        ListObjectsV2Request request = this.listFiles(folderPath);
        ListObjectsV2Result result;

        do {
            result = awss3Client.getClient().listObjectsV2(request);
            files = this.formateList(result, folderPath);
            request.setContinuationToken(result.getNextContinuationToken());
        } while (result.isTruncated());

        return files;
    }


    private ListObjectsV2Request listFiles(String folderPath){
        return new ListObjectsV2Request()
                .withBucketName(this.awsProperties.getBucketName())
                .withPrefix(folderPath + "/");
    }

    private List<S3File> formateList(ListObjectsV2Result result, String folderPath) {
        Objects.requireNonEmpty(result.getObjectSummaries(), FOLDER_EMPTY_EXCEPTION );

        return result.getObjectSummaries().stream()
                .filter(s3ObjectSummary -> !s3ObjectSummary.getKey().equals(folderPath + "/"))
                .map(s3ObjectSummary -> S3File.builder()
                        .size(s3ObjectSummary.getSize())
                        .filename(s3ObjectSummary.getKey()
                                                 .substring(s3ObjectSummary.getKey().lastIndexOf("/") + 1))
                        .build()).collect(Collectors.toList());
    }

























    public static void main(String[] args) {
        AWSS3Client awss3Client = new AWSS3Client();

        AwsProperties awsProperties = new AwsProperties();

        awsProperties.setServiceEndpoint("http://localhost:4568");
        awsProperties.setAccessKey("S3RVER");
        awsProperties.setSecretKey("S3RVER");
        awsProperties.setRegion("us-east-1");
        awsProperties.setBucketName("funcionarios");

        String fileName = "pedro.txt";
        File file = new File(fileName);

        S3Uploader s3Uploader = new S3Uploader(awss3Client, awsProperties);
        s3Uploader.putObject(file, "igor-de-campos", fileName);
        //List<S3File> files = s3Uploader.listObjectsInFolder("pedro-bonifacio-coco");
        //files.forEach( files1 -> System.out.println(files1.toString()));
    }
}
