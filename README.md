# Voting Session API

API REST para gerenciamento de sessões de votação em assembleias cooperativas.

## Tecnologias

- Java 21
- Spring Boot 4.0.2
- H2 Database (in-memory)
- Maven
- Arquitetura Hexagonal

## Como rodar

```bash
mvn spring-boot:run
```

A aplicação sobe em `http://localhost:8080`

## Endpoints principais

- `POST /api/v1/agendas` - Criar pauta
- `POST /api/v1/agendas/{id}/sessions` - Abrir sessão (duração opcional, padrão 1 min)
- `POST /api/v1/agendas/{id}/votes` - Registrar voto
- `GET /api/v1/agendas/{id}/result` - Obter resultado

## Documentação

Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## Bônus implementados

**Bônus 1:** Integração com API externa para validação de elegibilidade do eleitor (`https://user-info.herokuapp.com/users/{cpf}`)

**Bônus 2:** Fila in-memory para publicação de resultados quando sessão fecha. Endpoints:
- `GET /api/v1/messaging/messages` - Listar mensagens publicadas
- `GET /api/v1/messaging/queue-info` - Informações da fila

## Testes

```bash
mvn test
```

## Estrutura

```
src/main/java/br/com/bank/voting/
├── domain/           # Modelos e regras de negócio
├── application/      # Casos de uso e serviços
└── adapters/        # Controllers, JPA, integrações externas
```

## Configuração

Propriedades principais em `application.properties`:
- H2 console: `http://localhost:8080/h2-console`
- URL API externa: `voting.external.user-info.url`
- Mensageria: `voting.messaging.enabled`

