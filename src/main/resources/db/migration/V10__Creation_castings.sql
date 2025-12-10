-- ===============================================
-- SITEMETADATA OPTION TABLES (CASTING DOMAIN)
-- ===============================================

-- Tipo de proyecto (cine, serie, publicidad, etc.)
CREATE TABLE project_type_option (
  id                   uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  string_code          varchar(255) NOT NULL,
  category_string_code varchar(255) NOT NULL,
  created_at           timestamp NOT NULL DEFAULT now(),
  created_by           varchar(255) NOT NULL DEFAULT 'SYSTEM',
  modified_at          timestamp NOT NULL DEFAULT now(),
  modified_by          varchar(255) NOT NULL DEFAULT 'SYSTEM',
  deleted              boolean NOT NULL DEFAULT false
);

CREATE UNIQUE INDEX ux_project_type_option_string_code
  ON project_type_option (string_code);

CREATE INDEX idx_project_type_option_category
  ON project_type_option (category_string_code);


-- Status global del casting (Draft, Published, Paused, Closed, ...)
CREATE TABLE casting_status_option (
  id                   uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  string_code          varchar(255) NOT NULL,
  category_string_code varchar(255) NOT NULL,
  created_at           timestamp NOT NULL DEFAULT now(),
  created_by           varchar(255) NOT NULL DEFAULT 'SYSTEM',
  modified_at          timestamp NOT NULL DEFAULT now(),
  modified_by          varchar(255) NOT NULL DEFAULT 'SYSTEM',
  deleted              boolean NOT NULL DEFAULT false
);

CREATE UNIQUE INDEX ux_casting_status_option_string_code
  ON casting_status_option (string_code);

CREATE INDEX idx_casting_status_option_category
  ON casting_status_option (category_string_code);


-- Status por sección (NOT_STARTED / IN_PROGRESS / COMPLETED, etc.)
CREATE TABLE casting_section_status_option (
  id                   uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  string_code          varchar(255) NOT NULL,
  category_string_code varchar(255) NOT NULL,
  created_at           timestamp NOT NULL DEFAULT now(),
  created_by           varchar(255) NOT NULL DEFAULT 'SYSTEM',
  modified_at          timestamp NOT NULL DEFAULT now(),
  modified_by          varchar(255) NOT NULL DEFAULT 'SYSTEM',
  deleted              boolean NOT NULL DEFAULT false
);

CREATE UNIQUE INDEX ux_casting_section_status_option_string_code
  ON casting_section_status_option (string_code);

CREATE INDEX idx_casting_section_status_option_category
  ON casting_section_status_option (category_string_code);


-- Modalidad del casting (on-site, autocasting, etc.)
CREATE TABLE casting_modality_option (
  id                   uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  string_code          varchar(255) NOT NULL,
  category_string_code varchar(255) NOT NULL,
  created_at           timestamp NOT NULL DEFAULT now(),
  created_by           varchar(255) NOT NULL DEFAULT 'SYSTEM',
  modified_at          timestamp NOT NULL DEFAULT now(),
  modified_by          varchar(255) NOT NULL DEFAULT 'SYSTEM',
  deleted              boolean NOT NULL DEFAULT false
);

CREATE UNIQUE INDEX ux_casting_modality_option_string_code
  ON casting_modality_option (string_code);

CREATE INDEX idx_casting_modality_option_category
  ON casting_modality_option (category_string_code);


-- Modo de acting (NONE / GENERAL / PER_ROLE)
CREATE TABLE casting_acting_mode_option (
  id                   uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  string_code          varchar(255) NOT NULL,
  category_string_code varchar(255) NOT NULL,
  created_at           timestamp NOT NULL DEFAULT now(),
  created_by           varchar(255) NOT NULL DEFAULT 'SYSTEM',
  modified_at          timestamp NOT NULL DEFAULT now(),
  modified_by          varchar(255) NOT NULL DEFAULT 'SYSTEM',
  deleted              boolean NOT NULL DEFAULT false
);

