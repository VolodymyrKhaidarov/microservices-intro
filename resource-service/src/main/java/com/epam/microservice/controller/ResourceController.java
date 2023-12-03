package com.epam.microservice.controller;

import com.epam.microservice.service.ResourceService;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/resources")
public class ResourceController {

  private final ResourceService resourceService;

  @Autowired
  public ResourceController(ResourceService resourceService) {
    this.resourceService = resourceService;
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> download(@PathVariable Integer id) {
    return resourceService
        .getFileById(id)
        .map(file -> new ResponseEntity<>(file.getPayload(), HttpStatus.OK))
        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @PostMapping(path = "/")
  public ResponseEntity<?> upload(InputStream dataStream) throws IOException {
    Integer id = resourceService.addFile(dataStream.readAllBytes());
    return new ResponseEntity<>(id, HttpStatus.OK);
  }

  @DeleteMapping("/")
  public ResponseEntity<?> delete(String ids) {
    List<Integer> deleteFiles = resourceService.deleteFiles(ids);

    return new ResponseEntity<>(deleteFiles, HttpStatus.OK);
  }
}
