DELETE FROM casting_application_requirement_submission;
DELETE FROM casting_application;
DELETE FROM casting;

DROP TABLE IF EXISTS casting_application_requirement_submission CASCADE;
DROP TABLE IF EXISTS casting_role_skill CASCADE;
DROP TABLE IF EXISTS casting_role_profession CASCADE;
DROP TABLE IF EXISTS casting_role_remuneration CASCADE;
DROP TABLE IF EXISTS casting_role_characteristics CASCADE;
DROP TABLE IF EXISTS casting_requirement CASCADE;
DROP TABLE IF EXISTS casting_requirements_section CASCADE;
DROP TABLE IF EXISTS casting_remuneration CASCADE;
DROP TABLE IF EXISTS casting_roles_section CASCADE;
DROP TABLE IF EXISTS casting_basic_info CASCADE;
DROP TABLE IF EXISTS casting_role CASCADE;

ALTER TABLE casting
    ADD COLUMN IF NOT EXISTS title varchar(255),
    ADD COLUMN IF NOT EXISTS project_type_option_id uuid,
    ADD COLUMN IF NOT EXISTS casting_modality_option_id uuid,
    ADD COLUMN IF NOT EXISTS location_text varchar(255),
    ADD COLUMN IF NOT EXISTS application_deadline date,
    ADD COLUMN IF NOT EXISTS has_wardrobe_fitting boolean,
    ADD COLUMN IF NOT EXISTS wardrobe_fitting_date varchar(255),
    ADD COLUMN IF NOT EXISTS shooting_start_date date,
    ADD COLUMN IF NOT EXISTS shooting_end_date date,
    ADD COLUMN IF NOT EXISTS description text;

ALTER TABLE casting
    ALTER COLUMN title SET NOT NULL,
    ALTER COLUMN project_type_option_id DROP NOT NULL,
    ALTER COLUMN casting_modality_option_id DROP NOT NULL,
    ALTER COLUMN application_deadline DROP NOT NULL,
    ALTER COLUMN has_wardrobe_fitting DROP NOT NULL,
    ALTER COLUMN wardrobe_fitting_date DROP NOT NULL,
    ALTER COLUMN shooting_start_date DROP NOT NULL,
    ALTER COLUMN shooting_end_date DROP NOT NULL;

ALTER TABLE casting
    ALTER COLUMN wardrobe_fitting_date TYPE varchar(255)
    USING wardrobe_fitting_date::text;

ALTER TABLE casting
    DROP CONSTRAINT IF EXISTS fk_casting_project_type_option,
    ADD CONSTRAINT fk_casting_project_type_option
        FOREIGN KEY (project_type_option_id) REFERENCES project_type_option (id),
    DROP CONSTRAINT IF EXISTS fk_casting_casting_modality_option,
    ADD CONSTRAINT fk_casting_casting_modality_option
        FOREIGN KEY (casting_modality_option_id) REFERENCES casting_modality_option (id),
    DROP CONSTRAINT IF EXISTS chk_casting_shooting_date_range,
    ADD CONSTRAINT chk_casting_shooting_date_range
        CHECK (shooting_start_date IS NULL OR shooting_end_date IS NULL OR shooting_start_date <= shooting_end_date),
    DROP CONSTRAINT IF EXISTS chk_casting_wardrobe_requires_date,
    ADD CONSTRAINT chk_casting_wardrobe_requires_date
        CHECK (
            has_wardrobe_fitting IS DISTINCT FROM false
            OR wardrobe_fitting_date IS NULL
        );

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'casting_attachment'
          AND column_name = 'casting_basic_info_id'
    ) THEN
        EXECUTE 'ALTER TABLE public.casting_attachment RENAME COLUMN casting_basic_info_id TO casting_id';
    END IF;
END $$;

ALTER TABLE casting_attachment
    DROP CONSTRAINT IF EXISTS fk_casting_attachment_basic_info,
    DROP CONSTRAINT IF EXISTS fk_casting_attachment_casting,
    ALTER COLUMN casting_id SET NOT NULL,
    ALTER COLUMN file_url SET NOT NULL;

ALTER TABLE casting_attachment
    ADD CONSTRAINT fk_casting_attachment_casting
        FOREIGN KEY (casting_id) REFERENCES casting (id) ON DELETE CASCADE;

ALTER TABLE casting_attachment
    ALTER COLUMN file_name TYPE varchar(255),
    ALTER COLUMN file_type TYPE varchar(255);

DROP INDEX IF EXISTS idx_casting_attachment_basic_info;
CREATE INDEX IF NOT EXISTS idx_casting_attachment_casting ON casting_attachment (casting_id);
CREATE INDEX IF NOT EXISTS idx_casting_attachment_deleted ON casting_attachment (deleted);

