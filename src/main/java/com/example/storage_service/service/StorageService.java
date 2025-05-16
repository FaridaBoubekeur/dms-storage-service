package com.example.storage_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.UUID;

@Service
public class StorageService {

  @Value("${s3.bucket}")
  private String bucketName;

  @Value("${s3.endpoint}")
  private String endpoint;

  @Value("${s3.access-key}")
  private String accessKey;

  @Value("${s3.secret-key}")
  private String secretKey;

  private final S3Client s3;

  public StorageService(S3Client s3) {
    this.s3 = s3;
  }

  public String uploadFile(String documentId, MultipartFile file) throws IOException {
    // Ensure bucket exists
    createBucketIfNotExists();

    String key = documentId + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

    PutObjectRequest putRequest = PutObjectRequest.builder()
        .bucket(bucketName)
        .key(key)
        .contentType(file.getContentType())
        .build();

    s3.putObject(putRequest, RequestBody.fromBytes(file.getBytes()));
    return key;
  }

  private void createBucketIfNotExists() {
    try {
      // Check if bucket exists
      s3.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
    } catch (NoSuchBucketException e) {
      // Create bucket if it doesn't exist
      s3.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
      System.out.println("Created bucket: " + bucketName);
    } catch (Exception e) {
      System.err.println("Error checking bucket: " + e.getMessage());
    }
  }

  public String generatePresignedUrl(String documentId, String filename) {
    String key = documentId + "/" + filename;
    return generatePresignedUrlByKey(key);
  }

  public String generatePresignedUrlByKey(String key) {
    try {
      // Ensure bucket exists
      createBucketIfNotExists();

      S3Presigner presigner = S3Presigner.builder()
          .endpointOverride(URI.create(endpoint))
          .region(Region.US_EAST_1)
          .credentialsProvider(StaticCredentialsProvider.create(
              AwsBasicCredentials.create(accessKey, secretKey)))
          .build();

      GetObjectRequest getObjectRequest = GetObjectRequest.builder()
          .bucket(bucketName)
          .key(key)
          .build();

      GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
          .signatureDuration(Duration.ofMinutes(10))
          .getObjectRequest(getObjectRequest)
          .build();

      PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);

      // Fix 'documents.localhost' for browser compatibility
      String rawUrl = presignedRequest.url().toString();
      String fixedUrl = rawUrl.replace("documents.localhost", "localhost");

      // Log the URL for debugging
      System.out.println("Generated presigned URL: " + fixedUrl);

      return fixedUrl;
    } catch (Exception e) {
      System.err.println("Error generating presigned URL: " + e.getMessage());
      e.printStackTrace();
      throw new RuntimeException("Failed to generate download URL: " + e.getMessage(), e);
    }
  }
}