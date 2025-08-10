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

INSERT INTO color_option (
  id,
  string_code,
  created_at,
  created_by,
  modified_at,
  modified_by,
  deleted,
  category)
VALUES
    (gen_random_uuid(), 'color.black', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'HAIR'),
    (gen_random_uuid(), 'color.dark_brown', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'HAIR'),
    (gen_random_uuid(), 'color.light_brown', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'HAIR'),
    (gen_random_uuid(), 'color.brown', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'HAIR'),
    (gen_random_uuid(), 'color.blonde', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'HAIR'),
    (gen_random_uuid(), 'color.dark_blonde', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'HAIR'),
    (gen_random_uuid(), 'color.light_blonde', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'HAIR'),
    (gen_random_uuid(), 'color.red', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'HAIR'),
    (gen_random_uuid(), 'color.gray', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'HAIR'),
    (gen_random_uuid(), 'color.white', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'HAIR'),
    (gen_random_uuid(), 'color.no_hair', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'HAIR'),
    (gen_random_uuid(), 'color.amber', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'EYE'),
    (gen_random_uuid(), 'color.hazel', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'EYE'),
    (gen_random_uuid(), 'color.blue', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'EYE'),
    (gen_random_uuid(), 'color.light_blue', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'EYE'),
    (gen_random_uuid(), 'color.gray', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'EYE'),
    (gen_random_uuid(), 'color.brown', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'EYE'),
    (gen_random_uuid(), 'color.black', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'EYE'),
    (gen_random_uuid(), 'color.green', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'EYE'),
    (gen_random_uuid(), 'color.heterochromia', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'EYE');

INSERT INTO diet_option (
  id,
  string_code,
  created_at,
  created_by,
  modified_at,
  modified_by,
  deleted)
VALUES
    (gen_random_uuid(), 'diet.omnivore', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'diet.flexitarian', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'diet.vegetarian', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'diet.lacto_ovo_vegetarian', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'diet.vegan', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'diet.pescatarian', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'diet.ketogenic', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'diet.gluten_free', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'diet.lactose_free', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'diet.kosher', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'diet.halal', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false);
