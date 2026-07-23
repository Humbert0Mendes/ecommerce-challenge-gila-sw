import { Button, Paper, Stack, Table, TableBody, TableCell, TableHead, TableRow, TextField, Typography } from "@mui/material";
import { useMutation } from "@tanstack/react-query";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { OrderSuccessModal } from "../components/OrderSuccessModal";
import { useCart } from "../features/cart/CartContext";
import { useToast } from "../hooks/useToast";
import { ordersApi } from "../services/orders";
import type { OrderAccepted } from "../types";
import { formatCurrency } from "../utils/formatters";

export function CartPage() {
  const { items, remove, setQuantity, clear } = useCart();
  const toast = useToast();
  const navigate = useNavigate();
  const [successOrder, setSuccessOrder] = useState<OrderAccepted | null>(null);
  const [submittedTotal, setSubmittedTotal] = useState(0);
  const [submittedItems, setSubmittedItems] = useState(0);
  const total = items.reduce((sum, item) => sum + item.price * item.quantity, 0);

  const createOrder = useMutation({
    mutationFn: () => ordersApi.create(items.map((item) => ({ productId: item.id, quantity: item.quantity })), crypto.randomUUID()),
    onSuccess: (order) => {
      clear();
      toast({ severity: "success", message: `Order #${order.orderId} created.` });
      setSuccessOrder(order);
    },
    onError: () => toast({ severity: "error", message: "Unable to create the order. Check inventory and try again." }),
  });

  const submit = () => {
    setSubmittedTotal(total);
    setSubmittedItems(items.reduce((count, item) => count + item.quantity, 0));
    createOrder.mutate();
  };

  return <>
    {!items.length ? <Typography variant="h5">Your cart is empty.</Typography> : <>
      <Typography variant="h4" sx={{ mb: 2 }}>Cart</Typography>
      <Paper><Table><TableHead><TableRow><TableCell>Product</TableCell><TableCell>Quantity</TableCell><TableCell>Subtotal</TableCell><TableCell /></TableRow></TableHead><TableBody>{items.map((item) => <TableRow key={item.id}><TableCell>{item.name}</TableCell><TableCell><TextField type="number" size="small" value={item.quantity} inputProps={{ min: 1, max: item.stock, "aria-label": `Quantity for ${item.name}` }} onChange={(event) => setQuantity(item.id, Number(event.target.value))} /></TableCell><TableCell>{formatCurrency(item.price * item.quantity)}</TableCell><TableCell><Button color="error" onClick={() => remove(item.id)}>Remove</Button></TableCell></TableRow>)}</TableBody></Table></Paper>
      <Stack direction="row" justifyContent="space-between" sx={{ mt: 2 }}><Typography variant="h6">Total: {formatCurrency(total)}</Typography><Button variant="contained" disabled={createOrder.isPending} onClick={submit}>{createOrder.isPending ? "Processing order..." : "Place order"}</Button></Stack>
    </>}
    <OrderSuccessModal order={successOrder} total={submittedTotal} itemCount={submittedItems} onOrders={() => navigate("/orders")} onContinue={() => { setSuccessOrder(null); navigate("/products"); }} />
  </>;
}
