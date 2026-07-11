package com.example.ecommerce.pedidos.domain.event;

import java.math.BigDecimal;
import java.time.Instant;

public record PedidoCriadoEvent(
        String eventId,
        Long pedidoId,
        Long clienteId,
        BigDecimal valor,
        Instant criadoEm
) {
}
