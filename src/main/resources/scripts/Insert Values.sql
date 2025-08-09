------------------------------------------------------------------------
---- Insert Roles
------------------------------------------------------------------------
INSERT INTO roles (
  id,
  code,
  name_string_code,
  description,
  created_at,
  created_by,
  modified_at,
  modified_by,
  deleted
)

VALUES
(gen_random_uuid(), 'ACTOR', 'role.actor', 'Usuario que representa un actor', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
(gen_random_uuid(), 'CASTINERA', 'role.castinera',  'Usuario que busca y gestiona actores', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false);


------------------------------------------------------------------------
---- Insert Planes
------------------------------------------------------------------------
INSERT INTO plans (
  id,
  code,
  name_string_code,
  description,
  allows_custom_slug,
  created_at,
  created_by,
  modified_at,
  modified_by,
  deleted
)
VALUES
  (gen_random_uuid(), 'FREE', 'plan.free', 'Plan gratuito para todos los usuarios', false, NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
  (gen_random_uuid(), 'PREMIUM_ACTOR_1', 'plan.premium_actor_1', 'Permite slug personalizado', true, NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
  (gen_random_uuid(), 'PREMIUM_CASTINERA', 'plan.premium_castinera', 'Acceso a búsquedas de actores', true, NOW(), 'SYSTEM', NOW(), 'SYSTEM', false);


------------------------------------------------------------------------
---- Insert Site Metadata
------------------------------------------------------------------------
INSERT INTO skills (
  id,
  string_code,
  created_at,
  created_by,
  modified_at,
  modified_by,
  deleted)
VALUES
    (gen_random_uuid(), 'skill.clown', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'skill.physical_theater', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false ),
    (gen_random_uuid(), 'skill.fencing', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false);

INSERT INTO professions (
  id,
  string_code,
  created_at,
  created_by,
  modified_at,
  modified_by,
  deleted)
VALUES
    (gen_random_uuid(), 'profession.actor', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'profession.singer', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'profession.model', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'profession.musician', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'profession.dancer', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'profession.voice_talent', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'profession.comedy_standup', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false);