CREATE UNIQUE INDEX ux_casting_acting_mode_option_string_code
  ON casting_acting_mode_option (string_code);

CREATE INDEX idx_casting_acting_mode_option_category
  ON casting_acting_mode_option (category_string_code);


-- Tipo de compensación (Remunerado / Colaborativo / Sin remuneración)
CREATE TABLE casting_compensation_type_option (
  id                   uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  string_code          varchar(255) NOT NULL,
  category_string_code varchar(255) NOT NULL,
  created_at           timestamp NOT NULL DEFAULT now(),
  created_by           varchar(255) NOT NULL DEFAULT 'SYSTEM',
  modified_at          timestamp NOT NULL DEFAULT now(),
  modified_by          varchar(255) NOT NULL DEFAULT 'SYSTEM',
  deleted              boolean NOT NULL DEFAULT false
);

CREATE UNIQUE INDEX ux_casting_compensation_type_option_string_code
  ON casting_compensation_type_option (string_code);

CREATE INDEX idx_casting_compensation_type_option_category
  ON casting_compensation_type_option (category_string_code);


-- Tipo de rol (protagónico, secundario, extra, etc.)
CREATE TABLE role_type_option (
  id                   uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  string_code          varchar(255) NOT NULL,
  category_string_code varchar(255) NOT NULL,
  created_at           timestamp NOT NULL DEFAULT now(),
  created_by           varchar(255) NOT NULL DEFAULT 'SYSTEM',
  modified_at          timestamp NOT NULL DEFAULT now(),
  modified_by          varchar(255) NOT NULL DEFAULT 'SYSTEM',
  deleted              boolean NOT NULL DEFAULT false
);

CREATE UNIQUE INDEX ux_role_type_option_string_code
  ON role_type_option (string_code);

CREATE INDEX idx_role_type_option_category
  ON role_type_option (category_string_code);


-- Monedas (EUR, USD, ARS, etc.)
CREATE TABLE currency_option (
  id                   uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  string_code          varchar(255) NOT NULL,
  category_string_code varchar(255) NOT NULL,
  created_at           timestamp NOT NULL DEFAULT now(),
  created_by           varchar(255) NOT NULL DEFAULT 'SYSTEM',
  modified_at          timestamp NOT NULL DEFAULT now(),
  modified_by          varchar(255) NOT NULL DEFAULT 'SYSTEM',
  deleted              boolean NOT NULL DEFAULT false
);

CREATE UNIQUE INDEX ux_currency_option_string_code
  ON currency_option (string_code);

CREATE INDEX idx_currency_option_category
  ON currency_option (category_string_code);


-- Tipo de tarifa (por día, por hora, por proyecto, etc.)
CREATE TABLE pay_rate_type_option (
  id                   uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  string_code          varchar(255) NOT NULL,
  category_string_code varchar(255) NOT NULL,
  created_at           timestamp NOT NULL DEFAULT now(),
  created_by           varchar(255) NOT NULL DEFAULT 'SYSTEM',
  modified_at          timestamp NOT NULL DEFAULT now(),
  modified_by          varchar(255) NOT NULL DEFAULT 'SYSTEM',
  deleted              boolean NOT NULL DEFAULT false
);

CREATE UNIQUE INDEX ux_pay_rate_type_option_string_code
  ON pay_rate_type_option (string_code);

CREATE INDEX idx_pay_rate_type_option_category
  ON pay_rate_type_option (category_string_code);



-- ===============================================
-- ROOT ENTITY: CASTING
-- ===============================================

CREATE TABLE casting (
  id                             uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  employer_profile_id            uuid NOT NULL,    -- FK -> employer_profile
  casting_status_option_id       uuid NOT NULL,    -- FK -> casting_status_option (Draft, Published, ...)

  created_at                     timestamp NOT NULL DEFAULT now(),
  created_by                     varchar(255) NOT NULL DEFAULT 'SYSTEM',
  modified_at                    timestamp NOT NULL DEFAULT now(),
  modified_by                    varchar(255) NOT NULL DEFAULT 'SYSTEM',
  deleted                        boolean NOT NULL DEFAULT false,

  CONSTRAINT fk_casting_employer_profile
    FOREIGN KEY (employer_profile_id) REFERENCES employer_profile (id),

  CONSTRAINT fk_casting_status_option
    FOREIGN KEY (casting_status_option_id) REFERENCES casting_status_option (id)
);

