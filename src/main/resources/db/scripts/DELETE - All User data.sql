-- ⚠️ DEV ONLY: esto borra TODOS los usuarios y sus datos relacionados
-- Ejecutar en una transacción para poder hacer ROLLBACK si hace falta.

BEGIN;

TRUNCATE TABLE
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
