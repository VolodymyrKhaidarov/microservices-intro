package com.epam.microservice.config;

import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class ApplicationConfiguration {

  @Value("${aws.service.url}")
  private String awsServiceUrl;

  @Bean
  public S3Client getS3Client() {
    return S3Client.builder()
        .endpointOverride(URI.create(awsServiceUrl))
        .forcePathStyle(true)
        .credentialsProvider(DefaultCredentialsProvider.create())
        .region(Region.US_EAST_1)
        .build();
  }
}
