package com.example.ecommerce.pedidos.adapters.in.kafka;

import com.example.ecommerce.pedidos.application.port.in.ProcessarPedidoCriadoUseCase;
import com.example.ecommerce.pedidos.application.port.out.RegistrarPedidoDltPort;
import com.example.ecommerce.pedidos.domain.event.PedidoCriadoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Component;

@Component
public class PedidoCriadoKafkaConsumerAdapter {

    private static final Logger log = LoggerFactory.getLogger(PedidoCriadoKafkaConsumerAdapter.class);

    private final ProcessarPedidoCriadoUseCase processarPedidoCriadoUseCase;
    private final RegistrarPedidoDltPort registrarPedidoDltPort;

    public PedidoCriadoKafkaConsumerAdapter(
            ProcessarPedidoCriadoUseCase processarPedidoCriadoUseCase,
            RegistrarPedidoDltPort registrarPedidoDltPort
    ) {
        this.processarPedidoCriadoUseCase = processarPedidoCriadoUseCase;
        this.registrarPedidoDltPort = registrarPedidoDltPort;
    }

    @RetryableTopic(attempts = "3")
    @KafkaListener(
            topics = "${app.kafka.topics.pedido-criado}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumir(PedidoCriadoEvent event) {
        log.info(
                "Evento recebido: eventId={}, pedidoId={}, clienteId={}",
                event.eventId(),
                event.pedidoId(),
                event.clienteId()
        );

        processarPedidoCriadoUseCase.processar(event);

        log.info("Evento processado com sucesso: eventId={}", event.eventId());
    }

    @DltHandler
    public void consumirDlt(PedidoCriadoEvent event) {
        String motivo = "Mensagem enviada para DLT depois das tentativas de retry";
        registrarPedidoDltPort.registrarDlt(event, motivo);

        log.error(
                "Evento enviado para DLT: eventId={}, pedidoId={}, clienteId={}",
                event.eventId(),
                event.pedidoId(),
                event.clienteId()
        );
    }
}
