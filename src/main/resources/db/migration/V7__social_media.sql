-- V7__social_media_refactor.sql
-- 1) Crear tabla de opciones de redes sociales (metadata)
-- 2) Crear tabla de links talento-red social
-- 3) Migrar datos de talent_social_media (tabla antigua)
-- 4) Eliminar tabla antigua

---------------------------------
-- 1) social_media_option
---------------------------------
CREATE TABLE social_media_option (
  id                  uuid NOT NULL,
  created_at          timestamp(6) NULL,
  created_by          varchar(255) NULL,
  deleted             boolean NOT NULL,
  modified_at         timestamp(6) NULL,
  modified_by         varchar(255) NULL,
  string_code         varchar(255) NOT NULL,
  category_string_code varchar(255),
  technical_key       varchar(100) NOT NULL,
  CONSTRAINT social_media_option_pkey PRIMARY KEY (id)
);

-- Opcional: índice por technical_key
CREATE UNIQUE INDEX uk_social_media_option_technical_key
  ON social_media_option(technical_key);

-- Seed inicial (puedes ajustar string_code luego con tus traducciones)
INSERT INTO social_media_option (
  id, created_at, created_by, deleted, modified_at, modified_by,
  string_code, category_string_code, technical_key
)
VALUES
  (gen_random_uuid(), NOW(), 'SYSTEM', false, NOW(), 'SYSTEM',
   'sitemetadata.social_media.instagram', 'sitemetadata.social_media', 'instagram'),
  (gen_random_uuid(), NOW(), 'SYSTEM', false, NOW(), 'SYSTEM',
   'sitemetadata.social_media.tiktok',    'sitemetadata.social_media', 'tiktok'),
  (gen_random_uuid(), NOW(), 'SYSTEM', false, NOW(), 'SYSTEM',
   'sitemetadata.social_media.linkedin',  'sitemetadata.social_media', 'linkedin'),
  (gen_random_uuid(), NOW(), 'SYSTEM', false, NOW(), 'SYSTEM',
   'sitemetadata.social_media.x',         'sitemetadata.social_media', 'x'),
  (gen_random_uuid(), NOW(), 'SYSTEM', false, NOW(), 'SYSTEM',
   'sitemetadata.social_media.vimeo',     'sitemetadata.social_media', 'vimeo'),
  (gen_random_uuid(), NOW(), 'SYSTEM', false, NOW(), 'SYSTEM',
   'sitemetadata.social_media.imdb',      'sitemetadata.social_media', 'imdb'),
  (gen_random_uuid(), NOW(), 'SYSTEM', false, NOW(), 'SYSTEM',
   'sitemetadata.social_media.behance',   'sitemetadata.social_media', 'behance');

---------------------------------
-- 2) talent_social_media_link
---------------------------------
CREATE TABLE talent_social_media_link (
  id                     uuid NOT NULL,
  created_at             timestamp(6) NULL,
  created_by             varchar(255) NULL,
  deleted                boolean NOT NULL,
  modified_at            timestamp(6) NULL,
  modified_by            varchar(255) NULL,
  talent_profile_id      uuid NOT NULL,
  social_media_option_id uuid NOT NULL,
  url                    varchar(1024) NOT NULL,
  CONSTRAINT talent_social_media_link_pkey PRIMARY KEY (id),
  CONSTRAINT fk_talent_social_media_link_profile
      FOREIGN KEY (talent_profile_id) REFERENCES talent_profile(id),
  CONSTRAINT fk_talent_social_media_link_option
      FOREIGN KEY (social_media_option_id) REFERENCES social_media_option(id),
  CONSTRAINT uk_talent_social_media_link_profile_option
      UNIQUE (talent_profile_id, social_media_option_id)
);

-- Índice para búsquedas por perfil
CREATE INDEX idx_talent_social_media_link_profile
  ON talent_social_media_link(talent_profile_id);

---------------------------------
-- 3) Migrar datos desde talent_social_media (modelo antiguo)
---------------------------------
-- IMPORTANTE: asumo que la tabla antigua se llama talent_social_media
-- y tiene columnas instagram_url, tik_tok_url, linkedin_url, x_url, vimeo_url,
-- imdb_url, behance_url y talent_profile_id.

