import api from "./api";
import type { Order, OrderAccepted, Page } from "../types";
export const ordersApi = {
  list: (params: { page?: number; id?: number; status?: string } = {}) =>
    api
      .get<Page<Order>>("/api/v1/orders", { params })
      .then((r) => r.data),
  get: (id: number) =>
    api.get<Order>(`/api/v1/orders/${id}`).then((r) => r.data),
  create: (
    items: { productId: number; quantity: number }[],
    idempotencyKey: string,
  ) =>
    api
      .post<OrderAccepted>(
        "/api/v1/orders",
        { items },
        { headers: { "Idempotency-Key": idempotencyKey } },
      )
      .then((r) => r.data),
};
