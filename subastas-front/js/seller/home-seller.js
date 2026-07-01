import { requireRole, logout } from "../modules/auth.js";
import { getAllCategorias } from "../services/categoriasService.js";
import { getAllProductos, crearProducto, eliminarProducto, actualizarProducto } from "../services/productosService.js";
import { getAllSubastas, crearSubasta, publicarSubasta, eliminarSubasta } from "../services/subastasService.js";
import { getUsuarioMe } from "../services/usuariosService.js";
import { formatMoney, formatDate, estadoBadge, showError, hideError } from "../modules/utils.js";

if (!requireRole("SELLER")) {
  throw new Error("redirecting");
}

document.getElementById("logout-button").addEventListener("click", (e) => {
  e.preventDefault();
  logout();
});

let currentUserId = null;

const categoriaSelect = document.getElementById("prod-categoria");
const editCategoriaSelect = document.getElementById("edit-prod-categoria");
const productoSelect = document.getElementById("sub-producto");
const productosTableBody = document.getElementById("productos-table-body");
const subastasTableBody = document.getElementById("subastas-table-body");

const editProdModal = new bootstrap.Modal(document.getElementById("modal-editar-producto"));
const formEditarProducto = document.getElementById("form-editar-producto");
const editErrorBox = document.getElementById("edit-producto-error");

let categoriasById = new Map();
let productosById = new Map();

async function cargarCategorias() {
  const categorias = await getAllCategorias();
  categoriasById = new Map(categorias.map((c) => [c.id, c]));
  const optionsHtml = categorias.map((c) => `<option value="${c.id}">${c.nombre}</option>`).join("");
  categoriaSelect.innerHTML = optionsHtml;
  if (editCategoriaSelect) {
    editCategoriaSelect.innerHTML = optionsHtml;
  }
}

async function cargarProductos() {
  const productos = await getAllProductos();
  productosById = new Map(productos.map((p) => [p.id, p]));

  const misProductos = productos.filter((p) => p.vendedorId === currentUserId);

  productoSelect.innerHTML = misProductos
    .map((p) => `<option value="${p.id}">${p.nombre}</option>`)
    .join("");

  productosTableBody.innerHTML = misProductos
    .map(
      (p) => `
      <tr>
        <td>${p.nombre}</td>
        <td>${p.categoriaNombre ?? categoriasById.get(p.categoriaId)?.nombre ?? "-"}</td>
        <td>
          <button class="btn btn-sm btn-warning me-1" data-edit-producto="${p.id}">Editar</button>
          <button class="btn btn-sm btn-danger" data-delete-producto="${p.id}">Eliminar</button>
        </td>
      </tr>
    `
    )
    .join("");
}

async function cargarSubastas() {
  const subastas = await getAllSubastas();

  const misSubastas = subastas.filter((s) => {
    const prod = productosById.get(s.productoId);
    return prod && prod.vendedorId === currentUserId;
  });

  subastasTableBody.innerHTML = misSubastas
    .map((s) => {
      const producto = productosById.get(s.productoId);
      const montoActual = s.montoActual ?? s.precioInicial;
      const puedePublicar = s.estado === "BORRADOR";
      return `
        <tr>
          <td>${producto?.nombre ?? "Producto #" + s.productoId}</td>
          <td>${estadoBadge(s.estado)}</td>
          <td>${formatMoney(montoActual)}</td>
          <td>${formatDate(s.fechaInicio)}</td>
          <td>${formatDate(s.fechaCierre)}</td>
          <td>${s.usuarioGanadorNombre ? `${s.usuarioGanadorNombre} (ID: ${s.usuarioGanadorId})` : "-"}</td>
          <td>
            ${puedePublicar ? `<button class="btn btn-sm btn-success" data-publicar="${s.id}">Publicar</button>` : ""}
            <button class="btn btn-sm btn-danger" data-delete-subasta="${s.id}">Eliminar</button>
          </td>
        </tr>
      `;
    })
    .join("");
}

async function cargarTodo() {
  if (!currentUserId) {
    const me = await getUsuarioMe().catch(() => null);
    if (me) {
      currentUserId = me.id;
    }
  }
  await cargarCategorias();
  await cargarProductos();
  await cargarSubastas();
}

