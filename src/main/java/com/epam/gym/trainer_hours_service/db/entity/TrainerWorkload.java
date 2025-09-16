package com.epam.gym.trainer_hours_service.db.entity;

import java.util.Map;

import com.epam.gym.trainer_hours_service.config.WorkloadMapConverter;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "trainer_workload")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainerWorkload {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String trainerUsername;
	private String trainerFirstName;
	private String trainerLastName;

	private boolean isActive;
	
	@Convert(converter = WorkloadMapConverter.class)
	private Map<Integer, Map<Integer, Integer>> yearlySummary;
	
}
