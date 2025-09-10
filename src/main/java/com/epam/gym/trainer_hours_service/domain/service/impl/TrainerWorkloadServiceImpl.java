package com.epam.gym.trainer_hours_service.domain.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epam.gym.trainer_hours_service.api.dto.request.TrainerWorkloadRequest;
import com.epam.gym.trainer_hours_service.api.dto.response.TrainerWorkloadResponse;
import com.epam.gym.trainer_hours_service.db.entity.TrainerWorkload;
import com.epam.gym.trainer_hours_service.db.repository.TrainerWorkloadRepository;
import com.epam.gym.trainer_hours_service.domain.exception.BaseException;
import com.epam.gym.trainer_hours_service.domain.exception.ErrorMessage;
import com.epam.gym.trainer_hours_service.domain.exception.MessageType;
import com.epam.gym.trainer_hours_service.domain.service.ITrainerWorkloadService;
import com.epam.gym.trainer_hours_service.utils.ActionType;


@Service
public class TrainerWorkloadServiceImpl  implements ITrainerWorkloadService {
	
	private static final Logger logger =LoggerFactory.getLogger(TrainerWorkloadServiceImpl.class);
	
	private final TrainerWorkloadRepository trainerWorkloadRepository;
	
	public TrainerWorkloadServiceImpl(TrainerWorkloadRepository trainerWorkloadRepository) {
		this.trainerWorkloadRepository = trainerWorkloadRepository;
	}

	@Override
	@Transactional
	public void updateTrainerWorkload(TrainerWorkloadRequest request) {
		logger.info("Starting workload update for trainer: {}", request.trainerUsername());
		
		TrainerWorkload trainerWorkload = trainerWorkloadRepository.findByTrainerUsername(request.trainerUsername())
		.orElse(new TrainerWorkload());
				
		
		trainerWorkload.setTrainerUsername(request.trainerUsername());
		trainerWorkload.setTrainerFirstName(request.trainerFirstName());
		trainerWorkload.setTrainerLastName(request.trainerLastName());
		trainerWorkload.setIsActive(request.isActive());
		
		
		Map<Integer, Map<Integer, Integer>> yearlySummary = trainerWorkload.getYearlySummary();
		if(yearlySummary ==null) {
			yearlySummary=new HashMap<>();
			trainerWorkload.setYearlySummary(yearlySummary);
		}
		
		int year = request.trainingDate().getYear();
		int month =request.trainingDate().getMonthValue();
		int duration = request.trainingDuration();
		
		Map<Integer, Integer> monthlySummary = yearlySummary.getOrDefault(year, new HashMap<>());
		int currentDuration =monthlySummary.getOrDefault(month, 0);
		
		if (request.actionType() == ActionType.ADD) {
            currentDuration += duration;
        } else if (request.actionType() == ActionType.DELETE) {
            currentDuration -= duration;
        }
		
		monthlySummary.put(month, currentDuration);
        yearlySummary.put(year, monthlySummary);
		
        trainerWorkloadRepository.save(trainerWorkload);
        logger.info("Workload successfully updated for trainer: {}", request.trainerUsername());
	}

	@Override
	@Transactional(readOnly = true)
	public TrainerWorkloadResponse getTrainerWorkload(String trainerUsername) {
		logger.info("Retrieving workload for trainer: {}", trainerUsername);
		
		return trainerWorkloadRepository.findByTrainerUsername(trainerUsername)
				.map(TrainerWorkloadResponse::fromEntity)
				.orElseThrow(() ->{
					 logger.warn("Trainer workload record not found for: {}", trainerUsername);
	                    throw new BaseException(new ErrorMessage(MessageType.TRAINER_NOT_FOUND,"Trainer Username: "+ trainerUsername));
				});
	}

}
