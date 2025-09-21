package com.epam.gym.trainer_hours_service.api.dto.response;

import java.util.List;

import com.epam.gym.trainer_hours_service.db.entity.TrainerWorkload;
import com.epam.gym.trainer_hours_service.db.entity.TrainerWorkload.YearlySummary;

public record TrainerWorkloadResponse(
		String trainerUsername,
		String trainerFirstName,
		String trainerLastName,
		Boolean isActive,
		List<YearlySummary> yearlySummaries
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
