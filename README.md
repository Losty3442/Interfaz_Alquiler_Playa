# Alquiler de Equipos de Playa (Spring Boot Web App)

Aplicación web desarrollada en Java con Spring Boot, Thymeleaf y conectada a PostgreSQL (Supabase).

## Requisitos
- JDK 17+
- Maven 3.8+
- Conexión a Internet (Base de datos en la nube)

## Ejecutar localmente
```bash
mvn spring-boot:run
```
La aplicación estará disponible en `http://localhost:8080`.

## Despliegue en Railway
Este proyecto está optimizado para desplegarse en **Railway**.

1.  **Sube tu código a GitHub**.
2.  En Railway, crea un "New Project" -> "Deploy from GitHub repo".
3.  Selecciona tu repositorio.
4.  Railway detectará automáticamente que es un proyecto Maven y generará el Build.
5.  (Opcional) Si necesitas definir variables de entorno, ve a la pestaña "Variables" en Railway.

El archivo `pom.xml` ya incluye las dependencias necesarias (`spring-boot-starter-web`, `postgresql`, `HikariCP`).

## Estructura del Proyecto
- `src/main/java`: Código fuente Java.
  - `com.playa.alquiler.controller`: Controladores Web.
  - `com.playa.alquiler.db`: Gestión de conexión (HikariCP).
  - `com.playa.alquiler.dao`: Acceso a datos.
- `src/main/resources`:
  - `templates/`: Vistas HTML (Thymeleaf).
  - `static/`: CSS, JS e imágenes.
  - `application.properties`: Configuración de Spring.

## Notas
- La conexión a base de datos usa **HikariCP** para alto rendimiento.
- El puerto por defecto es `8080` (Railway lo asigna dinámicamente con la variable `$PORT`).
