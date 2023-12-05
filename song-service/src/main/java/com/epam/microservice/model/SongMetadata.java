package com.epam.microservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "song")
public class SongMetadata {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column
  @NotBlank(message = "Name is required")
  private String name;

  @Column
  private String artist;

  @Column
  private String album;

  @Column
  private String length;

  @Column
  private String year;

  @Column(name = "resource_id")
  @NotBlank(message = "Resource Id is required")
  private String resourceId;
}
