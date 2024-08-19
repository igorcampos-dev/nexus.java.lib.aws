package com.nexus.aws.cloud;

import com.nexus.aws.model.S3File;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface S3 {
    void putObject(MultipartFile file, String folder, String filename);
    List<S3File> listObjectsInFolder(String folderPath);
    void deleteFile(String folderName, String fileName);
    void updateObject(MultipartFile file, String folder, String filename);
    byte[] getFile(String folder, String filename);
}
