package com.epam.gym.trainer_hours_service.db.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.epam.gym.trainer_hours_service.db.entity.TrainerWorkload;

@Repository
public interface TrainerWorkloadRepository extends MongoRepository<TrainerWorkload, String> {

	Optional<TrainerWorkload> findByTrainerUsername(String trainerUsername);

	Optional<TrainerWorkload> findByTrainerFirstNameAndTrainerLastName(String trainerFirstName, String trainerLastName);
}
