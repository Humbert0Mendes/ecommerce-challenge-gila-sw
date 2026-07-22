import { Close } from "@mui/icons-material";
import { useEffect, useRef } from "react";
import type { Order } from "../types";
import { formatCurrency, formatDateTime } from "../utils/formatters";
import { OrderItemsTable } from "./OrderItemsTable";

export function OrderDetailsModal({ order, onClose, isLoading = false }: { order: Order | null; onClose: () => void; isLoading?: boolean }) {
  const dialogRef = useRef<HTMLDialogElement>(null);
  useEffect(() => {
    const dialog = dialogRef.current;
    if (!dialog) return;
    if (order && !dialog.open) dialog.showModal();
    if (!order && dialog.open) dialog.close();
  }, [order]);

  return (
    <dialog ref={dialogRef} className="modal" aria-labelledby="order-details-title" onClose={onClose}>
      {order && <div className="modal-box max-h-[calc(100vh-3rem)] max-w-3xl overflow-y-auto p-0">
        <div className="sticky top-0 z-10 flex items-start justify-between border-b border-base-300 bg-base-100 px-5 py-4">
          <div><h2 id="order-details-title" className="text-lg font-bold">Order Details #{order.id}</h2><p className="mt-1 text-sm text-base-content/70">Placed {formatDateTime(order.createdAt)}</p></div>
          <button className="btn btn-ghost btn-sm btn-circle" aria-label="Close order details" onClick={onClose}><Close fontSize="small" /></button>
        </div>
        <div className="space-y-5 p-5">
          <div className="grid gap-3 text-sm sm:grid-cols-2">
            <div className="rounded-box bg-base-200 p-3"><p className="text-xs text-base-content/60">Status</p><p className="mt-1 font-semibold">{order.status}</p></div>
            <div className="rounded-box bg-base-200 p-3"><p className="text-xs text-base-content/60">Order Total</p><p className="mt-1 font-semibold">{formatCurrency(order.total)}</p></div>
          </div>
          <div><h3 className="mb-2 font-semibold">Items</h3>{isLoading ? <span className="loading loading-spinner loading-sm" aria-label="Loading order details" /> : <OrderItemsTable items={order.items} />}</div>
        </div>
        <div className="modal-action m-0 border-t border-base-300 px-5 py-4"><button className="btn" onClick={onClose}>Close</button></div>
      </div>}
      <form method="dialog" className="modal-backdrop"><button aria-label="Close order details">close</button></form>
    </dialog>
  );
}
