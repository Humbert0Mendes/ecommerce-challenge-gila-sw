# Product Order API

REST backend for managing products and orders, developed with Java 21, Spring Boot, PostgreSQL, and Flyway.

## Prerequisites

- Java 21
- Docker Desktop running

## Run Locally

```powershell
docker compose up -d
mvn spring-boot:run
```

The API starts at `http://localhost:8080`. The Swagger UI is available at `http://localhost:8080/swagger-ui.html`.

## Authentication

Business endpoints require a Bearer JWT. Configure `JWT_ISSUER_URI`, `JWT_JWK_SET_URI`, and `JWT_AUDIENCE` for the identity provider used in your environment.

The health endpoint (`/actuator/health`) is publicly available.

## Payment and idempotency

`POST /api/v1/orders` requires `Idempotency-Key`. The key is linked to the JWT subject, and when the request is repeated with the same payload, it returns the same order without reserving inventory or processing the payment again. Reusing the key with a different payload returns `409 Conflict`.

The payment is processed through a fake payment gateway. By default, it is approved; set `FAKE_PAYMENT_DECLINE=true` to simulate a decline and validate the restocking with the `DECLINED` status.

## Local JWT (Keycloak)

Running `docker compose up -d` also starts Keycloak at `http://localhost:8081` and automatically imports the `product-order` realm. The local `product-order-api` client uses a service account and already includes the audience required by the API.

To obtain a development JWT, import [the Postman collection](postman/Product-Order-API.postman_collection.json) and run the `Authentication > Generate JWT` request. The request stores the returned access token in the `jwt` collection variable, which is automatically used by the protected API requests.

The `admin/admin` credentials and the client secret included in the collection are for local development only.

## Tests

```powershell
mvn test
```

Integration tests use Testcontainers, follow the `*IT` convention, and require a running Docker Engine:

```powershell
mvn verify -Pintegration
```

## Routes

- `POST`, `GET`, `GET /{id}`, `PUT`, and `DELETE` for `/api/v1/products`
- `GET /api/v1/products` accepts the optional filters `name`, `sku`, `category`, `minPrice`, `maxPrice`, `minWeight`, and `maxWeight`
- `POST /api/v1/products/import` (`multipart/form-data`, `file` part)
- `POST`, `GET`, and `GET /{id}` for `/api/v1/orders` (`POST` requires `Idempotency-Key`)

The example CSV file provided for the challenge was downloaded on July 18, 2026.
