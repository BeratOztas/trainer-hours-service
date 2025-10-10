package com.epam.gym.trainer_hours_service.domain.service;

import com.epam.trainingcommons.dto.TrainerWorkloadRequest;
import com.epam.trainingcommons.dto.TrainerWorkloadResponse;

public interface ITrainerWorkloadService {

	void updateTrainerWorkload(TrainerWorkloadRequest request);

	TrainerWorkloadResponse getTrainerWorkload(String trainerUsername);
	
}
