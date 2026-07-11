package com.example.ecommerce.pedidos.application.port.in;

public record PedidoPublicadoResultado(
        String message,
        String eventId,
        String topic,
        int partition,
        long offset
) {
}
