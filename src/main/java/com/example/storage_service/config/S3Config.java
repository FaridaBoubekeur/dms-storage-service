package com.example.storage_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.S3Configuration.Builder;

import java.net.URI;

@Configuration
public class S3Config {

 @Value("${s3.endpoint}")
 private String endpoint;

 @Value("${s3.access-key}")
 private String accessKey;

 @Value("${s3.secret-key}")
 private String secretKey;

 @Bean
 public S3Client s3Client() {
  return S3Client.builder()
    .credentialsProvider(
      StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
    .endpointOverride(URI.create(endpoint))
    .region(Region.US_EAST_1) // MinIO ignores region but SDK needs one
    .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
    .build();
 }
}
