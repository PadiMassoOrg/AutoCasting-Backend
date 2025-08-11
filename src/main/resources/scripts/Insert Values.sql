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
  deleted,
  category)
VALUES
    (gen_random_uuid(), 'skill.athletics', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'SPORT'),
    (gen_random_uuid(), 'skill.basketball', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'SPORT'),
    (gen_random_uuid(), 'skill.boxing', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'SPORT'),
    (gen_random_uuid(), 'skill.cycling', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'SPORT'),
    (gen_random_uuid(), 'skill.football', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'SPORT'),
    (gen_random_uuid(), 'skill.gymnastics', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'SPORT'),
    (gen_random_uuid(), 'skill.handball', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'SPORT'),
    (gen_random_uuid(), 'skill.hockey', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'SPORT'),
    (gen_random_uuid(), 'skill.swimming', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'SPORT'),
    (gen_random_uuid(), 'skill.padel', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'SPORT'),
    (gen_random_uuid(), 'skill.rugby', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'SPORT'),
    (gen_random_uuid(), 'skill.tennis', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'SPORT'),
    (gen_random_uuid(), 'skill.volleyball', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'SPORT'),
    (gen_random_uuid(), 'skill.acrobatics', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'PHYSICAL'),
    (gen_random_uuid(), 'skill.aerial_acrobatics', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'PHYSICAL'),
    (gen_random_uuid(), 'skill.martial_arts', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'PHYSICAL'),
    (gen_random_uuid(), 'skill.capoeira', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'PHYSICAL'),
    (gen_random_uuid(), 'skill.stage_combat', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'PHYSICAL'),
    (gen_random_uuid(), 'skill.contortion', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'PHYSICAL'),
    (gen_random_uuid(), 'skill.tightrope_slackline', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'PHYSICAL'),
    (gen_random_uuid(), 'skill.horseback_riding', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'PHYSICAL'),
    (gen_random_uuid(), 'skill.climbing', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'PHYSICAL'),
    (gen_random_uuid(), 'skill.stage_fencing', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'PHYSICAL'),
    (gen_random_uuid(), 'skill.juggling', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'PHYSICAL'),
    (gen_random_uuid(), 'skill.pantomime', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'PHYSICAL'),
    (gen_random_uuid(), 'skill.physical_theater', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'PHYSICAL'),
    (gen_random_uuid(), 'skill.parkour', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'PHYSICAL'),
    (gen_random_uuid(), 'skill.skating', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'PHYSICAL'),
    (gen_random_uuid(), 'skill.stunts', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'PHYSICAL'),
    (gen_random_uuid(), 'skill.aerial_skills', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'PHYSICAL'),
    (gen_random_uuid(), 'skill.harness_wirework', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'PHYSICAL'),
    (gen_random_uuid(), 'skill.stilts', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'PHYSICAL'),
    (gen_random_uuid(), 'skill.german', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'LANGUAGE'),
    (gen_random_uuid(), 'skill.chinese_mandarin', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'LANGUAGE'),
    (gen_random_uuid(), 'skill.spanish_arg', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'LANGUAGE'),
    (gen_random_uuid(), 'skill.french', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'LANGUAGE'),
    (gen_random_uuid(), 'skill.english', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'LANGUAGE'),
    (gen_random_uuid(), 'skill.italian', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'LANGUAGE'),
    (gen_random_uuid(), 'skill.portuguese_br', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'LANGUAGE'),
    (gen_random_uuid(), 'skill.russian', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'LANGUAGE'),
    (gen_random_uuid(), 'skill.spanish_arg', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'ACCENTS'),
    (gen_random_uuid(), 'skill.spanish_es', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'ACCENTS'),
    (gen_random_uuid(), 'skill.spanish_neutral', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'ACCENTS'),
    (gen_random_uuid(), 'skill.spanish_co', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'ACCENTS'),
    (gen_random_uuid(), 'skill.spanish_ch', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'ACCENTS'),
    (gen_random_uuid(), 'skill.spanish_pe', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'ACCENTS'),
    (gen_random_uuid(), 'skill.spanish_ve', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'ACCENTS'),
    (gen_random_uuid(), 'skill.english_us', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'ACCENTS'),
    (gen_random_uuid(), 'skill.english_uk', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'ACCENTS');

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
    (gen_random_uuid(), 'profession.dancer', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'profession.singer', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'profession.influencer', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'profession.model', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'profession.musician', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'profession.comedy_standup', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'profession.voice_talent', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false);

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

INSERT INTO production_type  (
  id,
  string_code,
  created_at,
  created_by,
  modified_at,
  modified_by,
  deleted)
VALUES
    (gen_random_uuid(), 'production_type.theatre', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'production_type.television_streaming', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'production_type.film', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'production_type.commercial', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false);
