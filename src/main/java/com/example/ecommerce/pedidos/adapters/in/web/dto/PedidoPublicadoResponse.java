package com.example.ecommerce.pedidos.adapters.in.web.dto;

public record PedidoPublicadoResponse(
        String message,
        String eventId,
        String topic,
        int partition,
        long offset
) {
}
