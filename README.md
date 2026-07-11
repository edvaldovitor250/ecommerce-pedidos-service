# Ecommerce Pedidos Service

Microserviço de emissão de pedidos do e-commerce construído com **Spring Boot e Apache Kafka**, utilizando arquitetura hexagonal, SOLID e Clean Code.

## Fluxo principal

```text
POST /pedidos
  -> REST Adapter (API REST)
  -> Use Case (criar pedido)
  -> Domínio (regras de negócio)
  -> Porta de saída
  -> Kafka Producer Adapter
  -> Topic pedido-criado
  -> Kafka Consumer Adapter
  -> Use Case de processamento
  -> Persistência em memória (ou banco real)
```

## 1. Tecnologias

- Java 17
- Spring Boot 3.3.5
- Spring Kafka
- Spring Web
- Spring Validation
- Spring Actuator
- Apache Kafka em Docker
- Maven
- JUnit 5
- Checkstyle
- JaCoCo
- Docker / Docker Compose
- GitHub Actions (CI/CD)
- GitHub Container Registry

## 2. Requisitos

```text
Java 17+
Maven 3.9+
Docker
Docker Compose
```

## 3. Como rodar

### Kafka + aplicação (Docker Compose)

```bash
docker compose --profile app up --build
```

### Apenas Kafka

```bash
docker compose up -d kafka
```

### Aplicação local (com Kafka via Docker)

```bash
mvn spring-boot:run
```

A aplicação fica disponível em:

```text
http://localhost:8080
```

## 4. Endpoints

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/pedidos` | Emitir pedido no Kafka |
| GET | `/pedidos/recebidos` | Listar pedidos processados pelo consumer |
| GET | `/pedidos/dlt` | Listar pedidos enviados para DLT |
| DELETE | `/pedidos/memoria` | Limpar registros em memória |
| GET | `/actuator/health` | Health check |
| GET | `/actuator/metrics` | Métricas Prometheus |

## 5. Como emitir um pedido

```bash
curl -X POST http://localhost:8080/pedidos \
  -H "Content-Type: application/json" \
  -d '{
    "pedidoId": 123,
    "clienteId": 10,
    "valor": 250.00
  }'
```

Resposta:

```json
{
  "message": "Pedido publicado no Kafka",
  "eventId": "uuid-gerado",
  "topic": "pedido-criado",
  "partition": 0,
  "offset": 0
}
```

## 6. Testar retry e DLT

O projeto simula falha quando `clienteId = 999`.

```bash
curl -X POST http://localhost:8080/pedidos \
  -H "Content-Type: application/json" \
  -d '{
    "pedidoId": 456,
    "clienteId": 999,
    "valor": 150.00
  }'
