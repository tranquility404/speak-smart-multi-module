package com.tranquility.common.infrastructure;

import com.tranquility.common.file.FileStorage;
import com.tranquility.common.file.model.StorageBucket;
import com.tranquility.common.file.model.StorageLocation;
import com.tranquility.common.file.model.UploadResult;
import com.tranquility.common.file.model.UploadFile;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Duration;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SupabaseFileStorage implements FileStorage {

    private final RestClient client;

    @Value("${supabase.url}")
    private final String supabaseUrl;

    @Value("${supabase.service-role-key}")
    private final String serviceRoleKey;

    @Value("${supabase.storage.buckets.user}")
    private final String userBucket;

    @Value("${supabase.storage.buckets.public}")
    private final String publicBucket;

    @Value("${supabase.storage.buckets.temp}")
    private final String tempBucket;

    @Override
    public UploadResult upload(UploadFile file) {
        String objectKey = file.objectKey();
        byte[] content = file.bytes();

        client.post()
                .uri(supabaseUrl + "/storage/v1/object/{bucket}/{objectKey}",
                        resolveBucket(file.bucket()),
                        objectKey)
                .header("Authorization", "Bearer " + serviceRoleKey)
                .header("apikey", serviceRoleKey)
                .header("Content-Type", file.contentType())
                .body(content)
                .retrieve()
                .toBodilessEntity();

        return UploadResult.create(file);
    }

    @Override
    public InputStream download(StorageLocation location) {
        byte[] content = client.get()
                .uri(supabaseUrl + "/storage/v1/object/{bucket}/{objectKey}",
                        resolveBucket(location.bucket()),
                        location.objectKey())
                .header("Authorization", "Bearer " + serviceRoleKey)
                .header("apikey", serviceRoleKey)
                .retrieve()
                .body(byte[].class);

        return new ByteArrayInputStream(content);
    }

    @Override
    public String generateAccessUrl(StorageLocation location, Duration expiresIn) {
        // Supabase signed URL creation
        // /storage/v1/object/sign/{bucket}/{path}

        SignedUrlResponse response = client.post()
                .uri(supabaseUrl + "/storage/v1/object/sign/{bucket}/{objectKey}",
                        resolveBucket(location.bucket()),
                        location.objectKey())
                .header("Authorization", "Bearer " + serviceRoleKey)
                .header("apikey", serviceRoleKey)
                .body(Map.of("expiresIn", expiresIn.toSeconds()))
                .retrieve()
                .body(SignedUrlResponse.class);

        return supabaseUrl + "/storage/v1" + response.signedURL();
    }

    @Override
    public String generatePublicUrl(StorageLocation location) {
        return "%s/storage/v1/object/public/%s/%s"
                .formatted(
                        supabaseUrl,
                        resolveBucket(location.bucket()),
                        location.objectKey()
                );
    }

    @Override
    public void delete(StorageLocation location) {
        client.delete()
                .uri(supabaseUrl + "/storage/v1/object/{bucket}/{objectKey}",
                        resolveBucket(location.bucket()),
                        location.objectKey())
                .header("Authorization", "Bearer " + serviceRoleKey)
                .header("apikey", serviceRoleKey)
                .retrieve()
                .toBodilessEntity();
    }

    private String resolveBucket(StorageBucket bucket) {
        return switch (bucket) {
            case USER_FILES -> userBucket;
            case PUBLIC_ASSETS -> publicBucket;
            case TEMP_FILES -> tempBucket;
        };
    }

    private record SignedUrlResponse(
            String signedURL
    ) {}
}
