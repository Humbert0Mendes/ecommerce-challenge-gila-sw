import { Alert, Snackbar } from "@mui/material";
import { createContext, useContext, useState } from "react";
type Toast = { message: string; severity: "success" | "error" | "info" };
const ToastContext = createContext<(t: Toast) => void>(() => undefined);
export function ToastProvider({ children }: { children: React.ReactNode }) {
  const [toast, setToast] = useState<Toast | null>(null);
  return (
    <ToastContext.Provider value={setToast}>
      {children}
      <Snackbar
        open={!!toast}
        autoHideDuration={4000}
        onClose={() => setToast(null)}
      >
        <Alert severity={toast?.severity}>{toast?.message}</Alert>
      </Snackbar>
    </ToastContext.Provider>
  );
}
export const useToast = () => useContext(ToastContext);
