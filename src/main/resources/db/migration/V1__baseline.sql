-- V1__schema.sql
-- Esquema base (solo DDL). No crear flyway_schema_history: la maneja Flyway.

-- 1) Aseguramos el schema y el search_path
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_namespace WHERE nspname = 'public') THEN
    EXECUTE 'CREATE SCHEMA public';
  END IF;
END$$;

-- Si tu proveedor revocó permisos en public, quizá necesites (opcional):
-- ALTER SCHEMA public OWNER TO CURRENT_USER;

SET search_path = public;

-- 2) Extensiones necesarias (si tu plataforma lo permite)
CREATE EXTENSION IF NOT EXISTS pgcrypto WITH SCHEMA public;


CREATE TABLE public.roles (
  id uuid NOT NULL,
  created_at timestamp(6) NULL,
  created_by varchar(255) NULL,
  deleted bool NOT NULL,
  modified_at timestamp(6) NULL,
  modified_by varchar(255) NULL,
  code varchar(255) NOT NULL,
  description varchar(255) NULL,
  name_string_code varchar(255) NOT NULL,
  CONSTRAINT roles_pkey PRIMARY KEY (id),
  CONSTRAINT ukch1113horj4qr56f91omojv8 UNIQUE (code)
);

CREATE TABLE public."plans" (
  id uuid NOT NULL,
  created_at timestamp(6) NULL,
  created_by varchar(255) NULL,
  deleted bool NOT NULL,
  modified_at timestamp(6) NULL,
  modified_by varchar(255) NULL,
  allows_custom_slug bool NOT NULL,
  code varchar(255) NOT NULL,
  description varchar(255) NULL,
  name_string_code varchar(255) NOT NULL,
  CONSTRAINT plans_pkey PRIMARY KEY (id),
  CONSTRAINT ukbsiq2g7uq9l49v27bsijicsys UNIQUE (code)
);

CREATE TABLE public.gender_option (
  id uuid NOT NULL,
  created_at timestamp(6) NULL,
  created_by varchar(255) NULL,
  deleted bool NOT NULL,
  modified_at timestamp(6) NULL,
  modified_by varchar(255) NULL,
  category_string_code varchar(255) NULL,
  string_code varchar(255) NOT NULL,
  CONSTRAINT gender_option_pkey PRIMARY KEY (id)
);

CREATE TABLE public.color_option (
  id uuid NOT NULL,
  created_at timestamp(6) NULL,
  created_by varchar(255) NULL,
  deleted bool NOT NULL,
  modified_at timestamp(6) NULL,
  modified_by varchar(255) NULL,
  category_string_code varchar(255) NULL,
  string_code varchar(255) NOT NULL,
  CONSTRAINT color_option_pkey PRIMARY KEY (id)
);

CREATE TABLE public.diet_option (
  id uuid NOT NULL,
  created_at timestamp(6) NULL,
  created_by varchar(255) NULL,
  deleted bool NOT NULL,
  modified_at timestamp(6) NULL,
  modified_by varchar(255) NULL,
  category_string_code varchar(255) NULL,
  string_code varchar(255) NOT NULL,
  CONSTRAINT diet_option_pkey PRIMARY KEY (id)
);

CREATE TABLE public.professions (
  id uuid NOT NULL,
  created_at timestamp(6) NULL,
  created_by varchar(255) NULL,
  deleted bool NOT NULL,
  modified_at timestamp(6) NULL,
  modified_by varchar(255) NULL,
  category_string_code varchar(255) NULL,
  string_code varchar(255) NOT NULL,
  CONSTRAINT professions_pkey PRIMARY KEY (id)
);

CREATE TABLE public.production_type (
  id uuid NOT NULL,
  created_at timestamp(6) NULL,
  created_by varchar(255) NULL,
  deleted bool NOT NULL,
  modified_at timestamp(6) NULL,
  modified_by varchar(255) NULL,
  category_string_code varchar(255) NULL,
  string_code varchar(255) NOT NULL,
  CONSTRAINT production_type_pkey PRIMARY KEY (id)
);

CREATE TABLE public.skills (
  id uuid NOT NULL,
  created_at timestamp(6) NULL,
  created_by varchar(255) NULL,
  deleted bool NOT NULL,
  modified_at timestamp(6) NULL,
  modified_by varchar(255) NULL,
  category_string_code varchar(255) NULL,
  string_code varchar(255) NOT NULL,
  CONSTRAINT skills_pkey PRIMARY KEY (id)
);

