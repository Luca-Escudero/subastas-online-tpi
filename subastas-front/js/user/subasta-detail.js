import { requireRole, logout } from "../modules/auth.js";
import { getSubastaById } from "../services/subastasService.js";
import { getProductoById } from "../services/productosService.js";
import { getCategoriaById } from "../services/categoriasService.js";
import { crearPuja } from "../services/pujasService.js";
import { formatMoney, formatDate, estadoBadge, showError, hideError } from "../modules/utils.js";

if (!requireRole("USER")) {
  throw new Error("redirecting");
}

document.getElementById("logout-button").addEventListener("click", (e) => {
  e.preventDefault();
  logout();
});

const params = new URLSearchParams(window.location.search);
const subastaId = params.get("id");

const alertBox = document.getElementById("alert-box");
const pujaCard = document.getElementById("puja-card");
const pujaError = document.getElementById("puja-error");
const pujaSuccess = document.getElementById("puja-success");
const montoInput = document.getElementById("monto-puja");

let subastaActual = null;

function showAlert(message, type = "warning") {
  alertBox.className = `alert alert-${type}`;
  alertBox.textContent = message;
  alertBox.style.display = "block";
}

async function init() {
  const nombreElement = document.getElementById("producto-nombre");

  if (!subastaId || subastaId === "undefined" || subastaId === "null") {
    nombreElement.textContent = "Subasta no especificada";
    showAlert("No se especificó qué subasta ver.", "danger");
    pujaCard.style.display = "none";
    return;
  }

  try {
    const subasta = await getSubastaById(subastaId);
    subastaActual = subasta;
    const producto = await getProductoById(subasta.productoId);

    nombreElement.textContent = producto.nombre;
    document.getElementById("producto-descripcion").textContent = producto.descripcion;
    document.getElementById("producto-imagen").src = producto.imagenUrl || "/assets/img/logo.png";
    document.getElementById("producto-vendedor").textContent = producto.vendedorNombre || "-";

    if (producto.categoriaNombre) {
      document.getElementById("producto-categoria").textContent = producto.categoriaNombre;
    } else if (producto.categoriaId) {
      const categoria = await getCategoriaById(producto.categoriaId).catch(() => null);
      document.getElementById("producto-categoria").textContent = categoria?.nombre || "-";
    }

    document.getElementById("subasta-estado").innerHTML = estadoBadge(subasta.estado);
    const montoActual = subasta.montoActual ?? subasta.precioInicial;
    document.getElementById("subasta-monto").textContent = formatMoney(montoActual);
    document.getElementById("subasta-incremento").textContent = formatMoney(subasta.incrementoMinimo);
    document.getElementById("subasta-inicio").textContent = formatDate(subasta.fechaInicio);
    document.getElementById("subasta-cierre").textContent = formatDate(subasta.fechaCierre);

    if (subasta.usuarioGanadorEmail) {
      document.getElementById("ganador-container").style.display = "block";
      document.getElementById("subasta-ganador").textContent = obfuscateEmail(subasta.usuarioGanadorEmail);
    } else {
      document.getElementById("ganador-container").style.display = "none";
    }

    const minimoSugerido = Number(montoActual) + Number(subasta.incrementoMinimo);
    montoInput.min = minimoSugerido;
    montoInput.value = minimoSugerido;

    if (!["PUBLICADA", "ACTIVA"].includes(subasta.estado)) {
      pujaCard.style.display = "none";
      showAlert("Esta subasta no está aceptando ofertas en este momento.");
    }
  } catch (error) {
    console.error(error);
    nombreElement.textContent = "Error al cargar";
    showAlert("No se pudo cargar la subasta: " + (error.message || ""), "danger");
    pujaCard.style.display = "none";
  }
}

document.getElementById("form-puja").addEventListener("submit", async (event) => {
  event.preventDefault();
  hideError(pujaError);
  pujaSuccess.style.display = "none";

  const monto = Number(montoInput.value);
  const minimo = Number(subastaActual?.montoActual ?? subastaActual?.precioInicial ?? 0) + Number(subastaActual?.incrementoMinimo ?? 0);

  if (monto < minimo) {
    showError(pujaError, `Tu oferta debe ser de al menos ${formatMoney(minimo)}.`);
    return;
  }

  const button = document.getElementById("puja-button");
  button.disabled = true;

  try {
    await crearPuja({ subastaId: Number(subastaId), monto });
    pujaSuccess.textContent = "¡Oferta registrada con éxito!";
    pujaSuccess.style.display = "block";
    init();
  } catch (error) {
    console.error(error);
    if (error.status === 404) {
      showError(pujaError, "El sistema de pujas todavía no está disponible en el servidor (endpoint /api/pujas pendiente).");
    } else {
      showError(pujaError, error.message || "No se pudo registrar la oferta.");
    }
  } finally {
    button.disabled = false;
  }
});

function obfuscateEmail(email) {
  if (!email) return "-";
  const parts = email.split("@");
  if (parts.length !== 2) return email;
  const [localPart, domain] = parts;
  const obfuscatedLocal = localPart.length > 2 
    ? localPart.substring(0, 2) + "*".repeat(localPart.length - 2)
    : localPart[0] + "*";
  const domainParts = domain.split(".");
  const obfuscatedDomain = domainParts[0].length > 1
    ? domainParts[0][0] + "*".repeat(domainParts[0].length - 1)
    : "*";
  const tld = domainParts.slice(1).join(".");
  return `${obfuscatedLocal}@${obfuscatedDomain}.${tld}`;
}

init();
