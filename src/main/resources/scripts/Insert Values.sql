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
  category_string_code)
VALUES
    (gen_random_uuid(), 'sitemetadata.skill.athletics', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.sport'),
    (gen_random_uuid(), 'sitemetadata.skill.basketball', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.sport'),
    (gen_random_uuid(), 'sitemetadata.skill.boxing', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.sport'),
    (gen_random_uuid(), 'sitemetadata.skill.cycling', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.sport'),
    (gen_random_uuid(), 'sitemetadata.skill.football', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.sport'),
    (gen_random_uuid(), 'sitemetadata.skill.gymnastics', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.sport'),
    (gen_random_uuid(), 'sitemetadata.skill.handball', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.sport'),
    (gen_random_uuid(), 'sitemetadata.skill.hockey', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.sport'),
    (gen_random_uuid(), 'sitemetadata.skill.swimming', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.sport'),
    (gen_random_uuid(), 'sitemetadata.skill.padel', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.sport'),
    (gen_random_uuid(), 'sitemetadata.skill.rugby', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.sport'),
    (gen_random_uuid(), 'sitemetadata.skill.tennis', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.sport'),
    (gen_random_uuid(), 'sitemetadata.skill.volleyball', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.sport'),
    (gen_random_uuid(), 'sitemetadata.skill.acrobatics', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.physical'),
    (gen_random_uuid(), 'sitemetadata.skill.aerial_acrobatics', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.physical'),
    (gen_random_uuid(), 'sitemetadata.skill.martial_arts', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.physical'),
    (gen_random_uuid(), 'sitemetadata.skill.capoeira', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.physical'),
    (gen_random_uuid(), 'sitemetadata.skill.stage_combat', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.physical'),
    (gen_random_uuid(), 'sitemetadata.skill.contortion', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.physical'),
    (gen_random_uuid(), 'sitemetadata.skill.tightrope_slackline', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.physical'),
    (gen_random_uuid(), 'sitemetadata.skill.horseback_riding', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.physical'),
    (gen_random_uuid(), 'sitemetadata.skill.climbing', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.physical'),
    (gen_random_uuid(), 'sitemetadata.skill.stage_fencing', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.physical'),
    (gen_random_uuid(), 'sitemetadata.skill.juggling', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.physical'),
    (gen_random_uuid(), 'sitemetadata.skill.pantomime', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.physical'),
    (gen_random_uuid(), 'sitemetadata.skill.physical_theater', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.physical'),
    (gen_random_uuid(), 'sitemetadata.skill.parkour', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.physical'),
    (gen_random_uuid(), 'sitemetadata.skill.skating', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.physical'),
    (gen_random_uuid(), 'sitemetadata.skill.stunts', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.physical'),
    (gen_random_uuid(), 'sitemetadata.skill.aerial_skills', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.physical'),
    (gen_random_uuid(), 'sitemetadata.skill.harness_wirework', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.physical'),
    (gen_random_uuid(), 'sitemetadata.skill.stilts', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.physical'),
    (gen_random_uuid(), 'sitemetadata.skill.german', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.language'),
    (gen_random_uuid(), 'sitemetadata.skill.chinese_mandarin', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.language'),
    (gen_random_uuid(), 'sitemetadata.skill.spanish_arg', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.language'),
    (gen_random_uuid(), 'sitemetadata.skill.french', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.language'),
    (gen_random_uuid(), 'sitemetadata.skill.english', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.language'),
    (gen_random_uuid(), 'sitemetadata.skill.italian', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.language'),
    (gen_random_uuid(), 'sitemetadata.skill.portuguese_br', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.language'),
    (gen_random_uuid(), 'sitemetadata.skill.russian', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.language'),
    (gen_random_uuid(), 'sitemetadata.skill.spanish_arg', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.accent'),
    (gen_random_uuid(), 'sitemetadata.skill.spanish_es', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.accent'),
    (gen_random_uuid(), 'sitemetadata.skill.spanish_neutral', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.accent'),
    (gen_random_uuid(), 'sitemetadata.skill.spanish_co', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.accent'),
    (gen_random_uuid(), 'sitemetadata.skill.spanish_ch', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.accent'),
    (gen_random_uuid(), 'sitemetadata.skill.spanish_pe', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.accent'),
    (gen_random_uuid(), 'sitemetadata.skill.spanish_ve', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.accent'),
    (gen_random_uuid(), 'sitemetadata.skill.english_us', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.accent'),
    (gen_random_uuid(), 'sitemetadata.skill.english_uk', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.accent');

INSERT INTO professions (
  id,
  string_code,
  created_at,
  created_by,
  modified_at,
  modified_by,
  deleted,
  category_string_code)
VALUES
    (gen_random_uuid(), 'sitemetadata.profession.actor', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.scenic'),
    (gen_random_uuid(), 'sitemetadata.profession.dancer', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.scenic'),
    (gen_random_uuid(), 'sitemetadata.profession.singer', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.scenic'),
    (gen_random_uuid(), 'sitemetadata.profession.influencer', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.scenic'),
    (gen_random_uuid(), 'sitemetadata.profession.model', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.scenic'),
    (gen_random_uuid(), 'sitemetadata.profession.musician', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.scenic'),
    (gen_random_uuid(), 'sitemetadata.profession.standup', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.scenic'),
    (gen_random_uuid(), 'sitemetadata.profession.voice_talent', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.scenic');

INSERT INTO gender_option (
  id,
  string_code,
  created_at,
  created_by,
  modified_at,
  modified_by,
  deleted)
VALUES
    (gen_random_uuid(), 'sitemetadata.gender.male', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'sitemetadata.gender.female', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'sitemetadata.gender.other', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false);

INSERT INTO color_option (
  id,
  string_code,
  created_at,
  created_by,
  modified_at,
  modified_by,
  deleted,
  category_string_code)
VALUES
    (gen_random_uuid(), 'sitemetadata.color.black', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.hair_color'),
    (gen_random_uuid(), 'sitemetadata.color.dark_brown', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.hair_color'),
    (gen_random_uuid(), 'sitemetadata.color.light_brown', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.hair_color'),
    (gen_random_uuid(), 'sitemetadata.color.brown', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.hair_color'),
    (gen_random_uuid(), 'sitemetadata.color.blonde', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.hair_color'),
    (gen_random_uuid(), 'sitemetadata.color.dark_blonde', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.hair_color'),
    (gen_random_uuid(), 'sitemetadata.color.light_blonde', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.hair_color'),
    (gen_random_uuid(), 'sitemetadata.color.red', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.hair_color'),
    (gen_random_uuid(), 'sitemetadata.color.gray', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.hair_color'),
    (gen_random_uuid(), 'sitemetadata.color.white', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.hair_color'),
    (gen_random_uuid(), 'sitemetadata.color.no_hair', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.hair_color'),
    (gen_random_uuid(), 'sitemetadata.color.amber', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.eye_color'),
    (gen_random_uuid(), 'sitemetadata.color.hazel', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.eye_color'),
    (gen_random_uuid(), 'sitemetadata.color.blue', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.eye_color'),
    (gen_random_uuid(), 'sitemetadata.color.light_blue', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.eye_color'),
    (gen_random_uuid(), 'sitemetadata.color.gray', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.eye_color'),
    (gen_random_uuid(), 'sitemetadata.color.brown', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.eye_color'),
    (gen_random_uuid(), 'sitemetadata.color.black', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.eye_color'),
    (gen_random_uuid(), 'sitemetadata.color.green', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.eye_color'),
    (gen_random_uuid(), 'sitemetadata.color.heterochromia', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.eye_color');

INSERT INTO diet_option (
  id,
  string_code,
  created_at,
  created_by,
  modified_at,
  modified_by,
  deleted)
VALUES
    (gen_random_uuid(), 'sitemetadata.diet.omnivore', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'sitemetadata.diet.flexitarian', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'sitemetadata.diet.vegetarian', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'sitemetadata.diet.lacto_ovo_vegetarian', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'sitemetadata.diet.vegan', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'sitemetadata.diet.pescatarian', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'sitemetadata.diet.ketogenic', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'sitemetadata.diet.gluten_free', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'sitemetadata.diet.lactose_free', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'sitemetadata.diet.kosher', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'sitemetadata.diet.halal', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false);

INSERT INTO production_type  (
  id,
  string_code,
  created_at,
  created_by,
  modified_at,
  modified_by,
  deleted)
VALUES
    (gen_random_uuid(), 'sitemetadata.production_type.theatre', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'sitemetadata.production_type.television_streaming', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'sitemetadata.production_type.film', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'sitemetadata.production_type.commercial', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false);
