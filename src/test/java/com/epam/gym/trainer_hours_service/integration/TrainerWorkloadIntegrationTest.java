package com.epam.gym.trainer_hours_service.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.epam.gym.trainer_hours_service.db.entity.TrainerWorkload;
import com.epam.gym.trainer_hours_service.db.entity.TrainerWorkload.MonthlySummary;
import com.epam.gym.trainer_hours_service.db.entity.TrainerWorkload.YearlySummary;
import com.epam.gym.trainer_hours_service.db.repository.TrainerWorkloadRepository;
import com.epam.gym.trainer_hours_service.mq.config.TestKafkaConfig;
import com.epam.trainingcommons.dto.TrainerWorkloadRequest;
import com.epam.trainingcommons.utils.ActionType;

@SpringBootTest(
	    properties = {
	        "spring.cloud.discovery.enabled=false",
	        "spring.kafka.bootstrap-servers=${spring.kafka.bootstrap-servers}"
	    }
	)
@Testcontainers
@Import(TestKafkaConfig.class)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@Disabled
class TrainerWorkloadIntegrationTest {

    @Container
    static final KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.0.0"));

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", () -> kafka.getBootstrapServers());
        
        registry.add("spring.kafka.consumer.group-id", () -> "trainer-workload-test-group");
    }

    @Autowired
    private KafkaTemplate<String, TrainerWorkloadRequest> kafkaTemplate;

    @Autowired
    private TrainerWorkloadRepository workloadRepository;

    @BeforeEach
    void cleanDb() {
        workloadRepository.deleteAll();
    }

    @Test
    @DisplayName("Kafka message updates workload in the database")
    void testKafkaMessageProcessing() throws Exception {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
            "Ahmet.Hoca",
            "Ahmet",
            "Hoca",
            true,
            LocalDate.of(2025, 8, 1),
            45,
            ActionType.ADD,
            UUID.randomUUID().toString()
        );

        kafkaTemplate.send("trainer-workload-topic", request).get();
        

        await().atMost(10, TimeUnit.SECONDS).until(() ->
            workloadRepository.findByTrainerUsername("Ahmet.Hoca").isPresent()
        );

        Optional<TrainerWorkload> optionalWorkload = workloadRepository.findByTrainerUsername("Ahmet.Hoca");

        assertTrue(optionalWorkload.isPresent(), "Workload record should exist in the database.");
        TrainerWorkload workload = optionalWorkload.get();
        assertEquals("Ahmet", workload.getTrainerFirstName(), "First name should match");
        assertEquals("Hoca", workload.getTrainerLastName(), "Last name should match");

        Optional<YearlySummary> yearlySummary = workload.getYearlySummary().stream()
                .filter(ys -> ys.getYear() == 2025)
                .findFirst();

        assertTrue(yearlySummary.isPresent(), "Yearly summary for 2025 should exist.");
        
        Optional<MonthlySummary> monthlySummary = yearlySummary.get().getMonthlySummary().stream()
                .filter(ms -> ms.getMonth() == 8)
                .findFirst();

        assertTrue(monthlySummary.isPresent(), "Monthly summary for August should exist.");
        
        long totalMinutes = monthlySummary.get().getTotalTrainingMinutes();
        assertEquals(45, totalMinutes, "Total training minutes for August 2025 should be 45");
    }

 
}