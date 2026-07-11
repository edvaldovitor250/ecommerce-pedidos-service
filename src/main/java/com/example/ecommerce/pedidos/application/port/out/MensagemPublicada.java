package com.example.ecommerce.pedidos.application.port.out;

public record MensagemPublicada(
        String topic,
        int partition,
        long offset
) {
}
