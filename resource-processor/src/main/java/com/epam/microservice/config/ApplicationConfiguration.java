package com.epam.microservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;
import org.springframework.web.client.RestClient;

@Configuration
@Slf4j
public class ApplicationConfiguration {

  @Value(value = "${kafka.backoff.interval}")
  private Long interval;

  @Value(value = "${kafka.backoff.max_failure}")
  private Long maxAttempts;

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
