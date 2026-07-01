import { api } from "./apiConfig.js";

const PATH = "/categorias";

export async function getAllCategorias() {
  return api.get(PATH);
}

export async function getCategoriaById(id) {
  return api.get(`${PATH}/${id}`);
}

// Solo ADMIN
export async function crearCategoria(nombre) {
  return api.post(PATH, { nombre });
}
