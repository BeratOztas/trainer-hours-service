package com.epam.gym.trainer_hours_service.api.dto.request;

import java.time.LocalDate;

import com.epam.gym.trainer_hours_service.utils.ActionType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TrainerWorkloadRequest(
		
		@NotBlank(message = "Trainer username must not be blank") 
		String trainerUsername,

		@NotBlank(message = "Trainer first name must not be blank") 
		String trainerFirstName,

		@NotBlank(message = "Trainer last name must not be blank") 
		String trainerLastName,

		@NotNull(message = "Trainer status must be provided") 
		Boolean isActive,

		@NotNull(message = "Training date must be provided") 
		LocalDate trainingDate,

		@NotNull(message = "Training duration must be provided") @Min(value = 1, message = "Training duration must be at least 1 minute") 
		Integer trainingDuration,
		
		@NotNull(message = "ActionType must not be blank")
		ActionType actionType
		) {

}
