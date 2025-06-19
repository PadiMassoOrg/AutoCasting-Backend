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

| Tecnología       | Descripción                                   |
|------------------|-----------------------------------------------|
| Java 21          | Lenguaje principal                            |
| Spring Boot 3.5  | Framework principal                           |
| Spring Security  | Autenticación y autorización                  |
| PostgreSQL       | Base de datos relacional                      |
| JPA (Hibernate)  | ORM para acceso a datos                       |
| Gradle           | Build tool                                    |
| Lombok           | Anotaciones para simplificar el código        |
| OAuth2 Client    | Login con Google/Apple/Facebook/etc.          |
| JWT              | Autenticación basada en tokens                |
| Springdoc OpenAPI| Documentación interactiva vía Swagger         |
| Docker (opcional)| Contenedores para entorno de desarrollo       |

---

## 🔍 Documentación API (Swagger)

http://localhost:8080/swagger-ui.html

---

##  ✅ Testing

- JUnit 5
- Mockito
- TEstcontainers para PostgreSQL (Opcional)

---

##  License

Este proyecto está bajo licencia privada de [PadiMasso Software].

---

##  👥 Equipo
CTO: Tomas Padilla

CEO: Matias Di Masso

COO: Juan Waldmann

Repositorio principal: https://github.com/PadiMassoOrg