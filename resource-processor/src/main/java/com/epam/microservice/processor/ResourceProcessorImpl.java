package com.epam.microservice.processor;

import com.epam.microservice.exception.ResourceNotAvailableException;
import com.epam.microservice.exception.ResourceProcessorException;
import com.epam.microservice.model.Metadata;
import com.epam.microservice.parser.ResourceParser;
import java.io.ByteArrayInputStream;
import java.text.MessageFormat;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
public class ResourceProcessorImpl implements ResourceProcessor {

  private final ResourceParser resourceParser;
  private final RestClient restClient;
  private final KafkaTemplate<String, String> kafkaTemplate;

  @Value("${spring.kafka.response_topic}")
  private String responseTopic;

  @Value("${song.service.url}")
  private String songServiceUrl;

  @Value("${resource.service.url}")
  private String resourceServiceUrl;

  public ResourceProcessorImpl(
      ResourceParser resourceParser,
      RestClient restClient,
      KafkaTemplate<String, String> kafkaTemplate) {
    this.resourceParser = resourceParser;
    this.restClient = restClient;
    this.kafkaTemplate = kafkaTemplate;
  }

  @Override
  public void processMetadata(Integer resourceId) {
    byte[] resourceData = getResourceData(resourceId);
    Metadata metadata = parseResourceMetadata(resourceId, resourceData);
    postResourceMetadata(resourceId, metadata);
  }

  private byte[] getResourceData(Integer resourceId) {
    ByteArrayResource resource =
        restClient
            .get()
            .uri(resourceServiceUrl + "/" + resourceId)
            .retrieve()
            .body(ByteArrayResource.class);

    if (Objects.nonNull(resource)) {
      log.info(MessageFormat.format("ResourceId={0}: Resource downloaded", resourceId));
      return resource.getByteArray();
    } else {
      throw new ResourceNotAvailableException(
          MessageFormat.format("ResourceId={0}: Resource not available", resourceId));
    }
  }

  private Metadata parseResourceMetadata(Integer resourceId, byte[] resourceData) {
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(resourceData);
    Metadata metadata = resourceParser.getMetadata(resourceId, byteArrayInputStream);
    log.info(MessageFormat.format("ResourceId={0}: Resource metadata parsed", resourceId));
    return metadata;
  }

  private void postResourceMetadata(Integer resourceId, Metadata metadata) {
    Integer metadataId =
        restClient
            .post()
            .uri(songServiceUrl)
            .contentType(MediaType.APPLICATION_JSON)
            .body(metadata)
            .retrieve()
            .body(Integer.class);

    if (Objects.nonNull(metadataId)) {
      log.info(
          MessageFormat.format(
              "ResourceId={0}: MetadataId={1} inserted/updated in DB", resourceId, metadataId));
    } else {
      throw new ResourceProcessorException(
          MessageFormat.format(
              "ResourceId={0}: Something went wrong, please try again later", resourceId));
    }

    log.info(
        MessageFormat.format(
            "ResourceId={0}: MetadataId={1} has been processed. Sent response to Resource Service",
            resourceId, metadataId));
    kafkaTemplate.send(responseTopic, resourceId.toString());
  }
}
