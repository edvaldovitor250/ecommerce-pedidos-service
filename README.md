# Ecommerce Pedidos Service

Microservico de emissao de pedidos do e-commerce construido com **Spring Boot e Apache Kafka**, utilizando arquitetura hexagonal, SOLID e Clean Code, com autenticacao via **OpenID Connect (OIDC)** e **OAuth 2.0**.

## Fluxo principal

```text
POST /pedidos
  -> REST Adapter (API REST)
  -> Use Case (criar pedido)
  -> Dominio (regras de negocio)
  -> Porta de saida
  -> Kafka Producer Adapter
  -> Topic pedido-criado
  -> Kafka Consumer Adapter
  -> Use Case de processamento
  -> Persistencia em memoria (ou banco real)
```

## Fluxo de autenticacao

```text
Usuario acessa /login
  -> Tela de login (Thymeleaf)
  -> Credenciais locais OU OAuth2/OIDC (Keycloak)
  -> Spring Security + OAuth2 Client
  -> Redirect para Keycloak (OpenID Connect)
  -> Keycloak autentica e retorna tokens
  -> Spring Security cria sessao
  -> Dashboard com dados do usuario
```

## 1. Tecnologias

- Java 17
- Spring Boot 3.3.5
- Spring Security
- Spring OAuth2 Client (OpenID Connect / OAuth 2.0)
- Spring Thymeleaf
- Spring Kafka
- Spring Web
- Spring Validation
- Spring Actuator
- Apache Kafka em Docker
- Keycloak (Identity Provider / OIDC)
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

### Kafka + Keycloak + aplicacao (Docker Compose)

```bash
docker compose --profile app up --build
```

### Apenas Kafka + Keycloak

```bash
docker compose up -d kafka keycloak
```

### Apenas Kafka

```bash
docker compose up -d kafka
```

### Aplicacao local (com Kafka e Keycloak via Docker)

```bash
mvn spring-boot:run
```

A aplicacao fica disponivel em:

```text
http://localhost:8080
```

## 4. Configurar Keycloak (Identity Provider)

### 4.1 Acessar o Keycloak Admin

```text
URL: http://localhost:8180
Admin User: admin
Admin Password: admin
```

### 4.2 Criar o Realm

1. Acesse o Keycloak Admin Console
2. Clique em **Create Realm**
3. Nome: `ecommerce`
4. Clique em **Create**

### 4.3 Criar o Client (OAuth2 / OIDC)

1. No realm `ecommerce`, va em **Clients** > **Create client**
2. Client type: `OpenID Connect`
3. Client ID: `ecommerce-pedidos`
4. Clique em **Next**
5. Client authentication: `On`
6. Authentication flow: `Standard flow` + `Direct access grants`
7. Valid redirect URIs: `http://localhost:8080/login/oauth2/code/keycloak`
8. Web origins: `http://localhost:8080`
9. Clique em **Save**

### 4.4 Copiar o Client Secret

1. Va na aba **Credentials** do client
2. Copie o **Client Secret**
3. Atualize a variavel de ambiente ou o `application.yml`:

```yaml
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_KEYCLOAK_CLIENT_SECRET: <seu-client-secret>
```

### 4.5 Criar Usuario

1. Va em **Users** > **Add user**
2. Preencha os dados
3. Na aba **Credentials**, defina a senha
4. Clique em **Create**

## 5. Endpoints

| Metodo | Endpoint | Descricao | Auth |
|---|---|---|---|
| GET | `/` | Redirect para login | Nao |
| GET | `/login` | Tela de login | Nao |
| GET | `/dashboard` | Painel do usuario | OAuth2/OIDC |
| POST | `/logout` | Encerrar sessao | Sim |
| POST | `/pedidos` | Emitir pedido no Kafka | OAuth2/OIDC |
| GET | `/pedidos/recebidos` | Listar pedidos processados | OAuth2/OIDC |
| GET | `/pedidos/dlt` | Listar pedidos enviados para DLT | OAuth2/OIDC |
| DELETE | `/pedidos/memoria` | Limpar registros em memoria | OAuth2/OIDC |
| GET | `/actuator/health` | Health check | Nao |
| GET | `/actuator/metrics` | Metricas Prometheus | Nao |

## 6. Como emitir um pedido

### Com autenticacao (via dashboard)

Acesse o dashboard em `http://localhost:8080/dashboard`, faca login e use as acoes rapidas.

### Via API (com token de acesso)

```bash
# Obter token de acesso
TOKEN=$(curl -s -X POST http://localhost:8180/realms/ecommerce/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=client_credentials" \
  -d "client_id=ecommerce-pedidos" \
  -d "client_secret=<seu-client-secret>" | jq -r '.access_token')

# Emitir pedido
curl -X POST http://localhost:8080/pedidos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
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

## 7. Testar retry e DLT

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

## 8. Script de teste rapido

```bash
chmod +x testar.sh
./testar.sh
```

## 9. Estrutura do projeto

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
│   │       ├── WebController.java
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
    │   ├── ApplicationBeanConfig.java
    │   ├── PropertiesConfig.java
    │   └── SecurityConfig.java
    ├── kafka/
    │   └── KafkaTopicConfig.java
    └── properties/
        ├── KafkaTopicsProperties.java
        └── ProcessamentoPedidoProperties.java
```

```text
src/main/resources/
├── application.yml
├── static/
│   └── css/
│       └── style.css
└── templates/
    ├── login.html
    ├── dashboard.html
    └── error/
        └── 403.html
```

## 10. Arquitetura hexagonal

O centro da aplicacao e o **dominio** e a **camada de aplicacao**. Eles nao dependem de Kafka, REST, banco de dados ou qualquer detalhe tecnico.

