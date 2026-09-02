package com.tranquility.common.file.model;

import com.tranquility.common.file.FileStorageException;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public record UploadFile(
        StorageBucket bucket,
        String path,
        String fileId,
        InputStream content,
        String originalFileName,
        String contentType,
        long size
) {

    public UploadFile {
        if (fileId == null) {
            fileId = UUID.randomUUID().toString();
        }
    }

    public String objectKey() {
        return "%s/%s".formatted(path, fileId);
    }

    public byte[] bytes() {
        try {
            return content.readAllBytes();
        } catch (IOException e) {
            throw new FileStorageException("Failed to read file", e);
        }
    }
}