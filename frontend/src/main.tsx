import { CssBaseline, ThemeProvider, createTheme } from "@mui/material";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { BrowserRouter } from "react-router-dom";
import App from "./App";
import { ErrorBoundary } from "./components/ErrorBoundary";
import { CartProvider } from "./features/cart/CartContext";
import { ToastProvider } from "./hooks/useToast";
import { initializeAuth } from "./services/auth";
const client = new QueryClient({
  defaultOptions: { queries: { retry: 2, staleTime: 30_000 } },
});
const render = () =>
  createRoot(document.getElementById("root")!).render(
    <StrictMode>
      <ThemeProvider
        theme={createTheme({ palette: { primary: { main: "#14532d" } } })}
      >
        <CssBaseline />
        <ErrorBoundary>
          <QueryClientProvider client={client}>
            <ToastProvider>
              <CartProvider>
                <BrowserRouter>
                  <App />
                </BrowserRouter>
              </CartProvider>
            </ToastProvider>
          </QueryClientProvider>
        </ErrorBoundary>
      </ThemeProvider>
    </StrictMode>,
  );
const timeout = new Promise<never>((_, reject) =>
  window.setTimeout(
    () => reject(new Error("Keycloak não respondeu a tempo")),
    8_000,
  ),
);
Promise.race([initializeAuth(), timeout])
  .catch((error) => console.error("Falha ao inicializar autenticação:", error))
  .finally(render);
