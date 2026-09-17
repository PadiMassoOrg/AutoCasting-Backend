# Changelog

## 2026-09-17

- Versión: `1.2.5`
- UI: el email de reset de contraseña se alinea visualmente con el nuevo email de bienvenida de talento (logo más grande, mismo padding de header, footer con ícono de Instagram + links en vez del bloque de soporte técnico).
- Cleanup: se elimina la variable de contexto `supportEmail` y las keys `mail.footer.support`/`mail.footer.address`, ya sin uso tras el cambio de footer.

## 2026-09-17

- Versión: `1.2.4`
- Talent: email de bienvenida automático al confirmar el último paso del onboarding (solo TALENT). El template varía según visibilidad en catálogo: perfil completo -> link al perfil público; perfil incompleto (sin fotos) -> invitación a completar el perfil, sin exponer un link público con imágenes placeholder.
- Admin: nueva acción masiva "Enviar email de bienvenida" en el listado de usuarios (selección múltiple + endpoint `POST /admin/users/bulk/welcome-email`).
- Fix: el remitente de los emails mostraba "info" en vez de "autocasting" como nombre visible.
- Scripts: nuevo `manual-scripts/HARD_DELETE_USER_BY_EMAIL.sql` (dev-only) para borrar un usuario y todo lo asociado por email, para testeo manual repetido.

## 2026-09-17

- Versión: `1.2.3`
- Admin: nuevo filtro "no visible en catálogo" en el listado de usuarios (`GET /admin/users?notVisibleInCatalog=true`) — detecta talentos ocultos del catálogo público por falta de fotos, suspensión o borrado.
- Tests: `CastingStatusTransitionPolicyTest` cubriendo las transiciones de estado de castings.

## 2026-09-17

- Versión: `1.2.2`
- Hotfix: `Procfile` apuntaba a `autocasting-1.2.0.jar` hardcodeado; tras el bump de versión a `1.2.1` el jar generado no coincidía y el dyno crasheaba en boot (H10) en cada deploy. Cambiado a `build/libs/*.jar` para no depender de la versión.

## 2026-09-10

- Versión: `1.2.0`
- Auth: refresh-token support (`RefreshTokenEntity`, `V50__add_refresh_tokens`), rotation, `/auth/refresh` and `/auth/logout` (body or HttpOnly cookie), single access-token renewal path for web + mobile.
- Auth: native Google Sign-In for mobile (`/auth/google/mobile`, `GoogleIdTokenVerifierService`), `users.google_id` (`V48`), `GOOGLE` account provider.
- Legal: Terms & Privacy release 3.6.0 published via `V49` — adds explicit mobile-app coverage (secure device storage, app sessions). Terms versioning stores DB entry with email.
- Infra: Dockerfile + `.dockerignore` for Render deployment.
- Seeder: reworked demo seed (`SEED_DEMO_30_USERS_3_EMPLOYERS.sql`).

## 2026-07-02

- Versión: `1.0.4`
- Admin tool handles User Upsert
- Fetch Pages

## 2026-06-23

- Versión: `1.0.3`
- Corrección del CUIT/CUIL en los documentos legales de Terms and Conditions y Privacy Policy.
- Nueva release legal `3.5.0` en backend.

## 2026-06-19

- Versión: `1.0.2`
- Admin Tool basic functionalities.
- Base administrativa inicial para usuarios y castings.

## 2026-06-18

- Versión: `1.0.1`
- Hotfix del envío de emails de reset password.
- Se corrigió la configuración SMTP para usar la casilla de Hostinger `info@autocasting.app`.

## 2026-06-17

- Versión: `1.0.0`
- MVP 1
- Primera versión funcional de la plataforma.
- Publicación inicial de los términos legales y la política de privacidad.
- Base preparada para el flujo de aceptación legal y versionado.
