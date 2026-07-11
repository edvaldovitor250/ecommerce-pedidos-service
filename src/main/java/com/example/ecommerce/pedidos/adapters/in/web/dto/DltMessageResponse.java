package com.example.ecommerce.pedidos.adapters.in.web.dto;

import com.example.ecommerce.pedidos.domain.event.PedidoCriadoEvent;

import java.time.Instant;

public record DltMessageResponse(
        PedidoCriadoEvent event,
        String motivo,
        Instant recebidoEm
) {
}