CREATE INDEX idx_casting_employer_profile
  ON casting (employer_profile_id);

CREATE INDEX idx_casting_status_option
  ON casting (casting_status_option_id);

CREATE INDEX idx_casting_deleted
  ON casting (deleted);



-- ===============================================
-- SECTION: BASIC INFO
-- ===============================================

CREATE TABLE casting_basic_info (
  id                         uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  casting_id                 uuid NOT NULL UNIQUE,  -- 1:1 con casting

  -- Status de esta sección: NOT_STARTED / IN_PROGRESS / COMPLETED
  section_status_option_id   uuid,

  title                      varchar(255) NOT NULL,
  project_type_option_id     uuid,          -- FK -> project_type_option
  location_text              varchar(255),
  casting_modality_option_id uuid,          -- FK -> casting_modality_option
  casting_modality_text      varchar(255),
  application_deadline       date,
  has_wardrobe_fitting       boolean NOT NULL DEFAULT false,
  wardrobe_fitting_text      varchar(255),
  shooting_start_date        date,
  shooting_end_date          date,
  description                varchar(255),

  created_at                 timestamp NOT NULL DEFAULT now(),
  created_by                 varchar(255) NOT NULL DEFAULT 'SYSTEM',
  modified_at                timestamp NOT NULL DEFAULT now(),
  modified_by                varchar(255) NOT NULL DEFAULT 'SYSTEM',
  deleted                    boolean NOT NULL DEFAULT false,

  CONSTRAINT fk_casting_basic_info_casting
    FOREIGN KEY (casting_id) REFERENCES casting (id) ON DELETE CASCADE,

  CONSTRAINT fk_casting_basic_info_project_type
    FOREIGN KEY (project_type_option_id) REFERENCES project_type_option (id),

  CONSTRAINT fk_casting_basic_info_casting_modality
    FOREIGN KEY (casting_modality_option_id) REFERENCES casting_modality_option (id),

  CONSTRAINT fk_casting_basic_info_section_status
    FOREIGN KEY (section_status_option_id) REFERENCES casting_section_status_option (id)
);

CREATE INDEX idx_casting_basic_info_casting
  ON casting_basic_info (casting_id);

CREATE INDEX idx_casting_basic_info_section_status
  ON casting_basic_info (section_status_option_id);

CREATE INDEX idx_casting_basic_info_deleted
  ON casting_basic_info (deleted);


-- Adjuntos (documentos, guiones, fotos de referencia, etc.)
CREATE TABLE casting_attachment (
  id                    uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  casting_basic_info_id uuid NOT NULL,     -- FK -> casting_basic_info
  file_url              text NOT NULL,
  file_name             varchar(255),
  file_type             varchar(100),
  file_size             bigint,

  created_at            timestamp NOT NULL DEFAULT now(),
  created_by            varchar(255) NOT NULL DEFAULT 'SYSTEM',
  modified_at           timestamp NOT NULL DEFAULT now(),
  modified_by           varchar(255) NOT NULL DEFAULT 'SYSTEM',
  deleted               boolean NOT NULL DEFAULT false,

  CONSTRAINT fk_casting_attachment_basic_info
    FOREIGN KEY (casting_basic_info_id) REFERENCES casting_basic_info (id) ON DELETE CASCADE
);

CREATE INDEX idx_casting_attachment_basic_info
  ON casting_attachment (casting_basic_info_id);

CREATE INDEX idx_casting_attachment_deleted
  ON casting_attachment (deleted);


-- ===============================================
-- SECTION: ROLES
-- ===============================================

