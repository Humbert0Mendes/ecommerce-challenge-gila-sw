import {
  Button,
  Paper,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  TextField,
  Typography,
} from "@mui/material";
import { useMutation } from "@tanstack/react-query";
import { useNavigate } from "react-router-dom";
import { useCart } from "../features/cart/CartContext";
import { ordersApi } from "../services/orders";
import { useToast } from "../hooks/useToast";
export function CartPage() {
  const { items, remove, setQuantity, clear } = useCart();
  const toast = useToast();
  const navigate = useNavigate();
  const createOrder = useMutation({
    mutationFn: () =>
      ordersApi.create(
        items.map((i) => ({ productId: i.id, quantity: i.quantity })),
        crypto.randomUUID(),
      ),
    onSuccess: (order) => {
      clear();
      toast({ severity: "success", message: `Pedido #${order.id} criado.` });
      navigate(`/orders/${order.id}`);
    },
    onError: (error) =>
      toast({
        severity: "error",
        message:
          "Não foi possível criar o pedido. Confira o estoque e tente novamente.",
      }),
  });
  const total = items.reduce((sum, i) => sum + i.price * i.quantity, 0);
  if (!items.length)
    return <Typography variant="h5">Seu carrinho está vazio.</Typography>;
  return (
    <>
      <Typography variant="h4" sx={{ mb: 2 }}>
        Carrinho
      </Typography>
      <Paper>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Produto</TableCell>
              <TableCell>Quantidade</TableCell>
              <TableCell>Subtotal</TableCell>
              <TableCell />
            </TableRow>
          </TableHead>
          <TableBody>
            {items.map((item) => (
              <TableRow key={item.id}>
                <TableCell>{item.name}</TableCell>
                <TableCell>
                  <TextField
                    type="number"
                    size="small"
                    value={item.quantity}
                    inputProps={{ min: 1, max: item.stock }}
                    onChange={(e) =>
                      setQuantity(item.id, Number(e.target.value))
                    }
                  />
                </TableCell>
                <TableCell>{(item.price * item.quantity).toFixed(2)}</TableCell>
                <TableCell>
                  <Button color="error" onClick={() => remove(item.id)}>
                    Remover
                  </Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </Paper>
      <Stack direction="row" justifyContent="space-between" sx={{ mt: 2 }}>
        <Typography variant="h6">Total: {total.toFixed(2)}</Typography>
        <Button
          variant="contained"
          disabled={createOrder.isPending}
          onClick={() => createOrder.mutate()}
        >
          Finalizar pedido
        </Button>
      </Stack>
    </>
  );
}
