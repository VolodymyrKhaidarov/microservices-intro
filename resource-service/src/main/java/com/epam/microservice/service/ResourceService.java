package com.epam.microservice.service;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface ResourceService {

  Integer addResource(MultipartFile multipartFile);

  byte[] getResourceById(Integer id);

  List<Integer> deleteResources(String ids);
}