CREATE TABLE public.legal_documents (
  id uuid NOT NULL,
  "type" varchar(32) NOT NULL,
  locale varchar(8) NOT NULL,
  "version" varchar(32) NOT NULL,
  title text NOT NULL,
  slug text NOT NULL,
  status varchar(16) NOT NULL,
  format varchar(16) NOT NULL,
  body_template text NOT NULL,
  content_hash varchar(64) NOT NULL,
  effective_at timestamptz NOT NULL,
  published_at timestamptz NULL,
  created_at timestamp(6) NULL,
  created_by varchar(255) NULL,
  modified_at timestamp(6) NULL,
  modified_by varchar(255) NULL,
  deleted bool NOT NULL,
  CONSTRAINT legal_documents_pkey PRIMARY KEY (id),
  CONSTRAINT uq_doc UNIQUE (type, locale, version)
);
CREATE INDEX idx_doc_lookup ON public.legal_documents (type, locale, status, effective_at);

/* Usuarios y entidades que dependen de catálogos */
CREATE TABLE public.users (
  id uuid NOT NULL,
  created_at timestamp(6) NULL,
  created_by varchar(255) NULL,
  deleted bool NOT NULL,
  modified_at timestamp(6) NULL,
  modified_by varchar(255) NULL,
  email varchar(255) NOT NULL,
  "password" varchar(255) NULL,
  user_account_provider varchar(255) NOT NULL,
  role_id uuid NOT NULL,
  CONSTRAINT users_pkey PRIMARY KEY (id),
  CONSTRAINT uk6dotkott2kjsp8vw4d0m25fb7 UNIQUE (email),
  CONSTRAINT users_user_account_provider_check CHECK (
    (user_account_provider)::text = ANY (ARRAY['LOCAL','OTHER']::text[])
  ),
  CONSTRAINT fkp56c1712k691lhsyewcssf40f FOREIGN KEY (role_id) REFERENCES public.roles(id)
);

CREATE TABLE public.profile (
  id uuid NOT NULL,
  created_at timestamp(6) NULL,
  created_by varchar(255) NULL,
  deleted bool NOT NULL,
  modified_at timestamp(6) NULL,
  modified_by varchar(255) NULL,
  default_slug varchar(255) NOT NULL,
  premium_slug varchar(255) NULL,
  plan_id uuid NOT NULL,
  user_id uuid NOT NULL,
  CONSTRAINT profile_pkey PRIMARY KEY (id),
  CONSTRAINT uks1vv3awi6krwxfomipnl7o558 UNIQUE (default_slug),
  CONSTRAINT uks0ihtejjm8ok00dcfow6kmrkf UNIQUE (premium_slug),
  CONSTRAINT ukc1dkiawnlj6uoe6fnlwd6j83j UNIQUE (user_id),
  CONSTRAINT fk2oe2dtxlgbtvd2dsrxhlvgo5p FOREIGN KEY (plan_id) REFERENCES public."plans"(id),
  CONSTRAINT fks14jvsf9tqrcnly0afsv0ngwv FOREIGN KEY (user_id) REFERENCES public.users(id)
);

CREATE TABLE public.profile_basic_info (
  id uuid NOT NULL,
  created_at timestamp(6) NULL,
  created_by varchar(255) NULL,
  deleted bool NOT NULL,
  modified_at timestamp(6) NULL,
  modified_by varchar(255) NULL,
  birth_date date NULL,
  stage_name varchar(255) NOT NULL,
  gender_id uuid NULL,
  profile_id uuid NOT NULL,
  CONSTRAINT profile_basic_info_pkey PRIMARY KEY (id),
  CONSTRAINT ukoxl4623wy0efyoxvi1ggmc4h9 UNIQUE (profile_id),
  CONSTRAINT fk3bvsge9vjxpofl2vs8niqswm8 FOREIGN KEY (gender_id) REFERENCES public.gender_option(id),
  CONSTRAINT fko9seovpiuc12dotvwy1qoe41o FOREIGN KEY (profile_id) REFERENCES public.profile(id)
);

