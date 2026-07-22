import { Add, UploadFile } from "@mui/icons-material";
import {
  Button,
  CircularProgress,
  IconButton,
  Paper,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  TextField,
  Typography,
} from "@mui/material";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useState } from "react";
import { productsApi } from "../services/products";
import type { Product, ProductInput } from "../types";
import { ProductForm } from "../features/products/ProductForm";
import { ImportDialog } from "../features/products/ImportDialog";
import { useCart } from "../features/cart/CartContext";
import { useToast } from "../hooks/useToast";
export function ProductsPage() {
  const [name, setName] = useState("");
  const [editing, setEditing] = useState<Product | undefined>();
  const [form, setForm] = useState(false);
  const [importing, setImporting] = useState(false);
  const queryClient = useQueryClient();
  const toast = useToast();
  const { add } = useCart();
  const query = useQuery({
    queryKey: ["products", name],
    queryFn: () =>
      productsApi.list({ name: name || undefined, page: 0, size: 20 }),
  });
  const refresh = () =>
    queryClient.invalidateQueries({ queryKey: ["products"] });
  const save = useMutation({
    mutationFn: (data: ProductInput) =>
      editing ? productsApi.update(editing.id, data) : productsApi.create(data),
    onSuccess: () => {
      refresh();
      setForm(false);
      toast({ severity: "success", message: "Produto salvo." });
    },
    onError: () =>
      toast({
        severity: "error",
        message: "Não foi possível salvar. Verifique se o SKU já existe.",
      }),
  });
  const remove = useMutation({
    mutationFn: productsApi.remove,
    onSuccess: () => {
      refresh();
      toast({ severity: "success", message: "Produto removido." });
    },
  });
  return (
    <>
      <Stack
        direction={{ xs: "column", sm: "row" }}
        justifyContent="space-between"
        spacing={2}
        sx={{ mb: 2 }}
      >
        <Typography variant="h4">Produtos</Typography>
        <Stack direction="row" spacing={1}>
          <Button startIcon={<UploadFile />} onClick={() => setImporting(true)}>
            Importar CSV
          </Button>
          <Button
            startIcon={<Add />}
            variant="contained"
            onClick={() => {
              setEditing(undefined);
              setForm(true);
            }}
          >
            Novo produto
          </Button>
        </Stack>
      </Stack>
      <TextField
        label="Filtrar por nome"
        value={name}
        onChange={(e) => setName(e.target.value)}
        sx={{ mb: 2 }}
      />
      {query.isLoading ? (
        <CircularProgress />
      ) : query.isError ? (
        <Typography color="error">
          Erro de rede. Tente recarregar a página.
        </Typography>
      ) : (
        <Paper>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Nome</TableCell>
                <TableCell>SKU</TableCell>
                <TableCell>Categoria</TableCell>
                <TableCell>Preço</TableCell>
                <TableCell>Estoque</TableCell>
                <TableCell>Ações</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {query.data?.content.length ? (
                query.data.content.map((product) => (
                  <TableRow key={product.id}>
                    <TableCell>{product.name}</TableCell>
                    <TableCell>{product.sku}</TableCell>
                    <TableCell>{product.category}</TableCell>
                    <TableCell>{product.price}</TableCell>
                    <TableCell>{product.stock}</TableCell>
                    <TableCell>
                      <Button
                        disabled={!product.stock}
                        onClick={() => {
                          add(product);
                          toast({
                            severity: "success",
                            message: "Adicionado ao carrinho.",
                          });
                        }}
                      >
                        Adicionar
                      </Button>
                      <Button
                        onClick={() => {
                          setEditing(product);
                          setForm(true);
                        }}
                      >
                        Editar
                      </Button>
                      <Button
                        color="error"
                        onClick={() => remove.mutate(product.id)}
                      >
                        Excluir
                      </Button>
                    </TableCell>
                  </TableRow>
                ))
              ) : (
                <TableRow>
                  <TableCell colSpan={6}>Nenhum produto encontrado.</TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </Paper>
      )}
      {form && (
        <ProductForm
          product={editing}
          onClose={() => setForm(false)}
          onSave={(data) => save.mutate(data)}
        />
      )}
      {importing && (
        <ImportDialog onClose={() => setImporting(false)} onDone={refresh} />
      )}
    </>
  );
}
