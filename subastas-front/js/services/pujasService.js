import { api } from "./apiConfig.js";

// NOTA: el backend todavía no tiene implementado el PujaController/entidad Puja.
// El SecurityConfig ya reserva la ruta (rol USER, POST /api/pujas/**), así que
// este service queda listo para cuando se implemente del lado del back.
// Hasta entonces, esta llamada va a devolver 404.

const PATH = "/pujas";

export async function crearPuja({ subastaId, monto }) {
  return api.post(PATH, { subastaId, monto });
}

export async function getPujasPorSubasta(subastaId) {
  return api.get(`${PATH}/subasta/${subastaId}`);
}
