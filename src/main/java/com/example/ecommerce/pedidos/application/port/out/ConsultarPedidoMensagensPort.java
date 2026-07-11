package com.example.ecommerce.pedidos.application.port.out;

import com.example.ecommerce.pedidos.domain.event.PedidoCriadoEvent;
import com.example.ecommerce.pedidos.domain.model.DltMessage;
import java.util.List;

public interface ConsultarPedidoMensagensPort {

    List<PedidoCriadoEvent> listarRecebidos();

    List<DltMessage> listarDlt();
}
