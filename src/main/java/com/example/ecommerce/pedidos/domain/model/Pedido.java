package com.example.ecommerce.pedidos.domain.model;

import com.example.ecommerce.pedidos.domain.event.PedidoCriadoEvent;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public class Pedido {

    private final Long pedidoId;
    private final Long clienteId;
    private final BigDecimal valor;

    private Pedido(Long pedidoId, Long clienteId, BigDecimal valor) {
        this.pedidoId = validarId(pedidoId, "pedidoId");
        this.clienteId = validarId(clienteId, "clienteId");
        this.valor = validarValor(valor);
    }

    public static Pedido criar(Long pedidoId, Long clienteId, BigDecimal valor) {
        return new Pedido(pedidoId, clienteId, valor);
    }

    public PedidoCriadoEvent gerarEventoCriado(String eventId, Instant criadoEm) {
        Objects.requireNonNull(eventId, "eventId é obrigatório");
        Objects.requireNonNull(criadoEm, "criadoEm é obrigatório");

        return new PedidoCriadoEvent(eventId, pedidoId, clienteId, valor, criadoEm);
    }

    private static Long validarId(Long value, String campo) {
        Objects.requireNonNull(value, campo + " é obrigatório");

        if (value <= 0) {
            throw new IllegalArgumentException(campo + " deve ser positivo");
        }

        return value;
    }

    private static BigDecimal validarValor(BigDecimal valor) {
        Objects.requireNonNull(valor, "valor é obrigatório");

        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("valor deve ser maior que zero");
        }

        return valor;
    }
}
