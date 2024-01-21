package com.epam.microservice.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Metadata {

  private String resourceId;
  private String name;
  private String artist;
  private String album;
  private String length;
  private String year;
}
