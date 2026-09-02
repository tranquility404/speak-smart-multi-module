package com.tranquility.common.file.model;

public record StorageLocation(
        StorageBucket bucket,
        String path,
        String fileId
) {
    public String objectKey() {
        return "%s/%s".formatted(path, fileId);
    }
}