-- Sección de Roles de un casting (engloba todos los roles de ese casting)
-- 1:1 con CASTING
-- Sirve para:
--   - guardar el status de la sección (NOT_STARTED / IN_PROGRESS / COMPLETED)
--   - guardar notas generales aplicables a todos los roles
CREATE TABLE casting_roles_section (
  id                        uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  casting_id                uuid NOT NULL UNIQUE,

  -- Status de la sección Roles
  section_status_option_id  uuid,

  -- Notas generales para todos los roles de este casting
  notes                     text,

  created_at                timestamp NOT NULL DEFAULT now(),
  created_by                varchar(255) NOT NULL DEFAULT 'SYSTEM',
  modified_at               timestamp NOT NULL DEFAULT now(),
  modified_by               varchar(255) NOT NULL DEFAULT 'SYSTEM',
  deleted                   boolean NOT NULL DEFAULT false,

  CONSTRAINT fk_casting_roles_section_casting
    FOREIGN KEY (casting_id) REFERENCES casting (id) ON DELETE CASCADE,

  CONSTRAINT fk_casting_roles_section_status
    FOREIGN KEY (section_status_option_id) REFERENCES casting_section_status_option (id)
);

CREATE INDEX idx_casting_roles_section_casting
  ON casting_roles_section (casting_id);

CREATE INDEX idx_casting_roles_section_status
  ON casting_roles_section (section_status_option_id);

CREATE INDEX idx_casting_roles_section_deleted
  ON casting_roles_section (deleted);



-- Rol dentro del casting (nombre del personaje, edad, género, etc.)
-- Cada fila es UN rol específico dentro de la sección Roles
CREATE TABLE casting_role (
  id                      uuid PRIMARY KEY DEFAULT gen_random_uuid(),

  -- Sección de roles a la que pertenece este rol (1 sección -> N roles)
  casting_roles_section_id uuid NOT NULL,

  -- Flag para marcar si ESTE rol está "completo" según tus reglas virtuales
  -- (se usa para calcular el status de la sección y para resaltar roles incompletos en UI)
  is_complete             boolean NOT NULL DEFAULT false,

  role_name               varchar(255) NOT NULL,
  role_type_option_id     uuid,
  gender_option_id        uuid,
  age_min                 smallint,
  age_max                 smallint,
  description             text,

  created_at              timestamp NOT NULL DEFAULT now(),
  created_by              varchar(255) NOT NULL DEFAULT 'SYSTEM',
  modified_at             timestamp NOT NULL DEFAULT now(),
  modified_by             varchar(255) NOT NULL DEFAULT 'SYSTEM',
  deleted                 boolean NOT NULL DEFAULT false,

  CONSTRAINT fk_casting_role_roles_section
    FOREIGN KEY (casting_roles_section_id) REFERENCES casting_roles_section (id) ON DELETE CASCADE,

  CONSTRAINT fk_casting_role_role_type_option
    FOREIGN KEY (role_type_option_id) REFERENCES role_type_option (id),

  CONSTRAINT fk_casting_role_gender_option
    FOREIGN KEY (gender_option_id) REFERENCES gender_option (id)
);

CREATE INDEX idx_casting_role_roles_section
  ON casting_role (casting_roles_section_id);

CREATE INDEX idx_casting_role_deleted
  ON casting_role (deleted);



-- Profesiones requeridas por rol (Actor, Músico, etc.) ManyToMany
CREATE TABLE casting_role_profession (
  casting_role_id uuid NOT NULL,
  profession_id   uuid NOT NULL,
  PRIMARY KEY (casting_role_id, profession_id),

  CONSTRAINT fk_casting_role_profession_role
    FOREIGN KEY (casting_role_id) REFERENCES casting_role (id) ON DELETE CASCADE,

  CONSTRAINT fk_casting_role_profession_profession
    FOREIGN KEY (profession_id) REFERENCES professions (id)
);

CREATE INDEX idx_casting_role_profession_profession
  ON casting_role_profession (profession_id);



