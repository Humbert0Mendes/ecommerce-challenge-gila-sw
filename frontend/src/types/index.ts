export type Product = {
  id: number;
  name: string;
  sku: string;
  description: string;
  category: string;
  price: number;
  stock: number;
  weightKg: number;
};
export type ProductInput = Omit<Product, "id">;
export type Page<T> = {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
};
export type CartItem = Product & { quantity: number };
export type OrderItem = {
  productId: number;
  productName?: string | null;
  quantity: number;
  unitPrice: number;
  subtotal: number;
};
export type Order = {
  id: number;
  createdAt: string;
  status: "APPROVED" | "DECLINED" | string;
  items: OrderItem[];
  total: number;
};
export type ImportResult = {
  imported: number;
  skipped: number;
  errors: { line?: number; message?: string; reason?: string }[];
};
