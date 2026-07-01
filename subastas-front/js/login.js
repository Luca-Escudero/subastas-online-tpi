import { loginUser, redirectToDashboard } from "./modules/auth.js";
import { showError, hideError } from "./modules/utils.js";

const form = document.getElementById("form-login");
const errorBox = document.getElementById("error");
const logButton = document.getElementById("log-button");

form.addEventListener("submit", async (event) => {
  event.preventDefault();
  hideError(errorBox);

  const email = document.getElementById("email").value.trim();
  const password = document.getElementById("password").value.trim();

  logButton.disabled = true;
  logButton.textContent = "Ingresando...";

  try {
    const session = await loginUser(email, password);
    redirectToDashboard(session);
  } catch (error) {
    console.error(error);
    showError(errorBox, error.message || "Email o contraseña incorrectos.");
  } finally {
    logButton.disabled = false;
    logButton.textContent = "Iniciar Sesión";
  }
});
