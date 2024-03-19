package com.nexus.aws.exception;

public class FileAlreadyExistsException extends RuntimeException {
    public FileAlreadyExistsException(String s){
        super(s);
    }
}
