package com.example.ecommerce.pedidos.application.service;

import com.example.ecommerce.pedidos.application.port.in.CriarPedidoCommand;
import com.example.ecommerce.pedidos.application.port.in.PedidoPublicadoResultado;
import com.example.ecommerce.pedidos.application.port.out.EventIdGeneratorPort;
import com.example.ecommerce.pedidos.application.port.out.MensagemPublicada;
import com.example.ecommerce.pedidos.application.port.out.PublicarPedidoCriadoPort;
import com.example.ecommerce.pedidos.application.port.out.RelogioPort;
import com.example.ecommerce.pedidos.domain.event.PedidoCriadoEvent;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CriarPedidoServiceTest {

    private static final Instant AGORA = Instant.parse("2026-07-07T10:00:00Z");

    @Test
    void deveCriarPedidoEPublicarEventoComDependenciasInjetadas() {
        AtomicReference<PedidoCriadoEvent> eventoPublicado = new AtomicReference<>();
        PublicarPedidoCriadoPort publicarPedidoCriadoPort = event -> {
            eventoPublicado.set(event);
            return new MensagemPublicada("pedido-criado", 1, 20L);
        };
        EventIdGeneratorPort eventIdGeneratorPort = () -> "evt-001";
        RelogioPort relogioPort = () -> AGORA;
        CriarPedidoService service = new CriarPedidoService(
                publicarPedidoCriadoPort,
                eventIdGeneratorPort,
                relogioPort
        );

        PedidoPublicadoResultado resultado = service.criar(new CriarPedidoCommand(
                123L,
                10L,
                BigDecimal.valueOf(250.00)
        ));

        assertEquals("Pedido publicado no Kafka", resultado.message());
        assertEquals("evt-001", resultado.eventId());
        assertEquals("pedido-criado", resultado.topic());
        assertEquals(1, resultado.partition());
        assertEquals(20L, resultado.offset());
        assertNotNull(eventoPublicado.get());
        assertEquals("evt-001", eventoPublicado.get().eventId());
        assertEquals(AGORA, eventoPublicado.get().criadoEm());
    }
}
