package com.tutorial.mailService.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public NewTopic mailEventsTopic() {
        return TopicBuilder.name("mail-events")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic authEventsDlqTopic() {
        return TopicBuilder.name("auth-events.dlq")
                .partitions(3)
                .replicas(1)
                .build();
    }
}