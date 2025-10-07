package com.epam.gym.trainer_hours_service.api.mapper;

import com.epam.gym.trainer_hours_service.db.entity.TrainerWorkload;
import com.epam.trainingcommons.dto.MonthlySummaryDto;
import com.epam.trainingcommons.dto.TrainerWorkloadResponse;
import com.epam.trainingcommons.dto.YearlySummaryDto;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class TrainerWorkloadMapper {

    public TrainerWorkloadResponse toDto(TrainerWorkload entity) {
        if (entity == null) {
            return null;
        }

        return new TrainerWorkloadResponse(
            entity.getTrainerUsername(),
            entity.getTrainerFirstName(),
            entity.getTrainerLastName(),
            entity.getIsActive(),
            entity.getYearlySummary().stream()
                .map(this::toYearlySummaryDto)
                .collect(Collectors.toList())
        );
    }

    private YearlySummaryDto toYearlySummaryDto(TrainerWorkload.YearlySummary entity) {
        return new YearlySummaryDto(
            entity.getYear(),
            entity.getMonthlySummary().stream()
                .map(this::toMonthlySummaryDto)
                .collect(Collectors.toList())
        );
    }

    private MonthlySummaryDto toMonthlySummaryDto(TrainerWorkload.MonthlySummary entity) {
        return new MonthlySummaryDto(
            entity.getMonth(),
            entity.getTotalTrainingMinutes()
        );
    }
}