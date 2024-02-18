package com.epam.microservice.service;

import com.epam.microservice.model.StorageObject;
import java.util.List;

public interface StorageService {

  Integer addStorage(StorageObject storageObject);

  List<Integer> deleteStorages(String ids);

  List<StorageObject> getStorages();
}
