import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  List,
  ListItem,
  Typography,
} from "@mui/material";
import { useState } from "react";
import { productsApi } from "../../services/products";
import { useToast } from "../../hooks/useToast";
export function ImportDialog({
  onClose,
  onDone,
}: {
  onClose: () => void;
  onDone: () => void;
}) {
  const [file, setFile] = useState<File>();
  const [preview, setPreview] = useState<string[]>([]);
  const [loading, setLoading] = useState(false);
  const toast = useToast();
  const select = async (candidate?: File) => {
    if (!candidate) return;
    setFile(candidate);
    setPreview(
      (await candidate.text()).split(/\r?\n/).filter(Boolean).slice(0, 6),
    );
  };
  const submit = async () => {
    if (!file) return;
    setLoading(true);
    try {
      const result = await productsApi.importCsv(file);
      toast({
        severity: result.errors.length ? "info" : "success",
        message: `${result.imported} importado(s), ${result.skipped} ignorado(s). ${result.errors.map((e) => e.message ?? e.reason ?? "").join(" ")}`,
      });
      onDone();
      onClose();
    } catch {
      toast({ severity: "error", message: "Falha ao importar CSV." });
    } finally {
      setLoading(false);
    }
  };
  return (
    <Dialog open onClose={onClose} fullWidth>
      <DialogTitle>Importar CSV</DialogTitle>
      <DialogContent>
        <Button component="label" variant="outlined">
          Selecionar arquivo
          <input
            hidden
            type="file"
            accept=".csv,text/csv"
            onChange={(e) => select(e.target.files?.[0])}
          />
        </Button>
        {file && <Typography sx={{ mt: 2 }}>{file.name}</Typography>}
        {preview.length > 0 && (
          <>
            <Typography variant="subtitle2" sx={{ mt: 2 }}>
              Prévia (primeiras linhas)
            </Typography>
            <List dense>
              {preview.map((row, index) => (
                <ListItem key={index}>{row}</ListItem>
              ))}
            </List>
          </>
        )}
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>Cancelar</Button>
        <Button
          variant="contained"
          disabled={!file || loading}
          onClick={submit}
        >
          Confirmar importação
        </Button>
      </DialogActions>
    </Dialog>
  );
}
