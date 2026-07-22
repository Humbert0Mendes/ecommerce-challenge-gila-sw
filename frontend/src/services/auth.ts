import Keycloak from "keycloak-js";
const keycloak = new Keycloak({
  url: import.meta.env.VITE_KEYCLOAK_URL ?? "http://localhost:8081",
  realm: import.meta.env.VITE_KEYCLOAK_REALM ?? "product-order",
  clientId: import.meta.env.VITE_KEYCLOAK_CLIENT_ID ?? "product-order-web",
});
export async function initializeAuth() {
  await keycloak.init({
    onLoad: "check-sso",
    pkceMethod: "S256",
    checkLoginIframe: false,
  });
}
export function login() {
  return keycloak.login({ redirectUri: `${window.location.origin}/products` });
}
export async function freshToken() {
  if (!keycloak.authenticated) return undefined;
  await keycloak.updateToken(30);
  return keycloak.token;
}
export default keycloak;
