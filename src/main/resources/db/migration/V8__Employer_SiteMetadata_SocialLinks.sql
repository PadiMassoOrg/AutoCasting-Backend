-- 1) SiteMetadata: company_type_option
CREATE TABLE IF NOT EXISTS company_type_option (
    id                  uuid         NOT NULL,
    created_at          timestamp(6) NULL,
    created_by          varchar(255) NULL,
    deleted             bool         NOT NULL,
    modified_at         timestamp(6) NULL,
    modified_by         varchar(255) NULL,
    category_string_code varchar(255) NULL,
    string_code         varchar(255) NOT NULL,
    CONSTRAINT company_type_option_pkey PRIMARY KEY (id)
);

INSERT INTO company_type_option (
    id,
    string_code,
    category_string_code,
    created_at,
    created_by,
    modified_at,
    modified_by,
    deleted
)
VALUES
    (gen_random_uuid(), 'sitemetadata.company_type.producer',         'sitemetadata.company_type', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'sitemetadata.company_type.director',         'sitemetadata.company_type', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'sitemetadata.company_type.agent',            'sitemetadata.company_type', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'sitemetadata.company_type.talent_agency',    'sitemetadata.company_type', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'sitemetadata.company_type.publicity_agency', 'sitemetadata.company_type', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'sitemetadata.company_type.content_creator',  'sitemetadata.company_type', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'sitemetadata.company_type.company',          'sitemetadata.company_type', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'sitemetadata.company_type.theater_company',  'sitemetadata.company_type', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'sitemetadata.company_type.institution',      'sitemetadata.company_type', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false);


-- 2) EmployerProfile
CREATE TABLE IF NOT EXISTS employer_profile (
    id           uuid         NOT NULL,
    created_at   timestamp(6) NULL,
    created_by   varchar(255) NULL,
    deleted      bool         NOT NULL,
    modified_at  timestamp(6) NULL,
    modified_by  varchar(255) NULL,
    default_slug varchar(255) NOT NULL,
    premium_slug varchar(255) NULL,
    plan_id      uuid         NOT NULL,
    user_id      uuid         NOT NULL,
    CONSTRAINT employer_profile_pkey PRIMARY KEY (id),
    CONSTRAINT uk_employer_profile_user UNIQUE (user_id),
    CONSTRAINT uk_employer_profile_default_slug UNIQUE (default_slug),
    CONSTRAINT uk_employer_profile_premium_slug UNIQUE (premium_slug),
    CONSTRAINT fk_employer_profile_plan FOREIGN KEY (plan_id) REFERENCES "plans"(id),
    CONSTRAINT fk_employer_profile_user FOREIGN KEY (user_id) REFERENCES users(id)
);


-- 3) EmployerBasicInfo
CREATE TABLE IF NOT EXISTS employer_basic_info (
    id                  uuid         NOT NULL,
    created_at          timestamp(6) NULL,
    created_by          varchar(255) NULL,
    deleted             bool         NOT NULL,
    modified_at         timestamp(6) NULL,
    modified_by         varchar(255) NULL,
    user_name           varchar(255) NULL,
    company_name        varchar(255) NULL,
    company_type_id     uuid         NULL,
    email               varchar(255) NULL,
    image_url           varchar(255) NULL,
    address             varchar(255) NULL,
    website             varchar(255) NULL,
    about               varchar(255) NULL,
    employer_profile_id uuid         NOT NULL,
    CONSTRAINT employer_basic_info_pkey PRIMARY KEY (id),
    CONSTRAINT uk_employer_basic_info_profile UNIQUE (employer_profile_id),
    CONSTRAINT fk_employer_basic_info_company_type FOREIGN KEY (company_type_id) REFERENCES company_type_option(id),
    CONSTRAINT fk_employer_basic_info_profile FOREIGN KEY (employer_profile_id) REFERENCES employer_profile(id)
);


-- 4) Social links compartidos: talent_social_media_link -> ProfileSocialMediaLinkEntity
ALTER TABLE talent_social_media_link
    ALTER COLUMN talent_profile_id DROP NOT NULL;

ALTER TABLE talent_social_media_link
    ADD COLUMN employer_basic_info_id uuid NULL;

ALTER TABLE talent_social_media_link
    ADD CONSTRAINT fk_talent_social_media_link_employer_basic_info
        FOREIGN KEY (employer_basic_info_id)
        REFERENCES employer_basic_info(id);

ALTER TABLE talent_social_media_link
    ADD CONSTRAINT uk_talent_social_media_link_employer_option
        UNIQUE (employer_basic_info_id, social_media_option_id);

CREATE INDEX idx_talent_social_media_link_employer
    ON talent_social_media_link (employer_basic_info_id);

ALTER TABLE talent_social_media_link
    ADD CONSTRAINT chk_talent_social_media_link_target
    CHECK (
        (talent_profile_id IS NOT NULL AND employer_basic_info_id IS NULL) OR
        (talent_profile_id IS NULL AND employer_basic_info_id IS NOT NULL)
    );



-- 5) Crear employer_profile para todos los usuarios que aún no lo tengan
INSERT INTO employer_profile (
    id,
    created_at,
    created_by,
    deleted,
    modified_at,
    modified_by,
    default_slug,
    premium_slug,
    plan_id,
    user_id
)
SELECT
    gen_random_uuid()                                     AS id,
    NOW()                                                 AS created_at,
    'SYSTEM'                                              AS created_by,
    false                                                 AS deleted,
    NOW()                                                 AS modified_at,
    'SYSTEM'                                              AS modified_by,
    'AC-' || substr(gen_random_uuid()::text, 1, 8)        AS default_slug,
    NULL                                                  AS premium_slug,
    tp.plan_id                                            AS plan_id,
    u.id                                                  AS user_id
FROM users u
JOIN talent_profile tp ON tp.user_id = u.id
LEFT JOIN employer_profile ep ON ep.user_id = u.id
WHERE ep.id IS NULL;


-- 6) Crear employer_basic_info para todos los employer_profile que aún no lo tengan
INSERT INTO employer_basic_info (
    id,
    created_at,
    created_by,
    deleted,
    modified_at,
    modified_by,
    user_name,
    company_name,
    company_type_id,
    email,
    image_url,
    address,
    website,
    about,
    employer_profile_id
)
SELECT
    gen_random_uuid()          AS id,
    NOW()                      AS created_at,
    'SYSTEM'                   AS created_by,
    false                      AS deleted,
    NOW()                      AS modified_at,
    'SYSTEM'                   AS modified_by,
    NULL                       AS user_name,
    NULL                       AS company_name,
    NULL                       AS company_type_id,
    u.email                    AS email,
    NULL                       AS image_url,
    NULL                       AS address,
    NULL                       AS website,
    NULL                       AS about,
    ep.id                      AS employer_profile_id
FROM users u
JOIN employer_profile ep ON ep.user_id = u.id
LEFT JOIN employer_basic_info ebi ON ebi.employer_profile_id = ep.id
WHERE ebi.id IS NULL;

