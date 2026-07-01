# Subastas Online — Front

Front vanilla (HTML + Bootstrap 5 + AdminLTE + JS ES6 modules, sin React) hecho a medida
para consumir el backend `subastas-online-tpi`. Sigue la misma estructura de carpetas que
el front de Clínica Salud+ (`services/`, `modules/`, páginas por rol).

## Cómo correrlo
1. Necesitás levantar el backend en `http://localhost:8080` (ver `apiConfig.js` si cambiás el puerto).
2. Servilo con un servidor estático (ej. extensión "Live Server" de VSCode, o `npx serve .`). **No lo
   abras como `file://` directo**, porque los módulos ES6 (`type="module"`) no cargan así.
3. Si lo servís en un puerto distinto a 5500/3000, agregalo en `CorsConfig.java` del backend
   (`allowedOrigins`), si no el navegador va a bloquear las peticiones.

## Roles
- **USER**: catálogo de subastas + detalle + pujar (`pages/user/`).
- **SELLER**: crear productos, crear y publicar subastas (`pages/seller/`).
- **ADMIN**: gestionar usuarios (bloquear/activar) y categorías (`pages/admin/`).

Todo usuario que se registra queda con USER + SELLER automáticamente (así lo define el backend).
El rol ADMIN se asigna manualmente en la base de datos.

## Pendiente / limitaciones conocidas (del lado del backend)
1. **No existe `PujaController`** todavía. `js/services/pujasService.js` ya apunta a
   `POST /api/pujas`, pero hoy devuelve 404. Cuando implementes la entidad `Puja` y el
   controller, el botón "Pujar" en `subasta-detail.html` va a empezar a funcionar solo.
2. **No hay endpoint `/api/usuarios/me`**. Por eso el panel de vendedor (`home-seller.html`)
   lista TODOS los productos/subastas del sistema, no solo los del vendedor logueado — no hay
   forma de filtrar "los míos" desde el front sin esa info. Convendría agregar ese endpoint,
   o que `GET /api/productos` acepte un filtro `?vendedorId=` o devuelva solo lo propio para SELLER.
3. CORS estaba deshabilitado por completo; se agregó `CorsConfig.java` + `.cors(...)` en
   `SecurityConfig.java` para permitir que el front llame a la API desde otro puerto.
