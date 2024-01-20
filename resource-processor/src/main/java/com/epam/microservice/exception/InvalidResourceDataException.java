package com.epam.microservice.exception;

public class InvalidResourceDataException extends RuntimeException {
  public InvalidResourceDataException(String message) {
    super(message);
  }
}
