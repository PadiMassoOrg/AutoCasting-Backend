-- ============================================================
-- Applications Module (simple, 1 role per application)
-- ============================================================

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- -----------------------------
-- 0) Application Status Option (Employer-only)
-- -----------------------------
CREATE TABLE IF NOT EXISTS casting_application_status_option
(
    id                   uuid PRIMARY KEY      DEFAULT gen_random_uuid(),
    string_code          varchar(255) NOT NULL,
    category_string_code varchar(255) NOT NULL,

    created_at           timestamp    NOT NULL DEFAULT now(),
    created_by           varchar(255) NOT NULL DEFAULT 'SYSTEM',
    modified_at          timestamp    NOT NULL DEFAULT now(),
    modified_by          varchar(255) NOT NULL DEFAULT 'SYSTEM',
    deleted              boolean      NOT NULL DEFAULT false
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_application_status_option_string_code
    ON casting_application_status_option (string_code);

CREATE INDEX IF NOT EXISTS idx_application_status_option_category
    ON casting_application_status_option (category_string_code);

CREATE INDEX IF NOT EXISTS idx_application_status_option_deleted
    ON casting_application_status_option (deleted);

-- Seeds (INCLUYE BLANK)
INSERT INTO casting_application_status_option (id, string_code, category_string_code, created_at, created_by,
                                               modified_at,
                                               modified_by, deleted)
VALUES (gen_random_uuid(), 'sitemetadata.application_status.blank', 'sitemetadata.application_status', NOW(), 'SYSTEM',
        NOW(), 'SYSTEM', false),
       (gen_random_uuid(), 'sitemetadata.application_status.preselected', 'sitemetadata.application_status', NOW(),
        'SYSTEM', NOW(), 'SYSTEM', false),
       (gen_random_uuid(), 'sitemetadata.application_status.selected', 'sitemetadata.application_status', NOW(),
        'SYSTEM', NOW(), 'SYSTEM', false),
       (gen_random_uuid(), 'sitemetadata.application_status.not_proceeding', 'sitemetadata.application_status', NOW(),
        'SYSTEM', NOW(), 'SYSTEM', false),
       (gen_random_uuid(), 'sitemetadata.application_status.viewed', 'sitemetadata.application_status', NOW(), 'SYSTEM',
        NOW(), 'SYSTEM', false)
ON CONFLICT (string_code) DO NOTHING;


-- -----------------------------
-- 1) casting_application
--   - 1 talent aplica a 1 role específico
--   - status NOT NULL (default: BLANK desde service o desde inserts explícitos)
--   - NO applied_at (created_at ya lo cubre)
-- -----------------------------
CREATE TABLE IF NOT EXISTS casting_application
(
    id                           uuid PRIMARY KEY      DEFAULT gen_random_uuid(),

    casting_role_id              uuid         NOT NULL,
    talent_profile_id            uuid         NOT NULL,

    application_status_option_id uuid         NOT NULL,

    message                      text         NULL,

    created_at                   timestamp    NOT NULL DEFAULT now(),
    created_by                   varchar(255) NOT NULL DEFAULT 'SYSTEM',
    modified_at                  timestamp    NOT NULL DEFAULT now(),
    modified_by                  varchar(255) NOT NULL DEFAULT 'SYSTEM',
    deleted                      boolean      NOT NULL DEFAULT false,

    CONSTRAINT fk_casting_application_casting_role
        FOREIGN KEY (casting_role_id) REFERENCES casting_role (id),

    CONSTRAINT fk_casting_application_talent_profile
        FOREIGN KEY (talent_profile_id) REFERENCES talent_profile (id),

    CONSTRAINT fk_casting_application_status_option
        FOREIGN KEY (application_status_option_id) REFERENCES casting_application_status_option (id),

    CONSTRAINT uq_casting_application UNIQUE (casting_role_id, talent_profile_id)
);

CREATE INDEX IF NOT EXISTS idx_casting_application_role
    ON casting_application (casting_role_id);

CREATE INDEX IF NOT EXISTS idx_casting_application_talent
    ON casting_application (talent_profile_id);

CREATE INDEX IF NOT EXISTS idx_casting_application_status
    ON casting_application (application_status_option_id);

CREATE INDEX IF NOT EXISTS idx_casting_application_deleted
    ON casting_application (deleted);


-- -----------------------------
-- 2) casting_application_requirement_submission
--   - 1 submission por requirement por application
--   - sin submitted_at (AuditableEntity.created_at ya cubre)
-- -----------------------------
CREATE TABLE IF NOT EXISTS casting_application_requirement_submission
(
    id                     uuid PRIMARY KEY      DEFAULT gen_random_uuid(),

    application_id         uuid         NOT NULL,
    casting_requirement_id uuid         NOT NULL,

    audio_url              text         NULL,
    video_url              text         NULL,
    notes                  text         NULL,

    created_at             timestamp    NOT NULL DEFAULT now(),
    created_by             varchar(255) NOT NULL DEFAULT 'SYSTEM',
    modified_at            timestamp    NOT NULL DEFAULT now(),
    modified_by            varchar(255) NOT NULL DEFAULT 'SYSTEM',
    deleted                boolean      NOT NULL DEFAULT false,

    CONSTRAINT fk_app_req_submission_application
        FOREIGN KEY (application_id) REFERENCES casting_application (id) ON DELETE CASCADE,

    CONSTRAINT fk_app_req_submission_requirement
        FOREIGN KEY (casting_requirement_id) REFERENCES casting_requirement (id),

    CONSTRAINT uq_app_requirement UNIQUE (application_id, casting_requirement_id)
);

CREATE INDEX IF NOT EXISTS idx_app_req_submission_application
    ON casting_application_requirement_submission (application_id);

CREATE INDEX IF NOT EXISTS idx_app_req_submission_requirement
    ON casting_application_requirement_submission (casting_requirement_id);

CREATE INDEX IF NOT EXISTS idx_app_req_submission_deleted
    ON casting_application_requirement_submission (deleted);
