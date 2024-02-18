package com.epam.microservice.service;

import com.epam.microservice.model.StorageObject;
import com.epam.microservice.repository.StorageRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.springframework.stereotype.Service;

@Service
public class StorageServiceImpl implements StorageService {

  private final StorageRepository storageRepository;

  public StorageServiceImpl(StorageRepository storageRepository) {
    this.storageRepository = storageRepository;
  }

  @Override
  public Integer addStorage(StorageObject storageObject) {
    return storageRepository.save(storageObject).getId();
  }

  @Override
  public StorageObject getStorageById(Integer id) {
    return storageRepository.findById(id).orElse(null);
  }

  @Override
  public List<Integer> deleteStoragesByIds(String ids) {
    return Optional.ofNullable(ids).map(str -> str.split(",")).stream()
        .flatMap(Stream::of)
        .filter(str -> str.matches("-?(0|[1-9]\\d*)"))
        .map(Integer::parseInt)
        .map(storageRepository::findById)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .peek(resource -> storageRepository.deleteById(resource.getId()))
        .map(StorageObject::getId)
        .toList();
  }

  @Override
  public List<StorageObject> getAllStorages() {
    return StreamSupport.stream(storageRepository.findAll().spliterator(), false).toList();
  }
}
