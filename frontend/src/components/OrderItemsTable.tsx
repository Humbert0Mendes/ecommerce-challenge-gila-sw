import type { OrderItem } from "../types";
import { formatCurrency } from "../utils/formatters";

export function OrderItemsTable({ items }: { items: OrderItem[] }) {
  if (!items.length) return <p className="text-sm text-base-content/70">This order has no items.</p>;
  return (
    <>
      <div className="hidden overflow-x-auto sm:block">
        <table className="table table-sm">
          <thead><tr><th>Product</th><th>Quantity</th><th>Unit Price</th><th className="text-right">Subtotal</th></tr></thead>
          <tbody>{items.map((item) => <tr key={item.productId}><td><p className="font-medium">{item.productName ?? `Product #${item.productId}`}</p><p className="text-xs text-base-content/60">ID #{item.productId}</p></td><td>{item.quantity}</td><td>{formatCurrency(item.unitPrice)}</td><td className="text-right font-medium">{formatCurrency(item.subtotal)}</td></tr>)}</tbody>
        </table>
      </div>
      <div className="space-y-3 sm:hidden">{items.map((item) => <article key={item.productId} className="card border border-base-300 bg-base-100"><div className="card-body gap-2 p-4"><div><h3 className="font-semibold">{item.productName ?? `Product #${item.productId}`}</h3><p className="text-xs text-base-content/60">Product ID #{item.productId}</p></div><div className="grid grid-cols-3 gap-2 text-sm"><span>Qty: {item.quantity}</span><span>{formatCurrency(item.unitPrice)}</span><span className="text-right font-medium">{formatCurrency(item.subtotal)}</span></div></div></article>)}</div>
    </>
  );
}
