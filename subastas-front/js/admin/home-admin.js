import { requireRole, logout } from "../modules/auth.js";
import { getAllUsuarios, cambiarEstadoUsuario } from "../services/usuariosService.js";
import { getAllCategorias, crearCategoria } from "../services/categoriasService.js";
import { showError, hideError } from "../modules/utils.js";

if (!requireRole("ADMIN")) {
  throw new Error("redirecting");
}

document.getElementById("logout-button").addEventListener("click", (e) => {
  e.preventDefault();
  logout();
});

const usuariosTableBody = document.getElementById("usuarios-table-body");
const categoriasList = document.getElementById("categorias-list");

async function cargarUsuarios() {
  const usuarios = await getAllUsuarios();
  usuariosTableBody.innerHTML = usuarios
    .map(
      (u) => `
      <tr>
        <td>${u.nombre} ${u.apellido}</td>
        <td>${u.email}</td>
        <td>${u.telefono ?? "-"}</td>
        <td>${(u.roles ?? []).join(", ")}</td>
        <td>${u.activo ? '<span class="badge bg-success">Activo</span>' : '<span class="badge bg-danger">Bloqueado</span>'}</td>
        <td>
          <button class="btn btn-sm ${u.activo ? "btn-danger" : "btn-success"}" data-toggle-id="${u.id}" data-activo="${u.activo}">
            ${u.activo ? "Bloquear" : "Activar"}
          </button>
        </td>
      </tr>
    `
    )
    .join("");
}

async function cargarCategorias() {
  const categorias = await getAllCategorias();
  categoriasList.innerHTML = categorias
    .map((c) => `<span class="badge bg-secondary p-2">${c.nombre}</span>`)
    .join("");
}

usuariosTableBody.addEventListener("click", async (event) => {
  const id = event.target.dataset.toggleId;
  if (!id) return;
  const activo = event.target.dataset.activo === "true";
  try {
    await cambiarEstadoUsuario(id, !activo);
    await cargarUsuarios();
  } catch (error) {
    alert(error.message || "No se pudo cambiar el estado del usuario.");
  }
});

document.getElementById("form-categoria").addEventListener("submit", async (event) => {
  event.preventDefault();
  const errorBox = document.getElementById("categoria-error");
  hideError(errorBox);

  const nombre = document.getElementById("categoria-nombre").value.trim();
  try {
    await crearCategoria(nombre);
    event.target.reset();
    await cargarCategorias();
  } catch (error) {
    showError(errorBox, error.message || "No se pudo crear la categoría.");
  }
});

Promise.all([cargarUsuarios(), cargarCategorias()]).catch((error) => {
  console.error(error);
  alert("Error al cargar el panel de administración: " + (error.message || ""));
});
