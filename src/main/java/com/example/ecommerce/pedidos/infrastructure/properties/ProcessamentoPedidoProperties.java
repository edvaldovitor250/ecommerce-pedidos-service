package com.example.ecommerce.pedidos.infrastructure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.processing")
public record ProcessamentoPedidoProperties(Long failWhenClienteId) {
}
