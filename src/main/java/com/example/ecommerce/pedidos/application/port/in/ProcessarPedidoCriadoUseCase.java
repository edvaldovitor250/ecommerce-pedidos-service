package com.example.ecommerce.pedidos.application.port.in;

import com.example.ecommerce.pedidos.domain.event.PedidoCriadoEvent;

public interface ProcessarPedidoCriadoUseCase {

    void processar(PedidoCriadoEvent event);
}
