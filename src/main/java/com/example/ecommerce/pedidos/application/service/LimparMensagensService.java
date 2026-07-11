package com.example.ecommerce.pedidos.application.service;

import com.example.ecommerce.pedidos.application.port.in.LimparMensagensUseCase;
import com.example.ecommerce.pedidos.application.port.out.LimparPedidoMensagensPort;

public class LimparMensagensService implements LimparMensagensUseCase {

    private final LimparPedidoMensagensPort limparPedidoMensagensPort;

    public LimparMensagensService(LimparPedidoMensagensPort limparPedidoMensagensPort) {
        this.limparPedidoMensagensPort = limparPedidoMensagensPort;
    }

    @Override
    public void limpar() {
        limparPedidoMensagensPort.limpar();
    }
}
