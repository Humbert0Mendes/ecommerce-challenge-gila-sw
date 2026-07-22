import { act, renderHook } from "@testing-library/react";
import { describe, expect, it } from "vitest";
import { CartProvider, useCart } from "./CartContext";
import type { Product } from "../../types";
const product: Product = {
  id: 1,
  name: "A",
  sku: "A",
  description: "A",
  category: "A",
  price: 10,
  stock: 2,
  weightKg: 1,
};
describe("cart", () => {
  it("persists and does not exceed stock", () => {
    const { result } = renderHook(() => useCart(), { wrapper: CartProvider });
    act(() => {
      result.current.add(product);
      result.current.add(product);
      result.current.add(product);
    });
    expect(result.current.items[0].quantity).toBe(2);
    expect(
      JSON.parse(localStorage.getItem("product-order-cart")!)[0].quantity,
    ).toBe(2);
  });
});
