import { Button, Paper, Typography } from "@mui/material";
import { login } from "../services/auth";
export function LoginPage() { return <Paper sx={{ p: 4, textAlign: "center" }}><Typography variant="h4">B2B Commerce</Typography><Typography sx={{ my: 2 }}>Sign in to browse the catalog and place orders.</Typography><Button variant="contained" onClick={login}>Sign in with Keycloak</Button></Paper>; }
