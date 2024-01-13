package com.epam.microservice.service;

import com.epam.microservice.exception.InvalidFileException;
import com.epam.microservice.exception.ResourceNotFoundException;
import com.epam.microservice.model.Resource;
import com.epam.microservice.repository.ResourceRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ResourceServiceImpl implements ResourceService {

  private final ResourceRepository resourceRepository;
  private final S3Service s3Service;

  @Autowired
  public ResourceServiceImpl(ResourceRepository resourceRepository, S3Service s3Service) {
    this.resourceRepository = resourceRepository;
    this.s3Service = s3Service;
  }

  public Integer addResource(MultipartFile multipartFile) {

    if (!"mp3".equals(FilenameUtils.getExtension(multipartFile.getOriginalFilename()))) {
      throw new InvalidFileException("Invalid file");
    }

    String resourceKey = s3Service.uploadResource(multipartFile);
    return resourceRepository.findByResourceKey(resourceKey).stream()
        .findAny()
        .orElseGet(() -> resourceRepository.save(new Resource(null, s3Service.getS3Bucket(), resourceKey)))
        .getId();
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

    List<Integer> deletedFiles = new ArrayList<>();

    for (Integer id : parseCSV(ids)) {
      resourceRepository
          .findById(id)
          .ifPresent(
              resource -> {
                resourceRepository.deleteById(resource.getId());
                s3Service.deleteResource(resource.getResourceKey());
                deletedFiles.add(id);
              });
    }
    return deletedFiles;
  }

  private List<Integer> parseCSV(String csv) {
    return Optional.ofNullable(csv).map(str -> str.split(",")).stream()
        .flatMap(Stream::of)
        .filter(str -> str.matches("-?(0|[1-9]\\d*)"))
        .map(Integer::parseInt)
        .toList();
  }
}
