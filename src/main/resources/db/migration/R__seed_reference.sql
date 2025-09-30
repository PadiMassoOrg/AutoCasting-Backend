-- ============================================================================
-- Repeatable seed de catálogos (idempotente) - PROD SAFE
-- - Requiere PostgreSQL 15+ (Heroku PG 17.x OK)
-- - Si existe, actualiza; si falta, inserta.
-- - Mantiene created_at/created_by y sólo toca modified_* al actualizar.
-- ============================================================================
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- ----------------------------------------------------------------------------
-- ROLES  (clave funcional: code)  UNIQUE(code) existe en tu esquema
-- ----------------------------------------------------------------------------
MERGE INTO public.roles AS t
USING (
  VALUES
    ('ACTOR',     'role.actor',     'Usuario que representa un actor',            false),
    ('CASTINERA', 'role.castinera', 'Usuario que busca y gestiona actores',       false)
) AS s(code, name_string_code, description, deleted)
ON (t.code = s.code)
WHEN MATCHED THEN UPDATE SET
  name_string_code = s.name_string_code,
  description      = s.description,
  deleted          = s.deleted,
  modified_at      = NOW(),
  modified_by      = 'SYSTEM'
WHEN NOT MATCHED THEN
  INSERT (id, code, name_string_code, description, created_at, created_by, modified_at, modified_by, deleted)
  VALUES (gen_random_uuid(), s.code, s.name_string_code, s.description, NOW(), 'SYSTEM', NOW(), 'SYSTEM', s.deleted);

-- ----------------------------------------------------------------------------
-- PLANS  (clave funcional: code)  UNIQUE(code) existe en tu esquema
-- ----------------------------------------------------------------------------
MERGE INTO public."plans" AS t
USING (
  VALUES
    ('FREE',               'plan.free',             'Plan gratuito para todos los usuarios', false, false),
    ('PREMIUM_ACTOR_1',    'plan.premium_actor_1',  'Permite slug personalizado',            true,  false),
    ('PREMIUM_CASTINERA',  'plan.premium_castinera','Acceso a búsquedas de actores',         true,  false)
) AS s(code, name_string_code, description, allows_custom_slug, deleted)
ON (t.code = s.code)
WHEN MATCHED THEN UPDATE SET
  name_string_code   = s.name_string_code,
  description        = s.description,
  allows_custom_slug = s.allows_custom_slug,
  deleted            = s.deleted,
  modified_at        = NOW(),
  modified_by        = 'SYSTEM'
WHEN NOT MATCHED THEN
  INSERT (id, code, name_string_code, description, allows_custom_slug, created_at, created_by, modified_at, modified_by, deleted)
  VALUES (gen_random_uuid(), s.code, s.name_string_code, s.description, s.allows_custom_slug, NOW(), 'SYSTEM', NOW(), 'SYSTEM', s.deleted);

