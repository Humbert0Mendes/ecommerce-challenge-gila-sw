import { Button, Paper, Typography } from "@mui/material";
import { login } from "../services/auth";
export function LoginPage() {
  return (
    <Paper sx={{ p: 4, textAlign: "center" }}>
      <Typography variant="h4">B2B Commerce</Typography>
      <Typography sx={{ my: 2 }}>
        Entre para consultar o catálogo e realizar pedidos.
      </Typography>
      <Button variant="contained" onClick={login}>
        Entrar com Keycloak
      </Button>
    </Paper>
  );
}
