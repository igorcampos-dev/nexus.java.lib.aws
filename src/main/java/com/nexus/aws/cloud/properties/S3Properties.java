package com.nexus.aws.cloud.properties;

public interface S3Properties {
    String getServiceEndpoint();
    String getAccessKey();
    String getSecretKey();
    String getRegion();
    String getBucketName();
}
