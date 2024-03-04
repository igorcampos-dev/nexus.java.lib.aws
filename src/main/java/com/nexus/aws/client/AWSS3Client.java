package com.nexus.aws.client;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.nexus.aws.properties.AwsProperties;
import org.springframework.stereotype.Component;

@Component
public class AWSS3Client {

    private AmazonS3 client;
    private final AwsProperties awsProperties = new AwsProperties();



    public AmazonS3 getClient() {
        if (client == null) {
            client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(getProperties()))
                    .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(awsProperties.getServiceEndpoint(), awsProperties.getRegion()))
                    .build();
        }
        return client;
    }

    private BasicAWSCredentials getProperties(){
        awsProperties.setServiceEndpoint("http://localhost:4568");
        awsProperties.setAccessKey("S3RVER");
        awsProperties.setSecretKey("S3RVER");
        awsProperties.setRegion("us-east-1");
        awsProperties.setBucketName("funcionarios");
        return new BasicAWSCredentials(awsProperties.getAccessKey(), awsProperties.getSecretKey());
    }
}
