package com.epam.microservice.repository;

import com.epam.microservice.model.StorageObject;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StorageRepository extends CrudRepository<StorageObject, Integer> {}
