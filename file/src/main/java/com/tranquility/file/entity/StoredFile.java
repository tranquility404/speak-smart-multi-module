package com.tranquility.file.entity;

import com.tranquility.common.model.AuditableEntity;
import com.tranquility.common.file.model.FileVisibility;
import com.tranquility.common.file.model.StorageBucket;
import com.tranquility.common.file.model.UploadResult;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(
        name = "files",
        indexes = {
                @Index(name = "idx_files_owner_id", columnList = "ownerId")
        }
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoredFile extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID ownerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StorageBucket bucket;

    @Column(nullable = false)
    private String path;

    @Column(nullable = false, unique = true)
    private String fileId;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false)
    private long size;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FileVisibility visibility;

    @Column(nullable = false)
    private String publicUrl;

    public static StoredFile createPublicFile(
            UUID ownerId,
            StorageBucket bucket,
            String path,
            String fileId,
            String originalFileName,
            String contentType,
            long size,
            String publicUrl
    ) {
        StoredFile file = new StoredFile();
        file.setOwnerId(ownerId);
        file.setBucket(bucket);
        file.setPath(path);
        file.setFileId(fileId);
        file.setOriginalFileName(originalFileName);
        file.setContentType(contentType);
        file.setSize(size);
        file.setVisibility(FileVisibility.PUBLIC);
        file.setPublicUrl(publicUrl);
        return file;
    }

    public static StoredFile createPublicFile(
            UUID ownerId,
            UploadResult result,
            String publicUrl
    ) {
        return createPublicFile(
                ownerId,
                result.bucket(),
                result.path(),
                result.fileId(),
                result.originalFileName(),
                result.contentType(),
                result.size(),
                publicUrl
        );
    }

    public static StoredFile createPrivateFile(
            UUID ownerId,
            StorageBucket bucket,
            String path,
            String fileId,
            String originalFileName,
            String contentType,
            long size
    ) {
        StoredFile file = new StoredFile();
        file.setOwnerId(ownerId);
        file.setBucket(bucket);
        file.setPath(path);
        file.setFileId(fileId);
        file.setOriginalFileName(originalFileName);
        file.setContentType(contentType);
        file.setSize(size);
        file.setVisibility(FileVisibility.PRIVATE);
        return file;
    }

    public static StoredFile createPrivateFile(
            UUID ownerId,
            UploadResult result
    ) {
        return createPrivateFile(
                ownerId,
                result.bucket(),
                result.path(),
                result.fileId(),
                result.originalFileName(),
                result.contentType(),
                result.size()
        );
    }
}