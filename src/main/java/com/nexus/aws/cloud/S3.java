package com.nexus.aws.cloud;

import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.nexus.aws.model.S3File;
import java.io.InputStream;
import java.util.List;

public interface S3 {
    void verifyBucketExistsOrElseCreate();
    void verifyFolderExistsOrElseCreate(String folder);
    void createFolder(String folderName);
    void putObject(InputStream file, String folder, String filename);
    List<S3File> listObjectsInFolder(String folderPath);
    List<S3File> formateList(ListObjectsV2Result result, String folderPath);
    void deleteFile(String folderName, String fileName);

}
