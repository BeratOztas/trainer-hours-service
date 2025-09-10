package com.epam.gym.trainer_hours_service.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.epam.gym.trainer_hours_service.api.dto.request.TrainerWorkloadRequest;
import com.epam.gym.trainer_hours_service.api.dto.response.TrainerWorkloadResponse;
import com.epam.gym.trainer_hours_service.domain.service.ITrainerWorkloadService;
import com.epam.gym.trainer_hours_service.security.JwtTokenExtractor;
import com.epam.gym.trainer_hours_service.security.JwtTokenProvider;
import com.epam.gym.trainer_hours_service.utils.ActionType;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(
	    controllers = TrainerWorkloadController.class, 
	    excludeAutoConfiguration = {SecurityAutoConfiguration.class}
	)
public class TrainerWorkloadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ITrainerWorkloadService trainerWorkloadService;
    
    @MockBean
    private JwtTokenProvider jwtTokenProvider; 
    
    @MockBean
    private JwtTokenExtractor jwtTokenExtractor;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/v1/trainer-workload - Success")
    void updateTrainerWorkload_success() throws Exception {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "Ahmet.Hoca",
                "Ahmet",
                "Hoca",
                true,
                LocalDate.of(2025, 8, 1),
                60,
               ActionType.ADD
        );

        // Mock service call
        Mockito.doNothing().when(trainerWorkloadService).updateTrainerWorkload(any());

        mockMvc.perform(post("/api/v1/trainer-workload")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer dummyToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Workload successfully updated. for Trainer: Ahmet.Hoca"));
    }

    @Test
    @DisplayName("GET /api/v1/trainer-workload/{trainerUsername} - Success")
    void getTrainerWorkload_success() throws Exception {
        // Arrange: yearlySummary map
        Map<Integer, Map<Integer, Integer>> yearlySummary = new HashMap<>();
        yearlySummary.put(2025, Map.of(8, 60));

        TrainerWorkloadResponse response = new TrainerWorkloadResponse(
                "Ahmet.Hoca",
                "Ahmet",
                "Hoca",
                true,
                yearlySummary
        );

        when(trainerWorkloadService.getTrainerWorkload("Ahmet.Hoca"))
                .thenReturn(response);

        // Act + Assert
        mockMvc.perform(get("/api/v1/trainer-workload/Ahmet.Hoca")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer test-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trainerUsername").value("Ahmet.Hoca"))
                .andExpect(jsonPath("$.trainerFirstName").value("Ahmet"))
                .andExpect(jsonPath("$.trainerLastName").value("Hoca"))
                .andExpect(jsonPath("$.isActive").value(true))
                .andExpect(jsonPath("$.yearlySummary.2025.8").value(60));

        verify(trainerWorkloadService).getTrainerWorkload("Ahmet.Hoca");
    }
    
    @Test
    @DisplayName("GET /api/v1/trainer-workload/{trainerUsername} - Not Found")
    void getTrainerWorkload_notFound() throws Exception {
        Mockito.when(trainerWorkloadService.getTrainerWorkload(eq("Unknown")))
                .thenThrow(new RuntimeException("Trainer not found"));

        mockMvc.perform(get("/api/v1/trainer-workload/Unknown"))
                .andExpect(status().is5xxServerError()); 
    }

    @Test
    @DisplayName("POST /api/v1/trainer-workload - Invalid Request (400)")
    void updateTrainerWorkload_invalidRequest() throws Exception {
        mockMvc.perform(post("/api/v1/trainer-workload")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer dummyToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}

