package com.epam.microservice.repository;

import com.epam.microservice.model.AudioFile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceRepository extends CrudRepository<AudioFile, Integer> {

}
