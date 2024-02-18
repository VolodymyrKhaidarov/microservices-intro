Feature: get song

  Scenario: client makes call to GET /songs/1
    When the client calls get method with id 1
    Then the client receives status code 200
    And the client receives id 1, name "AudioTrack", resourceId "666"