CREATE TABLE public.profile_contact (
  id uuid NOT NULL,
  created_at timestamp(6) NULL,
  created_by varchar(255) NULL,
  deleted bool NOT NULL,
  modified_at timestamp(6) NULL,
  modified_by varchar(255) NULL,
  email varchar(255) NULL,
  phone_number varchar(255) NULL,
  profile_id uuid NOT NULL,
  CONSTRAINT profile_contact_pkey PRIMARY KEY (id),
  CONSTRAINT ukjfubme5tlmcml2aeg5nvan334 UNIQUE (profile_id),
  CONSTRAINT fk8jelp1wrpm1i0xdctfppihtnu FOREIGN KEY (profile_id) REFERENCES public.profile(id)
);

CREATE TABLE public.profile_media (
  id uuid NOT NULL,
  created_at timestamp(6) NULL,
  created_by varchar(255) NULL,
  deleted bool NOT NULL,
  modified_at timestamp(6) NULL,
  modified_by varchar(255) NULL,
  full_body_image_url varchar(255) NULL,
  headshot_image_url varchar(255) NULL,
  introduction_video_url varchar(255) NULL,
  show_reel_video_url varchar(255) NULL,
  profile_id uuid NOT NULL,
  CONSTRAINT profile_media_pkey PRIMARY KEY (id),
  CONSTRAINT ukeyw2dx2yxbydvyvmsb27a4xf UNIQUE (profile_id),
  CONSTRAINT fkiy97kv2mpud9y5i0cg4w5e1rx FOREIGN KEY (profile_id) REFERENCES public.profile(id)
);

CREATE TABLE public.profile_characteristics (
  id uuid NOT NULL,
  created_at timestamp(6) NULL,
  created_by varchar(255) NULL,
  deleted bool NOT NULL,
  modified_at timestamp(6) NULL,
  modified_by varchar(255) NULL,
  chest_cm varchar(255) NULL,
  dress_size varchar(255) NULL,
  driving_license bool NULL,
  height_cm int4 NULL,
  hip_cm varchar(255) NULL,
  pant_size varchar(255) NULL,
  passport bool NULL,
  shirt_size varchar(255) NULL,
  shoe_size varchar(255) NULL,
  tattoo bool NULL,
  waist_cm varchar(255) NULL,
  weight_kg int4 NULL,
  diet_option_id uuid NULL,
  eye_color_id uuid NULL,
  hair_color_id uuid NULL,
  profile_id uuid NOT NULL,
  CONSTRAINT profile_characteristics_pkey PRIMARY KEY (id),
  /* NOTA: estas UQ hacen que cada valor se use SOLO una vez globalmente.
     Si no era intencional, reemplázalas por INDEX normales. */
  CONSTRAINT uk9h72dqftit3bssthsmfns3bti UNIQUE (diet_option_id),
  CONSTRAINT ukof5nt12u8iraqfbem856g7idd UNIQUE (eye_color_id),
  CONSTRAINT ukpc57mbg1qhlb203q9f8xiy7up UNIQUE (hair_color_id),
  CONSTRAINT ukh9n1p0psgfb7hwd03p77tqxpp UNIQUE (profile_id),
  CONSTRAINT fk37v8snusb5d798x7us1hdxavx FOREIGN KEY (diet_option_id) REFERENCES public.diet_option(id),
  CONSTRAINT fk8hgt8xpk7qe8o8fbt6b37497j FOREIGN KEY (profile_id) REFERENCES public.profile(id),
  CONSTRAINT fker8oxm5f9s2cdo574gr3h3fo0 FOREIGN KEY (eye_color_id) REFERENCES public.color_option(id),
  CONSTRAINT fkl50j6kradcd2uunxsdycm1sg7 FOREIGN KEY (hair_color_id) REFERENCES public.color_option(id)
);

CREATE TABLE public.profile_credit (
  id uuid NOT NULL,
  created_at timestamp(6) NULL,
  created_by varchar(255) NULL,
  deleted bool NOT NULL,
  modified_at timestamp(6) NULL,
  modified_by varchar(255) NULL,
  producer_name varchar(255) NULL,
  project_name varchar(255) NULL,
  "role" varchar(255) NULL,
  "year" varchar(255) NULL,
  production_type_id uuid NOT NULL,
  profile_id uuid NOT NULL,
  CONSTRAINT profile_credit_pkey PRIMARY KEY (id),
  CONSTRAINT fkfhtrlbi7w94n53h6dt3clt82u FOREIGN KEY (profile_id) REFERENCES public.profile(id),
  CONSTRAINT fkgha7fqcorbg3u7dmb9b9cxd3c FOREIGN KEY (production_type_id) REFERENCES public.production_type(id)
);

