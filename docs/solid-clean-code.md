# SOLID e Clean Code

## Single Responsibility

Cada classe tem um único motivo para mudar. `PedidoController` muda com a API, `CriarPedidoService` com o caso de uso, `PedidoCriadoKafkaProducerAdapter` com a publicação Kafka.

## Open/Closed

`ProcessarPedidoCriadoService` aceita novas regras via `List<PedidoCriadoProcessingRule>` sem alteração no service.

## Liskov Substitution

As portas permitem substituir implementações sem quebrar o caso de uso (ex: Kafka → RabbitMQ → SQS).

## Interface Segregation

Portas pequenas e específicas: `RegistrarPedidoRecebidoPort`, `RegistrarPedidoDltPort`, `ConsultarPedidoMensagensPort`, `LimparPedidoMensagensPort`.

## Dependency Inversion

Casos de uso dependem de abstrações (`EventIdGeneratorPort`, `RelogioPort`, `PublicarPedidoCriadoPort`), não de `UUID.randomUUID()`, `Instant.now()` ou `KafkaTemplate`.

## Clean Code

Nomes explícitos, classes pequenas, baixo acoplamento, DTOs isolados na borda web, records imutáveis, Bean Validation, tratamento centralizado de exceções, configuração tipada.
