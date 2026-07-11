package com.example.ecommerce.pedidos.application.port.out;

import com.example.ecommerce.pedidos.domain.event.PedidoCriadoEvent;

public interface RegistrarPedidoDltPort {

    void registrarDlt(PedidoCriadoEvent event, String motivo);
}
