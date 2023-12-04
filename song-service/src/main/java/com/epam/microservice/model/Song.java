package com.epam.microservice.model;

import jakarta.persistence.*;
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
public class Song {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column
  private String name;

  @Column
  private String artist;

  @Column
  private String length;

  @Column
  private String year;

  @Column(name = "resource_id")
  private String resourceId;
}