package com.epam.microservice.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

@Service
@Slf4j
public class MessageConsumer {

  private final ResourceProcessor resourceProcessor;

  public MessageConsumer(ResourceProcessor resourceProcessor) {
    this.resourceProcessor = resourceProcessor;
  }

  @KafkaListener(id = "resource", topics = "#{systemEnvironment['spring.kafka.topic']}")
  public void listen(String resourceId) {
    log.info(MessageFormat.format("Message received: ResourceId={0}", resourceId));
    resourceProcessor.processMetadata(Integer.valueOf(resourceId));
  }
}