-- Características físicas requeridas por rol (clon simplificado de talent_characteristics)
CREATE TABLE casting_role_characteristics (
  id              uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  created_at      timestamp,
  created_by      varchar(255),
  deleted         boolean NOT NULL DEFAULT false,
  modified_at     timestamp,
  modified_by     varchar(255),

  chest_cm        varchar(255),
  dress_size      varchar(255),
  driving_license boolean,
  height_cm       integer,
  hip_cm          varchar(255),
  pant_size       varchar(255),
  passport        boolean,
  shirt_size      varchar(255),
  shoe_size       varchar(255),
  tattoo          boolean,
  waist_cm        varchar(255),
  weight_kg       integer,

  diet_option_id  uuid,
  eye_color_id    uuid,
  hair_color_id   uuid,
  casting_role_id uuid NOT NULL,
  ethnicity_id    uuid,

  CONSTRAINT uk_casting_role_characteristics_role UNIQUE (casting_role_id)
);

CREATE INDEX idx_casting_role_characteristics_diet
  ON casting_role_characteristics (diet_option_id);

CREATE INDEX idx_casting_role_characteristics_ethnicity
  ON casting_role_characteristics (ethnicity_id);

CREATE INDEX idx_casting_role_characteristics_eye
  ON casting_role_characteristics (eye_color_id);

CREATE INDEX idx_casting_role_characteristics_hair
  ON casting_role_characteristics (hair_color_id);

ALTER TABLE casting_role_characteristics
  ADD CONSTRAINT fk_casting_role_characteristics_diet
    FOREIGN KEY (diet_option_id) REFERENCES diet_option(id);

ALTER TABLE casting_role_characteristics
  ADD CONSTRAINT fk_casting_role_characteristics_ethnicity
    FOREIGN KEY (ethnicity_id) REFERENCES ethnicity_option(id);

ALTER TABLE casting_role_characteristics
  ADD CONSTRAINT fk_casting_role_characteristics_eye
    FOREIGN KEY (eye_color_id) REFERENCES color_option(id);

ALTER TABLE casting_role_characteristics
  ADD CONSTRAINT fk_casting_role_characteristics_hair
    FOREIGN KEY (hair_color_id) REFERENCES color_option(id);

ALTER TABLE casting_role_characteristics
  ADD CONSTRAINT fk_casting_role_characteristics_role
    FOREIGN KEY (casting_role_id) REFERENCES casting_role(id) ON DELETE CASCADE;


-- Skills requeridas por rol (ManyToMany con skills)
CREATE TABLE casting_role_skill (
  casting_role_id uuid NOT NULL,
  skill_id        uuid NOT NULL,
  PRIMARY KEY (casting_role_id, skill_id),

  CONSTRAINT fk_casting_role_skill_role
    FOREIGN KEY (casting_role_id) REFERENCES casting_role (id) ON DELETE CASCADE,

  CONSTRAINT fk_casting_role_skill_skill
    FOREIGN KEY (skill_id) REFERENCES skills (id)
);

CREATE INDEX idx_casting_role_skill_skill
  ON casting_role_skill (skill_id);


-- ===============================================
-- SECTION: ACTING
-- ===============================================

-- Configuración principal de Acting (modo: NONE / GENERAL / PER_ROLE)
CREATE TABLE casting_acting (
  id                       uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  casting_id               uuid NOT NULL UNIQUE,

  -- Status de esta sección (según requisitos de acting configurados)
  section_status_option_id uuid,

  casting_acting_mode_option_id uuid NOT NULL, -- FK -> casting_acting_mode_option

  created_at               timestamp NOT NULL DEFAULT now(),
  created_by               varchar(255) NOT NULL DEFAULT 'SYSTEM',
  modified_at              timestamp NOT NULL DEFAULT now(),
  modified_by              varchar(255) NOT NULL DEFAULT 'SYSTEM',
  deleted                  boolean NOT NULL DEFAULT false,

  CONSTRAINT fk_casting_acting_casting
    FOREIGN KEY (casting_id) REFERENCES casting (id) ON DELETE CASCADE,

  CONSTRAINT fk_casting_acting_mode_option
    FOREIGN KEY (casting_acting_mode_option_id) REFERENCES casting_acting_mode_option (id),

  CONSTRAINT fk_casting_acting_section_status
    FOREIGN KEY (section_status_option_id) REFERENCES casting_section_status_option (id)
);

