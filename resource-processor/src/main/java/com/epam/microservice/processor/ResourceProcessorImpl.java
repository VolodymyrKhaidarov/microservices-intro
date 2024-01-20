package com.epam.microservice.processor;

import com.epam.microservice.exception.InvalidResourceDataException;
import com.epam.microservice.exception.ResourceNotAvailableException;
import com.epam.microservice.model.ResourceMetadata;
import com.epam.microservice.parser.ResourceParser;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
public class ResourceProcessorImpl implements ResourceProcessor {

  private final ResourceParser resourceParser;
  private final RestClient restClient;

  @Value("${song.service.url}")
  private String songServiceUrl;

  @Value("${resource.service.url}")
  private String resourceServiceUrl;

  public ResourceProcessorImpl(ResourceParser resourceParser, RestClient restClient) {
    this.resourceParser = resourceParser;
    this.restClient = restClient;
  }

  @Override
  public Integer processMetadata(Integer resourceId) {
    return getResourceData(restClient, resourceServiceUrl, resourceId)
        .flatMap(resource -> parseResourceMetadata(resourceParser, resourceId, resource))
        .flatMap(metadata -> postResourceMetadata(restClient, songServiceUrl, metadata))
        .orElse(-1);
  }

  private Optional<byte[]> getResourceData(
      RestClient restClient, String resourceServiceUrl, Integer resourceId) {
    try {
      return Optional.ofNullable(
              restClient
                  .get()
                  .uri(resourceServiceUrl + "/" + resourceId)
                  .retrieve()
                  .onStatus(
                      HttpStatusCode::is4xxClientError,
                      (request, response) -> {
                        throw new ResourceNotAvailableException(
                            MessageFormat.format(
                                " ResourceId={0}: Resource not available", resourceId));
                      })
                  .body(ByteArrayResource.class))
          .map(
              resource -> {
                log.info(MessageFormat.format(" ResourceId={0}: Resource downloaded", resourceId));
                return resource;
              })
          .map(ByteArrayResource::getByteArray);
    } catch (ResourceNotAvailableException exception) {
      log.error(exception.getMessage());
      return Optional.empty();
    }
  }

  private static Optional<ResourceMetadata> parseResourceMetadata(
      ResourceParser resourceParser, Integer resourceId, byte[] resourceData) {
    try (InputStream inputStream = new ByteArrayInputStream(resourceData)) {
      return Optional.of(resourceParser.getMetadata(resourceId, inputStream))
          .map(
              resourceMetadata -> {
                log.info(
                    MessageFormat.format(" ResourceId={0}: Resource metadata parsed", resourceId));
                return resourceMetadata;
              });

    } catch (IOException | InvalidResourceDataException exception) {
      log.error(exception.getMessage());
      return Optional.empty();
    }
  }

  private static Optional<Integer> postResourceMetadata(
      RestClient restClient, String songServiceUrl, ResourceMetadata metadata) {
    return Optional.ofNullable(
            restClient
                .post()
                .uri(songServiceUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(metadata)
                .retrieve()
                .body(Integer.class))
        .map(
            metadataId -> {
              log.info(
                  MessageFormat.format(
                      " MetadataId={0}: Metadata inserted/updated in DB", metadataId));
              return metadataId;
            });
  }
}
