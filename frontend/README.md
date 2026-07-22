# Product Order Frontend

SPA React para o catálogo B2B e pedidos. Usa React + TypeScript + Vite, Material UI, React Router v6, TanStack Query, React Hook Form/Zod, Axios e Keycloak JS.

## Executar

1. Inicie os serviços na raiz: `docker compose up -d`.
2. Copie `.env.example` para `.env` e ajuste se necessário.
3. Execute `npm.cmd install` e `npm.cmd run dev` dentro desta pasta.
4. Acesse `http://localhost:5173` e entre com `frontend-user` / `frontend-password`.

O realm local inclui o cliente público `product-order-web`, com Authorization Code + PKCE e audience da API. Não há `client_secret` no frontend.

## Decisões técnicas

- O token é renovado por `keycloak.updateToken` antes de cada chamada; Axios faz retry exponencial de falhas 5xx.
- Estado remoto fica no TanStack Query; o carrinho é Context mínimo e `localStorage`.
- O pedido gera `crypto.randomUUID()` a cada tentativa, enviado como `Idempotency-Key`.
- A importação mostra uma prévia local antes do envio e exibe o retorno de linhas rejeitadas.
- O app oferece estados de carregamento/vazio/erro, boundary global e feedback por Snackbar.

## Qualidade

`npm.cmd run test` executa Vitest/RTL. `npm.cmd run coverage` gera cobertura. Para auditoria final, execute Lighthouse em produção (`npm.cmd run build && npm.cmd run dev -- --host`).
