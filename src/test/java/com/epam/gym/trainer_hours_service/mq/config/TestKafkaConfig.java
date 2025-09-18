package com.epam.gym.trainer_hours_service.mq.config;

import java.time.Duration;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.epam.trainingcommons.dto.TrainerWorkloadRequest;

@TestConfiguration
public class TestKafkaConfig {

	  @Bean
	    public ProducerFactory<String, TrainerWorkloadRequest> producerFactory() {
	        Map<String, Object> configProps = new java.util.HashMap<>();
	        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); 
	        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
	        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
	        return new DefaultKafkaProducerFactory<>(configProps);
	    }

	    @Bean
	    public KafkaTemplate<String, TrainerWorkloadRequest> kafkaTemplate() {
	    	 KafkaTemplate<String, TrainerWorkloadRequest> template = new KafkaTemplate<>(producerFactory());
	    	 template.setCloseTimeout(Duration.ZERO);
	         return template;
	    }
}
