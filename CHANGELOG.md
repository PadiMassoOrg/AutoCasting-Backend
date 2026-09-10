# Changelog

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
