------------------------------------------------------------------------
---- Insert Roles
------------------------------------------------------------------------
INSERT INTO roles (
  id,
  code,
  name_string_code,
  description,
  is_active,
  created_at,
  created_by,
  modified_at,
  modified_by
)

VALUES
(gen_random_uuid(), 'ACTOR', 'role.actor', 'Usuario que representa un actor', true, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(gen_random_uuid(), 'CASTINERA', 'role.castinera',  'Usuario que busca y gestiona actores', true, NOW(), 'SYSTEM', NOW(), 'SYSTEM');


------------------------------------------------------------------------
---- Insert Planes
------------------------------------------------------------------------
INSERT INTO plans (
  id,
  code,
  name_string_code,
  description,
  allows_custom_slug,
  is_active,
  created_at,
  created_by,
  modified_at,
  modified_by
)
VALUES
  (gen_random_uuid(), 'FREE', 'plan.free', 'Plan gratuito para todos los usuarios', false, true, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
  (gen_random_uuid(), 'PREMIUM_ACTOR_1', 'plan.premium_actor_1', 'Permite slug personalizado', true, true, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
  (gen_random_uuid(), 'PREMIUM_CASTINERA', 'plan.premium_castinera', 'Acceso a búsquedas de actores', true, true, NOW(), 'SYSTEM', NOW(), 'SYSTEM');


------------------------------------------------------------------------
---- Insert Site Metadata
------------------------------------------------------------------------
INSERT INTO skills (id, string_code, is_active)
VALUES
    (gen_random_uuid(), 'skill.clown', true),
    (gen_random_uuid(), 'skill.physical_theater', true),
    (gen_random_uuid(), 'skill.fencing', true);

INSERT INTO professions (id, string_code, is_active)
VALUES
    (gen_random_uuid(), 'profession.actor', true),
    (gen_random_uuid(), 'profession.singer', true),
    (gen_random_uuid(), 'profession.model', true),
    (gen_random_uuid(), 'profession.musician', true),
    (gen_random_uuid(), 'profession.dancer', true),
    (gen_random_uuid(), 'profession.voice_talent', true),
    (gen_random_uuid(), 'profession.comedy_standup', true);

