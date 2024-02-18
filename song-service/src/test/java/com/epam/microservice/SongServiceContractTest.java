package com.epam.microservice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.epam.microservice.controller.SongController;
import com.epam.microservice.service.SongService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder;

@SpringBootTest
public class SongServiceContractTest {

  @Autowired
  SongController songController;

  @MockBean
  private SongService songService;

  @BeforeEach
  public void init() {
    StandaloneMockMvcBuilder mockMvcBuilder = MockMvcBuilders.standaloneSetup(songController);
    RestAssuredMockMvc.standaloneSetup(mockMvcBuilder);

    when(songService.addSongMetadata(any())).thenReturn(1);
  }
}
