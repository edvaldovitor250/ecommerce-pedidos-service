# Arquitetura hexagonal do projeto

Microserviço de emissão de pedidos do e-commerce usando arquitetura hexagonal para manter o domínio e os casos de uso livres de detalhes técnicos.

## Camadas

```text
domain          — regras de negócio puras
application     — casos de uso, portas e orquestração
adapters        — detalhes técnicos (REST, Kafka, memória)
infrastructure  — configuração de beans, propriedades
```

## Domain

Pacote: `com.example.ecommerce.pedidos.domain`

Contém: `Pedido`, `PedidoCriadoEvent`, `DltMessage`

O domínio não conhece Spring, Kafka, REST ou banco de dados.

## Application

Pacote: `com.example.ecommerce.pedidos.application`

Portas de entrada: `CriarPedidoUseCase`, `ProcessarPedidoCriadoUseCase`, `ConsultarMensagensUseCase`, `LimparMensagensUseCase`

Portas de saída: `PublicarPedidoCriadoPort`, `RegistrarPedidoRecebidoPort`, `RegistrarPedidoDltPort`, `ConsultarPedidoMensagensPort`, `LimparPedidoMensagensPort`, `EventIdGeneratorPort`, `RelogioPort`

## Adapters de entrada

- `PedidoController` — REST
- `PedidoCriadoKafkaConsumerAdapter` — Kafka consumer

## Adapters de saída

- `PedidoCriadoKafkaProducerAdapter` — Kafka producer
- `PedidoMensagemMemoryAdapter` — memória
- `UuidEventIdGeneratorAdapter` / `SystemClockAdapter` — sistema

## Infrastructure

- `ApplicationBeanConfig` — wiring dos beans
- `KafkaTopicConfig` — criação automática de topics
- `KafkaTopicsProperties` / `ProcessamentoPedidoProperties` — configuração tipada
