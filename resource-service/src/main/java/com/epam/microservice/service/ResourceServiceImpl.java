package com.epam.microservice.service;

import com.epam.microservice.exception.InvalidFileException;
import com.epam.microservice.exception.ResourceNotFoundException;
import com.epam.microservice.model.Resource;
import com.epam.microservice.repository.ResourceRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ResourceServiceImpl implements ResourceService {

  private final ResourceRepository resourceRepository;
  private final S3Service s3Service;
  private final KafkaTemplate<String, String> kafkaTemplate;

  @Value("${spring.kafka.topic}")
  private String topic;

  @Autowired
  public ResourceServiceImpl(
      ResourceRepository resourceRepository,
      S3Service s3Service,
      KafkaTemplate<String, String> kafkaTemplate) {
    this.resourceRepository = resourceRepository;
    this.s3Service = s3Service;
    this.kafkaTemplate = kafkaTemplate;
  }

  public Integer addResource(MultipartFile multipartFile) {

    if (!"mp3".equals(FilenameUtils.getExtension(multipartFile.getOriginalFilename()))) {
      throw new InvalidFileException("Invalid file");
    }

    String resourceKey = s3Service.uploadResource(multipartFile);
    Integer resourceId =
        resourceRepository.findByResourceKey(resourceKey).stream()
            .findFirst()
            .map(Resource::getId)
            .orElse(null);

    Resource resource = new Resource(resourceId, s3Service.getS3Bucket(), resourceKey);
    resourceId = resourceRepository.save(resource).getId();
    kafkaTemplate.send(topic, resourceId.toString());

    return resourceId;
  }

  public byte[] getResourceById(Integer id) {
    return resourceRepository
        .findById(id)
        .flatMap(resource -> s3Service.downloadResource(resource.getResourceKey()))
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
              s3Service.deleteResource(resource.getResourceKey());
            })
        .map(Resource::getId)
        .toList();
  }
}
