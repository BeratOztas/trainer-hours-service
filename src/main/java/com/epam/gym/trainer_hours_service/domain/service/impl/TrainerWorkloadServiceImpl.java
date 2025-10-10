package com.epam.gym.trainer_hours_service.domain.service.impl;

import java.util.Optional;

import org.jboss.logging.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.epam.gym.trainer_hours_service.api.mapper.TrainerWorkloadMapper;
import com.epam.gym.trainer_hours_service.db.entity.TrainerWorkload;
import com.epam.gym.trainer_hours_service.db.entity.TrainerWorkload.MonthlySummary;
import com.epam.gym.trainer_hours_service.db.entity.TrainerWorkload.YearlySummary;
import com.epam.gym.trainer_hours_service.db.repository.TrainerWorkloadRepository;
import com.epam.gym.trainer_hours_service.domain.exception.BaseException;
import com.epam.gym.trainer_hours_service.domain.exception.ErrorMessage;
import com.epam.gym.trainer_hours_service.domain.exception.MessageType;
import com.epam.gym.trainer_hours_service.domain.service.ITrainerWorkloadService;
import com.epam.trainingcommons.dto.TrainerWorkloadRequest;
import com.epam.trainingcommons.dto.TrainerWorkloadResponse;
import com.epam.trainingcommons.utils.ActionType;


@Service
public class TrainerWorkloadServiceImpl  implements ITrainerWorkloadService {
	
	private static final Logger logger =LoggerFactory.getLogger(TrainerWorkloadServiceImpl.class);
	
	private final TrainerWorkloadRepository trainerWorkloadRepository;
	
	 private final TrainerWorkloadMapper mapper;
	
	public TrainerWorkloadServiceImpl(TrainerWorkloadRepository trainerWorkloadRepository,TrainerWorkloadMapper mapper) {
		this.trainerWorkloadRepository = trainerWorkloadRepository;
		this.mapper=mapper;
	}

	@Override
	public void updateTrainerWorkload(TrainerWorkloadRequest request) {
		MDC.put("transactionID", request.transactionId());
		logger.info("Starting workload update for trainer: {}", request.trainerUsername());
		
		Optional<TrainerWorkload> existingTrainerWorkload =trainerWorkloadRepository.findByTrainerUsername(request.trainerUsername());
		
		TrainerWorkload trainerWorkload;
		
		if(existingTrainerWorkload.isPresent()) {
			trainerWorkload=existingTrainerWorkload.get();
			logger.debug("Existing TrainerWorkload found for user: {}", request.trainerUsername());
		}
		else {
			trainerWorkload =TrainerWorkload.builder()
					.trainerUsername(request.trainerUsername())
					.trainerFirstName(request.trainerFirstName())
					.trainerLastName(request.trainerLastName())
					.isActive(request.isActive())
					.build();
			logger.info("New TrainerWorkload document created for user: {}", request.trainerUsername());
		}
		
		// Extract year and month from the trainingDate.
		int year = request.trainingDate().getYear();
		int month =request.trainingDate().getMonthValue();
		int duration = (int) request.trainingDuration();
		
		Optional<YearlySummary> existingYearlySummary =trainerWorkload.getYearlySummary().stream()
				.filter(ys ->ys.getYear() ==year)
				.findFirst();
		
		// Find or create the YearlySummary
		YearlySummary yearlySummary;
		if(existingYearlySummary.isPresent()) {
			yearlySummary=existingYearlySummary.get();
			logger.info("Existing YearlySummary found for year: {}", year);
		}
		else {
			yearlySummary=YearlySummary.builder().year(year).build();
			
			trainerWorkload.getYearlySummary().add(yearlySummary); 
			
			logger.info("New YearlySummary created for year: {}", year);
		}
		
		// Find or create the MonthlySummary
		Optional<MonthlySummary> existingMontlySummary =yearlySummary.getMonthlySummary().stream()
				.filter(ms ->ms.getMonth()==month)
				.findFirst();
		
		MonthlySummary monthlySummary;
		if(existingMontlySummary.isPresent()) {
			monthlySummary=existingMontlySummary.get();
			logger.info("Existing MonthlySummary found for month: {}", month);
		}else {
			monthlySummary=MonthlySummary.builder().month(month).totalTrainingMinutes(0).build();
			yearlySummary.getMonthlySummary().add(monthlySummary);
			logger.info("New MonthlySummary created for month: {}", month);
		}
		
		// Update total training Minutes based on ActionType
		int currentDuration =monthlySummary.getTotalTrainingMinutes();
		if(request.actionType() == ActionType.ADD) {
			monthlySummary.setTotalTrainingMinutes(currentDuration + duration);
			logger.info("Training minutes incremented. Old: {} -> New: {}", currentDuration, monthlySummary.getTotalTrainingMinutes());
		}
		else if(request.actionType()==ActionType.DELETE) {
			monthlySummary.setTotalTrainingMinutes(currentDuration -duration);
			logger.info("Training minutes decremented. Old: {} -> New: {}", currentDuration, monthlySummary.getTotalTrainingMinutes());
		}
		
        trainerWorkloadRepository.save(trainerWorkload);
        
        logger.info("Workload successfully updated for trainer: {}", request.trainerUsername());
        MDC.clear();
	}

	@Override
	public TrainerWorkloadResponse getTrainerWorkload(String trainerUsername) {
		logger.info("Retrieving workload for trainer: {}", trainerUsername);
		
		return trainerWorkloadRepository.findByTrainerUsername(trainerUsername)
				.map(mapper::toDto)
				.orElseThrow(() ->{
					 logger.warn("Trainer workload record not found for: {}", trainerUsername);
	                    throw new BaseException(new ErrorMessage(MessageType.TRAINER_NOT_FOUND,"Trainer Username: "+ trainerUsername));
				});
	}

}
