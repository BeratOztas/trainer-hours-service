package com.epam.gym.trainer_hours_service.api.dto.response;

import java.util.List;
import java.util.stream.Collectors;

import com.epam.gym.trainer_hours_service.db.entity.TrainerWorkload;


public record TrainerWorkloadResponse(
		String trainerUsername,
		String trainerFirstName,
		String trainerLastName,
		boolean isActive,
		List<YearlySummary> yearlySummaries
		) {
	
	public static TrainerWorkloadResponse fromEntity(TrainerWorkload trainerWorkload) {
		List<YearlySummary> yearlySummaries = trainerWorkload.getYearlySummary().entrySet().stream()
				.map(yearEntry -> {
					List<MonthlySummary> monthlySummaries = yearEntry.getValue().entrySet().stream()
							.map(monthEntry -> new MonthlySummary(monthEntry.getKey(), monthEntry.getValue()))
							.collect(Collectors.toList());
					return new YearlySummary(yearEntry.getKey(), monthlySummaries);
				}).collect(Collectors.toList());
		
		return new TrainerWorkloadResponse(
						trainerWorkload.getTrainerUsername(),
						trainerWorkload.getTrainerFirstName(),
						trainerWorkload.getTrainerLastName(),
						trainerWorkload.isActive(),
						yearlySummaries);
	}
	public record YearlySummary(
			int year,
			List<MonthlySummary> monthlySummary
	) {}
	
	public record MonthlySummary(
			int month,
			int totalTrainingMinutes
	) {}
}
