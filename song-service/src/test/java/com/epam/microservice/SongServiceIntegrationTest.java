package com.epam.microservice;

import com.epam.microservice.model.SongMetadata;
import com.epam.microservice.repository.SongRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
class SongServiceIntegrationTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private SongRepository songRepository;

  @Test
  void getSongMetadata() throws Exception {
    SongMetadata songMetadata = new SongMetadata();
    songMetadata.setName("AudioTrack");
    songMetadata.setResourceId("666");
    songRepository.save(songMetadata);

    mockMvc
        .perform(MockMvcRequestBuilders.get("/songs/1"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("1")))
        .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("AudioTrack")))
        .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("666")));
  }
}
