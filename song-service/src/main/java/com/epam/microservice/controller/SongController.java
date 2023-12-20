package com.epam.microservice.controller;

import com.epam.microservice.model.SongMetadata;
import com.epam.microservice.service.SongService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/songs")
public class SongController {

  private final SongService songService;

  @Autowired
  public SongController(SongService songService) {
    this.songService = songService;
  }

  @PostMapping()
  public ResponseEntity<Integer> uploadSongMetadata(@Valid @RequestBody SongMetadata songMetadata) {
    Integer id = songService.addSongMetadata(songMetadata);

    return new ResponseEntity<>(id, HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<SongMetadata> getSongMetadata(@PathVariable Integer id) {
    SongMetadata songMetadata = songService.getSongMetadataById(id);

    return new ResponseEntity<>(songMetadata, HttpStatus.OK);
  }

  @DeleteMapping()
  public ResponseEntity<List<Integer>> deleteSongMetadata(
      @RequestParam(value = "id") @Size(max = 199) String id) {
    List<Integer> deletedSongMetadata = songService.deleteSongMetadata(id);

    return new ResponseEntity<>(deletedSongMetadata, HttpStatus.OK);
  }
}
