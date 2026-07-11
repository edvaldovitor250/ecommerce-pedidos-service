package com.example.ecommerce.pedidos.adapters.in.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CriarPedidoRequest(
        @NotNull(message = "pedidoId é obrigatório")
        @Positive(message = "pedidoId deve ser positivo")
        Long pedidoId,

        @NotNull(message = "clienteId é obrigatório")
        @Positive(message = "clienteId deve ser positivo")
        Long clienteId,

        @NotNull(message = "valor é obrigatório")
        @DecimalMin(value = "0.01", message = "valor deve ser maior que zero")
        BigDecimal valor
) {
}
