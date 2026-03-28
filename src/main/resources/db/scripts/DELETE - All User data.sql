-- ⚠️ DEV ONLY: Hard wipe de data operativa (usuarios + castings + aplicaciones).
-- Conserva catálogos/metadata sembrados por Flyway y flyway_schema_history.
-- Ejecutar en una transacción para poder hacer ROLLBACK si hace falta.

BEGIN;

TRUNCATE TABLE
  casting_application_requirement_submission,
  casting_application,
  casting_role_skill,
  casting_role_profession,
  casting_role_remuneration,
  casting_role_characteristics,
  casting_requirement,
  casting_role,
  casting_roles_section,
  casting_attachment,
  casting_basic_info,
  casting_remuneration,
  casting,
  legal_acceptances,
  talent_social_media_link,
  talent_media_other_pictures_url,
  talent_media,
  talent_basic_info_profession,
  talent_skill,
  talent_credit,
  talent_education,
  talent_characteristics,
  talent_contact,
  employer_basic_info,
  talent_profile,
  employer_profile,
  user_roles,
  users
RESTART IDENTITY CASCADE;

COMMIT;
