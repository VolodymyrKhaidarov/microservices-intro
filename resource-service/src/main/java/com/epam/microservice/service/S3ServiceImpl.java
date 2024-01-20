package com.epam.microservice.service;

import com.epam.microservice.exception.InvalidFileException;
import java.io.IOException;
import java.util.Optional;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3ServiceImpl implements S3Service {

  private final S3Client s3Client;

  @Getter
  @Value("${s3.bucket}")
  private String s3Bucket;

  public S3ServiceImpl(S3Client s3Client) {
    this.s3Client = s3Client;
  }

  @Override
  public String uploadResource(MultipartFile multipartFile) {
    String key = multipartFile.getOriginalFilename();
    try {
      PutObjectRequest request = PutObjectRequest.builder().key(key).bucket(s3Bucket).build();
      s3Client.putObject(request, RequestBody.fromBytes(multipartFile.getBytes()));
    } catch (IOException e) {
      throw new InvalidFileException("Invalid file");
    }

    return key;
  }

  @Override
  public Optional<byte[]> downloadResource(String key) {
    try {
      GetObjectRequest request = GetObjectRequest.builder().key(key).bucket(s3Bucket).build();
      return Optional.of(s3Client.getObject(request).readAllBytes());

    } catch (IOException ioException) {
      return Optional.empty();
    }
  }

  @Override
  public void deleteResource(String key) {
    DeleteObjectRequest deleteObjectRequest =
        DeleteObjectRequest.builder().key(key).bucket(s3Bucket).build();

    s3Client.deleteObject(deleteObjectRequest);
  }
}
