import { api } from "./apiConfig.js";

const PATH = "/subastas";

export async function getAllSubastas() {
  return api.get(PATH);
}

export async function getSubastaById(id) {
  return api.get(`${PATH}/${id}`);
}

// Solo SELLER - crea la subasta en estado BORRADOR
export async function crearSubasta({ productoId, fechaInicio, fechaCierre, precioInicial, incrementoMinimo }) {
  return api.post(PATH, { productoId, fechaInicio, fechaCierre, precioInicial, incrementoMinimo });
}

// Solo SELLER - pasa la subasta de BORRADOR a PUBLICADA
export async function publicarSubasta(id) {
  return api.patch(`${PATH}/${id}/publicacion`, null);
}

// Solo SELLER (dueño)
export async function eliminarSubasta(id) {
  return api.delete(`${PATH}/${id}`);
}

export async function getHistorialEstado(id) {
  return api.get(`${PATH}/${id}/historial`);
}
