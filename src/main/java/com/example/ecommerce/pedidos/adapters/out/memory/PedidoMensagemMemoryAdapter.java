package com.example.ecommerce.pedidos.adapters.out.memory;

import com.example.ecommerce.pedidos.application.port.out.ConsultarPedidoMensagensPort;
import com.example.ecommerce.pedidos.application.port.out.LimparPedidoMensagensPort;
import com.example.ecommerce.pedidos.application.port.out.RegistrarPedidoDltPort;
import com.example.ecommerce.pedidos.application.port.out.RegistrarPedidoRecebidoPort;
import com.example.ecommerce.pedidos.domain.event.PedidoCriadoEvent;
import com.example.ecommerce.pedidos.domain.model.DltMessage;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Component;

@Component
public class PedidoMensagemMemoryAdapter implements RegistrarPedidoRecebidoPort,
        RegistrarPedidoDltPort,
        ConsultarPedidoMensagensPort,
        LimparPedidoMensagensPort {

    private final List<PedidoCriadoEvent> recebidos = new CopyOnWriteArrayList<>();
    private final List<DltMessage> dlt = new CopyOnWriteArrayList<>();

    @Override
    public void registrarRecebido(PedidoCriadoEvent event) {
        recebidos.add(event);
    }

    @Override
    public void registrarDlt(PedidoCriadoEvent event, String motivo) {
        dlt.add(new DltMessage(event, motivo, Instant.now()));
    }

    @Override
    public List<PedidoCriadoEvent> listarRecebidos() {
        return List.copyOf(recebidos);
    }

    @Override
    public List<DltMessage> listarDlt() {
        return List.copyOf(dlt);
    }

    @Override
    public void limpar() {
        recebidos.clear();
        dlt.clear();
    }
}
