package com.epam.microservice.service;

import com.epam.microservice.model.Resource;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface ResourceService {

  Integer addResource(MultipartFile multipartFile);

  Resource getResourceById(Integer id);

  List<Integer> deleteResources(String ids);
}
