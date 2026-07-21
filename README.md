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

## Pagamentos e idempotÃªncia

`POST /api/v1/orders` exige o header `Idempotency-Key`. A chave Ã© vinculada ao sujeito do JWT e, em repetiÃ§Ãµes com o mesmo payload, retorna o mesmo pedido sem reservar estoque ou processar o pagamento novamente. Reutilizar a chave com outro payload retorna `409 Conflict`.

O pagamento Ã© processado por um gateway fake local. Ele aprova por padrÃ£o; defina `FAKE_PAYMENT_DECLINE=true` para simular uma recusa e validar a devoluÃ§Ã£o do estoque e o status `DECLINED`.

## JWT local (Keycloak)

O `docker compose up -d` tambem inicia o Keycloak em `http://localhost:8081` e importa automaticamente o realm `product-order`. O client local `product-order-api` usa uma Service Account e ja inclui a audiencia exigida pela API.

Para obter um JWT de desenvolvimento:

```powershell
$token = Invoke-RestMethod `
  -Method Post `
  -Uri "http://localhost:8081/realms/product-order/protocol/openid-connect/token" `
  -ContentType "application/x-www-form-urlencoded" `
  -Body @{
    grant_type = "client_credentials"
    client_id = "product-order-api"
    client_secret = "product-order-api-local-secret"
  }

$token.access_token
```

Use o valor retornado no header `Authorization: Bearer <token>`. As credenciais `admin/admin` e o client secret acima existem apenas para desenvolvimento local.

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
- `GET /api/v1/products` aceita os filtros opcionais `name`, `sku`, `category`, `minPrice`, `maxPrice`, `minWeight` e `maxWeight`
- `POST /api/v1/products/import` (`multipart/form-data`, campo `file`)
- `POST`, `GET` e `GET /{id} /api/v1/orders`
