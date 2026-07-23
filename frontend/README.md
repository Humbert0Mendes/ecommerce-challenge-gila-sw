# Product Order Frontend

A React single-page application for a B2B product catalog, CSV imports, shopping cart, and order management.

## Technology Stack

- React 19 and TypeScript
- Vite
- React Router v6
- TanStack Query
- Axios
- Keycloak JS
- Material UI icons and foundational components
- Tailwind CSS and daisyUI for responsive UI composition
- React Hook Form and Zod
- Vitest and React Testing Library

## Prerequisites

- Node.js 20 or newer
- The backend, Keycloak, and supporting services running locally

## Getting Started

1. Start the services from the repository root:

   ```bash
   docker compose up -d
   ```

2. In this directory, copy `.env.example` to `.env` and adjust values if required.

3. Install dependencies and start the development server:

   ```bash
   npm.cmd install
   npm.cmd run dev
   ```

4. Open `http://localhost:5173`.

For the local environment, sign in with `frontend-user` / `frontend-password`.

## Environment Variables

| Variable | Description | Default |
| --- | --- | --- |
| `VITE_API_URL` | Product Order API base URL | `http://localhost:8080` |
| `VITE_KEYCLOAK_URL` | Keycloak server URL | `http://localhost:8081` |
| `VITE_KEYCLOAK_REALM` | Keycloak realm | `product-order` |
| `VITE_KEYCLOAK_CLIENT_ID` | Public SPA client ID | `product-order-web` |

The SPA uses Authorization Code Flow with PKCE. A client secret is never stored in the frontend.

## Available Scripts

| Command | Description |
| --- | --- |
| `npm.cmd run dev` | Starts the Vite development server. |
| `npm.cmd run build` | Type-checks and creates a production build. |
| `npm.cmd run test` | Runs the Vitest test suite. |
| `npm.cmd run test:watch` | Runs tests in watch mode. |
| `npm.cmd run coverage` | Generates test coverage. |

## Architecture Decisions and Trade-offs

### Remote state: TanStack Query

**Decision:** Backend resources such as products, categories, and orders are managed through TanStack Query.

**Why:** It provides caching, loading and error states, request deduplication, refetching, and invalidation after mutations. Components declare the data they need instead of manually coordinating request lifecycles.

**Trade-off:** It adds an abstraction and a dependency compared with `useEffect` plus Axios. It is worthwhile here because the application has pagination, filters, mutations, and multiple screens consuming API data.

### Local and shared state: `useState` and Context API

**Decision:** Local visual state remains in `useState`; the shopping cart uses a small Context API provider backed by `localStorage`.

**Why:** The cart is the only cross-page client-side state that needs shared access. This keeps the state model simple and avoids a global store for short-lived UI concerns.

**Trade-off:** Context is not ideal for many frequently changing, unrelated global domains. Redux or another state-management solution could be introduced if global workflows, offline synchronization, or cross-domain client state become significantly more complex.

### Side effects: `useEffect` only for browser synchronization

**Decision:** `useEffect` is reserved for genuine side effects, such as synchronizing React state with the native HTML `dialog` API.

**Why:** Data fetching is remote-state management and is delegated to TanStack Query. This prevents manually duplicating loading, error, cache, and refresh logic.

**Trade-off:** Developers must understand where each type of state belongs. The split is intentional: browser synchronization uses React effects; server data uses query and mutation hooks.

### UI system: Material UI, Tailwind CSS, and daisyUI

**Decision:** Material UI remains available for established foundational components and icons, while Tailwind CSS and daisyUI are used for responsive layout and reusable visual patterns.

**Why:** This supports incremental modernization without replacing working components, while daisyUI provides consistent semantic components such as buttons, cards, tables, alerts, and modals.

**Trade-off:** Using two UI systems requires discipline to maintain consistent spacing, colors, and interaction patterns. New visual components should prioritize Tailwind and daisyUI where appropriate, while existing Material UI components are reused when replacing them brings no clear benefit.

### Authentication: Keycloak and bearer tokens

**Decision:** Authentication is handled by Keycloak JS, and Axios attaches a refreshed bearer token to API requests.

**Why:** The frontend delegates identity management to an OpenID Connect provider and does not handle user passwords or client secrets. The backend remains the authority for token validation and authorization.

**Trade-off:** The application depends on Keycloak availability and correct local realm/client configuration. Token refresh behavior must also be tested in environments with different session policies.

### API boundaries and business rules

**Decision:** The frontend sends intent and displays results; prices, inventory validation, authorization, idempotency, CSV validation, and order calculations remain in the backend.

**Why:** The browser is not a trusted environment. Keeping critical rules in the API ensures consistency for every client and prevents client-side manipulation from becoming a business-rule bypass.

**Trade-off:** Some validations may require a round trip to the API before the user receives definitive feedback. The UI mitigates this with clear loading, success, error, and empty states.

### Filtering and pagination

**Decision:** Product category filters and order search parameters are sent to the backend together with pagination.

**Why:** Paginated client-side filtering would only search loaded records and could produce incomplete results. Backend filtering scales to the complete catalog and reduces network transfer.

**Trade-off:** Filtering depends on API availability and adds request latency. Search requests are debounced to reduce unnecessary calls while users type.

### Order idempotency

**Decision:** Each order submission includes an `Idempotency-Key` generated with `crypto.randomUUID()`.

**Why:** The backend can safely recognize a repeated submission and avoid duplicate orders caused by retries, refreshes, or unreliable networks.

**Trade-off:** Correct behavior depends on server-side idempotency storage and retention rules. The frontend alone cannot guarantee deduplication.

## Quality Checks

Run the following commands before submitting changes:

```bash
npm.cmd run test
npm.cmd run build
```

The project currently has no lint script configured. For a production UX audit, create a production build and run Lighthouse against the served application.