CREATE TABLE public.profile_education (
  id uuid NOT NULL,
  created_at timestamp(6) NULL,
  created_by varchar(255) NULL,
  deleted bool NOT NULL,
  modified_at timestamp(6) NULL,
  modified_by varchar(255) NULL,
  course_name varchar(255) NULL,
  graduation_year varchar(255) NULL,
  institution varchar(255) NULL,
  profile_id uuid NOT NULL,
  CONSTRAINT profile_education_pkey PRIMARY KEY (id),
  CONSTRAINT fknyamm2kg8h69w8hslttg6i2y FOREIGN KEY (profile_id) REFERENCES public.profile(id)
);

CREATE TABLE public.profile_social_media (
  id uuid NOT NULL,
  created_at timestamp(6) NULL,
  created_by varchar(255) NULL,
  deleted bool NOT NULL,
  modified_at timestamp(6) NULL,
  modified_by varchar(255) NULL,
  instagram_url varchar(255) NULL,
  tik_tok_url varchar(255) NULL,
  profile_id uuid NOT NULL,
  CONSTRAINT profile_social_media_pkey PRIMARY KEY (id),
  CONSTRAINT uk7k03keyece8o8lo0qu9binlog UNIQUE (profile_id),
  CONSTRAINT fk18svgm9sfla1s4yv6grlm99k1 FOREIGN KEY (profile_id) REFERENCES public.profile(id)
);

CREATE TABLE public.profile_skill (
  profile_id uuid NOT NULL,
  skill_id uuid NOT NULL,
  CONSTRAINT profile_skill_pkey PRIMARY KEY (profile_id, skill_id),
  CONSTRAINT fkcxbysh527relenn6vd73sj0x8 FOREIGN KEY (profile_id) REFERENCES public.profile(id),
  CONSTRAINT fkox34jgj5yu0xn58roe0b49d8i FOREIGN KEY (skill_id) REFERENCES public.skills(id)
);

CREATE TABLE public.basic_info_profession (
  basic_info_id uuid NOT NULL,
  profession_id uuid NOT NULL,
  CONSTRAINT basic_info_profession_pkey PRIMARY KEY (basic_info_id, profession_id),
  CONSTRAINT fkrsgpihn1djur17i3e9kunu51g FOREIGN KEY (basic_info_id) REFERENCES public.profile_basic_info(id),
  CONSTRAINT fk2f1asm35gl1jcx29vswi7acbh FOREIGN KEY (profession_id) REFERENCES public.professions(id)
);

CREATE TABLE public.media_entity_other_pictures_url (
  media_entity_id uuid NOT NULL,
  url varchar(255) NULL,
  idx int4 NOT NULL,
  CONSTRAINT media_entity_other_pictures_url_pkey PRIMARY KEY (media_entity_id, idx),
  CONSTRAINT fkoj6b1jfhqwl5xhm7rdcggmwma FOREIGN KEY (media_entity_id) REFERENCES public.profile_media(id)
);

/* Aceptaciones legales (depende de legal_documents) */
CREATE TABLE public.legal_acceptances (
  id uuid NOT NULL,
  user_id uuid NOT NULL,
  legal_document_id uuid NOT NULL,
  accepted_at timestamptz NOT NULL,
  ip varchar(255) NULL,
  user_agent varchar(512) NULL,
  content_hash varchar(64) NOT NULL,
  created_at timestamp(6) NULL,
  created_by varchar(255) NULL,
  modified_at timestamp(6) NULL,
  modified_by varchar(255) NULL,
  deleted bool NOT NULL,
  CONSTRAINT legal_acceptances_pkey PRIMARY KEY (id),
  CONSTRAINT fk_legal_acc_doc FOREIGN KEY (legal_document_id) REFERENCES public.legal_documents(id)
);
CREATE INDEX idx_acc_doc ON public.legal_acceptances (legal_document_id);
CREATE INDEX idx_acc_user ON public.legal_acceptances (user_id);
