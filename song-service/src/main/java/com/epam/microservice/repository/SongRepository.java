package com.epam.microservice.repository;

import com.epam.microservice.model.SongMetadata;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SongRepository extends CrudRepository<SongMetadata, Integer> {

  List<SongMetadata> findByResourceId(String resourceId);
}
