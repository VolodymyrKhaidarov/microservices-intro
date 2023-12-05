package com.epam.microservice.service;

import com.epam.microservice.exception.InvalidFileException;
import com.epam.microservice.exception.ResourceNotFoundException;
import com.epam.microservice.model.Resource;
import com.epam.microservice.repository.ResourceRepository;
import java.io.IOException;
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

  @Autowired
  public ResourceServiceImpl(ResourceRepository resourceRepository) {
    this.resourceRepository = resourceRepository;
  }

  public Integer addResource(MultipartFile multipartFile) {
    Resource resource = new Resource();

    if (!"mp3".equals(FilenameUtils.getExtension(multipartFile.getOriginalFilename()))) {
      throw new InvalidFileException("Invalid file");
    }

    try {
      byte[] bytes = multipartFile.getBytes();
      resource.setPayload(bytes);
    } catch (IOException exception) {
      throw new InvalidFileException("Invalid file");
    }

    resource = resourceRepository.save(resource);
    return resource.getId();
  }

  public Resource getResourceById(Integer id) {
    return resourceRepository
        .findById(id)
        .orElseThrow(
            () -> new ResourceNotFoundException("The resource with id " + id + " does not exist"));
  }

  @Override
  public List<Integer> deleteResources(String ids) {

    List<Integer> deletedFiles = new ArrayList<>();

    for (Integer id : parseCSV(ids)) {
      if (resourceRepository.existsById(id)) {
        resourceRepository.deleteById(id);
        deletedFiles.add(id);
      }
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
