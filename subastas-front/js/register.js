import { registerUser } from "./modules/auth.js";
import { showError, hideError } from "./modules/utils.js";

const form = document.getElementById("form-register");
const errorBox = document.getElementById("error");
const successBox = document.getElementById("success");
const registerButton = document.getElementById("register-button");

form.addEventListener("submit", async (event) => {
  event.preventDefault();
  hideError(errorBox);
  successBox.style.display = "none";

  const datos = {
    nombre: document.getElementById("nombre").value.trim(),
    apellido: document.getElementById("apellido").value.trim(),
    email: document.getElementById("email").value.trim(),
    telefono: document.getElementById("telefono").value.trim(),
    password: document.getElementById("password").value,
  };

  registerButton.disabled = true;
  registerButton.textContent = "Creando cuenta...";

  try {
    await registerUser(datos);
    successBox.textContent = "Cuenta creada con éxito. Ya podés iniciar sesión.";
    successBox.style.display = "block";
    form.reset();
    setTimeout(() => {
      window.location.href = "login.html";
    }, 1500);
  } catch (error) {
    console.error(error);
    showError(errorBox, error.message || "No se pudo crear la cuenta. Intentá de nuevo.");
  } finally {
    registerButton.disabled = false;
    registerButton.textContent = "Registrarme";
  }
});
