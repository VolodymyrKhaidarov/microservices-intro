package com.epam.microservice.service;

import com.epam.microservice.model.StorageObject;
import com.epam.microservice.model.StorageType;

public interface StorageService {

  StorageObject getStorage(StorageType storageType);
}
