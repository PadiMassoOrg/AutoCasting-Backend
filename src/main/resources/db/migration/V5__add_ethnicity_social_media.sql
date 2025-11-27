-- V5__social_media_and_ethnicity.sql

ALTER TABLE talent_social_media
    ADD COLUMN IF NOT EXISTS linkedin_url VARCHAR(255),
    ADD COLUMN IF NOT EXISTS x_url        VARCHAR(255),
    ADD COLUMN IF NOT EXISTS vimeo_url    VARCHAR(255),
    ADD COLUMN IF NOT EXISTS imdb_url     VARCHAR(255),
    ADD COLUMN IF NOT EXISTS behance_url  VARCHAR(255);

-- Crear tabla ethnicity_option (similar a gender_option)
CREATE TABLE IF NOT EXISTS public.ethnicity_option (
    id                  uuid          NOT NULL,
    created_at          timestamp(6)  NULL,
    created_by          varchar(255)  NULL,
    deleted             bool          NOT NULL,
    modified_at         timestamp(6)  NULL,
    modified_by         varchar(255)  NULL,
    category_string_code varchar(255) NULL,
    string_code         varchar(255)  NOT NULL,
    CONSTRAINT ethnicity_option_pkey PRIMARY KEY (id)
);

-- 3) Inserts base (string_code vacío, tú luego los reemplazas)
INSERT INTO ethnicity_option (
    id,
    string_code,
    created_at,
    created_by,
    modified_at,
    modified_by,
    deleted
)
VALUES
    -- Afrodescendiente
    (gen_random_uuid(), 'sitemetadata.ethnicity.afro_descendant', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false), -- sitemetadata.ethnicity.afro_descendant

    -- Asiática / Asiático
    (gen_random_uuid(), 'sitemetadata.ethnicity.asian', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false), -- sitemetadata.ethnicity.asian

    -- Blanca / Caucásica
    (gen_random_uuid(), 'sitemetadata.ethnicity.white_caucasian', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false), -- sitemetadata.ethnicity.white_caucasian

    -- Indígena / Originaria
    (gen_random_uuid(), 'sitemetadata.ethnicity.indigenous_native', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false), -- sitemetadata.ethnicity.indigenous_native

    -- Latina / Latino / Hispana / Hispano
    (gen_random_uuid(), 'sitemetadata.ethnicity.latino_hispanic', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false), -- sitemetadata.ethnicity.latino_hispanic

    -- Medio Oriente / Norte de África
    (gen_random_uuid(), 'sitemetadata.ethnicity.middle_east_north_africa', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false), -- sitemetadata.ethnicity.middle_east_north_africa

    -- Mixta / Mestiza
    (gen_random_uuid(), 'sitemetadata.ethnicity.mixed', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false), -- sitemetadata.ethnicity.mixed

    -- Prefiero no especificar
    (gen_random_uuid(), 'sitemetadata.ethnicity.prefer_not_to_say', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false); -- sitemetadata.ethnicity.prefer_not_to_say
