package com.epam.microservice.service;

import static com.epam.microservice.model.StorageType.PERMANENT;
import static com.epam.microservice.model.StorageType.STAGING;
import static java.text.MessageFormat.format;

import com.epam.microservice.exception.InvalidFileException;
import com.epam.microservice.exception.ResourceNotFoundException;
import com.epam.microservice.model.Resource;
import com.epam.microservice.model.StorageObject;
import com.epam.microservice.model.StorageType;
import com.epam.microservice.repository.ResourceRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
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

    byte[] bytes;

    try {
      bytes = multipartFile.getBytes();
    } catch (IOException e) {
      throw new InvalidFileException("Invalid file");
    }

    String key = multipartFile.getOriginalFilename();

    Integer id = getResourceIdIfExists(key);
    StorageObject stagStorage = getStorage(STAGING);
    Resource resource = new Resource(id, stagStorage.getBucket(), stagStorage.getPath(), key);

    id = resourceRepository.save(resource).getId();
    log.info(format("ResourceKey {0}: Assigned ResourceId {1}", key, id));
    log.info(format("ResourceId {0}: Saved in resource-db", id));

    s3Service.uploadResource(stagStorage.getPath() + key, bytes, stagStorage.getBucket());
    log.info(format("ResourceId {0}: Saved in staging storage", id));

    kafkaTemplate.send(requestTopic, id.toString());
    log.info(format("ResourceId {0}: Processing request sent to topic {1}", id, requestTopic));

    return id;
  }

  public byte[] getResourceById(Integer id) {
    return resourceRepository
        .findById(id)
        .flatMap(
            resource ->
                s3Service.downloadResource(
                    resource.getPath() + resource.getResourceKey(), resource.getBucket()))
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
              s3Service.deleteResource(
                  resource.getPath() + resource.getResourceKey(), resource.getBucket());
            })
        .map(Resource::getId)
        .toList();
  }

  @Override
  public void moveToPermanentStage(Integer id) {

    String resourceKey = resourceRepository.findById(id).map(Resource::getResourceKey).orElse(null);

    if (Objects.nonNull(resourceKey)) {

      StorageObject stagStorage = getStorage(STAGING);
      StorageObject permStorage = getStorage(PERMANENT);

      s3Service.moveResource(
          stagStorage.getPath() + resourceKey,
          stagStorage.getBucket(),
          permStorage.getPath() + resourceKey,
          permStorage.getBucket());

      Resource permResource =
          new Resource(id, permStorage.getBucket(), permStorage.getPath(), resourceKey);

      resourceRepository.save(permResource);

      log.info(format("ResourceId {0}: Resource moved to permanent storage", id));
    }
  }

  private Integer getResourceIdIfExists(String resourceKey) {
    return resourceRepository.findByResourceKey(resourceKey).stream()
        .findFirst()
        .map(Resource::getId)
        .orElse(null);
  }

  public StorageObject getStorage(StorageType storageType) {
    return getStorages().stream()
        .filter(storageObject -> storageType.toString().equals(storageObject.getStorageType()))
        .findFirst()
        .orElse(null);
  }

  @CircuitBreaker(name = "storage-service", fallbackMethod = "getStubbedStorages")
  public List<StorageObject> getStorages() {
    return restClient
        .get()
        .uri(storageServiceUrl)
        .retrieve()
        .body(new ParameterizedTypeReference<>() {});
  }

  public List<StorageObject> getStubbedStorages(Throwable ex) {
    log.warn("Circuit Breaker fallback method called");
    return List.of(
        new StorageObject(1, STAGING.toString(), "staging-storage", "staging-folder/"),
        new StorageObject(2, PERMANENT.toString(), "permanent-storage", "permanent-folder/"));
  }
}
