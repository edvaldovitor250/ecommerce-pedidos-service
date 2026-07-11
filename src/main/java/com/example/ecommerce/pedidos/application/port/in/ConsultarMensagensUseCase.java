package com.example.ecommerce.pedidos.application.port.in;

import com.example.ecommerce.pedidos.domain.event.PedidoCriadoEvent;
import com.example.ecommerce.pedidos.domain.model.DltMessage;
import java.util.List;

public interface ConsultarMensagensUseCase {

    List<PedidoCriadoEvent> listarRecebidos();

    List<DltMessage> listarDlt();
}
