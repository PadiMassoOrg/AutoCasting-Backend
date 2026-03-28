# Hard Wipe de Base de Datos (sin perder data base de Flyway)

Este proyecto incluye el script:

- `src/main/resources/db/scripts/DELETE - All User data.sql`

Objetivo:

- Borrar la data operativa creada en uso normal de la app (usuarios, perfiles, castings y aplicaciones).
- Mantener la data estructural/base que viene de migraciones Flyway (catálogos, opciones, metadatos).
- Evitar tener que redeployar o rehacer migraciones para seguir trabajando en desarrollo.

## Que SI elimina

El script hace `TRUNCATE ... RESTART IDENTITY CASCADE` sobre tablas operativas:

- Usuarios y perfiles:
  - `users`
  - `user_roles`
  - `talent_profile`
  - `talent_basic_info`
  - `talent_contact`
  - `talent_characteristics`
  - `talent_media`
  - `talent_media_other_pictures_url`
  - `talent_social_media_link`
  - `talent_skill`
  - `talent_basic_info_profession`
  - `talent_credit`
  - `talent_education`
  - `employer_profile`
  - `employer_basic_info`
  - `legal_acceptances`

- Castings:
  - `casting`
  - `casting_basic_info`
  - `casting_attachment`
  - `casting_roles_section`
  - `casting_role`
  - `casting_requirement`
  - `casting_role_characteristics`
  - `casting_role_remuneration`
  - `casting_role_profession`
  - `casting_role_skill`
  - `casting_remuneration`

- Aplicaciones:
  - `casting_application`
  - `casting_application_requirement_submission`

## Que NO elimina

No toca tablas de referencia/sitemetadata cargadas por Flyway, por ejemplo:

- `roles`, `plans`
- `gender_option`, `color_option`, `diet_option`, `ethnicity_option`
- `professions`, `skills`, `production_type`
- `social_media_option`, `company_type_option`
- `project_type_option`, `casting_status_option`, `casting_section_status_option`
- `casting_modality_option`, `role_type_option`
- `casting_compensation_type_option`, `pay_rate_type_option`, `currency_option`
- `casting_application_status_option`
- `legal_documents`
- `flyway_schema_history`

## Como ejecutarlo

Ejemplo en PostgreSQL:

```bash
psql "$DATABASE_URL" -f "src/main/resources/db/scripts/DELETE - All User data.sql"
```

## Advertencias

- Es destructivo: pensado para `dev/test`, no para producción.
- Si agregas nuevas tablas operativas en futuras migraciones, sumalas a este script.
