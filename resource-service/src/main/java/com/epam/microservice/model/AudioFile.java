package com.epam.microservice.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "audiofile")
public class AudioFile {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Integer id;

  @Column(name = "payload")
  private byte[] payload;
}