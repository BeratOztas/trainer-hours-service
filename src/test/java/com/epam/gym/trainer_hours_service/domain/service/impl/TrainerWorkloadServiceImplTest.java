package com.epam.gym.trainer_hours_service.domain.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import com.epam.gym.trainer_hours_service.api.dto.response.TrainerWorkloadResponse;
import com.epam.gym.trainer_hours_service.db.entity.TrainerWorkload;
import com.epam.gym.trainer_hours_service.db.entity.TrainerWorkload.MonthlySummary;
import com.epam.gym.trainer_hours_service.db.entity.TrainerWorkload.YearlySummary;
import com.epam.gym.trainer_hours_service.db.repository.TrainerWorkloadRepository;
import com.epam.trainingcommons.dto.TrainerWorkloadRequest;
import com.epam.trainingcommons.utils.ActionType;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadServiceImplTest {

	@Mock
    private TrainerWorkloadRepository trainerWorkloadRepository;

    @InjectMocks
    private TrainerWorkloadServiceImpl trainerWorkloadService;

    private TrainerWorkload existingTrainer;
    private TrainerWorkloadRequest request;

    @BeforeEach
    void setUp() {
        // Clear MDC before each test
        MDC.clear();
        
        // Setup an existing trainer workload document
        List<MonthlySummary> monthlySummaries = new ArrayList<>();
        monthlySummaries.add(new MonthlySummary(8, 60));
        
        List<YearlySummary> yearlySummaries = new ArrayList<>();
        yearlySummaries.add(new YearlySummary(2025, monthlySummaries));
        
        existingTrainer = TrainerWorkload.builder()
                .trainerUsername("Ahmet.Hoca")
                .trainerFirstName("Ahmet")
                .trainerLastName("Hoca")
                .isActive(true)
                .yearlySummary(yearlySummaries)
                .build();
    }
    
    @Test
    @DisplayName("should update existing workload duration for a trainer with ActionType.ADD")
    void updateTrainerWorkload_updateExistingAdd() {
        request = new TrainerWorkloadRequest(
            "Ahmet.Hoca",
            "Ahmet",
            "Hoca",
            true,
            LocalDate.of(2025, 8, 15),
            30,
            ActionType.ADD,
            UUID.randomUUID().toString()
        );
        when(trainerWorkloadRepository.findByTrainerUsername(request.trainerUsername()))
                .thenReturn(Optional.of(existingTrainer));

        trainerWorkloadService.updateTrainerWorkload(request);

        verify(trainerWorkloadRepository, times(1)).save(any(TrainerWorkload.class));
        assertEquals(90, existingTrainer.getYearlySummary().get(0).getMonthlySummary().get(0).getTotalTrainingMinutes());
    }
    
    @Test
    @DisplayName("should update existing workload duration for a trainer with ActionType.DELETE")
    void updateTrainerWorkload_updateExistingDelete() {
        request = new TrainerWorkloadRequest(
            "Ahmet.Hoca",
            "Ahmet",
            "Hoca",
            true,
            LocalDate.of(2025, 8, 15),
            20,
            ActionType.DELETE,
            UUID.randomUUID().toString()
        );
        when(trainerWorkloadRepository.findByTrainerUsername(request.trainerUsername()))
                .thenReturn(Optional.of(existingTrainer));

        trainerWorkloadService.updateTrainerWorkload(request);

        verify(trainerWorkloadRepository, times(1)).save(any(TrainerWorkload.class));
        assertEquals(40, existingTrainer.getYearlySummary().get(0).getMonthlySummary().get(0).getTotalTrainingMinutes()); // 60 - 20 = 40
    }
    
    @Test
    @DisplayName("should create new trainer workload when record does not exist")
    void updateTrainerWorkload_newTrainer() {
        request = new TrainerWorkloadRequest(
            "Yeni.Hoca",
            "Yeni",
            "Hoca",
            true,
            LocalDate.of(2025, 9, 20),
            45,
            ActionType.ADD,
            UUID.randomUUID().toString()
        );
        when(trainerWorkloadRepository.findByTrainerUsername(request.trainerUsername()))
                .thenReturn(Optional.empty());

        trainerWorkloadService.updateTrainerWorkload(request);

        verify(trainerWorkloadRepository, times(1)).save(any(TrainerWorkload.class));
    }
    
    @Test
    @DisplayName("should retrieve trainer workload successfully")
    void getTrainerWorkload_success() {
        when(trainerWorkloadRepository.findByTrainerUsername("Ahmet.Hoca"))
                .thenReturn(Optional.of(existingTrainer));

        TrainerWorkloadResponse response = trainerWorkloadService.getTrainerWorkload("Ahmet.Hoca");

        assertNotNull(response);
        assertEquals("Ahmet.Hoca", response.trainerUsername());
        assertEquals(2025, response.yearlySummaries().get(0).getYear());
        assertEquals(8, response.yearlySummaries().get(0).getMonthlySummary().get(0).getMonth());
        assertEquals(60, response.yearlySummaries().get(0).getMonthlySummary().get(0).getTotalTrainingMinutes());
        verify(trainerWorkloadRepository).findByTrainerUsername("Ahmet.Hoca");
    }

    @Test
    @DisplayName("should throw exception when trainer workload is not found")
    void getTrainerWorkload_notFound() {
        when(trainerWorkloadRepository.findByTrainerUsername("UnknownTrainer"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> trainerWorkloadService.getTrainerWorkload("UnknownTrainer"));
        verify(trainerWorkloadRepository).findByTrainerUsername("UnknownTrainer");
    }
}