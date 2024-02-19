package com.epam.microservice.service;

import java.io.IOException;
import java.util.Optional;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3ServiceImpl implements S3Service {

  private final S3Client s3Client;

  public S3ServiceImpl(S3Client s3Client) {
    this.s3Client = s3Client;
  }

  @Override
  public String uploadResource(String key, byte[] payload, String bucket) {

    PutObjectRequest request = PutObjectRequest.builder().key(key).bucket(bucket).build();
    s3Client.putObject(request, RequestBody.fromBytes(payload));

    return key;
  }

  @Override
  public Optional<byte[]> downloadResource(String key, String bucket) {

    GetObjectRequest request = GetObjectRequest.builder().key(key).bucket(bucket).build();

    try {
      return Optional.of(s3Client.getObject(request).readAllBytes());
    } catch (IOException ioException) {
      return Optional.empty();
    }
  }

  @Override
  public void deleteResource(String key, String bucket) {

    DeleteObjectRequest request = DeleteObjectRequest.builder().key(key).bucket(bucket).build();

    s3Client.deleteObject(request);
  }

  @Override
  public void moveResource(
      String sourceKey, String sourceBucket, String destinationKey, String destinationBucket) {

    CopyObjectRequest copyObjectRequest =
        CopyObjectRequest.builder()
            .sourceKey(sourceKey)
            .sourceBucket(sourceBucket)
            .destinationKey(destinationKey)
            .destinationBucket(destinationBucket)
            .build();

    s3Client.copyObject(copyObjectRequest);
    deleteResource(sourceKey, sourceBucket);
  }
}
