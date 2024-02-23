package com.epam.microservice.service;

import static com.epam.microservice.model.StorageType.STAGING;

import com.epam.microservice.model.Resource;
import com.epam.microservice.model.StorageObject;
import com.epam.microservice.repository.ResourceRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mock.web.MockMultipartFile;

class ResourceServiceImplTest {

  private static final Integer ONE = 1;
  private static final Integer TWO = 2;
  private static final String FILENAME = "AudioTrack.mp3";
  private static final String STAGING_BUCKET = "staging-storage";
  private static final String STAGING_FOLDER = "staging-folder/";
  private static final byte[] PAYLOAD = "DoReMi".getBytes();

  @Mock private ResourceRepository repository;
  @Mock private KafkaTemplate<String, String> kafkaTemplate;
  @Mock private S3Service s3Service;
  @Mock private StorageService storageService;

  @Test
  void addResourceTest() {

    MockitoAnnotations.openMocks(this);
    ResourceService service = new ResourceServiceImpl(repository, s3Service, kafkaTemplate, storageService);

    MockMultipartFile multipartFile = new MockMultipartFile(FILENAME, FILENAME, null, PAYLOAD);
    StorageObject storageObject = new StorageObject(ONE, STAGING.toString(), STAGING_BUCKET, STAGING_FOLDER);
    Resource resource = new Resource(ONE, STAGING_BUCKET, STAGING_FOLDER, FILENAME);

    Mockito.when(storageService.getStorage(STAGING)).thenReturn(storageObject);
    Mockito.when(repository.findByResourceKey(FILENAME)).thenReturn(List.of());
    Mockito.when(repository.save(Mockito.any())).thenReturn(resource);

    Integer resourceId = service.addResource(multipartFile);

    Assertions.assertEquals(ONE, resourceId);
  }

  @Test
  void getResourceByIdTest() {

    MockitoAnnotations.openMocks(this);
    ResourceService resourceService = new ResourceServiceImpl(repository, s3Service, kafkaTemplate, storageService);
    Resource resource = new Resource(ONE, STAGING_BUCKET, STAGING_FOLDER, FILENAME);

    Mockito.when(repository.findById(ONE)).thenReturn(Optional.of(resource));
    Mockito.when(s3Service.downloadResource(STAGING_FOLDER + FILENAME, STAGING_BUCKET))
        .thenReturn(Optional.of(PAYLOAD));

    byte[] payload = resourceService.getResourceById(ONE);

    Assertions.assertEquals(PAYLOAD, payload);
  }

  @Test
  void deleteResourcesTest() {

    MockitoAnnotations.openMocks(this);
    ResourceService resourceService = new ResourceServiceImpl(repository, s3Service, kafkaTemplate, storageService);
    Resource resource = new Resource(ONE, STAGING_BUCKET, STAGING_FOLDER, FILENAME);

    Mockito.when(repository.findById(ONE)).thenReturn(Optional.of(resource));
    Mockito.when(repository.findById(TWO)).thenReturn(Optional.empty());

    List<Integer> deletedIds = resourceService.deleteResources(ONE + "," + TWO);

    Assertions.assertEquals(List.of(ONE), deletedIds);
  }
}
