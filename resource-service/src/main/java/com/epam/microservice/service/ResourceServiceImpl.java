package com.epam.microservice.service;

import com.epam.microservice.model.AudioFile;
import com.epam.microservice.repository.ResourceRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResourceServiceImpl implements ResourceService {

  private final ResourceRepository resourceRepository;

  @Autowired
  public ResourceServiceImpl(ResourceRepository resourceRepository) {
    this.resourceRepository = resourceRepository;
  }

  public Integer addFile(byte[] payload) {
    AudioFile audioFile = new AudioFile();
    audioFile.setPayload(payload);
    audioFile = resourceRepository.save(audioFile);
    return audioFile.getId();
  }

  public Optional<AudioFile> getFileById(Integer id) {
    return resourceRepository.findById(id);
  }

  @Override
  public List<Integer> deleteFiles(String ids) {

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
