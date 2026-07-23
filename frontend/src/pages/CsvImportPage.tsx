import { Typography } from "@mui/material";
import { useNavigate } from "react-router-dom";
import { useQueryClient } from "@tanstack/react-query";
import { CsvImportPanel } from "../features/products/CsvImportPanel";
import { invalidateProductQueries } from "../services/productQueries";
export function CsvImportPage() { const navigate = useNavigate(); const queryClient = useQueryClient(); const onImportDone = async () => { await invalidateProductQueries(queryClient); navigate("/products"); }; return <div className="mx-auto max-w-2xl"><Typography variant="h4">Import CSV</Typography><Typography color="text.secondary" sx={{ mt: 0.5, mb: 3 }}>Upload products in bulk and review each validation result.</Typography><div className="rounded-xl bg-white p-6 shadow-sm ring-1 ring-slate-200"><CsvImportPanel onDone={() => void onImportDone()} onCancel={() => navigate("/products")} /></div></div>; }
