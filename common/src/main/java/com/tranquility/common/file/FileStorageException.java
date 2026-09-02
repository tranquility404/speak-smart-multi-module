package com.tranquility.common.file;

public class FileStorageException extends RuntimeException {
    public Exception srcExc;
    public FileStorageException(String message, Exception e) {
        super(message);
        this.srcExc = e;
    }
}
