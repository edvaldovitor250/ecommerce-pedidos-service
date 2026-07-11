package com.example.ecommerce.pedidos.domain.model;

import com.example.ecommerce.pedidos.domain.event.PedidoCriadoEvent;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PedidoTest {

    private static final Instant CRIADO_EM = Instant.parse("2026-07-07T10:00:00Z");

    @Test
    void deveGerarEventoPedidoCriado() {
        Pedido pedido = Pedido.criar(123L, 10L, BigDecimal.valueOf(250.00));

        PedidoCriadoEvent event = pedido.gerarEventoCriado("evt-123", CRIADO_EM);

        assertEquals("evt-123", event.eventId());
        assertEquals(123L, event.pedidoId());
        assertEquals(10L, event.clienteId());
        assertEquals(0, BigDecimal.valueOf(250.00).compareTo(event.valor()));
        assertEquals(CRIADO_EM, event.criadoEm());
    }

    @Test
    void deveBloquearValorZerado() {
        assertThrows(IllegalArgumentException.class, () ->
                Pedido.criar(123L, 10L, BigDecimal.ZERO)
        );
    }

    @Test
    void deveBloquearPedidoIdInvalido() {
        assertThrows(IllegalArgumentException.class, () ->
                Pedido.criar(0L, 10L, BigDecimal.TEN)
        );
    }

    @Test
    void deveBloquearEventoSemIdentificador() {
        Pedido pedido = Pedido.criar(123L, 10L, BigDecimal.TEN);

        assertThrows(NullPointerException.class, () -> pedido.gerarEventoCriado(null, CRIADO_EM));
    }

    @Test
    void deveBloquearEventoSemData() {
        Pedido pedido = Pedido.criar(123L, 10L, BigDecimal.TEN);

        assertThrows(NullPointerException.class, () -> pedido.gerarEventoCriado("evt-123", null));
    }
}
