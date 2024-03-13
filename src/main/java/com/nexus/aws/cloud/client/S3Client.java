package com.nexus.aws.cloud.client;

import com.amazonaws.services.s3.AmazonS3;
import com.nexus.aws.cloud.properties.S3Properties;

public interface S3Client {
    AmazonS3 getClient();
    S3Properties getAwsProperties();
}
