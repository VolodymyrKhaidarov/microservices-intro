package com.epam.microservice.exception;

public class ResourceProcessorException extends RuntimeException {
  public ResourceProcessorException() {
    super();
  }

  public ResourceProcessorException(String message) {
    super(message);
  }
}
