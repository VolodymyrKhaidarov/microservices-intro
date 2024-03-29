package com.epam.microservice.controller;

import com.epam.microservice.model.StorageObject;
import com.epam.microservice.service.StorageService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/storages")
public class StorageController {

  private final StorageService storageService;

  public StorageController(StorageService storageService) {
    this.storageService = storageService;
  }

  @PostMapping
  public ResponseEntity<Integer> addStorage(@RequestBody StorageObject storageObject) {
    Integer id = storageService.addStorage(storageObject);
    return new ResponseEntity<>(id, HttpStatus.OK);
  }

  @GetMapping()
  public ResponseEntity<List<StorageObject>> getStorages() {
    return new ResponseEntity<>(storageService.getStorages(), HttpStatus.OK);
  }

  @DeleteMapping
  public List<Integer> deleteStorages(@RequestParam(value = "ids") String ids) {
    return storageService.deleteStorages(ids);
  }
}
