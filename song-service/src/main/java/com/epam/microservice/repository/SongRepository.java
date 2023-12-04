package com.epam.microservice.repository;

import com.epam.microservice.model.Song;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SongRepository extends CrudRepository<Song, Integer> {}