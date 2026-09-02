package com.tranquility.common.file.model;

public record UploadResult(
        StorageBucket bucket,
        String path,
        String fileId,
        String originalFileName,
        String contentType,
        long size
) {
    public static UploadResult create(UploadFile file) {
        return new UploadResult(
                file.bucket(),
                file.path(),
                file.fileId(),
                file.originalFileName(),
                file.contentType(),
                file.size()
        );
    }

    public StorageLocation storageLocation() {
        return new StorageLocation(
                bucket,
                path,
                fileId
        );
    }
}