-- ----------------------------------------------------------------------------
-- SKILLS  (clave funcional: string_code)
--   NOTA: eliminé el duplicado 'sitemetadata.skill.spanish_arg' del bloque "accent"
-- ----------------------------------------------------------------------------
MERGE INTO public.skills AS t
USING (
  VALUES
    -- sports
    ('sitemetadata.skill.athletics',     'sitemetadata.category.sport',   false),
    ('sitemetadata.skill.basketball',    'sitemetadata.category.sport',   false),
    ('sitemetadata.skill.boxing',        'sitemetadata.category.sport',   false),
    ('sitemetadata.skill.cycling',       'sitemetadata.category.sport',   false),
    ('sitemetadata.skill.football',      'sitemetadata.category.sport',   false),
    ('sitemetadata.skill.gymnastics',    'sitemetadata.category.sport',   false),
    ('sitemetadata.skill.handball',      'sitemetadata.category.sport',   false),
    ('sitemetadata.skill.hockey',        'sitemetadata.category.sport',   false),
    ('sitemetadata.skill.swimming',      'sitemetadata.category.sport',   false),
    ('sitemetadata.skill.padel',         'sitemetadata.category.sport',   false),
    ('sitemetadata.skill.rugby',         'sitemetadata.category.sport',   false),
    ('sitemetadata.skill.tennis',        'sitemetadata.category.sport',   false),
    ('sitemetadata.skill.volleyball',    'sitemetadata.category.sport',   false),

    -- physical
    ('sitemetadata.skill.acrobatics',        'sitemetadata.category.physical', false),
    ('sitemetadata.skill.aerial_acrobatics', 'sitemetadata.category.physical', false),
    ('sitemetadata.skill.martial_arts',      'sitemetadata.category.physical', false),
    ('sitemetadata.skill.capoeira',          'sitemetadata.category.physical', false),
    ('sitemetadata.skill.stage_combat',      'sitemetadata.category.physical', false),
    ('sitemetadata.skill.contortion',        'sitemetadata.category.physical', false),
    ('sitemetadata.skill.tightrope_slackline','sitemetadata.category.physical',false),
    ('sitemetadata.skill.horseback_riding',  'sitemetadata.category.physical', false),
    ('sitemetadata.skill.climbing',          'sitemetadata.category.physical', false),
    ('sitemetadata.skill.stage_fencing',     'sitemetadata.category.physical', false),
    ('sitemetadata.skill.juggling',          'sitemetadata.category.physical', false),
    ('sitemetadata.skill.pantomime',         'sitemetadata.category.physical', false),
    ('sitemetadata.skill.physical_theater',  'sitemetadata.category.physical', false),
    ('sitemetadata.skill.parkour',           'sitemetadata.category.physical', false),
    ('sitemetadata.skill.skating',           'sitemetadata.category.physical', false),
    ('sitemetadata.skill.stunts',            'sitemetadata.category.physical', false),
    ('sitemetadata.skill.aerial_skills',     'sitemetadata.category.physical', false),
    ('sitemetadata.skill.harness_wirework',  'sitemetadata.category.physical', false),
    ('sitemetadata.skill.stilts',            'sitemetadata.category.physical', false),

    -- language
    ('sitemetadata.skill.german',            'sitemetadata.category.language', false),
    ('sitemetadata.skill.chinese_mandarin',  'sitemetadata.category.language', false),
    ('sitemetadata.skill.spanish_arg',       'sitemetadata.category.language', false),
    ('sitemetadata.skill.french',            'sitemetadata.category.language', false),
    ('sitemetadata.skill.english',           'sitemetadata.category.language', false),
    ('sitemetadata.skill.italian',           'sitemetadata.category.language', false),
    ('sitemetadata.skill.portuguese_br',     'sitemetadata.category.language', false),
    ('sitemetadata.skill.russian',           'sitemetadata.category.language', false),

    -- accent
    ('sitemetadata.skill.spanish_arg',       'sitemetadata.category.accent', false),
    ('sitemetadata.skill.spanish_es',       'sitemetadata.category.accent', false),
    ('sitemetadata.skill.spanish_neutral',  'sitemetadata.category.accent', false),
    ('sitemetadata.skill.spanish_co',       'sitemetadata.category.accent', false),
    ('sitemetadata.skill.spanish_ch',       'sitemetadata.category.accent', false),
    ('sitemetadata.skill.spanish_pe',       'sitemetadata.category.accent', false),
    ('sitemetadata.skill.spanish_ve',       'sitemetadata.category.accent', false),
    ('sitemetadata.skill.english_us',       'sitemetadata.category.accent', false),
    ('sitemetadata.skill.english_uk',       'sitemetadata.category.accent', false)
) AS s(string_code, category_string_code, deleted)
ON (t.string_code = s.string_code AND t.category_string_code = s.category_string_code)
WHEN MATCHED THEN UPDATE SET
  category_string_code = s.category_string_code,
  deleted              = s.deleted,
  modified_at          = NOW(),
  modified_by          = 'SYSTEM'
