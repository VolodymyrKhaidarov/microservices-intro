package com.epam.microservice.config;

import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;
import org.springframework.web.client.RestClient;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Slf4j
@Configuration
public class ApplicationConfiguration {

  @Value(value = "${kafka.backoff.interval}")
  private Long interval;

  @Value(value = "${kafka.backoff.max_failure}")
  private Long maxAttempts;

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

  @Bean
  RestClient getRestClient() {
    return RestClient.create();
  }

  @Bean
  public DefaultErrorHandler errorHandler() {
    return new DefaultErrorHandler(
        (consumerRecord, exception) -> log.error(exception.getMessage()),
        new FixedBackOff(interval, maxAttempts));
  }
}
