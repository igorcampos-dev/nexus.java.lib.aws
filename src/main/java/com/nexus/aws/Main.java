package com.nexus.aws;

import com.nexus.aws.cloud.S3;
import com.nexus.aws.cloud.S3Impl;
import com.nexus.aws.cloud.client.S3Client;
import com.nexus.aws.cloud.client.S3ClientImpl;
import com.nexus.aws.cloud.properties.S3PropertiesImpl;

import java.io.File;

public class Main {


    public static void main(String[] args) {
        S3PropertiesImpl s3Properties = new S3PropertiesImpl();
        S3Client s3Client = new S3ClientImpl(s3Properties);
        S3 s3 = new S3Impl(s3Client);
        File file = new File("pedro.txt");

        s3.putObject(file, "Gustavo-Oliveira", "Gustavo-Oliveira-03.2023");
    }
}
