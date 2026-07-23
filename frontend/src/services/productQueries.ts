import type { QueryClient } from "@tanstack/react-query";

export const invalidateProductQueries = (client: QueryClient) =>
  Promise.all([
    client.invalidateQueries({ queryKey: ["products"] }),
    client.invalidateQueries({ queryKey: ["product-categories"] }),
  ]);
