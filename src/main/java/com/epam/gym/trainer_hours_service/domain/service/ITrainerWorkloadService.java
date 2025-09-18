package com.epam.gym.trainer_hours_service.domain.service;

import com.epam.gym.trainer_hours_service.api.dto.response.TrainerWorkloadResponse;
import com.epam.trainingcommons.dto.TrainerWorkloadRequest;

public interface ITrainerWorkloadService {

	void updateTrainerWorkload(TrainerWorkloadRequest request);

	TrainerWorkloadResponse getTrainerWorkload(String trainerUsername);
}
