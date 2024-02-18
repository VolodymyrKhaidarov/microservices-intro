package com.epam.microservice.service;

import com.epam.microservice.model.Resource;
import com.epam.microservice.repository.ResourceRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.client.RestClient;

@Disabled
class ResourceServiceImplTest {

  private static final Integer RESOURCE_ID_1 = 1;
  private static final Integer RESOURCE_ID_2 = 2;
  private static final String S3BUCKET = "s3bucket";
  private static final String FILENAME = "AudioTrack.mp3";
  private static final String STORAGE_PATH = "permanent-storage/permanent";
  private static final byte[] PAYLOAD = "DoReMi".getBytes();

  @Mock
  private ResourceRepository resourceRepository;
  @Mock
  private KafkaTemplate<String, String> kafkaTemplate;
  @Mock
  private S3Service s3Service;
  @Mock
  private RestClient restClient;

  @Test
  void addResourceTest() {

    MockitoAnnotations.openMocks(this);
    ResourceService resourceService = new ResourceServiceImpl(resourceRepository, s3Service, kafkaTemplate, restClient);
    MockMultipartFile multipartFile = new MockMultipartFile(FILENAME, FILENAME, null, PAYLOAD);
    Resource resource = new Resource(RESOURCE_ID_1, S3BUCKET, FILENAME);

    Mockito.when(s3Service.uploadResource(FILENAME, PAYLOAD, STORAGE_PATH)).thenReturn(FILENAME);
    Mockito.when(resourceRepository.findByResourceKey(FILENAME)).thenReturn(List.of());
    Mockito.when(resourceRepository.save(Mockito.any())).thenReturn(resource);

    Integer resourceId = resourceService.addResource(multipartFile);

    Assertions.assertEquals(RESOURCE_ID_1, resourceId);
  }

  @Test
  void getResourceTest() {

    MockitoAnnotations.openMocks(this);
    ResourceService resourceService = new ResourceServiceImpl(resourceRepository, s3Service, kafkaTemplate, restClient);
    Resource resource = new Resource(RESOURCE_ID_1, S3BUCKET, FILENAME);

    Mockito.when(resourceRepository.findById(RESOURCE_ID_1)).thenReturn(Optional.of(resource));
    Mockito.when(s3Service.downloadResource(FILENAME, STORAGE_PATH)).thenReturn(Optional.of(PAYLOAD));

    byte[] payload = resourceService.getResourceById(RESOURCE_ID_1);

    Assertions.assertEquals(PAYLOAD, payload);
  }

  @Test
  void deleteResourcesTest() {

    MockitoAnnotations.openMocks(this);
    ResourceService resourceService = new ResourceServiceImpl(resourceRepository, s3Service, kafkaTemplate, restClient);
    Resource resource = new Resource(RESOURCE_ID_1, S3BUCKET, FILENAME);

    Mockito.when(resourceRepository.findById(RESOURCE_ID_1)).thenReturn(Optional.of(resource));
    Mockito.when(resourceRepository.findById(RESOURCE_ID_2)).thenReturn(Optional.empty());

    List<Integer> deletedIds = resourceService.deleteResources(RESOURCE_ID_1 + "," + RESOURCE_ID_2);

    Assertions.assertEquals(List.of(RESOURCE_ID_1), deletedIds);
  }
}
