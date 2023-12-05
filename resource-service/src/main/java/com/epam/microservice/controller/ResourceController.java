package com.epam.microservice.controller;

import com.epam.microservice.model.Resource;
import com.epam.microservice.service.ResourceService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/resources")
public class ResourceController {

  private final ResourceService resourceService;

  @Autowired
  public ResourceController(ResourceService resourceService) {
    this.resourceService = resourceService;
  }

  @PostMapping(path = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Integer> uploadResource(@RequestParam("file") MultipartFile multipartFile) {
    Integer id = resourceService.addResource(multipartFile);
    return new ResponseEntity<>(id, HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<byte[]> downloadResource(@PathVariable Integer id) {
    Resource resource = resourceService.getResourceById(id);
    return new ResponseEntity<>(resource.getPayload(), HttpStatus.OK);
  }

  @DeleteMapping("/")
  public ResponseEntity<List<Integer>> deleteResource(@RequestParam(value = "id") String id) {
    List<Integer> deletedFiles = resourceService.deleteResources(id);

    return new ResponseEntity<>(deletedFiles, HttpStatus.OK);
  }
}