**Adapters de entrada**: recebem estimulos externos e chamam portas de entrada.
- REST Controller (`PedidoController`)
- Web Controller (`WebController`) - Thymeleaf pages
- Kafka Consumer (`PedidoCriadoKafkaConsumerAdapter`)

**Adapters de saida**: implementam portas de saida com tecnologia concreta.
- Kafka Producer (`PedidoCriadoKafkaProducerAdapter`)
- Memoria (`PedidoMensagemMemoryAdapter`)
- UUID e relogio do sistema

**Infraestrutura**: configura beans, propriedades e recursos tecnicos.
- `SecurityConfig` - Configuracao de seguranca OAuth2/OIDC
- `ApplicationBeanConfig` - Beans da aplicacao
- `PropertiesConfig` - Propriedades customizadas

## 11. Seguranca (OAuth 2.0 / OpenID Connect)

### Configuracao

A autenticacao e configurada via `SecurityConfig.java` utilizando:

- **Spring Security** - Framework de seguranca do Spring
- **Spring OAuth2 Client** - Cliente OAuth2/OIDC
- **Keycloak** - Identity Provider (OpenID Connect)

### Fluxo de autenticacao

1. Usuario acessa `/` ou `/dashboard`
2. Spring Security redireciona para `/login`
3. Usuario pode fazer login com:
   - **Credenciais locais** (form-based login)
   - **Keycloak** (OpenID Connect / OAuth 2.0)
4. Keycloak autentica e retorna tokens (ID Token + Access Token)
5. Spring Security cria sessao e redireciona para `/dashboard`
6. Dashboard exibe informacoes do usuario (nome, email, roles)

### Protecao de endpoints

- `/login`, `/css/**`, `/js/**`, `/images/**`, `/error` - Publico
- `/pedidos/**` - Requer autenticacao OAuth2/OIDC
- `/dashboard` - Requer autenticacao OAuth2/OIDC
- `/actuator/health`, `/actuator/metrics` - Publico

### Roles (Keycloak)

O sistema extrai roles do Keycloak via `realm_access.roles` e adiciona como `ROLE_` prefixado.

## 12. SOLID

- **S** - Cada classe tem uma unica responsabilidade.
- **O** - O processamento aceita novas regras sem alterar o use case (`PedidoCriadoProcessingRule`).
- **L** - Portas permitem substituir implementacoes (Kafka -> RabbitMQ -> SQS).
- **I** - Portas pequenas e especificas, sem interfaces gigantes.
- **D** - Casos de uso dependem de abstracoes, nao de classes concretas.

## 13. CI

```text
.github/workflows/ci.yml
```

Roda em PRs para `main` e pushes em `main`/`develop`.

Etapas: checkout -> Java 17 -> mvn clean verify -> JaCoCo report -> build Docker image.

## 14. CD

```text
.github/workflows/cd.yml
```

Roda em pushes na `main` e tags `v*.*.*`.

Etapas: checkout -> Java 17 -> verify -> login GHCR -> build + push Docker image.

Tags geradas: `main`, `v1.0.0`, `sha-<commit>`.

## 15. Docker

```bash
docker build -t ecommerce-pedidos-service:local .
```

```bash
docker run --rm -p 8080:8080 \
  -e SPRING_KAFKA_BOOTSTRAP_SERVERS=host.docker.internal:9092 \
  -e SPRING_SECURITY_OAUTH2_CLIENT_PROVIDER_KEYCLOAK_ISSUER_URI=http://host.docker.internal:8180/realms/ecommerce \
  -e SPRING_SECURITY_OAUTH2_CLIENT_PROVIDER_KEYCLOAK_JWK_SET_URI=http://host.docker.internal:8180/realms/ecommerce/protocol/openid-connect/certs \
  ecommerce-pedidos-service:local
```

## 16. Configuracao

Variaveis de ambiente suportadas:

### Kafka

| Variavel | Default |
|---|---|
| `SPRING_KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092` |
| `SPRING_KAFKA_CONSUMER_GROUP_ID` | `pedido-service-group` |
| `APP_KAFKA_TOPIC_PEDIDO_CRIADO` | `pedido-criado` |
| `APP_PROCESSING_FAIL_WHEN_CLIENTE_ID` | `999` |

### OAuth2 / OIDC (Keycloak)

| Variavel | Default |
|---|---|
| `SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_KEYCLOAK_CLIENT-ID` | `ecommerce-pedidos` |
| `SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_KEYCLOAK_CLIENT_SECRET` | `ZKmLqLhZzHqQZkXwZvFmJxRnTyBcDeFg` |
| `SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_KEYCLOAK_CLIENT-NAME` | `Keycloak` |
| `SPRING_SECURITY_OAUTH2_CLIENT_PROVIDER_KEYCLOAK_ISSUER_URI` | `http://localhost:8180/realms/ecommerce` |
| `SPRING_SECURITY_OAUTH2_CLIENT_PROVIDER_KEYCLOAK_JWK_SET_URI` | `http://localhost:8180/realms/ecommerce/protocol/openid-connect/certs` |

## 17. Rodar testes

```bash
mvn test
```

```bash
mvn clean verify
```

## 18. Parar tudo

```bash
docker compose down -v
```

## 19. URLs importantes

| Servico | URL |
|---|---|
| Aplicacao | `http://localhost:8080` |
| Login | `http://localhost:8080/login` |
| Dashboard | `http://localhost:8080/dashboard` |
| Keycloak Admin | `http://localhost:8180` |
| Kafka | `localhost:9092` |
| Health Check | `http://localhost:8080/actuator/health` |
| Metrics | `http://localhost:8080/actuator/prometheus` |
