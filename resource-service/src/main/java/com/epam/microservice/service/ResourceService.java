package com.epam.microservice.service;

import com.epam.microservice.model.AudioFile;

import java.util.List;
import java.util.Optional;

public interface ResourceService {

    Integer addFile(byte[] payload);

    Optional<AudioFile> getFileById(Integer id);

    List<Integer> deleteFiles(String ids);
}