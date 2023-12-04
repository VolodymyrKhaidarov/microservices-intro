package com.epam.microservice.service;

import com.epam.microservice.model.Song;
import java.util.List;
import java.util.Optional;

public interface SongService {

  Integer addSong(Song song);

  Optional<Song> getSongById(Integer id);

  List<Integer> deleteSongs(String ids);
}
