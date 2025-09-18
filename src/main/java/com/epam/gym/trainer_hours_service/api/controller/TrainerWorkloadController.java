package com.epam.gym.trainer_hours_service.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RestController;

import com.epam.gym.trainer_hours_service.api.dto.response.TrainerWorkloadResponse;
import com.epam.gym.trainer_hours_service.domain.service.ITrainerWorkloadService;
import com.epam.trainingcommons.dto.TrainerWorkloadRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/trainer-workload")
@Tag(name = "Trainer Workload", description = "API for managing trainer workload and training events.")
public class TrainerWorkloadController {

	private static final Logger logger = LoggerFactory.getLogger(TrainerWorkloadController.class);

	private final ITrainerWorkloadService trainerWorkloadService;

	public TrainerWorkloadController(ITrainerWorkloadService trainerWorkloadService) {
		this.trainerWorkloadService = trainerWorkloadService;
	}
	

	@PostMapping
	@Operation(summary = "Update trainer workload", description = "Handles ADD or DELETE training events to update a trainer's workload.")
	@ApiResponse(responseCode = "200", description = "Workload successfully updated.")
	@ApiResponse(responseCode = "400", description = "Invalid request payload.")
	public ResponseEntity<String> updateTrainerWorkload(@Valid @RequestBody TrainerWorkloadRequest request
			,@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

		logger.info("Received request to update trainer workload. Trainer: {}", request.trainerUsername());
		trainerWorkloadService.updateTrainerWorkload(request);
		return ResponseEntity.ok("Workload successfully updated. for Trainer: " + request.trainerUsername());

	}
	
	@GetMapping("/{trainerUsername}")
	@Operation(summary = "Get trainer workload", description = "Retrieves the total training duration for a specific trainer by their username.")
	@ApiResponse(responseCode = "200", description = "Workload found.")
	@ApiResponse(responseCode = "404", description = "Trainer not found.")
	public ResponseEntity<TrainerWorkloadResponse> getTrainerWorkload(@PathVariable String trainerUsername){
		logger.info("Retrieving workload for trainer: {}", trainerUsername);
		return ResponseEntity.ok(trainerWorkloadService.getTrainerWorkload(trainerUsername));
	}

}
