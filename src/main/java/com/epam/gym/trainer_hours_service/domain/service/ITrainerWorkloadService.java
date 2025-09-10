package com.epam.gym.trainer_hours_service.domain.service;

import com.epam.gym.trainer_hours_service.api.dto.request.TrainerWorkloadRequest;
import com.epam.gym.trainer_hours_service.api.dto.response.TrainerWorkloadResponse;

public interface ITrainerWorkloadService {

	void updateTrainerWorkload(TrainerWorkloadRequest request);

	TrainerWorkloadResponse getTrainerWorkload(String trainerUsername);
}
