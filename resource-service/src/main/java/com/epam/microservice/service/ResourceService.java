package com.epam.microservice.service;

import com.epam.microservice.model.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ResourceService {

  Integer addResource(MultipartFile multipartFile);

  Resource getResourceById(Integer id);

  List<Integer> deleteResources(String ids);
}
