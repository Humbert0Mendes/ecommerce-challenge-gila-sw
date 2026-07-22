import api from "./api";
import type { Order, Page } from "../types";
export const ordersApi = {
  list: (page = 0) =>
    api
      .get<Page<Order>>("/api/v1/orders", { params: { page } })
      .then((r) => r.data),
  get: (id: number) =>
    api.get<Order>(`/api/v1/orders/${id}`).then((r) => r.data),
  create: (
    items: { productId: number; quantity: number }[],
    idempotencyKey: string,
  ) =>
    api
      .post<Order>(
        "/api/v1/orders",
        { items },
        { headers: { "Idempotency-Key": idempotencyKey } },
      )
      .then((r) => r.data),
};
