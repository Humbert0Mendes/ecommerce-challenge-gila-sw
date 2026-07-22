import { Dialog, DialogContent, DialogTitle } from "@mui/material";
import { CsvImportPanel } from "./CsvImportPanel";

export function ImportDialog({
  onClose,
  onDone,
}: {
  onClose: () => void;
  onDone: () => void;
}) {
  return (
    <Dialog open onClose={onClose} fullWidth maxWidth="sm">
      <DialogTitle>Import Products from CSV</DialogTitle>
      <DialogContent>
        <div className="pt-2">
          <CsvImportPanel onDone={onDone} onCancel={onClose} />
        </div>
      </DialogContent>
    </Dialog>
  );
}
