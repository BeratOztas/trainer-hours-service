Feature: Get Trainer Workload API Component Tests

  Scenario: 1. Successfully Retrieve Trainer Workload
    Given a trainer with username "Ahmet.Hoca" has an existing workload record
    When a request is made to get the workload for trainer "Ahmet.Hoca"
    Then the response status code should be 200
    And the response body should contain the workload details for "Ahmet.Hoca"

  Scenario: 2. Fail to Retrieve Workload for Non-Existent Trainer
    Given no workload record exists for a trainer with username "Asli.Hoca"
    When a request is made to get the workload for trainer "Asli.Hoca"
    Then the response status code should be 404
    And the response body should contain the error message "Trainer Username: Asli.Hoca"