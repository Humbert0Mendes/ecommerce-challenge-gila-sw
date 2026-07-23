import type { OrderStatus } from "../types";

const statusConfig: Record<OrderStatus, { label: string; className: string }> = {
  CREATED: { label: "Created", className: "badge-neutral" },
  PROCESSING: { label: "Processing", className: "badge-warning" },
  CONFIRMED: { label: "Confirmed", className: "badge-success" },
  PAYMENT_FAILED: { label: "Payment Failed", className: "badge-error" },
};

export function OrderStatusBadge({ status }: { status: string }) {
  const config = statusConfig[status as OrderStatus] ?? {
    label: "Unknown",
    className: "badge-neutral",
  };

  return (
    <span
      className={`badge ${config.className}`}
      aria-label={`Order status: ${config.label}`}
    >
      {config.label}
    </span>
  );
}
