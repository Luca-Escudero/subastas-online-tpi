import { api } from "./apiConfig.js";

// POST /api/auth/login -> { token }
export async function login(email, password) {
  return api.post("/auth/login", { email, password }, { auth: false });
}

// POST /api/usuarios -> registro público (queda con rol USER por defecto en el backend)
export async function register({ nombre, apellido, email, password, telefono }) {
  return api.post("/usuarios", { nombre, apellido, email, password, telefono }, { auth: false });
}
