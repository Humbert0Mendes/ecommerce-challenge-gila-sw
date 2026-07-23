import { CheckCircle } from "@mui/icons-material";
import { Dialog, DialogActions, DialogContent, DialogTitle, Button } from "@mui/material";
import type { OrderAccepted } from "../types";
import { formatCurrency } from "../utils/formatters";
import { OrderStatusBadge } from "./OrderStatusBadge";

export function OrderSuccessModal({ order, total, itemCount, onOrders, onContinue }: { order: OrderAccepted | null; total: number; itemCount: number; onOrders: () => void; onContinue: () => void }) {
  return <Dialog open={order !== null} onClose={onContinue} aria-labelledby="order-success-title"><DialogTitle id="order-success-title" className="text-center"><CheckCircle color="success" sx={{ fontSize: 54 }} /><br />Order placed successfully!</DialogTitle><DialogContent><p>Your order has been placed successfully and will be confirmed shortly.</p>{order && <div className="mt-5 grid gap-3 rounded-box bg-base-200 p-4 text-sm sm:grid-cols-2"><div><p className="text-base-content/60">Order ID</p><p className="font-semibold">#{order.orderId}</p></div><div><p className="text-base-content/60">Status</p><div className="mt-1"><OrderStatusBadge status={order.status} /></div></div><div><p className="text-base-content/60">Total</p><p className="font-semibold">{formatCurrency(total)}</p></div><div><p className="text-base-content/60">Items</p><p className="font-semibold">{itemCount} {itemCount === 1 ? "item" : "items"}</p></div></div>}</DialogContent><DialogActions><Button onClick={onContinue}>Continue shopping</Button><Button variant="contained" onClick={onOrders}>View orders</Button></DialogActions></Dialog>;
}
