package com.epam.microservice.service;

import com.epam.microservice.model.StorageObject;
import java.util.List;

public interface StorageService {

  Integer addStorage(StorageObject storageObject);

  StorageObject getStorageById(Integer id);

  List<Integer> deleteStoragesByIds(String ids);

  List<StorageObject> getAllStorages();
}
