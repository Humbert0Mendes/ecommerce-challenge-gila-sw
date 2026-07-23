import { render, screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";
import { OrderStatusBadge } from "./OrderStatusBadge";

describe("OrderStatusBadge", () => {
  it.each([
    ["CREATED", "Created", "badge-neutral"],
    ["PROCESSING", "Processing", "badge-warning"],
    ["CONFIRMED", "Confirmed", "badge-success"],
    ["PAYMENT_FAILED", "Payment Failed", "badge-error"],
  ])("renders %s with its configured visual style", (status, label, className) => {
    render(<OrderStatusBadge status={status} />);
    expect(screen.getByText(label)).toHaveClass(className);
  });

  it("uses a neutral fallback for an unknown status", () => {
    render(<OrderStatusBadge status="SOMETHING_NEW" />);
    expect(screen.getByText("Unknown")).toHaveClass("badge-neutral");
  });
});
