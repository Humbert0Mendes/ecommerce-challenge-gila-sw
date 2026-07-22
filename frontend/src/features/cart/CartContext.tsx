import { createContext, useContext, useEffect, useState } from "react";
import type { CartItem, Product } from "../../types";
type CartContextValue = {
  items: CartItem[];
  add: (p: Product) => void;
  remove: (id: number) => void;
  setQuantity: (id: number, quantity: number) => void;
  clear: () => void;
};
const CartContext = createContext<CartContextValue | null>(null);
const STORAGE_KEY = "product-order-cart";
export function CartProvider({ children }: { children: React.ReactNode }) {
  const [items, setItems] = useState<CartItem[]>(() =>
    JSON.parse(localStorage.getItem(STORAGE_KEY) ?? "[]"),
  );
  useEffect(
    () => localStorage.setItem(STORAGE_KEY, JSON.stringify(items)),
    [items],
  );
  const add = (p: Product) =>
    setItems((old) => {
      const found = old.find((i) => i.id === p.id);
      return found
        ? old.map((i) =>
            i.id === p.id
              ? { ...i, quantity: Math.min(i.quantity + 1, p.stock) }
              : i,
          )
        : [...old, { ...p, quantity: 1 }];
    });
  const setQuantity = (id: number, quantity: number) =>
    setItems((old) =>
      old.map((i) =>
        i.id === id
          ? { ...i, quantity: Math.max(1, Math.min(quantity, i.stock)) }
          : i,
      ),
    );
  return (
    <CartContext.Provider
      value={{
        items,
        add,
        remove: (id) => setItems((x) => x.filter((i) => i.id !== id)),
        setQuantity,
        clear: () => setItems([]),
      }}
    >
      {children}
    </CartContext.Provider>
  );
}
export const useCart = () => {
  const value = useContext(CartContext);
  if (!value) throw new Error("CartProvider ausente");
  return value;
};
