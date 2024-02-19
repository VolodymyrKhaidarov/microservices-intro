package com.epam.microservice.service;

import java.util.Optional;

public interface S3Service {

  String uploadResource(String key, byte[] payload, String bucket);

  Optional<byte[]> downloadResource(String key, String bucket);

  void deleteResource(String key, String bucket);

  void moveResource(String sourceKey, String sourceBucket, String destinationKey, String destinationBucket);
}
