import { requireRole, getSession, logout, hasRole } from "../modules/auth.js";
import { getAllSubastas } from "../services/subastasService.js";
import { getAllCategorias } from "../services/categoriasService.js";
import { getAllProductos } from "../services/productosService.js";
import { formatMoney, formatDate, estadoBadge } from "../modules/utils.js";

if (!requireRole("USER")) {
  throw new Error("redirecting");
}

document.getElementById("logout-button").addEventListener("click", (e) => {
  e.preventDefault();
  logout();
});

const session = getSession();
document.getElementById("navbar-user-email").textContent = session?.email ?? "Usuario";

if (hasRole("SELLER")) {
  document.getElementById("seller-nav-item").style.display = "block";
}

const container = document.getElementById("subastas-container");
const loadingMsg = document.getElementById("loading-msg");
const emptyMsg = document.getElementById("empty-msg");
const filtroCategoria = document.getElementById("filtro-categoria");

let subastas = [];
let productosById = new Map();
let categoriasById = new Map();

function renderCard(subasta) {
  const producto = productosById.get(subasta.productoId);
  const categoriaNombre = producto ? categoriasById.get(producto.categoriaId)?.nombre : null;
  const imagen = producto?.imagenUrl || "/assets/img/logo.png";
  const montoActual = subasta.montoActual ?? subasta.precioInicial;

  const card = document.createElement("div");
  card.className = "col-md-4 mb-4";
  card.innerHTML = `
    <div class="card h-100 shadow-sm">
      <img src="${imagen}" class="card-img-top" alt="${producto?.nombre ?? "Producto"}" style="height:180px;object-fit:cover" onerror="this.src='/assets/img/logo.png'">
      <div class="card-body d-flex flex-column">
        <h5 class="card-title">${producto?.nombre ?? "Producto #" + subasta.productoId}</h5>
        <p class="card-text text-muted small mb-1">${categoriaNombre ? "Categoría: " + categoriaNombre : ""}</p>
        <p class="mb-1">${estadoBadge(subasta.estado)}</p>
        <p class="mb-1">Oferta actual: <strong>${formatMoney(montoActual)}</strong></p>
        <p class="mb-1 small text-muted">Inicia: ${formatDate(subasta.fechaInicio)}</p>
        <p class="mb-2 small text-muted">Cierra: ${formatDate(subasta.fechaCierre)}</p>
        <a href="subasta-detail.html?id=${subasta.id}" class="btn btn-primary mt-auto">Ver detalle</a>
      </div>
    </div>
  `;
  return card;
}

function applyFilter() {
  const categoriaId = filtroCategoria.value;
  container.innerHTML = "";

  const visibles = subastas.filter((s) => {
    if (!["PUBLICADA", "ACTIVA"].includes(s.estado)) return false;
    if (!categoriaId) return true;
    const producto = productosById.get(s.productoId);
    return producto && String(producto.categoriaId) === categoriaId;
  });

  if (visibles.length === 0) {
    emptyMsg.style.display = "block";
  } else {
    emptyMsg.style.display = "none";
    visibles.forEach((s) => container.appendChild(renderCard(s)));
  }
}

filtroCategoria.addEventListener("change", applyFilter);

async function init() {
  try {
    const [subastasData, productosData, categoriasData] = await Promise.all([
      getAllSubastas(),
      getAllProductos(),
      getAllCategorias(),
    ]);

    subastas = subastasData ?? [];
    productosById = new Map((productosData ?? []).map((p) => [p.id, p]));
    categoriasById = new Map((categoriasData ?? []).map((c) => [c.id, c]));

    categoriasData?.forEach((c) => {
      const opt = document.createElement("option");
      opt.value = c.id;
      opt.textContent = c.nombre;
      filtroCategoria.appendChild(opt);
    });

    loadingMsg.style.display = "none";
    applyFilter();
  } catch (error) {
    console.error(error);
    loadingMsg.textContent = "No se pudieron cargar las subastas. " + (error.message || "");
  }
}

init();
