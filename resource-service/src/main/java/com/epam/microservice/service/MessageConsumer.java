package com.epam.microservice.service;

import java.text.MessageFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MessageConsumer {

  private final ResourceService resourceService;

  public MessageConsumer(ResourceService resourceService) {
    this.resourceService = resourceService;
  }

  @KafkaListener(
      id = "resource_service",
      topics = "#{systemEnvironment['spring.kafka.response_topic']}")
  public void listen(String resourceId) {
    log.info(
        MessageFormat.format(
            "ResourceId: {0}. Processing response received", resourceId));
    resourceService.moveToPermanentStage(Integer.valueOf(resourceId));
  }
}
