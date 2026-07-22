import { zodResolver } from "@hookform/resolvers/zod";
import { Button, Dialog, DialogActions, DialogContent, DialogTitle, Grid, TextField } from "@mui/material";
import { useEffect } from "react";
import { Controller, useForm } from "react-hook-form";
import { z } from "zod";
import type { Product, ProductInput } from "../../types";
const schema = z.object({ name: z.string().min(1).max(255), sku: z.string().min(1).max(20), description: z.string().min(1), category: z.string().min(1).max(100), price: z.coerce.number().min(0), stock: z.coerce.number().int().min(0), weightKg: z.coerce.number().min(0) });
type Form = z.infer<typeof schema>;
const labels: Record<keyof Form, string> = { name: "Name", sku: "SKU", description: "Description", category: "Category", price: "Price", stock: "Stock", weightKg: "Weight (kg)" };
const emptyProduct: Form = { name: "", sku: "", description: "", category: "", price: 0, stock: 0, weightKg: 0 };
export function ProductForm({ product, onClose, onSave }: { product?: Product; onClose: () => void; onSave: (data: ProductInput) => void }) {
  const { control, handleSubmit, reset, formState: { errors, isSubmitting } } = useForm<Form>({ resolver: zodResolver(schema), defaultValues: product ?? emptyProduct });
  useEffect(() => reset(product ?? emptyProduct), [product, reset]);
  const fields: (keyof Form)[] = ["name", "sku", "description", "category", "price", "stock", "weightKg"];
  return <Dialog open onClose={onClose} fullWidth><form onSubmit={handleSubmit((data) => onSave(data))}><DialogTitle>{product ? "Edit Product" : "New Product"}</DialogTitle><DialogContent><Grid container spacing={2} sx={{ pt: 1 }}>{fields.map((name) => <Grid size={{ xs: 12, sm: name === "description" ? 12 : 6 }} key={name}><Controller name={name} control={control} render={({ field }) => <TextField {...field} value={field.value ?? ""} fullWidth label={labels[name]} type={["price", "stock", "weightKg"].includes(name) ? "number" : "text"} error={!!errors[name]} helperText={errors[name]?.message} />} /></Grid>)}</Grid></DialogContent><DialogActions><Button onClick={onClose}>Cancel</Button><Button type="submit" variant="contained" disabled={isSubmitting}>Save</Button></DialogActions></form></Dialog>;
}
