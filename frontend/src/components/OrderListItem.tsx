import type { KeyboardEvent } from "react";
import type { Order } from "../types";
import { formatCurrency, formatDate, formatItemsCount } from "../utils/formatters";
import { OrderStatusBadge } from "./OrderStatusBadge";

export function OrderListItem({ order, onOpen }: { order: Order; onOpen: () => void }) {
  const onKeyDown = (event: KeyboardEvent<HTMLDivElement>) => {
    if (event.key === "Enter" || event.key === " ") {
      event.preventDefault();
      onOpen();
    }
  };

  return (
    <div
      className="card card-border cursor-pointer bg-base-100 transition hover:border-primary/40 hover:shadow-sm focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary"
      role="button"
      tabIndex={0}
      aria-label={`View details for order ${order.id}`}
      onClick={onOpen}
      onKeyDown={onKeyDown}
    >
      <div className="card-body gap-3 p-4">
        <div className="hidden grid-cols-9 items-center gap-4 md:grid">
          <div className="col-span-2"><p className="text-xs text-base-content/60">Order ID</p><p className="font-semibold">#{order.id}</p></div>
          <div className="col-span-2"><p className="text-xs text-base-content/60">Date</p><p>{formatDate(order.createdAt)}</p></div>
          <div className="col-span-2"><p className="text-xs text-base-content/60">Total</p><p className="font-medium">{formatCurrency(order.total)}</p></div>
          <div className="col-span-2"><p className="mb-1 text-xs text-base-content/60">Status</p><OrderStatusBadge status={order.status} /></div>
          <div><p className="text-xs text-base-content/60">Items</p><p>{formatItemsCount(order.items)}</p></div>
        </div>
        <div className="grid grid-cols-2 gap-x-4 gap-y-3 text-sm md:hidden">
          <div><p className="text-xs text-base-content/60">Order ID</p><p className="font-semibold">#{order.id}</p></div>
          <div><p className="text-xs text-base-content/60">Date</p><p>{formatDate(order.createdAt)}</p></div>
          <div><p className="text-xs text-base-content/60">Total</p><p className="font-medium">{formatCurrency(order.total)}</p></div>
          <div><p className="text-xs text-base-content/60">Items</p><p>{formatItemsCount(order.items)}</p></div>
          <div className="col-span-2"><p className="mb-1 text-xs text-base-content/60">Status</p><OrderStatusBadge status={order.status} /></div>
        </div>
      </div>
    </div>
  );
}
