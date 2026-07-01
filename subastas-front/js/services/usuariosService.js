import { api } from "./apiConfig.js";

const PATH = "/usuarios";

// Solo ADMIN
export async function getAllUsuarios() {
  return api.get(PATH);
}

// Solo ADMIN - activar/bloquear (PUT /usuarios/{id}/estado?activo=true|false)
export async function cambiarEstadoUsuario(id, activo) {
  return api.put(`${PATH}/${id}/estado?activo=${activo}`, null);
}

// Para cualquier usuario autenticado (retorna datos de la propia sesión)
export async function getUsuarioMe() {
  return api.get(`${PATH}/me`);
}
