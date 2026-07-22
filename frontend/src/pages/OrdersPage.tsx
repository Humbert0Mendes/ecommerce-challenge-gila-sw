import {
  CircularProgress,
  List,
  ListItemButton,
  ListItemText,
  Typography,
} from "@mui/material";
import { useQuery } from "@tanstack/react-query";
import { Link } from "react-router-dom";
import { ordersApi } from "../services/orders";
export function OrdersPage() {
  const query = useQuery({
    queryKey: ["orders"],
    queryFn: () => ordersApi.list(),
  });
  if (query.isLoading) return <CircularProgress />;
  if (query.isError)
    return (
      <Typography color="error">Não foi possível carregar pedidos.</Typography>
    );
  return (
    <>
      <Typography variant="h4">Pedidos</Typography>
      <List>
        {query.data?.content.length ? (
          query.data.content.map((order) => (
            <ListItemButton
              key={order.id}
              component={Link}
              to={`/orders/${order.id}`}
            >
              <ListItemText
                primary={`Pedido #${order.id} — ${order.status}`}
                secondary={`${new Date(order.createdAt).toLocaleString()} · Total ${order.total}`}
              />
            </ListItemButton>
          ))
        ) : (
          <Typography sx={{ mt: 2 }}>Nenhum pedido realizado.</Typography>
        )}
      </List>
    </>
  );
}
