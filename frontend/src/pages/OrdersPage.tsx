import { Clear, ReceiptLong, Search } from "@mui/icons-material";
import { Button, CircularProgress, InputAdornment, Paper, TextField, Typography } from "@mui/material";
import { useQuery } from "@tanstack/react-query";
import { useState } from "react";
import { EmptyState } from "../components/EmptyState";
import { NewOrderButton } from "../components/NewOrderButton";
import { OrderDetailsModal } from "../components/OrderDetailsModal";
import { OrderListItem } from "../components/OrderListItem";
import { useDebouncedValue } from "../hooks/useDebouncedValue";
import { ordersApi } from "../services/orders";
import type { Order } from "../types";

const STATUSES = ["CREATED", "PROCESSING", "CONFIRMED", "PAYMENT_FAILED"];

export function OrdersPage() {
  const [search, setSearch] = useState("");
  const [selected, setSelected] = useState<Order | null>(null);
  const normalized = useDebouncedValue(search).trim().toUpperCase();
  const request = normalized ? /^\d+$/.test(normalized) ? { id: Number(normalized) } : STATUSES.includes(normalized) ? { status: normalized } : { id: -1 } : {};
  const query = useQuery({ queryKey: ["orders", request], queryFn: () => ordersApi.list(request) });
  const detail = useQuery({ queryKey: ["order", selected?.id], queryFn: () => ordersApi.get(selected!.id), enabled: !!selected });

  return <div>
    <div className="mb-6 flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between"><div><Typography variant="h4">Orders</Typography><Typography color="text.secondary">Search your order history by order number or status.</Typography></div><NewOrderButton /></div>
    <Paper className="mb-5 p-4"><TextField fullWidth label="Search orders" placeholder="For example: 42 or CONFIRMED" value={search} onChange={(event) => setSearch(event.target.value)} InputProps={{ startAdornment: <InputAdornment position="start"><Search /></InputAdornment>, endAdornment: search ? <InputAdornment position="end"><Button size="small" startIcon={<Clear />} onClick={() => setSearch("")}>Clear</Button></InputAdornment> : undefined }} /></Paper>
    {query.isLoading ? <div className="flex justify-center py-16"><CircularProgress aria-label="Loading orders" /></div> : query.isError ? <EmptyState title="Unable to load orders" description="Check your connection and try again." action={<Button onClick={() => query.refetch()}>Try again</Button>} /> : !query.data?.content.length ? <EmptyState icon={<ReceiptLong sx={{ fontSize: 48 }} />} title="No orders found" description="Change or clear your search and try again." /> : <div className="space-y-3">{query.data.content.map((order) => <OrderListItem key={order.id} order={order} onOpen={() => setSelected(order)} />)}</div>}
    <OrderDetailsModal order={detail.data ?? selected} isLoading={detail.isLoading} onClose={() => setSelected(null)} />
  </div>;
}
