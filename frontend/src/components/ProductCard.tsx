import { AddShoppingCart, DeleteOutline, EditOutlined, Inventory2 } from "@mui/icons-material";
import { IconButton } from "@mui/material";
import type { Product } from "../types";
import { formatCurrency } from "../utils/formatters";

export function ProductCard({ product, onAdd, onEdit, onRemove }: { product: Product; onAdd: () => void; onEdit: () => void; onRemove: () => void }) {
  const unavailable = product.stock === 0;
  return <article className={`card border bg-base-100 shadow-sm transition hover:-translate-y-1 hover:shadow-md ${unavailable ? "opacity-75" : ""}`}>
    <figure className="h-36 bg-gradient-to-br from-blue-50 to-slate-100"><Inventory2 className="text-5xl text-blue-300" aria-label="Product image placeholder" /></figure>
    <div className="card-body gap-3 p-5">
      <div className="flex items-start justify-between gap-2"><h2 className="card-title text-base">{product.name}</h2><span className="badge badge-outline">{product.category}</span></div>
      <p className="line-clamp-2 min-h-10 text-sm text-base-content/70">{product.description}</p>
      <div className="flex items-center justify-between"><strong className="text-lg text-primary">{formatCurrency(product.price)}</strong><span className={`badge ${unavailable ? "badge-error" : product.stock < 10 ? "badge-warning" : "badge-success"}`}>{unavailable ? "Out of stock" : `${product.stock} in stock`}</span></div>
      <div className="card-actions items-center justify-between border-t pt-3"><button className="btn btn-primary btn-sm" disabled={unavailable} onClick={onAdd}><AddShoppingCart fontSize="small" />Add to cart</button><div><IconButton size="small" aria-label={`Edit ${product.name}`} onClick={onEdit}><EditOutlined fontSize="small" /></IconButton><IconButton size="small" color="error" aria-label={`Delete ${product.name}`} onClick={onRemove}><DeleteOutline fontSize="small" /></IconButton></div></div>
    </div>
  </article>;
}
