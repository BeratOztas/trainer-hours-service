package com.epam.gym.trainer_hours_service.domain.service.impl;

import com.epam.gym.trainer_hours_service.api.dto.response.TrainerWorkloadResponse;
import com.epam.gym.trainer_hours_service.db.entity.TrainerWorkload;
import com.epam.gym.trainer_hours_service.db.repository.TrainerWorkloadRepository;
import com.epam.gym.trainer_hours_service.domain.exception.BaseException;
import com.epam.trainingcommons.dto.TrainerWorkloadRequest;
import com.epam.trainingcommons.utils.ActionType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadServiceImplTest {

    @InjectMocks
    private TrainerWorkloadServiceImpl trainerWorkloadService;

    @Mock
    private TrainerWorkloadRepository trainerWorkloadRepository;

    @Test
    @DisplayName("should create a new workload record for a non-existing trainer")
    void updateTrainerWorkload_createNewRecord() {
        // Arrange
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "john.doe", "John", "Doe", true, LocalDate.of(2025, 1, 15), 60, ActionType.ADD,UUID.randomUUID().toString()
        );
        when(trainerWorkloadRepository.findByTrainerUsername("john.doe")).thenReturn(Optional.empty());

        // Act
        trainerWorkloadService.updateTrainerWorkload(request);

        // Assert
        verify(trainerWorkloadRepository).save(any(TrainerWorkload.class));
    }

    @Test
    @DisplayName("should update existing workload duration for a trainer with ActionType.ADD")
    void updateTrainerWorkload_updateExistingAdd() {
        // Arrange
        TrainerWorkload existingWorkload = new TrainerWorkload();
        existingWorkload.setTrainerUsername("john.doe");
        existingWorkload.setTrainerFirstName("John");
        existingWorkload.setTrainerLastName("Doe");
        existingWorkload.setActive(true);

        Map<Integer, Map<Integer, Integer>> yearlySummary = new HashMap<>();
        Map<Integer, Integer> monthlySummary = new HashMap<>();
        monthlySummary.put(1, 30);
        yearlySummary.put(2025, monthlySummary);
        existingWorkload.setYearlySummary(yearlySummary);

        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "john.doe", "John", "Doe", true, LocalDate.of(2025, 1, 20), 45, ActionType.ADD,UUID.randomUUID().toString()
        );

        when(trainerWorkloadRepository.findByTrainerUsername("john.doe")).thenReturn(Optional.of(existingWorkload));

        // Act
        trainerWorkloadService.updateTrainerWorkload(request);

        // Assert
        verify(trainerWorkloadRepository).save(existingWorkload);
        assertEquals(75, existingWorkload.getYearlySummary().get(2025).get(1));
    }

    @Test
    @DisplayName("should update existing workload duration for a trainer with ActionType.DELETE")
    void updateTrainerWorkload_updateExistingDelete() {
        // Arrange
        TrainerWorkload existingWorkload = new TrainerWorkload();
        existingWorkload.setTrainerUsername("john.doe");
        existingWorkload.setTrainerFirstName("John");
        existingWorkload.setTrainerLastName("Doe");
        existingWorkload.setActive(true);

        Map<Integer, Map<Integer, Integer>> yearlySummary = new HashMap<>();
        Map<Integer, Integer> monthlySummary = new HashMap<>();
        monthlySummary.put(1, 100);
        yearlySummary.put(2025, monthlySummary);
        existingWorkload.setYearlySummary(yearlySummary);

        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "john.doe", "John", "Doe", true, LocalDate.of(2025, 1, 25), 40, ActionType.DELETE,UUID.randomUUID().toString()
        );

        when(trainerWorkloadRepository.findByTrainerUsername("john.doe")).thenReturn(Optional.of(existingWorkload));

        // Act
        trainerWorkloadService.updateTrainerWorkload(request);

        // Assert
        verify(trainerWorkloadRepository).save(existingWorkload);
        assertEquals(60, existingWorkload.getYearlySummary().get(2025).get(1));
    }

    @Test
    @DisplayName("should return workload for an existing trainer")
    void getTrainerWorkload_success() {
        // Arrange
        TrainerWorkload existingWorkload = new TrainerWorkload();
        existingWorkload.setTrainerUsername("john.doe");
        existingWorkload.setTrainerFirstName("John");
        existingWorkload.setTrainerLastName("Doe");
        existingWorkload.setActive(true);

        existingWorkload.setYearlySummary(new HashMap<>());

        when(trainerWorkloadRepository.findByTrainerUsername("john.doe")).thenReturn(Optional.of(existingWorkload));

        // Act
        TrainerWorkloadResponse response = trainerWorkloadService.getTrainerWorkload("john.doe");

        // Assert
        assertNotNull(response);
        assertEquals("john.doe", response.trainerUsername());
        verify(trainerWorkloadRepository).findByTrainerUsername("john.doe");
    }

    @Test
    @DisplayName("should throw BaseException when trainer workload is not found")
    void getTrainerWorkload_trainerNotFound() {
        // Arrange
        when(trainerWorkloadRepository.findByTrainerUsername("non.existent")).thenReturn(Optional.empty());

        // Act & Assert
        BaseException thrown = assertThrows(BaseException.class, () -> {
            trainerWorkloadService.getTrainerWorkload("non.existent");
        });

        assertEquals("TRAINER_NOT_FOUND", thrown.getErrorMessage().getMessageType().name());
        assertTrue(thrown.getErrorMessage().getDetails().contains("non.existent"));
    }
}