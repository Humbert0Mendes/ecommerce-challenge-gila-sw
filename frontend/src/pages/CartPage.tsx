import { Button, Paper, Stack, Table, TableBody, TableCell, TableHead, TableRow, TextField, Typography } from "@mui/material";
import { useMutation } from "@tanstack/react-query";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { OrderSuccessModal } from "../components/OrderSuccessModal";
import { useCart } from "../features/cart/CartContext";
import { useToast } from "../hooks/useToast";
import { ordersApi } from "../services/orders";
import { formatCurrency } from "../utils/formatters";

export function CartPage() {
  const { items, remove, setQuantity, clear } = useCart(); const toast = useToast(); const navigate = useNavigate(); const [successOrderId, setSuccessOrderId] = useState<number | null>(null);
  const createOrder = useMutation({ mutationFn: () => ordersApi.create(items.map((i) => ({ productId: i.id, quantity: i.quantity })), crypto.randomUUID()), onSuccess: (order) => { clear(); toast({ severity: "success", message: `Order #${order.id} created.` }); setSuccessOrderId(order.id); }, onError: () => toast({ severity: "error", message: "Unable to create the order. Check inventory and try again." }) });
  const total = items.reduce((sum, i) => sum + i.price * i.quantity, 0);
  if (!items.length) return <Typography variant="h5">Your cart is empty.</Typography>;
  return <><Typography variant="h4" sx={{ mb: 2 }}>Cart</Typography><Paper><Table><TableHead><TableRow><TableCell>Product</TableCell><TableCell>Quantity</TableCell><TableCell>Subtotal</TableCell><TableCell /></TableRow></TableHead><TableBody>{items.map((item) => <TableRow key={item.id}><TableCell>{item.name}</TableCell><TableCell><TextField type="number" size="small" value={item.quantity} inputProps={{ min: 1, max: item.stock, "aria-label": `Quantity for ${item.name}` }} onChange={(e) => setQuantity(item.id, Number(e.target.value))} /></TableCell><TableCell>{formatCurrency(item.price * item.quantity)}</TableCell><TableCell><Button color="error" onClick={() => remove(item.id)}>Remove</Button></TableCell></TableRow>)}</TableBody></Table></Paper><Stack direction="row" justifyContent="space-between" sx={{ mt: 2 }}><Typography variant="h6">Total: {formatCurrency(total)}</Typography><Button variant="contained" disabled={createOrder.isPending} onClick={() => createOrder.mutate()}>{createOrder.isPending ? "Processing order..." : "Place order"}</Button></Stack><OrderSuccessModal orderId={successOrderId} onOrders={() => navigate("/orders")} onContinue={() => { setSuccessOrderId(null); navigate("/products"); }} /></>;
}
