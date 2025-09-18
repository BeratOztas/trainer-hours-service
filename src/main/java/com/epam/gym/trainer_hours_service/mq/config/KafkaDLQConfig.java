package com.epam.gym.trainer_hours_service.mq.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
@Configuration
public class KafkaDLQConfig {
	
	private static final Logger logger =LoggerFactory.getLogger(KafkaDLQConfig.class);
	
	@Value("${topic.trainer-workload-dlq}")
    private String dlqTopicName;

	@Value("${topic.partitions:1}")
    private int partitions;	

    @Value("${topic.replicas:1}")
    private int replicas;
    
    @Bean
    public DeadLetterPublishingRecoverer dlqPublishingRecoverer(KafkaTemplate<Object, Object> template) {
        return new DeadLetterPublishingRecoverer(template,
            (consumerRecord, exception) -> {
            	 logger.error("DLQ: Message failed for topic {}, key {}, error {}. Sending to {}",
            	            consumerRecord.topic(), consumerRecord.key(), exception.getMessage(), dlqTopicName);
                return new TopicPartition(dlqTopicName, consumerRecord.partition());
            }
        );
    }
    
    @Bean
    public NewTopic dlqTopic() {
        return new NewTopic(dlqTopicName, partitions, (short) replicas);
    }
    
}
