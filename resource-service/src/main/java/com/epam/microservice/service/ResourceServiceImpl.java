package com.epam.microservice.service;

import static java.text.MessageFormat.format;

import com.epam.microservice.exception.InvalidFileException;
import com.epam.microservice.exception.ResourceNotFoundException;
import com.epam.microservice.model.Resource;
import com.epam.microservice.model.StorageObject;
import com.epam.microservice.model.StorageType;
import com.epam.microservice.repository.ResourceRepository;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class ResourceServiceImpl implements ResourceService {

  private final ResourceRepository resourceRepository;
  private final S3Service s3Service;
  private final KafkaTemplate<String, String> kafkaTemplate;
  private final RestClient restClient;

  @Value("${spring.kafka.request_topic}")
  private String requestTopic;

  @Value("${storage.service.url}")
  private String storageServiceUrl;

  @Autowired
  public ResourceServiceImpl(
      ResourceRepository resourceRepository,
      S3Service s3Service,
      KafkaTemplate<String, String> kafkaTemplate,
      RestClient restClient) {
    this.resourceRepository = resourceRepository;
    this.s3Service = s3Service;
    this.kafkaTemplate = kafkaTemplate;
    this.restClient = restClient;
  }

  public Integer addResource(MultipartFile multipartFile) {

    if (!"mp3".equals(FilenameUtils.getExtension(multipartFile.getOriginalFilename()))) {
      throw new InvalidFileException("Invalid file");
    }

    String key = multipartFile.getOriginalFilename();
    log.info(format("ResourceKey={0}", key));

    byte[] bytes;

    try {
      bytes = multipartFile.getBytes();
    } catch (IOException e) {
      throw new InvalidFileException("Invalid file");
    }

    String stagingStoragePath = getStoragePath(StorageType.STAGING);
    log.info(format("ResourceKey={0}: Staging storage path: {1}", key, stagingStoragePath));

    s3Service.uploadResource(key, bytes, stagingStoragePath);
    log.info(
        format(
            "ResourceKey={0}: Resource uploaded to STAGING storage {1}", key, stagingStoragePath));

    Integer resourceId =
        resourceRepository.findByResourceKey(key).stream()
            .findFirst()
            .map(Resource::getId)
            .orElse(null);

    Resource resource = new Resource(resourceId, stagingStoragePath, key);
    resourceId = resourceRepository.save(resource).getId();

    log.info(format("ResourceKey={0}: Saved to resource-db with id: {1}", key, resourceId));

    kafkaTemplate.send(requestTopic, resourceId.toString());
    log.info(
        format(
            "ResourceKey={0}: Processing request with ResourceId {1} sent to topic: {2}",
            key, resourceId, requestTopic));

    return resourceId;
  }

  public byte[] getResourceById(Integer id) {
    return resourceRepository
        .findById(id)
        .flatMap(
            resource -> s3Service.downloadResource(resource.getResourceKey(), resource.getBucket()))
        .orElseThrow(
            () -> new ResourceNotFoundException("The resource with id " + id + " does not exist"));
  }

  @Override
  public List<Integer> deleteResources(String ids) {
    return Optional.ofNullable(ids).map(str -> str.split(",")).stream()
        .flatMap(Stream::of)
        .filter(str -> str.matches("-?(0|[1-9]\\d*)"))
        .map(Integer::parseInt)
        .map(resourceRepository::findById)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .peek(
            resource -> {
              resourceRepository.deleteById(resource.getId());
              s3Service.deleteResource(resource.getResourceKey(), resource.getBucket());
            })
        .map(Resource::getId)
        .toList();
  }

  @Override
  public void moveToPermanentStage(Integer id) {
    log.info(format("ResourceId={0}: Start moving to the PERMANENT storage", id));
    Optional<Resource> stagingResource = resourceRepository.findById(id);
    String stagingStoragePath = getStoragePath(StorageType.STAGING);
    String permanentStoragePath = getStoragePath(StorageType.PERMANENT);

    if (stagingResource.isPresent()) {
      log.info(format("ResourceId={0}: Resource found in resource-db", id));

      String resourceKey = stagingResource.get().getResourceKey();
      byte[] payload = s3Service.downloadResource(resourceKey, stagingStoragePath).orElse(null);
      log.info(format("ResourceId={0}: Retrieved ResourceKey: {1}", id, resourceKey));

      s3Service.deleteResource(resourceKey, getStoragePath(StorageType.STAGING));
      log.info(format("ResourceId={0}: Resource removed from the STAGING storage", id));

      s3Service.uploadResource(resourceKey, payload, permanentStoragePath);
      log.info(format("ResourceId={0}: Resource saved in the PERMANENT storage", id));

      resourceRepository.save(new Resource(id, permanentStoragePath, resourceKey));
      log.info(format("ResourceId={0}: Resource updated in resource-db", id));
    }
  }

  private String getStoragePath(StorageType storageType) {
    StorageObject storage = getStorageDetails(storageType);
    return storage.getBucket();
  }

  private StorageObject getStorageDetails(StorageType storageType) {
    return getStorages().stream()
        .filter(storageObject -> storageType.toString().equals(storageObject.getStorageType()))
        .findFirst()
        .orElse(null);
  }

  private List<StorageObject> getStorages() {
    return restClient
        .get()
        .uri(storageServiceUrl)
        .retrieve()
        .body(new ParameterizedTypeReference<>() {});
  }
}