// Crear producto
document.getElementById("form-producto").addEventListener("submit", async (event) => {
  event.preventDefault();
  const errorBox = document.getElementById("producto-error");
  hideError(errorBox);

  const datos = {
    nombre: document.getElementById("prod-nombre").value.trim(),
    descripcion: document.getElementById("prod-descripcion").value.trim(),
    imagenUrl: document.getElementById("prod-imagen").value.trim() || null,
    categoriaId: Number(categoriaSelect.value),
  };

  try {
    await crearProducto(datos);
    event.target.reset();
    await cargarProductos();
  } catch (error) {
    console.error(error);
    showError(errorBox, error.message || "No se pudo crear el producto.");
  }
});

// Crear subasta
document.getElementById("form-subasta").addEventListener("submit", async (event) => {
  event.preventDefault();
  const errorBox = document.getElementById("subasta-error");
  hideError(errorBox);

  const fechaInicioVal = document.getElementById("sub-inicio").value;
  const fechaCierreVal = document.getElementById("sub-cierre").value;

  let fechaInicio = null;
  let fechaCierre = null;

  try {
    if (fechaInicioVal) {
      const d = new Date(fechaInicioVal);
      if (isNaN(d.getTime())) {
        throw new Error("La fecha de inicio ingresada no es válida.");
      }
      fechaInicio = d.toISOString();
    }
    if (fechaCierreVal) {
      const d = new Date(fechaCierreVal);
      if (isNaN(d.getTime())) {
        throw new Error("La fecha de cierre ingresada no es válida.");
      }
      fechaCierre = d.toISOString();
    }
  } catch (e) {
    showError(errorBox, e.message);
    return;
  }

  const datos = {
    productoId: Number(productoSelect.value),
    fechaInicio,
    fechaCierre,
    precioInicial: Number(document.getElementById("sub-precio").value),
    incrementoMinimo: Number(document.getElementById("sub-incremento").value),
  };

  try {
    await crearSubasta(datos);
    event.target.reset();
    await cargarSubastas();
  } catch (error) {
    console.error(error);
    showError(errorBox, error.message || "No se pudo crear la subasta.");
  }
});

// Delegación de eventos para botones de tablas
productosTableBody.addEventListener("click", async (event) => {
  const deleteId = event.target.dataset.deleteProducto;
  const editId = event.target.dataset.editProducto;

  if (deleteId) {
    if (!confirm("¿Eliminar este producto?")) return;
    try {
      await eliminarProducto(deleteId);
      await cargarProductos();
    } catch (error) {
      alert(error.message || "No se pudo eliminar el producto.");
    }
  }

  if (editId) {
    const prod = productosById.get(Number(editId));
    if (prod) {
      document.getElementById("edit-prod-id").value = prod.id;
      document.getElementById("edit-prod-nombre").value = prod.nombre;
      document.getElementById("edit-prod-categoria").value = prod.categoriaId;
      document.getElementById("edit-prod-imagen").value = prod.imagenUrl || "";
      document.getElementById("edit-prod-descripcion").value = prod.descripcion || "";
      hideError(editErrorBox);
      editProdModal.show();
    }
  }
});

formEditarProducto.addEventListener("submit", async (event) => {
  event.preventDefault();
  hideError(editErrorBox);

  const id = Number(document.getElementById("edit-prod-id").value);
  const datos = {
    nombre: document.getElementById("edit-prod-nombre").value.trim(),
    descripcion: document.getElementById("edit-prod-descripcion").value.trim(),
    imagenUrl: document.getElementById("edit-prod-imagen").value.trim() || null,
    categoriaId: Number(document.getElementById("edit-prod-categoria").value),
  };

  const submitButton = document.getElementById("edit-producto-submit");
  submitButton.disabled = true;

  try {
    await actualizarProducto(id, datos);
    editProdModal.hide();
    await cargarTodo();
  } catch (error) {
    console.error(error);
    showError(editErrorBox, error.message || "No se pudo actualizar el producto.");
  } finally {
    submitButton.disabled = false;
  }
});

subastasTableBody.addEventListener("click", async (event) => {
  const publicarId = event.target.dataset.publicar;
  const deleteId = event.target.dataset.deleteSubasta;

  if (publicarId) {
    try {
      await publicarSubasta(publicarId);
      await cargarSubastas();
    } catch (error) {
      alert(error.message || "No se pudo publicar la subasta.");
    }
  }

  if (deleteId) {
    if (!confirm("¿Eliminar esta subasta?")) return;
    try {
      await eliminarSubasta(deleteId);
      await cargarSubastas();
    } catch (error) {
      alert(error.message || "No se pudo eliminar la subasta.");
    }
  }
});

cargarTodo().catch((error) => {
  console.error(error);
  alert("Error al cargar el panel de vendedor: " + (error.message || ""));
});
