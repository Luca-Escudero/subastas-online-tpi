import { login as serviceLogin, register as serviceRegister } from "../services/authService.js";
import { setToken, clearToken, getToken } from "../services/apiConfig.js";

const SESSION_KEY = "userSession";

const PATHS = {
  ADMIN: "/pages/admin/home-admin.html",
  SELLER: "/pages/seller/home-seller.html",
  USER: "/pages/user/home-user.html",
  LOGIN: "/pages/register-login/login.html",
};

// El token trae los claims "sub" (email) y "roles" (ej: ["ROLE_USER"]).
// Lo decodificamos en el navegador, no hace falta pedirle nada más al backend.
function decodeJwt(token) {
  try {
    const payload = token.split(".")[1];
    const json = decodeURIComponent(
      atob(payload.replace(/-/g, "+").replace(/_/g, "/"))
        .split("")
        .map((c) => "%" + c.charCodeAt(0).toString(16).padStart(2, "0"))
        .join("")
    );
    return JSON.parse(json);
  } catch (err) {
    console.error("No se pudo decodificar el token:", err);
    return null;
  }
}

function extractRoles(claims) {
  // claims.roles viene como [{ authority: "ROLE_USER" }, ...] (formato de Spring Security)
  if (!claims?.roles) return [];
  return claims.roles
    .map((r) => (typeof r === "string" ? r : r.authority))
    .filter(Boolean)
    .map((r) => r.replace("ROLE_", ""));
}

function saveSession(token) {
  const claims = decodeJwt(token);
  const roles = extractRoles(claims);
  const session = {
    email: claims?.sub ?? null,
    roles, // ej: ["ADMIN"], ["SELLER"], ["USER"]
  };
  setToken(token);
  localStorage.setItem(SESSION_KEY, JSON.stringify(session));
  return session;
}

export function getSession() {
  const session = localStorage.getItem(SESSION_KEY);
  return session ? JSON.parse(session) : null;
}

export function isAuthenticated() {
  return !!getToken() && !!getSession();
}

export function hasRole(role) {
  const session = getSession();
  return !!session?.roles?.includes(role);
}

export function logout() {
  clearToken();
  localStorage.removeItem(SESSION_KEY);
  window.location.href = PATHS.LOGIN;
}

export async function loginUser(email, password) {
  if (!email || !password) {
    throw new Error("Email y contraseña son requeridos.");
  }
  const { token } = await serviceLogin(email, password);
  return saveSession(token);
}

export async function registerUser(datos) {
  const { nombre, apellido, email, password, telefono } = datos;
  if (!nombre || !apellido || !email || !password || !telefono) {
    throw new Error("Todos los campos son requeridos.");
  }
  // El registro NO devuelve token: el backend solo crea el usuario.
  // Después de registrarse, el usuario tiene que loguearse.
  return serviceRegister({ nombre, apellido, email, password, telefono });
}

// Prioridad de roles para decidir a dónde mandar a alguien con varios roles
export function redirectToDashboard(session) {
  const roles = session?.roles ?? [];
  // El registro le da USER + SELLER a todo el mundo, así que priorizamos
  // mandarlo al catálogo (USER); desde ahí puede ir a "Mis Productos" (SELLER).
  if (roles.includes("ADMIN")) {
    window.location.href = PATHS.ADMIN;
  } else if (roles.includes("USER")) {
    window.location.href = PATHS.USER;
  } else if (roles.includes("SELLER")) {
    window.location.href = PATHS.SELLER;
  } else {
    window.location.href = PATHS.LOGIN;
  }
}

// Para usar al cargar cada página protegida: si no hay sesión o no tiene
// el rol pedido, patea al login.
export function requireRole(requiredRole) {
  if (!isAuthenticated()) {
    window.location.href = PATHS.LOGIN;
    return false;
  }
  if (requiredRole && !hasRole(requiredRole)) {
    alert("No tenés permisos para ver esta página.");
    redirectToDashboard(getSession());
    return false;
  }
  return true;
}

export { PATHS };
