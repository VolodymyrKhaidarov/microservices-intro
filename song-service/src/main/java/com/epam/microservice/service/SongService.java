package com.epam.microservice.service;

import com.epam.microservice.model.SongMetadata;
import java.util.List;

public interface SongService {

  Integer addSongMetadata(SongMetadata songMetadata);

  SongMetadata getSongMetadataById(Integer id);

  List<Integer> deleteSongMetadata(String ids);
}
