package com.epam.microservice.processor;

import java.text.MessageFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MessageConsumer {

  private final ResourceProcessor resourceProcessor;

  public MessageConsumer(ResourceProcessor resourceProcessor) {
    this.resourceProcessor = resourceProcessor;
  }

  @KafkaListener(
      id = "resource_processor",
      topics = "#{systemEnvironment['spring.kafka.request_topic']}")
  public void listen(String resourceId) {
    log.info(
        MessageFormat.format(
            "ResourceId={0}: Processing request received in Resource Processor", resourceId));
    resourceProcessor.processMetadata(Integer.valueOf(resourceId));
  }
}