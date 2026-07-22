import { Navigate, Route, Routes } from "react-router-dom";
import { Layout } from "./components/Layout";
import { ProductsPage } from "./pages/ProductsPage";
import { CartPage } from "./pages/CartPage";
import { OrdersPage } from "./pages/OrdersPage";
import { OrderDetailPage } from "./pages/OrderDetailPage";
import { LoginPage } from "./pages/LoginPage";
import keycloak from "./services/auth";
function Protected({ children }: { children: React.ReactNode }) {
  return keycloak.authenticated ? (
    <>{children}</>
  ) : (
    <Navigate to="/login" replace />
  );
}
export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route
        element={
          <Protected>
            <Layout />
          </Protected>
        }
      >
        <Route path="/products" element={<ProductsPage />} />
        <Route path="/cart" element={<CartPage />} />
        <Route path="/orders" element={<OrdersPage />} />
        <Route path="/orders/:id" element={<OrderDetailPage />} />
      </Route>
      <Route path="*" element={<Navigate to="/products" replace />} />
    </Routes>
  );
}
