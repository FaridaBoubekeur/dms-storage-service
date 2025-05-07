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
  String key = documentId + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

  PutObjectRequest putRequest = PutObjectRequest.builder()
    .bucket(bucketName)
    .key(key)
    .contentType(file.getContentType())
    .build();

  s3.putObject(putRequest, RequestBody.fromBytes(file.getBytes()));
  return key;
 }

 public String generatePresignedUrl(String documentId, String filename) {
  String key = documentId + "/" + filename;
  return generatePresignedUrlByKey(key);
 }

 public String generatePresignedUrlByKey(String key) {
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

  return presignedRequest.url().toString();
 }
}
