package com.epam.microservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ApplicationConfiguration {

  @Bean
  RestClient getRestClient() {
    return RestClient.create();
  }
}
