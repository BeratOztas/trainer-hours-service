package com.epam.gym.trainer_hours_service.mq.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConsumerConfig {

	private static final long RETRY=3L;
	private static final long PERIOD=1000L;
	
	@Bean
	public DefaultErrorHandler errorHandler(DeadLetterPublishingRecoverer recoverer) {
		
		FixedBackOff fixedBackOff =new FixedBackOff(PERIOD, RETRY);
		
		return new DefaultErrorHandler(recoverer,fixedBackOff);
	}
	
	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(ConsumerFactory<String, Object> consumerFactory,
			DefaultErrorHandler errorHandler){
		
		ConcurrentKafkaListenerContainerFactory<String, Object> factory =new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory);
		factory.setCommonErrorHandler(errorHandler);
		
		return factory;
	}
	
}
