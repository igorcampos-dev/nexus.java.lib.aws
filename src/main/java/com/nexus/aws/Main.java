package com.nexus.aws;

import com.nexus.aws.cloud.S3;
import com.nexus.aws.cloud.S3Impl;
import com.nexus.aws.cloud.client.S3Client;
import com.nexus.aws.cloud.client.S3ClientImpl;
import com.nexus.aws.cloud.properties.S3PropertiesImpl;
import org.springframework.core.io.Resource;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class Main {


    public static void main(String[] args) throws IOException {
        S3PropertiesImpl s3Properties = new S3PropertiesImpl();
        s3Properties.setRegion("us-east-1");
        s3Properties.setBucketName("funcionarios");
        s3Properties.setSecretKey("S3RVER");
        s3Properties.setAccessKey("S3RVER");
        s3Properties.setServiceEndpoint("http://localhost:4568");

        S3Client s3Client = new S3ClientImpl(s3Properties);
        S3 s3 = new S3Impl(s3Client);
        File file = new File("carlos-oliveira-03.2023.pdf");
        String folder = "carlos-oliveira";
        String filename = "03-2023";

       // s3.putObject(file, folder, filename);

        //s3.deleteFile(folder, filename);

    }


    }




