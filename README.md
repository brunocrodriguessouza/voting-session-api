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
- `POST /api/v1/agendas/{id}/sessions?durationMinutes=X` - Abrir sessão (duração opcional, padrão 1 minuto)
- `POST /api/v1/agendas/{id}/votes` - Registrar voto (SIM/NÃO)
- `GET /api/v1/agendas/{id}/result` - Obter resultado da votação

## Documentação

Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## Collection Postman

A collection do Postman com todos os endpoints e fluxo completo está disponível em:
- `Voting-Session-API.postman_collection.json` - Collection com todos os endpoints
- `Voting-Session-API.postman_environment.json` - Ambiente local

A collection inclui:
- Fluxo completo automatizado (criar pauta → abrir sessão → votar → obter resultado → verificar mensagens)
- Endpoints organizados por funcionalidade
- Variáveis automáticas (agendaId é salvo automaticamente)
- Exemplos de requisições prontas para uso

## Bônus implementados

### Bônus 1: Validação de Elegibilidade

Integração com API externa para verificar se o associado pode votar:
- Endpoint: `https://user-info.herokuapp.com/users/{cpf}`
- Retorna `ABLE_TO_VOTE` ou `UNABLE_TO_VOTE`
- CPF inválido retorna 404

### Bônus 2: Publicação de Resultados na Fila

Quando uma sessão de votação fecha, o resultado é automaticamente publicado em uma fila in-memory.

**Como funciona:**
1. Sessão fecha (tempo expira)
2. Ao consultar o resultado via `GET /api/v1/agendas/{id}/result`, se a sessão estiver `CLOSED`, o resultado é publicado na fila
3. Cada resultado publicado contém: agendaId, status da sessão, contagem de votos (SIM/NÃO) e resultado final

**Endpoints para visualizar:**
- `GET /api/v1/messaging/messages` - Lista todas as mensagens publicadas na fila
- `GET /api/v1/messaging/queue-info` - Informações sobre a fila (quantidade de mensagens)

**Exemplo de uso:**
```bash
# 1. Criar pauta, abrir sessão, votar
# 2. Aguardar sessão fechar
# 3. Consultar resultado (publica automaticamente)
GET /api/v1/agendas/{id}/result

# 4. Ver mensagens publicadas
GET /api/v1/messaging/messages
```

A fila mantém até 1000 mensagens e persiste enquanto a aplicação estiver rodando.

## Testes

```bash
mvn test
```

Cobertura de testes unitários para serviços, regras de negócio e adapters.

## Estrutura

```
src/main/java/br/com/bank/voting/
├── domain/           # Modelos e regras de negócio
├── application/      # Casos de uso e serviços
└── adapters/        # Controllers, JPA, integrações externas
```

## Configuração

Propriedades principais em `application.properties`:
- H2 console: `http://localhost:8080/h2-console` (user: sa, sem senha)
- URL API externa: `voting.external.user-info.url`
- Mensageria: `voting.messaging.enabled=true`

