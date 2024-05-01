package com.nexus.aws.cloud;

import com.nexus.aws.model.S3File;
import java.io.InputStream;
import java.util.List;

public interface S3 {
    void createFolder(String folderName);
    void putObject(InputStream file, String folder, String filename);
    List<S3File> listObjectsInFolder(String folderPath);
    void deleteFile(String folderName, String fileName);
    void updateObject(InputStream file, String folder, String filename);
    byte[] getFile(String folder, String filename);
}
