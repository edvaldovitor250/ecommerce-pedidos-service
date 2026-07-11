package com.example.ecommerce.pedidos.application.service;

import com.example.ecommerce.pedidos.application.policy.PedidoCriadoProcessingRule;
import com.example.ecommerce.pedidos.application.port.out.RegistrarPedidoRecebidoPort;
import com.example.ecommerce.pedidos.domain.event.PedidoCriadoEvent;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProcessarPedidoCriadoServiceTest {

    @Test
    void deveRegistrarEventoQuandoRegrasPassam() {
        List<PedidoCriadoEvent> recebidos = new ArrayList<>();
        RegistrarPedidoRecebidoPort registrarPedidoRecebidoPort = recebidos::add;
        ProcessarPedidoCriadoService service = new ProcessarPedidoCriadoService(
                registrarPedidoRecebidoPort,
                List.of(event -> { })
        );
        PedidoCriadoEvent event = criarEvento(10L);

        service.processar(event);

        assertEquals(List.of(event), recebidos);
    }

    @Test
    void naoDeveRegistrarEventoQuandoRegraFalha() {
        List<PedidoCriadoEvent> recebidos = new ArrayList<>();
        RegistrarPedidoRecebidoPort registrarPedidoRecebidoPort = recebidos::add;
        PedidoCriadoProcessingRule regra = event -> {
            throw new IllegalStateException("falha de regra");
        };
        ProcessarPedidoCriadoService service = new ProcessarPedidoCriadoService(
                registrarPedidoRecebidoPort,
                List.of(regra)
        );

        assertThrows(IllegalStateException.class, () -> service.processar(criarEvento(999L)));
        assertEquals(List.of(), recebidos);
    }

    private static PedidoCriadoEvent criarEvento(Long clienteId) {
        return new PedidoCriadoEvent(
                "evt-001",
                123L,
                clienteId,
                BigDecimal.TEN,
                Instant.parse("2026-07-07T10:00:00Z")
        );
    }
}
