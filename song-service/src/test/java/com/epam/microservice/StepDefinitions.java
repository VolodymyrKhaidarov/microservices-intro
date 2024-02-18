package com.epam.microservice;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.epam.microservice.model.SongMetadata;
import com.epam.microservice.repository.SongRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StepDefinitions {

  @LocalServerPort
  private int port;
  @Autowired
  SongRepository songRepository;

  private final RestClient restClient = RestClient.create();
  private ResponseEntity<?> response;

  @When("the client calls post method")
  public void theClientCallsPostMethod() {

    SongMetadata songMetadata = new SongMetadata();
    songMetadata.setName("AudioTrack");
    songMetadata.setResourceId("666");

    response =
        restClient
            .post()
            .uri("http://localhost:" + port + "/songs")
            .contentType(APPLICATION_JSON)
            .body(songMetadata)
            .retrieve()
            .toEntity(Integer.class);

    Assert.assertNotNull(response);
  }

  @Then("the client receives status code {int}")
  public void theClientReceivesStatusCode(int statusCode) {
    Assert.assertEquals(statusCode, response.getStatusCode().value());
  }

  @And("the client receives id {int}")
  public void theClientReceivesId(int id) {
    Assert.assertEquals(id, response.getBody());
  }

  @When("the client calls get method with id {int}")
  public void theClientCallsGetMethod(int id) {

    SongMetadata songMetadata = new SongMetadata();
    songMetadata.setName("AudioTrack");
    songMetadata.setResourceId("666");

    songRepository.save(songMetadata);

    response =
        restClient
            .get()
            .uri("http://localhost:" + port + "/songs/" + id)
            .retrieve()
            .toEntity(SongMetadata.class);

    Assert.assertNotNull(response);
  }

  @And("the client receives id {int}, name {string}, resourceId {string}")
  public void theClientReceivesSongMetadata(int id, String name, String resourceId) {

    SongMetadata metadata = (SongMetadata) response.getBody();

    assert metadata != null;
    Assert.assertEquals(id, metadata.getId().intValue());
    Assert.assertEquals(name, metadata.getName());
    Assert.assertEquals(resourceId, metadata.getResourceId());
  }
}