WHEN NOT MATCHED THEN
  INSERT (id, string_code, category_string_code, created_at, created_by, modified_at, modified_by, deleted)
  VALUES (gen_random_uuid(), s.string_code, s.category_string_code, NOW(), 'SYSTEM', NOW(), 'SYSTEM', s.deleted);

-- ----------------------------------------------------------------------------
-- PROFESSIONS  (clave: string_code)
-- ----------------------------------------------------------------------------
MERGE INTO public.professions AS t
USING (
  VALUES
    ('sitemetadata.profession.actor',        'sitemetadata.category.scenic', false),
    ('sitemetadata.profession.dancer',       'sitemetadata.category.scenic', false),
    ('sitemetadata.profession.singer',       'sitemetadata.category.scenic', false),
    ('sitemetadata.profession.influencer',   'sitemetadata.category.scenic', false),
    ('sitemetadata.profession.model',        'sitemetadata.category.scenic', false),
    ('sitemetadata.profession.musician',     'sitemetadata.category.scenic', false),
    ('sitemetadata.profession.standup',      'sitemetadata.category.scenic', false),
    ('sitemetadata.profession.voice_talent', 'sitemetadata.category.scenic', false)
) AS s(string_code, category_string_code, deleted)
ON (t.string_code = s.string_code)
WHEN MATCHED THEN UPDATE SET
  category_string_code = s.category_string_code,
  deleted              = s.deleted,
  modified_at          = NOW(),
  modified_by          = 'SYSTEM'
WHEN NOT MATCHED THEN
  INSERT (id, string_code, category_string_code, created_at, created_by, modified_at, modified_by, deleted)
  VALUES (gen_random_uuid(), s.string_code, s.category_string_code, NOW(), 'SYSTEM', NOW(), 'SYSTEM', s.deleted);

-- ----------------------------------------------------------------------------
-- GENDER_OPTION  (clave: string_code)
-- ----------------------------------------------------------------------------
MERGE INTO public.gender_option AS t
USING (
  VALUES
    ('sitemetadata.gender.male',          false),
    ('sitemetadata.gender.female',        false),
    ('sitemetadata.gender.male_trans',    false),
    ('sitemetadata.gender.female_trans',  false),
    ('sitemetadata.gender.non_binary',    false),
    ('sitemetadata.gender.other',         false)
) AS s(string_code, deleted)
ON (t.string_code = s.string_code)
WHEN MATCHED THEN UPDATE SET
  deleted     = s.deleted,
  modified_at = NOW(),
  modified_by = 'SYSTEM'
WHEN NOT MATCHED THEN
  INSERT (id, string_code, created_at, created_by, modified_at, modified_by, deleted)
  VALUES (gen_random_uuid(), s.string_code, NOW(), 'SYSTEM', NOW(), 'SYSTEM', s.deleted);

