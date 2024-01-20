package com.epam.microservice.service;

import com.epam.microservice.exception.SongMetadataNotFoundException;
import com.epam.microservice.model.SongMetadata;
import com.epam.microservice.repository.SongRepository;
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
    songRepository.findByResourceId(songMetadata.getResourceId()).stream()
        .findFirst()
        .map(SongMetadata::getId)
        .ifPresent(songMetadata::setId);

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
    return Optional.ofNullable(ids).map(str -> str.split(",")).stream()
        .flatMap(Stream::of)
        .filter(str -> str.matches("-?(0|[1-9]\\d*)"))
        .map(Integer::parseInt)
        .filter(songRepository::existsById)
        .peek(songRepository::deleteById)
        .toList();
  }
}
