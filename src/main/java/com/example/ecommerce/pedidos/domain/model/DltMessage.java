package com.example.ecommerce.pedidos.domain.model;

import com.example.ecommerce.pedidos.domain.event.PedidoCriadoEvent;

import java.time.Instant;

public record DltMessage(
        PedidoCriadoEvent event,
        String motivo,
        Instant recebidoEm
) {
}