CREATE INDEX idx_casting_acting_casting
  ON casting_acting (casting_id);

CREATE INDEX idx_casting_acting_section_status
  ON casting_acting (section_status_option_id);

CREATE INDEX idx_casting_acting_deleted
  ON casting_acting (deleted);


-- Bloques de requisitos de acting:
-- - GLOBAL: casting_role_id = NULL (mismo acting para todos los roles)
-- - POR ROL: casting_role_id = id del rol
-- slots_count = número de inputs (Acting 1..N) que se pedirán en ese bloque
CREATE TABLE casting_acting_requirement (
  id                 uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  casting_acting_id  uuid NOT NULL,  -- FK -> casting_acting
  casting_role_id    uuid,           -- FK -> casting_role (opcional, solo modo PER_ROLE)
  description        text,           -- instrucción que verá el Talent (ej. "Monólogo dramático 1 min")
  slots_count        integer NOT NULL,

  created_at         timestamp NOT NULL DEFAULT now(),
  created_by         varchar(255) NOT NULL DEFAULT 'SYSTEM',
  modified_at        timestamp NOT NULL DEFAULT now(),
  modified_by        varchar(255) NOT NULL DEFAULT 'SYSTEM',
  deleted            boolean NOT NULL DEFAULT false,

  CONSTRAINT fk_casting_acting_requirement_acting
    FOREIGN KEY (casting_acting_id) REFERENCES casting_acting (id) ON DELETE CASCADE,

  CONSTRAINT fk_casting_acting_requirement_role
    FOREIGN KEY (casting_role_id) REFERENCES casting_role (id) ON DELETE CASCADE
);

CREATE INDEX idx_casting_acting_requirement_acting
  ON casting_acting_requirement (casting_acting_id);

CREATE INDEX idx_casting_acting_requirement_role
  ON casting_acting_requirement (casting_role_id);

CREATE INDEX idx_casting_acting_requirement_deleted
  ON casting_acting_requirement (deleted);



-- ===============================================
-- SECTION: REMUNERATION
-- ===============================================

-- Configuración general de remuneración:
-- - compensation_type_option_id: Remunerado / Colaborativo / Sin remuneración
-- - pay_same_for_all_roles: ¿todos los roles pagan lo mismo?
CREATE TABLE casting_remuneration (
  id                          uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  casting_id                  uuid NOT NULL UNIQUE,

  -- Status de esta sección (según remuneraciones definidas)
  section_status_option_id    uuid,

  compensation_type_option_id uuid NOT NULL,   -- FK -> casting_compensation_type_option
  pay_same_for_all_roles      boolean NOT NULL DEFAULT true,

  created_at                  timestamp NOT NULL DEFAULT now(),
  created_by                  varchar(255) NOT NULL DEFAULT 'SYSTEM',
  modified_at                 timestamp NOT NULL DEFAULT now(),
  modified_by                 varchar(255) NOT NULL DEFAULT 'SYSTEM',
  deleted                     boolean NOT NULL DEFAULT false,

  CONSTRAINT fk_casting_remuneration_casting
    FOREIGN KEY (casting_id) REFERENCES casting (id) ON DELETE CASCADE,

  CONSTRAINT fk_casting_remuneration_compensation_type
    FOREIGN KEY (compensation_type_option_id) REFERENCES casting_compensation_type_option (id),

  CONSTRAINT fk_casting_remuneration_section_status
    FOREIGN KEY (section_status_option_id) REFERENCES casting_section_status_option (id)
);

CREATE INDEX idx_casting_remuneration_casting
  ON casting_remuneration (casting_id);

CREATE INDEX idx_casting_remuneration_section_status
  ON casting_remuneration (section_status_option_id);

CREATE INDEX idx_casting_remuneration_deleted
  ON casting_remuneration (deleted);


