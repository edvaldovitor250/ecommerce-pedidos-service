package com.example.ecommerce.pedidos.application.port.in;

import java.math.BigDecimal;

public record CriarPedidoCommand(
        Long pedidoId,
        Long clienteId,
        BigDecimal valor
) {
}
