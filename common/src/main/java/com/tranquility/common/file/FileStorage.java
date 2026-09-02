package com.tranquility.common.file;

import com.tranquility.common.file.model.StorageLocation;
import com.tranquility.common.file.model.UploadResult;
import com.tranquility.common.file.model.UploadFile;

import java.io.InputStream;
import java.time.Duration;

public interface FileStorage {

    UploadResult upload(UploadFile file);

    InputStream download(StorageLocation location);

    String generateAccessUrl(StorageLocation location, Duration expiresIn);

    public String generatePublicUrl(StorageLocation location);

    void delete(StorageLocation location);
}