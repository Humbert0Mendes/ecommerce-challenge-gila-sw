import api from "./api";
import type { ImportResult, Page, Product, ProductInput } from "../types";
export const productsApi = {
  list: (params: Record<string, unknown>) =>
    api.get<Page<Product>>("/api/v1/products", { params }).then((r) => r.data),
  get: (id: number) =>
    api.get<Product>(`/api/v1/products/${id}`).then((r) => r.data),
  create: (data: ProductInput) =>
    api.post<Product>("/api/v1/products", data).then((r) => r.data),
  update: (id: number, data: ProductInput) =>
    api.put<Product>(`/api/v1/products/${id}`, data).then((r) => r.data),
  remove: (id: number) => api.delete(`/api/v1/products/${id}`),
  importCsv: (file: File) => {
    const body = new FormData();
    body.append("file", file);
    return api
      .post<ImportResult>("/api/v1/products/import", body)
      .then((r) => r.data);
  },
};