CREATE TABLE IF NOT EXISTS casting_role (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    casting_id uuid NOT NULL,
    role_name varchar(255) NOT NULL,
    role_type_option_id uuid NOT NULL,
    gender_option_id uuid NOT NULL,
    age_min smallint NOT NULL,
    age_max smallint NOT NULL,
    description text,
    pay_rate_type_option_id uuid NOT NULL,
    currency_option_id uuid,
    amount numeric(12,2),
    remuneration_notes text,
    requires_audio boolean NOT NULL DEFAULT false,
    requires_video boolean NOT NULL DEFAULT false,
    requirement_description text,
    ethnicity_id uuid,
    tattoo boolean,
    passport boolean,
    driving_license boolean,
    height_cm integer,
    weight_kg integer,
    hair_color_id uuid,
    eye_color_id uuid,
    chest_cm varchar(255),
    waist_cm varchar(255),
    hip_cm varchar(255),
    shirt_size varchar(255),
    pant_size varchar(255),
    dress_size varchar(255),
    shoe_size varchar(255),
    diet_option_id uuid,
    created_at timestamp NOT NULL DEFAULT now(),
    created_by varchar(255) NOT NULL DEFAULT 'SYSTEM',
    modified_at timestamp NOT NULL DEFAULT now(),
    modified_by varchar(255) NOT NULL DEFAULT 'SYSTEM',
    deleted boolean NOT NULL DEFAULT false,
    CONSTRAINT fk_casting_role_casting
        FOREIGN KEY (casting_id) REFERENCES casting (id) ON DELETE CASCADE,
    CONSTRAINT fk_casting_role_role_type
        FOREIGN KEY (role_type_option_id) REFERENCES role_type_option (id),
    CONSTRAINT fk_casting_role_gender
        FOREIGN KEY (gender_option_id) REFERENCES gender_option (id),
    CONSTRAINT fk_casting_role_pay_rate_type
        FOREIGN KEY (pay_rate_type_option_id) REFERENCES pay_rate_type_option (id),
    CONSTRAINT fk_casting_role_currency
        FOREIGN KEY (currency_option_id) REFERENCES currency_option (id),
    CONSTRAINT fk_casting_role_ethnicity
        FOREIGN KEY (ethnicity_id) REFERENCES ethnicity_option (id),
    CONSTRAINT fk_casting_role_hair_color
        FOREIGN KEY (hair_color_id) REFERENCES color_option (id),
    CONSTRAINT fk_casting_role_eye_color
        FOREIGN KEY (eye_color_id) REFERENCES color_option (id),
    CONSTRAINT fk_casting_role_diet
        FOREIGN KEY (diet_option_id) REFERENCES diet_option (id),
    CONSTRAINT chk_casting_role_age_range
        CHECK (age_min <= age_max),
    CONSTRAINT chk_casting_role_age_min_non_negative
        CHECK (age_min >= 0),
    CONSTRAINT chk_casting_role_age_max_non_negative
        CHECK (age_max >= 0),
    CONSTRAINT chk_casting_role_amount_non_negative
        CHECK (amount IS NULL OR amount >= 0)
);

CREATE INDEX IF NOT EXISTS idx_casting_role_casting ON casting_role (casting_id);
CREATE INDEX IF NOT EXISTS idx_casting_role_deleted ON casting_role (deleted);

CREATE TABLE IF NOT EXISTS casting_role_profession (
    casting_role_id uuid NOT NULL,
    profession_id uuid NOT NULL,
    PRIMARY KEY (casting_role_id, profession_id),
    CONSTRAINT fk_casting_role_profession_role
        FOREIGN KEY (casting_role_id) REFERENCES casting_role (id) ON DELETE CASCADE,
    CONSTRAINT fk_casting_role_profession_profession
        FOREIGN KEY (profession_id) REFERENCES professions (id)
);

CREATE TABLE IF NOT EXISTS casting_role_skill (
    casting_role_id uuid NOT NULL,
    skill_id uuid NOT NULL,
    PRIMARY KEY (casting_role_id, skill_id),
    CONSTRAINT fk_casting_role_skill_role
        FOREIGN KEY (casting_role_id) REFERENCES casting_role (id) ON DELETE CASCADE,
    CONSTRAINT fk_casting_role_skill_skill
        FOREIGN KEY (skill_id) REFERENCES skills (id)
);

CREATE INDEX IF NOT EXISTS idx_casting_role_profession_profession ON casting_role_profession (profession_id);
CREATE INDEX IF NOT EXISTS idx_casting_role_skill_skill ON casting_role_skill (skill_id);

ALTER TABLE casting_application
    DROP CONSTRAINT IF EXISTS fk_casting_application_casting_role,
    ADD CONSTRAINT fk_casting_application_casting_role
        FOREIGN KEY (casting_role_id) REFERENCES casting_role (id);

CREATE TABLE IF NOT EXISTS casting_application_requirement_submission (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    application_id uuid NOT NULL,
    casting_role_id uuid NOT NULL,
    audio_url text,
    video_url text,
    notes text,
    created_at timestamp NOT NULL DEFAULT now(),
    created_by varchar(255) NOT NULL DEFAULT 'SYSTEM',
    modified_at timestamp NOT NULL DEFAULT now(),
    modified_by varchar(255) NOT NULL DEFAULT 'SYSTEM',
    deleted boolean NOT NULL DEFAULT false,
    CONSTRAINT fk_app_req_submission_application
        FOREIGN KEY (application_id) REFERENCES casting_application (id) ON DELETE CASCADE,
    CONSTRAINT fk_app_req_submission_role
        FOREIGN KEY (casting_role_id) REFERENCES casting_role (id),
    CONSTRAINT uq_app_role_submission UNIQUE (application_id, casting_role_id)
);

CREATE INDEX IF NOT EXISTS idx_app_req_submission_application ON casting_application_requirement_submission (application_id);
CREATE INDEX IF NOT EXISTS idx_app_req_submission_role ON casting_application_requirement_submission (casting_role_id);
CREATE INDEX IF NOT EXISTS idx_app_req_submission_deleted ON casting_application_requirement_submission (deleted);
