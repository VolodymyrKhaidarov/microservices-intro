Feature: post song

  Scenario: client makes call to POST /songs
    When the client calls post method
    Then the client receives status code 200
    And the client receives id 1