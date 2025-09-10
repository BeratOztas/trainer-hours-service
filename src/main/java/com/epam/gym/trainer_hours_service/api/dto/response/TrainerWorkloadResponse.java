package com.epam.gym.trainer_hours_service.api.dto.response;

import java.util.Map;

import com.epam.gym.trainer_hours_service.db.entity.TrainerWorkload;

public record TrainerWorkloadResponse(
		String trainerUsername,
		String trainerFirstName,
		String trainerLastName,
		Boolean isActive,
		Map<Integer, Map<Integer, Integer>> yearlySummary
		) {
	
	public static TrainerWorkloadResponse fromEntity(TrainerWorkload trainerWorkload) {
		return new TrainerWorkloadResponse(
						trainerWorkload.getTrainerUsername(),
						trainerWorkload.getTrainerFirstName(),
						trainerWorkload.getTrainerLastName(),
						trainerWorkload.getIsActive(),
						trainerWorkload.getYearlySummary());
	}
}
