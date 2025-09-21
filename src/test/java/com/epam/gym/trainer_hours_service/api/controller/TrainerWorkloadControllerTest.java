package com.epam.gym.trainer_hours_service.api.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import com.epam.gym.trainer_hours_service.api.dto.response.TrainerWorkloadResponse;
import com.epam.gym.trainer_hours_service.db.entity.TrainerWorkload.MonthlySummary;
import com.epam.gym.trainer_hours_service.db.entity.TrainerWorkload.YearlySummary;
import com.epam.gym.trainer_hours_service.domain.service.ITrainerWorkloadService;
import com.epam.gym.trainer_hours_service.security.JwtTokenExtractor;
import com.epam.gym.trainer_hours_service.security.JwtTokenProvider;

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

   
    @Test
    @DisplayName("GET /api/v1/trainer-workload/{trainerUsername} - Success")
    void getTrainerWorkload_success() throws Exception {
        
        List<MonthlySummary> monthlySummaries = List.of(
                new MonthlySummary(8, 60)
        );

        List<YearlySummary> yearlySummaries = List.of(
                new YearlySummary(2025, monthlySummaries)
        );


        TrainerWorkloadResponse response = new TrainerWorkloadResponse(
                "Ahmet.Hoca",
                "Ahmet",
                "Hoca",
                true,
                yearlySummaries
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
                .andExpect(jsonPath("$.yearlySummaries[0].year").value(2025))
                .andExpect(jsonPath("$.yearlySummaries[0].monthlySummary[0].month").value(8))
                .andExpect(jsonPath("$.yearlySummaries[0].monthlySummary[0].totalTrainingMinutes").value(60));

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
    
}

