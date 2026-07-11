package com.example.ecommerce.pedidos.application.port.in;

public interface CriarPedidoUseCase {

    PedidoPublicadoResultado criar(CriarPedidoCommand command);
}
