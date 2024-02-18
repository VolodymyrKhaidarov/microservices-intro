package com.epam.microservice.service;

import java.util.Optional;

public interface S3Service {

  String uploadResource(String key, byte[] payload, String s3Bucket);

  Optional<byte[]> downloadResource(String key, String s3Bucket);

  void deleteResource(String keys, String s3Bucket);
}