-- Detalle de remuneración por rol:
-- Si pay_same_for_all_roles = true:
--   el backend copia el mismo esquema a todos los roles en esta tabla.
-- Si es false:
--   el backend espera valores específicos por rol.
CREATE TABLE casting_role_remuneration (
  id                      uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  casting_role_id         uuid NOT NULL UNIQUE,     -- 1:1 con casting_role
  pay_rate_type_option_id uuid,                     -- FK -> pay_rate_type_option
  currency_option_id      uuid,                     -- FK -> currency_option
  amount                  numeric(12, 2),
  notes                   text,

  created_at              timestamp NOT NULL DEFAULT now(),
  created_by              varchar(255) NOT NULL DEFAULT 'SYSTEM',
  modified_at             timestamp NOT NULL DEFAULT now(),
  modified_by             varchar(255) NOT NULL DEFAULT 'SYSTEM',
  deleted                 boolean NOT NULL DEFAULT false,

  CONSTRAINT fk_casting_role_remuneration_role
    FOREIGN KEY (casting_role_id) REFERENCES casting_role (id) ON DELETE CASCADE,

  CONSTRAINT fk_casting_role_remuneration_pay_rate_type
    FOREIGN KEY (pay_rate_type_option_id) REFERENCES pay_rate_type_option (id),

  CONSTRAINT fk_casting_role_remuneration_currency
    FOREIGN KEY (currency_option_id) REFERENCES currency_option (id)
);

CREATE INDEX idx_casting_role_remuneration_role
  ON casting_role_remuneration (casting_role_id);

CREATE INDEX idx_casting_role_remuneration_deleted
  ON casting_role_remuneration (deleted);


-- ===============================================
-- SITEMETADATA SEEDS (CASTING DOMAIN)
-- ===============================================

