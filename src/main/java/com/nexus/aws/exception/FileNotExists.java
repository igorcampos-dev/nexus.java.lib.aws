package com.nexus.aws.exception;

public class FileNotExists extends RuntimeException {
    public FileNotExists(String s){
        super(s);
    }
}
