import axios from "axios";
import { freshToken } from "./auth";
const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL ?? "http://localhost:8080",
});
api.interceptors.request.use(async (config) => {
  const token = await freshToken();
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});
api.interceptors.response.use(undefined, async (error) => {
  const config = error.config;
  const status = error.response?.status;
  if (status >= 500 && config && (config.__retries ?? 0) < 3) {
    config.__retries = (config.__retries ?? 0) + 1;
    await new Promise((r) => setTimeout(r, 300 * 2 ** config.__retries));
    return api(config);
  }
  return Promise.reject(error);
});
export default api;
