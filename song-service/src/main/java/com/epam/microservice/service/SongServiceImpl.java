package com.epam.microservice.service;

import com.epam.microservice.exception.SongMetadataNotFoundException;
import com.epam.microservice.model.SongMetadata;
import com.epam.microservice.repository.SongRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SongServiceImpl implements SongService {

  private final SongRepository songRepository;

  @Autowired
  public SongServiceImpl(SongRepository songRepository) {
    this.songRepository = songRepository;
  }

  @Override
  public Integer addSongMetadata(SongMetadata songMetadata) {
    return songRepository.save(songMetadata).getId();
  }

  @Override
  public SongMetadata getSongMetadataById(Integer id) {
    return songRepository
        .findById(id)
        .orElseThrow(
            () ->
                new SongMetadataNotFoundException(
                    "The song metadata with id " + id + " does not exist"));
  }

  @Override
  public List<Integer> deleteSongMetadata(String ids) {

    List<Integer> deletedSongs = new ArrayList<>();

    for (Integer id : parseCSV(ids)) {
      if (songRepository.existsById(id)) {
        songRepository.deleteById(id);
        deletedSongs.add(id);
      }
    }
    return deletedSongs;
  }

  private List<Integer> parseCSV(String csv) {
    return Optional.ofNullable(csv).map(str -> str.split(",")).stream()
        .flatMap(Stream::of)
        .filter(str -> str.matches("-?(0|[1-9]\\d*)"))
        .map(Integer::parseInt)
        .toList();
  }
}
