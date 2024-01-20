package com.epam.microservice.exception;

public class ResourceNotAvailableException extends RuntimeException {
  public ResourceNotAvailableException(String message) {
    super(message);
  }
}
