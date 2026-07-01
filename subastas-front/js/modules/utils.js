export function formatMoney(value) {
  if (value === null || value === undefined) return "-";
  return new Intl.NumberFormat("es-AR", { style: "currency", currency: "ARS" }).format(value);
}

export function formatDate(value) {
  if (!value) return "-";
  const date = new Date(value);
  return date.toLocaleString("es-AR", { dateStyle: "short", timeStyle: "short" });
}

export function showError(container, message) {
  if (!container) {
    alert(message);
    return;
  }
  container.textContent = message;
  container.style.display = "block";
}

export function hideError(container) {
  if (!container) return;
  container.style.display = "none";
  container.textContent = "";
}

const ESTADO_BADGES = {
  BORRADOR: "bg-secondary",
  PUBLICADA: "bg-info",
  ACTIVA: "bg-success",
  FINALIZADA: "bg-dark",
  CANCELADA: "bg-danger",
  ADJUDICADA: "bg-primary",
  EN_DISPUTA: "bg-warning",
};

export function estadoBadge(estado) {
  const cls = ESTADO_BADGES[estado] || "bg-secondary";
  return `<span class="badge ${cls}">${estado}</span>`;
}