-- COLOR_OPTION (clave: string_code + category_string_code)
MERGE INTO public.color_option AS t
USING (
  VALUES
    -- hair
    ('sitemetadata.color.black',        'sitemetadata.category.hair_color', false),
    ('sitemetadata.color.dark_brown',   'sitemetadata.category.hair_color', false),
    ('sitemetadata.color.light_brown',  'sitemetadata.category.hair_color', false),
    ('sitemetadata.color.brown',        'sitemetadata.category.hair_color', false),
    ('sitemetadata.color.blonde',       'sitemetadata.category.hair_color', false),
    ('sitemetadata.color.dark_blonde',  'sitemetadata.category.hair_color', false),
    ('sitemetadata.color.light_blonde', 'sitemetadata.category.hair_color', false),
    ('sitemetadata.color.red',          'sitemetadata.category.hair_color', false),
    ('sitemetadata.color.gray',         'sitemetadata.category.hair_color', false),
    ('sitemetadata.color.white',        'sitemetadata.category.hair_color', false),
    ('sitemetadata.color.no_hair',      'sitemetadata.category.hair_color', false),
    -- eye
    ('sitemetadata.color.amber',        'sitemetadata.category.eye_color',  false),
    ('sitemetadata.color.hazel',        'sitemetadata.category.eye_color',  false),
    ('sitemetadata.color.blue',         'sitemetadata.category.eye_color',  false),
    ('sitemetadata.color.light_blue',   'sitemetadata.category.eye_color',  false),
    ('sitemetadata.color.gray',         'sitemetadata.category.eye_color',  false),
    ('sitemetadata.color.brown',        'sitemetadata.category.eye_color',  false),
    ('sitemetadata.color.black',        'sitemetadata.category.eye_color',  false),
    ('sitemetadata.color.green',        'sitemetadata.category.eye_color',  false),
    ('sitemetadata.color.heterochromia','sitemetadata.category.eye_color',  false)
) AS s(string_code, category_string_code, deleted)
ON (t.string_code = s.string_code AND t.category_string_code = s.category_string_code)
WHEN MATCHED THEN UPDATE SET
  category_string_code = s.category_string_code,
  deleted              = s.deleted,
  modified_at          = NOW(),
  modified_by          = 'SYSTEM'
WHEN NOT MATCHED THEN
  INSERT (id, string_code, category_string_code, created_at, created_by, modified_at, modified_by, deleted)
  VALUES (gen_random_uuid(), s.string_code, s.category_string_code, NOW(), 'SYSTEM', NOW(), 'SYSTEM', s.deleted);

-- ----------------------------------------------------------------------------
-- DIET_OPTION  (clave: string_code)
-- ----------------------------------------------------------------------------
MERGE INTO public.diet_option AS t
USING (
  VALUES
    ('sitemetadata.diet.omnivore',            false),
    ('sitemetadata.diet.flexitarian',         false),
    ('sitemetadata.diet.vegetarian',          false),
    ('sitemetadata.diet.lacto_ovo_vegetarian',false),
    ('sitemetadata.diet.vegan',               false),
    ('sitemetadata.diet.pescatarian',         false),
    ('sitemetadata.diet.ketogenic',           false),
    ('sitemetadata.diet.gluten_free',         false),
    ('sitemetadata.diet.lactose_free',        false),
    ('sitemetadata.diet.kosher',              false),
    ('sitemetadata.diet.halal',               false)
) AS s(string_code, deleted)
ON (t.string_code = s.string_code)
WHEN MATCHED THEN UPDATE SET
  deleted     = s.deleted,
  modified_at = NOW(),
  modified_by = 'SYSTEM'
WHEN NOT MATCHED THEN
  INSERT (id, string_code, created_at, created_by, modified_at, modified_by, deleted)
  VALUES (gen_random_uuid(), s.string_code, NOW(), 'SYSTEM', NOW(), 'SYSTEM', s.deleted);

-- ----------------------------------------------------------------------------
-- PRODUCTION_TYPE  (clave: string_code)
-- ----------------------------------------------------------------------------
MERGE INTO public.production_type AS t
USING (
  VALUES
    ('sitemetadata.production_type.theatre',               false),
    ('sitemetadata.production_type.television_streaming',  false),
    ('sitemetadata.production_type.film',                  false),
    ('sitemetadata.production_type.commercial',            false)
) AS s(string_code, deleted)
ON (t.string_code = s.string_code)
WHEN MATCHED THEN UPDATE SET
  deleted     = s.deleted,
  modified_at = NOW(),
  modified_by = 'SYSTEM'
WHEN NOT MATCHED THEN
  INSERT (id, string_code, created_at, created_by, modified_at, modified_by, deleted)
  VALUES (gen_random_uuid(), s.string_code, NOW(), 'SYSTEM', NOW(), 'SYSTEM', s.deleted);
