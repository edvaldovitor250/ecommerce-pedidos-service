package com.example.ecommerce.pedidos.application.service;

import com.example.ecommerce.pedidos.application.port.in.CriarPedidoCommand;
import com.example.ecommerce.pedidos.application.port.in.CriarPedidoUseCase;
import com.example.ecommerce.pedidos.application.port.in.PedidoPublicadoResultado;
import com.example.ecommerce.pedidos.application.port.out.EventIdGeneratorPort;
import com.example.ecommerce.pedidos.application.port.out.MensagemPublicada;
import com.example.ecommerce.pedidos.application.port.out.PublicarPedidoCriadoPort;
import com.example.ecommerce.pedidos.application.port.out.RelogioPort;
import com.example.ecommerce.pedidos.domain.event.PedidoCriadoEvent;
import com.example.ecommerce.pedidos.domain.model.Pedido;

public class CriarPedidoService implements CriarPedidoUseCase {

    private final PublicarPedidoCriadoPort publicarPedidoCriadoPort;
    private final EventIdGeneratorPort eventIdGeneratorPort;
    private final RelogioPort relogioPort;

    public CriarPedidoService(
            PublicarPedidoCriadoPort publicarPedidoCriadoPort,
            EventIdGeneratorPort eventIdGeneratorPort,
            RelogioPort relogioPort
    ) {
        this.publicarPedidoCriadoPort = publicarPedidoCriadoPort;
        this.eventIdGeneratorPort = eventIdGeneratorPort;
        this.relogioPort = relogioPort;
    }

    @Override
    public PedidoPublicadoResultado criar(CriarPedidoCommand command) {
        Pedido pedido = Pedido.criar(command.pedidoId(), command.clienteId(), command.valor());

        PedidoCriadoEvent event = pedido.gerarEventoCriado(
                eventIdGeneratorPort.gerar(),
                relogioPort.agora()
        );
        MensagemPublicada mensagemPublicada = publicarPedidoCriadoPort.publicar(event);

        return new PedidoPublicadoResultado(
                "Pedido publicado no Kafka",
                event.eventId(),
                mensagemPublicada.topic(),
                mensagemPublicada.partition(),
                mensagemPublicada.offset()
        );
    }
}
