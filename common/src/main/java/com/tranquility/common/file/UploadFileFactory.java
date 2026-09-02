package com.tranquility.common.file;

import com.tranquility.common.file.model.StorageBucket;
import com.tranquility.common.file.model.UploadFile;
import com.tranquility.common.utils.FileUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public final class UploadFileFactory {

    private UploadFileFactory() {}

    public static UploadFile userAudio(
            UUID userId,
            InputStream content,
            String originalFileName,
            long size
    ) throws IOException {
        return new UploadFile(
                StorageBucket.USER_FILES,
                "users/%s/audio".formatted(userId), // user_files/users/[useId]/audio/[fileId]
                null,
                content,
                originalFileName,
                FileUtils.detectContentType(content),
                size
        );
    }

    public static UploadFile profilePicture(
            UUID userId,
            InputStream content,
            String originalFileName,
            long size
    ) throws IOException {
        return new UploadFile(
                StorageBucket.PUBLIC_ASSETS,
                "users/profile",   // public_assets/users/profile/[userId]
                userId.toString(),
                content,
                originalFileName,
                FileUtils.detectContentType(content),
                size
        );
    }
}