-- 1) CASTING STATUS (Draft / Closed / Published / Paused / Archived)
INSERT INTO casting_status_option (
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
  (gen_random_uuid(), 'sitemetadata.casting_status.draft',     'sitemetadata.casting_status', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
  (gen_random_uuid(), 'sitemetadata.casting_status.closed',    'sitemetadata.casting_status', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
  (gen_random_uuid(), 'sitemetadata.casting_status.published', 'sitemetadata.casting_status', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
  (gen_random_uuid(), 'sitemetadata.casting_status.paused',    'sitemetadata.casting_status', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
  (gen_random_uuid(), 'sitemetadata.casting_status.archived',  'sitemetadata.casting_status', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false);



-- 2) SECTION STATUS (NOT_STARTED / IN_PROGRESS / COMPLETED)
INSERT INTO casting_section_status_option (
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
  (gen_random_uuid(), 'sitemetadata.casting_section_status.not_started', 'sitemetadata.casting_section_status', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
  (gen_random_uuid(), 'sitemetadata.casting_section_status.in_progress', 'sitemetadata.casting_section_status', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
  (gen_random_uuid(), 'sitemetadata.casting_section_status.completed',   'sitemetadata.casting_section_status', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false);



-- 3) PROJECT TYPE
-- [Digital/RRSS, Short film, Documentary, Feature film, Musical, Theater play,
--  Student project, Commercial, Music video, Other]
INSERT INTO project_type_option (
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
  (gen_random_uuid(), 'sitemetadata.project_type.digital_content', 'sitemetadata.project_type', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
  (gen_random_uuid(), 'sitemetadata.project_type.short_film',      'sitemetadata.project_type', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
  (gen_random_uuid(), 'sitemetadata.project_type.documentary',     'sitemetadata.project_type', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
  (gen_random_uuid(), 'sitemetadata.project_type.feature_film',    'sitemetadata.project_type', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
  (gen_random_uuid(), 'sitemetadata.project_type.musical',         'sitemetadata.project_type', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
  (gen_random_uuid(), 'sitemetadata.project_type.theatre_play',    'sitemetadata.project_type', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
  (gen_random_uuid(), 'sitemetadata.project_type.student_project', 'sitemetadata.project_type', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
  (gen_random_uuid(), 'sitemetadata.project_type.commercial',      'sitemetadata.project_type', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
  (gen_random_uuid(), 'sitemetadata.project_type.music_video',     'sitemetadata.project_type', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
  (gen_random_uuid(), 'sitemetadata.project_type.other',           'sitemetadata.project_type', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false);



-- 4) CASTING MODALITY [Presencial / Autocasting]
INSERT INTO casting_modality_option (
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
  (gen_random_uuid(), 'sitemetadata.casting_modality.on_site',    'sitemetadata.casting_modality', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
  (gen_random_uuid(), 'sitemetadata.casting_modality.autocasting','sitemetadata.casting_modality', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false);



-- 5) ROLE TYPE
-- [Lead, Secondary, Extra, Voice, Host, Creator/Influencer, Guest, Other]
INSERT INTO role_type_option (
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
  (gen_random_uuid(), 'sitemetadata.role_type.lead',      'sitemetadata.role_type', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
  (gen_random_uuid(), 'sitemetadata.role_type.secondary', 'sitemetadata.role_type', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
  (gen_random_uuid(), 'sitemetadata.role_type.extra',     'sitemetadata.role_type', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
  (gen_random_uuid(), 'sitemetadata.role_type.voice',     'sitemetadata.role_type', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
  (gen_random_uuid(), 'sitemetadata.role_type.host',      'sitemetadata.role_type', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
  (gen_random_uuid(), 'sitemetadata.role_type.creator',   'sitemetadata.role_type', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
  (gen_random_uuid(), 'sitemetadata.role_type.guest',     'sitemetadata.role_type', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
  (gen_random_uuid(), 'sitemetadata.role_type.other',     'sitemetadata.role_type', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false);



-- 6) ACTING MODE [General, Per role, None]
INSERT INTO casting_acting_mode_option (
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
  (gen_random_uuid(), 'sitemetadata.acting_mode.general',   'sitemetadata.acting_mode', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
  (gen_random_uuid(), 'sitemetadata.acting_mode.per_role',  'sitemetadata.acting_mode', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
  (gen_random_uuid(), 'sitemetadata.acting_mode.none',      'sitemetadata.acting_mode', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false);



-- 7) COMPENSATION TYPE [Remunerado, Colaborativo, Sin remuneración]
INSERT INTO casting_compensation_type_option (
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
  (gen_random_uuid(), 'sitemetadata.compensation_type.paid',         'sitemetadata.compensation_type', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
  (gen_random_uuid(), 'sitemetadata.compensation_type.collaborative','sitemetadata.compensation_type', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
  (gen_random_uuid(), 'sitemetadata.compensation_type.unpaid',         'sitemetadata.compensation_type', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false);



-- 8) PAY RATE TYPE [Fixed, Hour, Day, Week, Month, Season/Show, To be defined]
INSERT INTO pay_rate_type_option (
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
  (gen_random_uuid(), 'sitemetadata.pay_rate_type.fixed',       'sitemetadata.pay_rate_type', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
  (gen_random_uuid(), 'sitemetadata.pay_rate_type.per_hour',    'sitemetadata.pay_rate_type', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
  (gen_random_uuid(), 'sitemetadata.pay_rate_type.per_day',     'sitemetadata.pay_rate_type', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
  (gen_random_uuid(), 'sitemetadata.pay_rate_type.per_week',    'sitemetadata.pay_rate_type', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
  (gen_random_uuid(), 'sitemetadata.pay_rate_type.per_month',   'sitemetadata.pay_rate_type', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
  (gen_random_uuid(), 'sitemetadata.pay_rate_type.per_season',  'sitemetadata.pay_rate_type', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
  (gen_random_uuid(), 'sitemetadata.pay_rate_type.to_be_agreed','sitemetadata.pay_rate_type', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false);



-- 9) CURRENCY [ARS, USD]
INSERT INTO currency_option (
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
  (gen_random_uuid(), 'sitemetadata.currency.ars', 'sitemetadata.currency', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
  (gen_random_uuid(), 'sitemetadata.currency.usd', 'sitemetadata.currency', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false);
