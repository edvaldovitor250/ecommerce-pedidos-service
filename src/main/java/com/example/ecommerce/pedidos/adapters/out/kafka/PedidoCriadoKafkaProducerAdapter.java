package com.example.ecommerce.pedidos.adapters.out.kafka;

import com.example.ecommerce.pedidos.application.exception.MensagemPublicacaoException;
import com.example.ecommerce.pedidos.application.port.out.MensagemPublicada;
import com.example.ecommerce.pedidos.application.port.out.PublicarPedidoCriadoPort;
import com.example.ecommerce.pedidos.domain.event.PedidoCriadoEvent;
import com.example.ecommerce.pedidos.infrastructure.properties.KafkaTopicsProperties;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

@Component
public class PedidoCriadoKafkaProducerAdapter implements PublicarPedidoCriadoPort {

    private static final Logger log = LoggerFactory.getLogger(PedidoCriadoKafkaProducerAdapter.class);
    private static final int PUBLICATION_TIMEOUT_SECONDS = 10;

    private final KafkaTemplate<String, PedidoCriadoEvent> kafkaTemplate;
    private final KafkaTopicsProperties kafkaTopicsProperties;

    public PedidoCriadoKafkaProducerAdapter(
            KafkaTemplate<String, PedidoCriadoEvent> kafkaTemplate,
            KafkaTopicsProperties kafkaTopicsProperties
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaTopicsProperties = kafkaTopicsProperties;
    }

    @Override
    public MensagemPublicada publicar(PedidoCriadoEvent event) {
        String key = String.valueOf(event.pedidoId());

        try {
            SendResult<String, PedidoCriadoEvent> result = kafkaTemplate
                    .send(kafkaTopicsProperties.pedidoCriado(), key, event)
                    .get(PUBLICATION_TIMEOUT_SECONDS, TimeUnit.SECONDS);

            RecordMetadata metadata = result.getRecordMetadata();

            log.info(
                    "Evento publicado: eventId={}, topic={}, partition={}, offset={}",
                    event.eventId(),
                    metadata.topic(),
                    metadata.partition(),
                    metadata.offset()
            );

            return new MensagemPublicada(metadata.topic(), metadata.partition(), metadata.offset());
        } catch (Exception exception) {
            throw new MensagemPublicacaoException("Erro ao publicar evento PedidoCriado no Kafka", exception);
        }
    }
}
