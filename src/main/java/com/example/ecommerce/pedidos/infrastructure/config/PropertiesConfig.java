package com.example.ecommerce.pedidos.infrastructure.config;

import com.example.ecommerce.pedidos.infrastructure.properties.KafkaTopicsProperties;
import com.example.ecommerce.pedidos.infrastructure.properties.ProcessamentoPedidoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({KafkaTopicsProperties.class, ProcessamentoPedidoProperties.class})
public class PropertiesConfig {
}
