package com.epam.microservice.exception;

public class SongMetadataNotFoundException extends RuntimeException {

  public SongMetadataNotFoundException(String message) {
    super(message);
  }
}
