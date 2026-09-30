# AutoCasting-Backend

# 🎬 Auto Casting – Backend

**Auto Casting** es una plataforma que conecta actores con castineras de forma fácil, moderna y eficiente.  
Este repositorio contiene el backend desarrollado con **Java 21**, **Spring Boot 3.5.0**, **Gradle** y **PostgreSQL**.

---

## 🚀 Funcionalidades del MVP

- Registro y login de actores vía formulario o autenticación OAuth (Google, Apple, etc.).
- Creación y edición del perfil del actor con fotos, videos, características físicas, etc.
- Panel para castineras donde podrán:
    - Buscar y filtrar actores registrados.
    - Acceder a su perfil público.
    - Contactarlos.

---

## 🧱 Tech Stack

| Tecnología        | Descripción                             |
|-------------------|-----------------------------------------|
| Java 21           | Lenguaje principal                      |
| Spring Boot 3.5   | Framework principal                     |
| Spring Security   | Autenticación y autorización            |
| PostgreSQL        | Base de datos relacional                |
| JPA (Hibernate)   | ORM para acceso a datos                 |
| Gradle            | Build tool                              |
| Lombok            | Anotaciones para simplificar el código  |
| OAuth2 Client     | Login con Google/Apple/Facebook/etc.    |
| JWT               | Autenticación basada en tokens          |
| Springdoc OpenAPI | Documentación interactiva vía Swagger   |
| Docker (opcional) | Contenedores para entorno de desarrollo |

---

## 🔍 Documentación API (Swagger)

http://localhost:8080/swagger-ui.html

---

## 🚢 Deploy (Fly.io — Argentina)

| Item | Valor |
|------|-------|
| App | `autocasting-ar-api` |
| URL | https://autocasting-ar-api.fly.dev |
| Región | `gru` (São Paulo) |
| Máquina | 1 × `shared-cpu-1x`, 768 MB, siempre encendida (el cron de cierre de castings la necesita) |
| Base de datos + storage | Supabase `autocasting-latam` (`savubzaypraqdehudynb`, `sa-east-1`) |
| Config | `fly.toml` (solo tuning de JVM/pool/threads en `[env]`; toda la configuración de la app vive en Fly secrets) |

**Deploy (manual, desde la terminal):**
```bash
git checkout release/x.y.z
git pull
git status   # debe estar limpio: los cambios sin commitear también se suben
fly deploy
```
Correr desde `AutoCasting-Backend` (usa `fly.toml` de esa carpeta). Fly no conoce ramas: despliega lo que esté en la carpeta.

**Secrets requeridos en Fly** (solo nombres; los valores viven en Fly y en el gestor de contraseñas, nunca en git):
`DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`, `SUPABASE_URL`, `SUPABASE_SERVICE_ROLE_KEY`, `SUPABASE_MEDIA_BUCKET`, `SUPABASE_LEGAL_BUCKET`, `SUPABASE_LEGAL_PREFIX`, `SUPABASE_LEGAL_PDF_TTL_SECONDS`, `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET`, `GOOGLE_REDIRECT_URL`, `GOOGLE_MOBILE_IOS_CLIENT_ID`, `GOOGLE_MOBILE_ANDROID_CLIENT_ID`, `MAIL_USER`, `MAIL_PASS`, `FRONTEND_URL`, `BACKEND_URL`, `APP_JOBS_CLOSE_EXPIRED_CASTINGS_ENABLED`, `AUTO_CLOSE_CASTINGS_CRON`, `AUTO_CLOSE_CASTINGS_ZONE`.
Los secrets tienen prioridad sobre `[env]` de `fly.toml`: no duplicar nombres entre ambos.
`DATABASE_URL` usa el session pooler de Supabase (puerto 5432, IPv4); el transaction pooler (6543) rompe Flyway.

**Comandos útiles:** `fly logs`, `fly status`, `fly secrets list`, `fly scale show`. Parada de emergencia (corta todo el costo): `fly scale count 0`.

---

## ✅ Testing

- JUnit 5
- Mockito
- TEstcontainers para PostgreSQL (Opcional)

---

## License

Este proyecto está bajo licencia privada de [PadiMasso Software].

---

## 👥 Equipo

CTO: Tomas Padilla

CEO: Matias Di Masso

COO: Juan Waldmann

Repositorio principal: https://github.com/PadiMassoOrg