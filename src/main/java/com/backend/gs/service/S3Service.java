package com.backend.gs.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.time.Duration;

@Service
public class S3Service {

    @Value("${aws.region:us-east-1}")
    private String awsRegion;

    private S3Presigner createPresigner() {
        return S3Presigner.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    public String generatePresignedUrl(String bucket, String key, int expirationSeconds) {
        try (S3Presigner presigner = createPresigner()) {
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofSeconds(expirationSeconds))
                    .getObjectRequest(request -> request
                            .bucket(bucket)
                            .key(key))
                    .build();

            PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
            return presignedRequest.url().toString();
        }
    }

    public String extractKey(String s3Path) {
        // Remove s3:// prefix
        String path = s3Path.replace("s3://", "");
        int firstSlash = path.indexOf('/');
        if (firstSlash == -1) {
            throw new IllegalArgumentException("Invalid S3 path format: " + s3Path);
        }
        return path.substring(firstSlash + 1);
    }

    public String extractBucket(String s3Path) {
        // Remove s3:// prefix
        String path = s3Path.replace("s3://", "");
        int firstSlash = path.indexOf('/');
        if (firstSlash == -1) {
            throw new IllegalArgumentException("Invalid S3 path format: " + s3Path);
        }
        return path.substring(0, firstSlash);
    }

}