-- Instagram
INSERT INTO talent_social_media_link (
  id, created_at, created_by, deleted, modified_at, modified_by,
  talent_profile_id, social_media_option_id, url
)
SELECT
  gen_random_uuid(), NOW(), 'MIGRATION', false, NOW(), 'MIGRATION',
  tsm.talent_profile_id,
  (SELECT id FROM social_media_option WHERE technical_key = 'instagram'),
  btrim(tsm.instagram_url)
FROM talent_social_media tsm
WHERE tsm.instagram_url IS NOT NULL AND btrim(tsm.instagram_url) <> '';

-- TikTok
INSERT INTO talent_social_media_link (
  id, created_at, created_by, deleted, modified_at, modified_by,
  talent_profile_id, social_media_option_id, url
)
SELECT
  gen_random_uuid(), NOW(), 'MIGRATION', false, NOW(), 'MIGRATION',
  tsm.talent_profile_id,
  (SELECT id FROM social_media_option WHERE technical_key = 'tiktok'),
  btrim(tsm.tik_tok_url)
FROM talent_social_media tsm
WHERE tsm.tik_tok_url IS NOT NULL AND btrim(tsm.tik_tok_url) <> '';

-- LinkedIn
INSERT INTO talent_social_media_link (
  id, created_at, created_by, deleted, modified_at, modified_by,
  talent_profile_id, social_media_option_id, url
)
SELECT
  gen_random_uuid(), NOW(), 'MIGRATION', false, NOW(), 'MIGRATION',
  tsm.talent_profile_id,
  (SELECT id FROM social_media_option WHERE technical_key = 'linkedin'),
  btrim(tsm.linkedin_url)
FROM talent_social_media tsm
WHERE tsm.linkedin_url IS NOT NULL AND btrim(tsm.linkedin_url) <> '';

-- X (Twitter)
INSERT INTO talent_social_media_link (
  id, created_at, created_by, deleted, modified_at, modified_by,
  talent_profile_id, social_media_option_id, url
)
SELECT
  gen_random_uuid(), NOW(), 'MIGRATION', false, NOW(), 'MIGRATION',
  tsm.talent_profile_id,
  (SELECT id FROM social_media_option WHERE technical_key = 'x'),
  btrim(tsm.x_url)
FROM talent_social_media tsm
WHERE tsm.x_url IS NOT NULL AND btrim(tsm.x_url) <> '';

-- Vimeo
INSERT INTO talent_social_media_link (
  id, created_at, created_by, deleted, modified_at, modified_by,
  talent_profile_id, social_media_option_id, url
)
SELECT
  gen_random_uuid(), NOW(), 'MIGRATION', false, NOW(), 'MIGRATION',
  tsm.talent_profile_id,
  (SELECT id FROM social_media_option WHERE technical_key = 'vimeo'),
  btrim(tsm.vimeo_url)
FROM talent_social_media tsm
WHERE tsm.vimeo_url IS NOT NULL AND btrim(tsm.vimeo_url) <> '';

-- IMDb
INSERT INTO talent_social_media_link (
  id, created_at, created_by, deleted, modified_at, modified_by,
  talent_profile_id, social_media_option_id, url
)
SELECT
  gen_random_uuid(), NOW(), 'MIGRATION', false, NOW(), 'MIGRATION',
  tsm.talent_profile_id,
  (SELECT id FROM social_media_option WHERE technical_key = 'imdb'),
  btrim(tsm.imdb_url)
FROM talent_social_media tsm
WHERE tsm.imdb_url IS NOT NULL AND btrim(tsm.imdb_url) <> '';

-- Behance
INSERT INTO talent_social_media_link (
  id, created_at, created_by, deleted, modified_at, modified_by,
  talent_profile_id, social_media_option_id, url
)
SELECT
  gen_random_uuid(), NOW(), 'MIGRATION', false, NOW(), 'MIGRATION',
  tsm.talent_profile_id,
  (SELECT id FROM social_media_option WHERE technical_key = 'behance'),
  btrim(tsm.behance_url)
FROM talent_social_media tsm
WHERE tsm.behance_url IS NOT NULL AND btrim(tsm.behance_url) <> '';

---------------------------------
-- 4) Eliminar tabla antigua
---------------------------------
DROP TABLE talent_social_media;
