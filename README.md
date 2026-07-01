# TPI: Sistema de Subastas Online con Cierre Temporal y Puja Segura

## Información del Grupo
* **Universidad:** Universidad Tecnológica Nacional (UTN) - Facultad Regional Villa María
* **Materia:** Programación IV
* **Integrantes:** Luca Escudero & Tomás Grasso

## Estructura del Repositorio
Este es un proyecto full-stack estructurado en dos carpetas principales:
* **`/subastas-online-tpi`**: Backend desarrollado en Java con Spring Boot.
* **`/subastas-front`**: Frontend desarrollado en Vanilla HTML, CSS (Bootstrap 5 + plantilla AdminLTE) y JS ES6.

## Descripción del Proyecto
Este sistema de subastas online garantiza la integridad de las pujas en entornos concurrentes, respeta reglas temporales estrictas de apertura/cierre de subastas (con evaluación perezosa en UTC) y protege la privacidad de los usuarios controlando permisos basados en roles (**USER**, **SELLER**, **ADMIN**).

## Stack Tecnológico
* **Backend:** Java 21, Spring Boot 3, Hibernate/JPA, MySQL, Spring Security + JWT, Swagger UI (OpenAPI 3).
* **Frontend:** HTML5, CSS3 (Bootstrap 5, AdminLTE), JavaScript Moderno (módulos ES6).

---

## ⚙️ Configuración y Puesta en Marcha

Este proyecto utiliza variables de entorno para aislar las credenciales y garantizar la seguridad del código fuente.

### 1. Configuración del Entorno (.env)
Para que el sistema se conecte a la base de datos local, es estrictamente necesario configurar las variables de entorno:

1. En la raíz del proyecto, localizar el archivo de plantilla llamado `.env.example`.
2. Duplicar ese archivo y renombrar la copia exactamente como `.env` (este archivo está ignorado en Git por seguridad).
3. Abrir el nuevo archivo `.env` y completar las credenciales con las de su motor MySQL local:
   * `SUBASTAS_DB_NAME`: Nombre deseado para el esquema (ej: `subastas_db`).
   * `SUBASTAS_DB_USER`: Usuario local (ej: `root`).
   * `SUBASTAS_DB_PASSWORD`: Contraseña local.
   * `SUBASTAS_JWT_SECRET`: Clave alfanumérica secreta para firmar los tokens.

### 2. Base de Datos y Tablas (Automático)
El sistema está configurado para autogestionar su infraestructura de datos. **No es necesario ejecutar scripts SQL manualmente.** Al iniciar la aplicación, el servidor detectará si la base de datos declarada en el `.env` existe; si no existe, la creará automáticamente junto con toda la estructura de tablas y relaciones gracias a Hibernate.

### 3. Ejecutar el Backend
Levante el servidor de la API ejecutando la clase principal desde su IDE, o utilizando Maven desde la terminal en la carpeta `/subastas-online-tpi`:
```bash
mvn spring-boot:run
```

### 4. Ejecutar el Frontend
Para desplegar y ver la interfaz del usuario:
1. Asegúrese de tener levantado el backend en `http://localhost:8080`.
2. Sirva los archivos de la carpeta `/subastas-front` usando un servidor estático local.
   * *Opción recomendada:* Utilizar la extensión **Live Server** de VSCode.
   * *Opción de consola:* Ejecutar `npx serve .` o `python3 -m http.server` dentro de la carpeta `/subastas-front`.
   * **Importante:** No abrir el archivo `index.html` haciendo doble clic directo (`file://`), ya que al utilizar módulos ES6 (`type="module"`), el navegador bloqueará su carga por políticas de seguridad de recursos locales.

---

## 📖 Documentación y Pruebas (Swagger UI)

Este proyecto incluye la interfaz interactiva de **Swagger / OpenAPI 3** para facilitar la prueba de todos los endpoints y evaluar la arquitectura de seguridad.

**URL de acceso:** [http://localhost:8080/swagger-ui/index.html](https://www.google.com/search?q=http://localhost:8080/swagger-ui/index.html)

### ¿Cómo probar la API con la Seguridad JWT?

La plataforma implementa un sistema de seguridad estricto basado en roles. Para acceder a las rutas protegidas:

1. Desplegar el controlador de **Autenticación/Usuarios**.
2. Utilizar el endpoint `POST /api/usuarios` para registrar un usuario, o `POST /api/auth/login` para ingresar.
3. Copiar el valor del `token` devuelto en el JSON de respuesta.
4. Subir al inicio de la página de Swagger y hacer clic en el botón verde **"Authorize"** (arriba a la derecha).
5. Pegar el token en la caja de texto (sin la palabra "Bearer") y confirmar.
6. A partir de este momento, Swagger inyectará la identidad del usuario en cada petición para superar el filtro de seguridad.
