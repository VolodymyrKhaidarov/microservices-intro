package com.epam.microservice.repository;

import com.epam.microservice.model.Resource;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceRepository extends CrudRepository<Resource, Integer> {

  List<Resource> findByResourceKey(String resourceKey);
}
