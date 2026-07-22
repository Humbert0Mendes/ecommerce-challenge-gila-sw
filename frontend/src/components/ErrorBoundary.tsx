import { Button, Paper, Typography } from "@mui/material";
import { Component, type ErrorInfo, type ReactNode } from "react";
export class ErrorBoundary extends Component<
  { children: ReactNode },
  { error: boolean }
> {
  state = { error: false };
  static getDerivedStateFromError() {
    return { error: true };
  }
  componentDidCatch(_error: Error, _info: ErrorInfo) {}
  render() {
    return this.state.error ? (
      <Paper sx={{ p: 4, m: 4 }}>
        <Typography variant="h5">
          Não foi possível carregar esta tela.
        </Typography>
        <Button onClick={() => this.setState({ error: false })}>
          Tentar novamente
        </Button>
      </Paper>
    ) : (
      this.props.children
    );
  }
}
