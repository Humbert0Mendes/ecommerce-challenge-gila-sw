# Product Order API

Backend REST para produtos e pedidos, desenvolvido com Java 21, Spring Boot, PostgreSQL e Flyway.

## Pré-requisitos

- Java 21
- Docker Desktop em execução

## Executar localmente

```powershell
docker compose up -d
mvn spring-boot:run
```

A API inicia em `http://localhost:8080`; o Swagger UI fica em `http://localhost:8080/swagger-ui.html`.

## Autenticação

Os endpoints de negócio exigem Bearer JWT. Configure `JWT_ISSUER_URI`, `JWT_JWK_SET_URI` e `JWT_AUDIENCE` para o provedor de identidade usado no ambiente. O endpoint de saúde (`/actuator/health`) e a documentação OpenAPI são públicos.

## Testes

```powershell
mvn test
```

Os testes de integração com Testcontainers seguem a convenção `*IT` e devem ser executados em uma sessão com acesso ao Docker Engine:

```powershell
mvn verify -Pintegration
```

## Rotas

- `POST`, `GET`, `GET /{id}`, `PUT` e `DELETE /api/v1/products`
- `GET /api/v1/products/search?q=`
- `POST /api/v1/products/import` (`multipart/form-data`, campo `file`)
- `POST`, `GET` e `GET /{id} /api/v1/orders`
