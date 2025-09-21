package com.epam.gym.trainer_hours_service.db.entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document("trainer_workload")
@CompoundIndex(name = "full_name_index",def = " {'trainerFirstName':1 , 'trainerLastName':1} " )
@Builder
public class TrainerWorkload {

	@Id
	private String id;

	private String trainerUsername;
	private String trainerFirstName;
	private String trainerLastName;

	private Boolean isActive;
	
	@Builder.Default
	private List<YearlySummary> yearlySummary = new ArrayList<>();
	
	@Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class YearlySummary {
        private int year;
        @Builder.Default
        private List<MonthlySummary> monthlySummary = new ArrayList<>();
    }
	
	@Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MonthlySummary {
        private int month;
        private int totalTrainingMinutes;
    }
}
