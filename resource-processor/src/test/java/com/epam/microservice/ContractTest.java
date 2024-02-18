package com.epam.microservice;

import com.epam.microservice.model.Metadata;
import com.epam.microservice.processor.MessageConsumer;
import com.epam.microservice.processor.ResourceProcessor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
@AutoConfigureStubRunner(
    ids = {"com.epam.microservice:song-service:+:stubs:8080"},
    stubsMode = StubRunnerProperties.StubsMode.LOCAL)
@Disabled
class ContractTest {

  private final RestTemplate restTemplate = new RestTemplate();

  @MockBean
  ResourceProcessor resourceProcessor;

  @MockBean
  MessageConsumer messageConsumer;

  @Test
  void pingStub() {
    ResponseEntity<Void> response =
        restTemplate.getForEntity("http://localhost:8080/ping", Void.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  void addSong() {
    Metadata metadata =
        Metadata.builder()
            .name("Destiny")
            .artist("Markus Schulz, Delacey")
            .album("Watch the World")
            .length("227")
            .year("2015")
            .resourceId("1")
            .build();

    var response =
        restTemplate.postForEntity("http://localhost:8080/songs", metadata, Integer.class);

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(1, response.getBody());
  }
}
