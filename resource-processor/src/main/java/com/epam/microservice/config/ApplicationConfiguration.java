package com.epam.microservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ApplicationConfiguration {

  @Value("${song.service.url}")
  private String songServiceUrl;

  @Bean
  RestClient getRestClient() {
    return RestClient.create(songServiceUrl);
  }
}
