package com.example.ecommerce.pedidos.infrastructure.kafka;

import com.example.ecommerce.pedidos.infrastructure.properties.KafkaTopicsProperties;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic pedidoCriadoTopic(KafkaTopicsProperties kafkaTopicsProperties) {
        return TopicBuilder
                .name(kafkaTopicsProperties.pedidoCriado())
                .partitions(3)
                .replicas(1)
                .build();
    }
}
