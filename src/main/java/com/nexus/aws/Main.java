package com.nexus.aws;

import com.nexus.aws.cloud.S3;
import com.nexus.aws.cloud.S3Impl;
import com.nexus.aws.cloud.client.S3Client;
import com.nexus.aws.cloud.client.S3ClientImpl;
import com.nexus.aws.cloud.properties.S3PropertiesImpl;
import com.nexus.aws.model.S3File;

import java.io.File;
import java.util.List;

public class Main {


    public static void main(String[] args) {
        S3PropertiesImpl s3Properties = new S3PropertiesImpl();
        s3Properties.setRegion("us-east-1");
        s3Properties.setBucketName("funcionarios");
        s3Properties.setSecretKey("S3RVER");
        s3Properties.setAccessKey("S3RVER");
        s3Properties.setServiceEndpoint("http://localhost:4568");

        S3Client s3Client = new S3ClientImpl(s3Properties);
        S3 s3 = new S3Impl(s3Client);
        File file = new File("pedro.txt");

        s3.putObject(file, "carlos-oliveira", "carlos-oliveira-03.2023");
        List<S3File> files = s3.listObjectsInFolder("carlos-oliveira");

        files.forEach( filess -> System.out.println(filess));
    }
}
