package com.epam.microservice.service;

import static com.epam.microservice.model.StorageType.PERMANENT;
import static com.epam.microservice.model.StorageType.STAGING;

import com.epam.microservice.model.StorageObject;
import com.epam.microservice.model.StorageType;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
public class StorageServiceImpl implements StorageService {

  private final RestClient restClient;

  @Value("${storage.service.url}")
  private String storageServiceUrl;

  public StorageServiceImpl(RestClient restClient) {
    this.restClient = restClient;
  }

  @Override
  @CircuitBreaker(name = "storage-service", fallbackMethod = "getStorageFallback")
  public StorageObject getStorage(StorageType storageType) {
    List<StorageObject> storages =
        restClient
            .get()
            .uri(storageServiceUrl)
            .retrieve()
            .body(new ParameterizedTypeReference<>() {});

    return Optional.ofNullable(storages).orElse(List.of()).stream()
        .filter(storageObject -> storageType.toString().equals(storageObject.getStorageType()))
        .findFirst()
        .orElse(null);
  }

  public StorageObject getStorageFallback(StorageType storageType, Throwable ex) {
    log.warn("Circuit Breaker fallback method called");
    return storageType == STAGING
        ? new StorageObject(1, STAGING.toString(), "staging-storage", "staging-folder/")
        : new StorageObject(2, PERMANENT.toString(), "permanent-storage", "permanent-folder/");
  }
}
