import { Add, Search, UploadFile } from "@mui/icons-material";
import { Button, Chip, CircularProgress, InputAdornment, Paper, Stack, TextField, Typography } from "@mui/material";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useState } from "react";
import { EmptyState } from "../components/EmptyState";
import { ProductCard } from "../components/ProductCard";
import { ProductPagination } from "../components/ProductPagination";
import { useCart } from "../features/cart/CartContext";
import { ImportDialog } from "../features/products/ImportDialog";
import { ProductForm } from "../features/products/ProductForm";
import { useDebouncedValue } from "../hooks/useDebouncedValue";
import { useToast } from "../hooks/useToast";
import { productsApi } from "../services/products";
import { invalidateProductQueries } from "../services/productQueries";
import type { Product, ProductInput } from "../types";

export function ProductsPage() {
  const [name, setName] = useState(""); const [category, setCategory] = useState<string>(); const [page, setPage] = useState(0); const [editing, setEditing] = useState<Product>(); const [form, setForm] = useState(false); const [importing, setImporting] = useState(false);
  const debounced = useDebouncedValue(name); const client = useQueryClient(); const toast = useToast(); const { add } = useCart();
  const query = useQuery({ queryKey: ["products", debounced, category, page], queryFn: () => productsApi.list({ name: debounced || undefined, category, page, size: 12 }) });
  const categories = useQuery({ queryKey: ["product-categories"], queryFn: productsApi.categories });
  const refresh = () => invalidateProductQueries(client);
  const save = useMutation({ mutationFn: (data: ProductInput) => editing ? productsApi.update(editing.id, data) : productsApi.create(data), onSuccess: () => { refresh(); setForm(false); toast({ severity: "success", message: "Product saved." }); } });
  const remove = useMutation({ mutationFn: productsApi.remove, onSuccess: () => { refresh(); toast({ severity: "success", message: "Product removed." }); } });
  const resetPage = (setter: () => void) => { setter(); setPage(0); };
  return <div>
    <Stack direction={{ xs: "column", sm: "row" }} justifyContent="space-between" gap={2} sx={{ mb: 3 }}><div><Typography variant="h4">Products</Typography><Typography color="text.secondary">Catalog, inventory, and pricing in one place.</Typography></div><Stack direction="row" gap={1}><Button startIcon={<UploadFile />} onClick={() => setImporting(true)}>Import CSV</Button><Button variant="contained" startIcon={<Add />} onClick={() => { setEditing(undefined); setForm(true); }}>New Product</Button></Stack></Stack>
    <Paper className="mb-4 p-4"><TextField fullWidth label="Search products" value={name} onChange={(event) => resetPage(() => setName(event.target.value))} InputProps={{ startAdornment: <InputAdornment position="start"><Search /></InputAdornment> }} /></Paper>
    <div className="mb-5 flex flex-wrap gap-2"><Chip label="All" color={!category ? "primary" : "default"} onClick={() => resetPage(() => setCategory(undefined))} />{categories.data?.map((item) => <Chip key={item} label={item} color={category === item ? "primary" : "default"} variant={category === item ? "filled" : "outlined"} onClick={() => resetPage(() => setCategory(item))} />)}</div>
    {query.isLoading ? <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-3">{Array.from({ length: 6 }, (_, i) => <div key={i} className="skeleton h-80" />)}</div> : query.isError ? <EmptyState title="Unable to load products" description="Try again in a few moments." action={<Button variant="contained" onClick={() => query.refetch()}>Try again</Button>} /> : !query.data?.content.length ? <EmptyState title="No products found" description="Change your search or select another category." /> : <><div className="grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-3">{query.data.content.map((product) => <ProductCard key={product.id} product={product} onAdd={() => { add(product); toast({ severity: "success", message: "Product added to cart." }); }} onEdit={() => { setEditing(product); setForm(true); }} onRemove={() => remove.mutate(product.id)} />)}</div><ProductPagination page={query.data.page} totalPages={query.data.totalPages} onPage={setPage} /></>}
    {form && <ProductForm product={editing} onClose={() => setForm(false)} onSave={(data) => save.mutate(data)} />}{importing && <ImportDialog onClose={() => setImporting(false)} onDone={refresh} />}
  </div>;
}
