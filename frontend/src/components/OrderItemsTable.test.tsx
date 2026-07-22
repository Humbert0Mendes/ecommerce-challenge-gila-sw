import { render, screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";
import { OrderItemsTable } from "./OrderItemsTable";

describe("OrderItemsTable", () => {
  it("shows the product name and USD values", () => {
    render(<OrderItemsTable items={[{ productId: 12, productName: "Wireless Keyboard", quantity: 2, unitPrice: 49.9, subtotal: 99.8 }]} />);
    expect(screen.getAllByText("Wireless Keyboard").length).toBeGreaterThan(0);
    expect(screen.getAllByText("$49.90").length).toBeGreaterThan(0);
    expect(screen.getAllByText("$99.80").length).toBeGreaterThan(0);
  });

  it("falls back to the product ID for older orders", () => {
    render(<OrderItemsTable items={[{ productId: 12, quantity: 1, unitPrice: 10, subtotal: 10 }]} />);
    expect(screen.getAllByText("Product #12").length).toBeGreaterThan(0);
  });
});
