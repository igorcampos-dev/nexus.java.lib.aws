package com.nexus.aws.exception;

public class FolderEmptyException extends RuntimeException {
    public FolderEmptyException(String s){
        super(s);
    }
}
