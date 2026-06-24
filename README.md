# TPI: Sistema de Subastas Online con Cierre Temporal y Puja Segura

## Información del Grupo
* **Universidad:** Universidad Tecnológica Nacional (UTN) - Facultad Regional Villa María
* **Materia:** Programación IV
* **Integrantes:** Luca Escudero & Tomás Grasso

## Descripción del Proyecto
Este repositorio contiene el diseño y desarrollo completo del Backend (API REST, Base de Datos, Seguridad y Lógica de Negocio) para una plataforma de subastas online. El sistema garantiza la integridad de las pujas bajo entornos de alta concurrencia, respeta reglas temporales estrictas y protege la privacidad de los usuarios según sus roles (USER, SELLER, ADMIN).

## Stack Tecnológico
* **Lenguaje:** Java (JDK 21)
* **Framework Principal:** Spring Boot
* **Base de Datos:** MySQL
* **Seguridad:** Spring Security + JWT (JSON Web Tokens)

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

### 3. Ejecutar la Aplicación
Levantar el servidor ejecutando la clase principal `Application.java` desde su IDE, o utilizando Maven desde la terminal:
```bash
mvn spring-boot:run

```

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
