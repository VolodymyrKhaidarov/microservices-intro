package com.epam.microservice.service;

import com.epam.microservice.exception.InvalidFileException;
import com.epam.microservice.exception.ResourceNotFoundException;
import com.epam.microservice.model.Resource;
import com.epam.microservice.model.ResourceMetadata;
import com.epam.microservice.parser.ResourceParser;
import com.epam.microservice.repository.ResourceRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.exception.TikaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

@Service
public class ResourceServiceImpl implements ResourceService {

  private final ResourceRepository resourceRepository;
  private final ResourceParser resourceParser;
  private final RestClient restClient;

  @Autowired
  public ResourceServiceImpl(
      ResourceRepository resourceRepository, ResourceParser resourceParser, RestClient restClient) {

    this.resourceRepository = resourceRepository;
    this.resourceParser = resourceParser;
    this.restClient = restClient;
  }

  public Integer addResource(MultipartFile multipartFile) {

    if (!"mp3".equals(FilenameUtils.getExtension(multipartFile.getOriginalFilename()))) {
      throw new InvalidFileException("Invalid file");
    }

    Resource resource = new Resource();
    ResourceMetadata resourceMetadata;

    try {
      resource.setPayload(multipartFile.getBytes());
      resourceMetadata = resourceParser.getMetadata(multipartFile.getInputStream());
    } catch (IOException | TikaException | RuntimeException | SAXException exception) {
      throw new InvalidFileException("Invalid file");
    }

    resource = resourceRepository.save(resource);
    resourceMetadata.setResourceId(String.valueOf(resource.getId()));

    restClient
        .post()
        .contentType(MediaType.APPLICATION_JSON)
        .body(resourceMetadata)
        .retrieve()
        .body(Integer.class);

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
