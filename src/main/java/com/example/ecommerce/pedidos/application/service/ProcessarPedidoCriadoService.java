package com.example.ecommerce.pedidos.application.service;

import com.example.ecommerce.pedidos.application.policy.PedidoCriadoProcessingRule;
import com.example.ecommerce.pedidos.application.port.in.ProcessarPedidoCriadoUseCase;
import com.example.ecommerce.pedidos.application.port.out.RegistrarPedidoRecebidoPort;
import com.example.ecommerce.pedidos.domain.event.PedidoCriadoEvent;
import java.util.List;

public class ProcessarPedidoCriadoService implements ProcessarPedidoCriadoUseCase {

    private final RegistrarPedidoRecebidoPort registrarPedidoRecebidoPort;
    private final List<PedidoCriadoProcessingRule> processingRules;

    public ProcessarPedidoCriadoService(
            RegistrarPedidoRecebidoPort registrarPedidoRecebidoPort,
            List<PedidoCriadoProcessingRule> processingRules
    ) {
        this.registrarPedidoRecebidoPort = registrarPedidoRecebidoPort;
        this.processingRules = List.copyOf(processingRules);
    }

    @Override
    public void processar(PedidoCriadoEvent event) {
        processingRules.forEach(rule -> rule.validar(event));
        registrarPedidoRecebidoPort.registrarRecebido(event);
    }
}
