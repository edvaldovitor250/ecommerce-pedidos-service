package com.example.ecommerce.pedidos.infrastructure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.kafka.topics")
public record KafkaTopicsProperties(String pedidoCriado) {
}
