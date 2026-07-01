import { api } from "./apiConfig.js";

const PATH = "/productos";

export async function getAllProductos() {
  return api.get(PATH);
}

export async function getProductoById(id) {
  return api.get(`${PATH}/${id}`);
}

// Solo SELLER
export async function crearProducto({ nombre, descripcion, imagenUrl, categoriaId }) {
  return api.post(PATH, { nombre, descripcion, imagenUrl, categoriaId });
}

// Solo SELLER (dueño del producto)
export async function actualizarProducto(id, { nombre, descripcion, imagenUrl, categoriaId }) {
  return api.put(`${PATH}/${id}`, { nombre, descripcion, imagenUrl, categoriaId });
}

// Solo SELLER (dueño del producto)
export async function eliminarProducto(id) {
  return api.delete(`${PATH}/${id}`);
}
