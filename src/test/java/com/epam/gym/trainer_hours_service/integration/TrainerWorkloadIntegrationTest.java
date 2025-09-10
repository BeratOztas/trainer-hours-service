package com.epam.gym.trainer_hours_service.integration;

import com.epam.gym.trainer_hours_service.api.dto.request.TrainerWorkloadRequest;
import com.epam.gym.trainer_hours_service.db.repository.TrainerWorkloadRepository;
import com.epam.gym.trainer_hours_service.utils.ActionType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TrainerWorkloadIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TrainerWorkloadRepository workloadRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanDb() {
        workloadRepository.deleteAll();
    }

    @Test
    @DisplayName("POST + GET trainer workload integration test")
    @WithMockUser
    void testTrainerWorkloadIntegration() throws Exception {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "Ahmet.Hoca",
                "Ahmet",
                "Hoca",
                true,
                LocalDate.of(2025, 8, 1),
                45,
                ActionType.ADD
        );

        mockMvc.perform(post("/api/v1/trainer-workload")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer dummyToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Workload successfully updated. for Trainer: Ahmet.Hoca"));

        assertThat(workloadRepository.findAll()).hasSize(1);
        assertThat(workloadRepository.findAll().get(0).getTrainerUsername()).isEqualTo("Ahmet.Hoca");

        mockMvc.perform(get("/api/v1/trainer-workload/Ahmet.Hoca"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trainerUsername").value("Ahmet.Hoca"))
                .andExpect(jsonPath("$.trainerFirstName").value("Ahmet"))
                .andExpect(jsonPath("$.yearlySummary").isNotEmpty());
    }

    @Test
    @DisplayName("GET workload - trainer not found returns 404")
    @WithMockUser
    void testTrainerNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/trainer-workload/Unknown"))
                .andExpect(status().isNotFound());
    }
}
