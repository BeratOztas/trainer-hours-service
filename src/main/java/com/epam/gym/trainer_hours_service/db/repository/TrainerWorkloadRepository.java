package com.epam.gym.trainer_hours_service.db.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.epam.gym.trainer_hours_service.db.entity.TrainerWorkload;

public interface TrainerWorkloadRepository extends JpaRepository<TrainerWorkload, Long> {
	Optional<TrainerWorkload> findByTrainerUsername(String trainerUsername);
}
