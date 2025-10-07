package com.epam.gym.trainer_hours_service.config;


import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import com.epam.gym.trainer_hours_service.TrainerHoursServiceApplication;
import com.epam.gym.trainer_hours_service.db.repository.TrainerWorkloadRepository;
import com.epam.gym.trainer_hours_service.security.JwtTokenProvider;
import com.epam.trainingcommons.dto.TrainerWorkloadRequest;

import io.cucumber.spring.CucumberContextConfiguration;

@CucumberContextConfiguration
@SpringBootTest(
    classes = TrainerHoursServiceApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    properties = "spring.main.allow-bean-definition-overriding=true"
)
@AutoConfigureMockMvc
@Import({CTConfig.class })
public class CucumberConfig {

    public static final String BASE_JWT_TOKEN = "mock-base-jwt-token";

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private TrainerWorkloadRepository trainerWorkloadRepository;
    @MockBean
    private ConsumerFactory<String, Object> kafkaConsumerFactory;
    @MockBean
    private KafkaTemplate<String, TrainerWorkloadRequest> kafkaTemplate;

}