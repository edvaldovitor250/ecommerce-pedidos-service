package com.example.ecommerce.pedidos.application.service;

import com.example.ecommerce.pedidos.application.port.in.ConsultarMensagensUseCase;
import com.example.ecommerce.pedidos.application.port.out.ConsultarPedidoMensagensPort;
import com.example.ecommerce.pedidos.domain.event.PedidoCriadoEvent;
import com.example.ecommerce.pedidos.domain.model.DltMessage;
import java.util.List;

public class ConsultarMensagensService implements ConsultarMensagensUseCase {

    private final ConsultarPedidoMensagensPort consultarPedidoMensagensPort;

    public ConsultarMensagensService(ConsultarPedidoMensagensPort consultarPedidoMensagensPort) {
        this.consultarPedidoMensagensPort = consultarPedidoMensagensPort;
    }

    @Override
    public List<PedidoCriadoEvent> listarRecebidos() {
        return consultarPedidoMensagensPort.listarRecebidos();
    }

    @Override
    public List<DltMessage> listarDlt() {
        return consultarPedidoMensagensPort.listarDlt();
    }
}
