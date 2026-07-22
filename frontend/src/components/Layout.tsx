import {
  AppBar,
  Badge,
  Box,
  Button,
  Container,
  Toolbar,
  Typography,
} from "@mui/material";
import { Link, Outlet } from "react-router-dom";
import ShoppingCartIcon from "@mui/icons-material/ShoppingCart";
import keycloak, { login } from "../services/auth";
import { useCart } from "../features/cart/CartContext";
export function Layout() {
  const { items } = useCart();
  return (
    <>
      <AppBar position="static">
        <Toolbar>
          <Typography variant="h6" sx={{ flexGrow: 1 }}>
            B2B Commerce
          </Typography>
          <Button color="inherit" component={Link} to="/products">
            Produtos
          </Button>
          <Button color="inherit" component={Link} to="/orders">
            Pedidos
          </Button>
          <Button color="inherit" component={Link} to="/cart">
            <Badge badgeContent={items.length} color="secondary">
              <ShoppingCartIcon />
            </Badge>
          </Button>
          {keycloak.authenticated ? (
            <Button
              color="inherit"
              onClick={() =>
                keycloak.logout({ redirectUri: window.location.origin })
              }
            >
              Sair
            </Button>
          ) : (
            <Button color="inherit" onClick={login}>
              Entrar
            </Button>
          )}
        </Toolbar>
      </AppBar>
      <Container component="main" sx={{ py: 3 }}>
        <Outlet />
      </Container>
    </>
  );
}