```

Depois de ~10 segundos, consulte a DLT:

```bash
curl http://localhost:8080/pedidos/dlt
```

## 7. Script de teste rápido

```bash
chmod +x testar.sh
./testar.sh
```

## 8. Estrutura do projeto

```text
src/main/java/com/example/ecommerce/pedidos
├── EcommercePedidosApplication.java
├── domain
│   ├── event
│   │   └── PedidoCriadoEvent.java
│   └── model
│       ├── DltMessage.java
│       └── Pedido.java
├── application
│   ├── exception
│   │   └── MensagemPublicacaoException.java
│   ├── policy
│   │   └── PedidoCriadoProcessingRule.java
│   ├── port
│   │   ├── in
│   │   │   ├── ConsultarMensagensUseCase.java
│   │   │   ├── CriarPedidoCommand.java
│   │   │   ├── CriarPedidoUseCase.java
│   │   │   ├── LimparMensagensUseCase.java
│   │   │   ├── PedidoPublicadoResultado.java
│   │   │   └── ProcessarPedidoCriadoUseCase.java
│   │   └── out
│   │       ├── ConsultarPedidoMensagensPort.java
│   │       ├── EventIdGeneratorPort.java
│   │       ├── LimparPedidoMensagensPort.java
│   │       ├── MensagemPublicada.java
│   │       ├── PublicarPedidoCriadoPort.java
│   │       ├── RegistrarPedidoDltPort.java
│   │       ├── RegistrarPedidoRecebidoPort.java
│   │       └── RelogioPort.java
│   └── service
│       ├── ConsultarMensagensService.java
│       ├── CriarPedidoService.java
│       ├── LimparMensagensService.java
│       └── ProcessarPedidoCriadoService.java
├── adapters
│   ├── in
│   │   ├── kafka
│   │   │   └── PedidoCriadoKafkaConsumerAdapter.java
│   │   └── web
│   │       ├── PedidoController.java
│   │       ├── dto/
│   │       └── handler/
│   └── out
│       ├── kafka
│       │   └── PedidoCriadoKafkaProducerAdapter.java
│       ├── memory
│       │   └── PedidoMensagemMemoryAdapter.java
│       └── system
│           ├── SystemClockAdapter.java
│           └── UuidEventIdGeneratorAdapter.java
└── infrastructure
    ├── config/
    ├── kafka/
    │   └── KafkaTopicConfig.java
    └── properties/
        ├── KafkaTopicsProperties.java
        └── ProcessamentoPedidoProperties.java
```

## 9. Arquitetura hexagonal

O centro da aplicação é o **domínio** e a **camada de aplicação**. Eles não dependem de Kafka, REST, banco de dados ou qualquer detalhe técnico.

**Adapters de entrada**: recebem estímulos externos e chamam portas de entrada.
- REST Controller (`PedidoController`)
- Kafka Consumer (`PedidoCriadoKafkaConsumerAdapter`)

**Adapters de saída**: implementam portas de saída com tecnologia concreta.
- Kafka Producer (`PedidoCriadoKafkaProducerAdapter`)
- Memória (`PedidoMensagemMemoryAdapter`)
- UUID e relógio do sistema

**Infraestrutura**: configura beans, propriedades e recursos técnicos.

## 10. SOLID

- **S** — Cada classe tem uma única responsabilidade.
- **O** — O processamento aceita novas regras sem alterar o use case (`PedidoCriadoProcessingRule`).
- **L** — Portas permitem substituir implementações (Kafka → RabbitMQ → SQS).
- **I** — Portas pequenas e específicas, sem interfaces gigantes.
- **D** — Casos de uso dependem de abstrações, não de classes concretas.

## 11. CI

```text
.github/workflows/ci.yml
```

Roda em PRs para `main` e pushes em `main`/`develop`.

Etapas: checkout → Java 17 → mvn clean verify → JaCoCo report → build Docker image.

## 12. CD

```text
.github/workflows/cd.yml
```

Roda em pushes na `main` e tags `v*.*.*`.

Etapas: checkout → Java 17 → verify → login GHCR → build + push Docker image.

Tags geradas: `main`, `v1.0.0`, `sha-<commit>`.

## 13. Docker

```bash
docker build -t ecommerce-pedidos-service:local .
```

```bash
docker run --rm -p 8080:8080 \
  -e SPRING_KAFKA_BOOTSTRAP_SERVERS=host.docker.internal:9092 \
  ecommerce-pedidos-service:local
```

## 14. Configuração

Variáveis de ambiente suportadas:

| Variável | Default |
|---|---|
| `SPRING_KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092` |
| `SPRING_KAFKA_CONSUMER_GROUP_ID` | `pedido-service-group` |
| `APP_KAFKA_TOPIC_PEDIDO_CRIADO` | `pedido-criado` |
| `APP_PROCESSING_FAIL_WHEN_CLIENTE_ID` | `999` |

## 15. Rodar testes

```bash
mvn test
```

```bash
mvn clean verify
```

## 16. Parar tudo

```bash
docker compose down -v
```
