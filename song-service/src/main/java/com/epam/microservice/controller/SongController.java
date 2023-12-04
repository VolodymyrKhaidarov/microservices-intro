package com.epam.microservice.controller;

import com.epam.microservice.model.Song;
import com.epam.microservice.service.SongService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/songs")
public class SongController {

  private final SongService songService;

  @Autowired
  public SongController(SongService songService) {
    this.songService = songService;
  }

  @PostMapping(path = "/")
  public ResponseEntity<?> uploadSong(@Validated @RequestBody Song song) {
    Integer id = songService.addSong(song);
    return new ResponseEntity<>(id, HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getSong(@PathVariable Integer id) {
    return songService
        .getSongById(id)
        .map(song -> new ResponseEntity<>(song, HttpStatus.OK))
        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @DeleteMapping("/")
  public ResponseEntity<?> deleteSong(@RequestParam(value = "id") String id) {
    List<Integer> deletedSongs = songService.deleteSongs(id);

    return new ResponseEntity<>(deletedSongs, HttpStatus.OK);
  }
}