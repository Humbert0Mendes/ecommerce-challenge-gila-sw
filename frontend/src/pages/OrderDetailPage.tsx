import {
  CircularProgress,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  Typography,
} from "@mui/material";
import { useQuery } from "@tanstack/react-query";
import { useParams } from "react-router-dom";
import { ordersApi } from "../services/orders";
export function OrderDetailPage() {
  const id = Number(useParams().id);
  const query = useQuery({
    queryKey: ["orders", id],
    queryFn: () => ordersApi.get(id),
  });
  if (query.isLoading) return <CircularProgress />;
  if (query.isError || !query.data)
    return <Typography color="error">Pedido não encontrado.</Typography>;
  const order = query.data;
  return (
    <>
      <Typography variant="h4">Pedido #{order.id}</Typography>
      <Typography sx={{ mb: 2 }}>
        Status: {order.status} · Total: {order.total}
      </Typography>
      <Paper>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Produto</TableCell>
              <TableCell>Quantidade</TableCell>
              <TableCell>Preço unitário</TableCell>
              <TableCell>Subtotal</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {order.items.map((item) => (
              <TableRow key={item.productId}>
                <TableCell>{item.productId}</TableCell>
                <TableCell>{item.quantity}</TableCell>
                <TableCell>{item.unitPrice}</TableCell>
                <TableCell>{item.subtotal}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </Paper>
    </>
  );
}
