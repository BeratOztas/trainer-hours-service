package com.epam.gym.trainer_hours_service.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.epam.gym.trainer_hours_service.domain.service.ITrainerWorkloadService;
import com.epam.trainingcommons.dto.TrainerWorkloadRequest;

@Component
public class TrainingListener {

	private static final Logger logger = LoggerFactory.getLogger(TrainingListener.class);

	private final ITrainerWorkloadService trainerWorkloadService;

	public TrainingListener(ITrainerWorkloadService trainerWorkloadService) {
		this.trainerWorkloadService=trainerWorkloadService;
	}

	@KafkaListener(topics = "${topic.trainer-workload}",groupId = "${spring.kafka.consumer.group-id}"
			)
	public void consumeWorkloadUpdate(TrainerWorkloadRequest request) {
		 logger.info("Received workload update message with transactionId '{}' for trainer '{}'.",
	                request.transactionId(), request.trainerUsername());
		 
		 trainerWorkloadService.updateTrainerWorkload(request);
		 
		 logger.info("Successfully processed message for trainer '{}'.", request.trainerUsername());
	}
	
	
}
