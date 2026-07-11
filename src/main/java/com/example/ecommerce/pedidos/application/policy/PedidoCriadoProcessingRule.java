package com.example.ecommerce.pedidos.application.policy;

import com.example.ecommerce.pedidos.domain.event.PedidoCriadoEvent;

@FunctionalInterface
public interface PedidoCriadoProcessingRule {

    void validar(PedidoCriadoEvent event);
}
