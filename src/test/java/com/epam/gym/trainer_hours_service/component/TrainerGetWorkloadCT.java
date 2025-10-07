package com.epam.gym.trainer_hours_service.component;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.Collections;
import java.util.Optional;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import com.epam.gym.trainer_hours_service.config.CucumberConfig;
import com.epam.gym.trainer_hours_service.db.entity.TrainerWorkload;
import com.epam.gym.trainer_hours_service.db.repository.TrainerWorkloadRepository;
import com.epam.gym.trainer_hours_service.security.JwtTokenProvider;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class TrainerGetWorkloadCT {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private TestContext testContext; 

    @Autowired
    private TrainerWorkloadRepository trainerWorkloadRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Before
    public void setupScenario() {
        Mockito.reset(trainerWorkloadRepository, jwtTokenProvider);
        
        when(jwtTokenProvider.validateToken(CucumberConfig.BASE_JWT_TOKEN)).thenReturn(true);
        when(jwtTokenProvider.getUsernameFromToken(CucumberConfig.BASE_JWT_TOKEN)).thenReturn("ct.base.user");
    }

    
    @Given("a trainer with username {string} has an existing workload record")
    public void a_trainer_with_username_has_an_existing_workload_record(String trainerUsername) {
        TrainerWorkload workload = TrainerWorkload.builder()
            .trainerUsername(trainerUsername)
            .trainerFirstName("Ahmet")
            .trainerLastName("Hoca")
            .isActive(true)
            .yearlySummary(Collections.emptyList())
            .build();
        
        when(trainerWorkloadRepository.findByTrainerUsername(trainerUsername)).thenReturn(Optional.of(workload));
    }

    @Given("no workload record exists for a trainer with username {string}")
    public void no_workload_record_exists_for_a_trainer_with_username(String trainerUsername) {
        when(trainerWorkloadRepository.findByTrainerUsername(trainerUsername)).thenReturn(Optional.empty());
    }

    @When("a request is made to get the workload for trainer {string}")
    public void a_request_is_made_to_get_the_workload_for_trainer(String trainerUsername) throws Exception {
        String url = "/api/v1/trainer-workload/" + trainerUsername;
        
        testContext.setResult(mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + CucumberConfig.BASE_JWT_TOKEN))
                .andReturn());
    }

    @And("the response body should contain the workload details for {string}")
    public void the_response_body_should_contain_the_workload_details_for(String trainerUsername) throws Exception {
        String responseJson = testContext.getResult().getResponse().getContentAsString();
        
        assertTrue(responseJson.contains("\"trainerUsername\":\"" + trainerUsername + "\""), "Response should contain the correct username");
        assertTrue(responseJson.contains("\"trainerFirstName\":\"Ahmet\""), "Response should contain the first name");
    }

    @Then("the response status code should be {int}")
    public void the_response_status_code_should_be(int expectedStatusCode) {
        org.junit.jupiter.api.Assertions.assertEquals(expectedStatusCode, testContext.getResult().getResponse().getStatus(), "Expected HTTP status code does not match.");
    }

    @And("the response body should contain the error message {string}")
    public void the_response_body_should_contain_the_error_message(String expectedMessage) throws Exception {
        String responseJson = testContext.getResult().getResponse().getContentAsString();
        assertTrue(responseJson.contains(expectedMessage), "Response should contain the error message: " + expectedMessage);
    }
}