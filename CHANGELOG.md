# Changelog

## 2026-09-23

- Versión: `1.3.4`
- Fix: el tipo de tarifa "A convenir" (`to_be_agreed`) ya no exige un monto al guardar un rol de casting ni bloquea la publicación del casting — se trata como "sin tarifa fija" igual que "No remunerado"/"Colaborativo"/"Cooperativo" (`PayRateTypeSupport.isUnpaidLike`, ahora compartido entre `CastingRoleServiceImpl`, `CastingServiceImpl.isPublishable` y `CastingMapper`).
- Fix: los roles con tarifa "sin monto fijo" (No remunerado, A convenir, Colaborativo, Cooperativo) ahora completan la moneda con ARS por defecto si no se especifica una, en vez de dejarla en `null` para algunos casos — evita que el selector de moneda del formulario quede vacío al abrir un rol existente.
- Fix: el orden de los roles dentro de un casting (`CastingEntity.roles`) ahora es estable (`@OrderBy("createdAt ASC")`) — antes era un `Set` sin orden garantizado, por lo que la lista de roles podía reordenarse al editar un rol.
- Scripts: `SEED_DEMO_30_USERS_3_EMPLOYERS.sql` actualizado para reflejar el nuevo comportamiento de moneda por defecto en roles "A convenir"/"No remunerado".

## 2026-09-20

- Versión: `1.3.3`
- Fix: los endpoints de applicants del employer (`GET /employer/castings/{slug}/applicants` y su variante `/grouped`) ya no devuelven postulantes cuyo perfil de talento no es visible en el catálogo público (perfil/cuenta borrado o suspendido, o sin foto de cara y de cuerpo completo) — mismo criterio que `TalentProfileSpecs`/`TalentCatalogVisibility`.
- Refactor: se consolida la regla de "visible en catálogo" (borrado/suspendido/fotos requeridas), antes reimplementada por separado en `TalentProfileSpecs`, `AdminUserSpecs` y `CastingApplicationSpecs`, en dos únicas fuentes de verdad: `TalentProfileSpecs.visibleInCatalogPredicate` (nivel SQL, reutilizable desde cualquier `From` unido a `TalentProfileEntity`) y `TalentCatalogVisibility.isVisibleInCatalog` (nivel Java, ahora también delegando el check de fotos a `TalentMediaRequirements`). Sin cambio de comportamiento en los call sites existentes.

## 2026-09-19 (2)

- Versión: `1.3.2`
- Scheduler: `CastingDeadlineScheduler.closeExpiredCastings()` ahora captura cualquier excepción del auto-cierre de castings y la loguea explícitamente en nivel ERROR, en vez de dejar que el `LoggingErrorHandler` genérico de Spring la trague en silencio.
- CORS: se agrega `https://www.autocasting.app` a los orígenes permitidos (ya estaba `https://autocasting.app` sin `www`).
- Ops: se fija `MALLOC_ARENA_MAX=2` en el dyno de Heroku para acotar el overhead nativo de malloc de glibc, como paso inicial (Fase 0) de la propuesta de migración de infraestructura, antes de considerar un upgrade de plan pago.

## 2026-09-19

- Versión: `1.3.1`
- Admin: nuevo endpoint `DELETE /admin/users/{userId}/profiles/talent/media/{slot}` para quitar una foto puntual (headshot, foto de cuerpo completo, u otra foto por índice) del perfil de un talento sin suspender la cuenta. Motivo obligatorio, auditado en el historial (`TALENT_PROFILE`).
- Storage: el borrado también elimina el archivo en Supabase Storage usando el service-role key server-side (nunca expuesto a un cliente). Requiere la variable de entorno `SUPABASE_MEDIA_BUCKET` además de `SUPABASE_URL`/`SUPABASE_SERVICE_ROLE_KEY`, ya configuradas.

## 2026-09-18

- Versión: `1.3.0`
- CI: nuevo workflow de GitHub Actions (build + test contra Postgres) en push/PR a `main`, `develop` y `release/**`.
- Tests: nuevas suites de tests unitarios (JUnit 5 + Mockito) cubriendo validación de Casting/Casting Role, el resumen de checkout de castings del employer, el onboarding y creación de perfil de employer, y las transiciones de estado no restringidas de postulantes en el flujo de revisión del employer.
- Scripts: `SEED_DEMO_30_USERS_3_EMPLOYERS.sql` ahora siembra 6 registros de educación y 6 de créditos por talento demo, para poder testear manualmente el scroll de esos paneles en el perfil público.

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
