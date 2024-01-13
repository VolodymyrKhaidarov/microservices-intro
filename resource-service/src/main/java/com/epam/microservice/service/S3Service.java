package com.epam.microservice.service;

import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;

public interface S3Service {

  String getS3Bucket();

  String uploadResource(MultipartFile multipartFile);

  Optional<byte[]> downloadResource(String key);

  void deleteResource(String keys);
}
