-- V2__rename_talent.sql
-- Renombra TODO el dominio "profile" a "talent_*"
-- y corrige UNIQUE globales en characteristics (diet/eye/hair) → índices normales.

SET search_path = public;

----------------------------
-- 1) Tabla principal
----------------------------
ALTER TABLE public.profile RENAME TO talent_profile;

----------------------------
-- 2) BASIC INFO  (profile_basic_info → talent_basic_info)
----------------------------
ALTER TABLE public.profile_basic_info
  DROP CONSTRAINT IF EXISTS fko9seovpiuc12dotvwy1qoe41o,  -- FK profile_id → profile
  DROP CONSTRAINT IF EXISTS ukoxl4623wy0efyoxvi1ggmc4h9;  -- UQ(profile_id)

ALTER TABLE public.profile_basic_info RENAME TO talent_basic_info;
ALTER TABLE public.talent_basic_info RENAME COLUMN profile_id TO talent_profile_id;

ALTER TABLE public.talent_basic_info
  ADD CONSTRAINT uk_talent_basic_info_profile UNIQUE (talent_profile_id),
  ADD CONSTRAINT fk_talent_basic_info__talent_profile
    FOREIGN KEY (talent_profile_id) REFERENCES public.talent_profile(id);

-- Nota: fk3bvsge9vjxpofl2vs8niqswm8 (gender_id → gender_option) queda intacta.

----------------------------
-- 3) CONTACT  (profile_contact → talent_contact)
----------------------------
ALTER TABLE public.profile_contact
  DROP CONSTRAINT IF EXISTS fk8jelp1wrpm1i0xdctfppihtnu,  -- FK profile_id → profile
  DROP CONSTRAINT IF EXISTS ukjfubme5tlmcml2aeg5nvan334;  -- UQ(profile_id)

ALTER TABLE public.profile_contact RENAME TO talent_contact;
ALTER TABLE public.talent_contact RENAME COLUMN profile_id TO talent_profile_id;

ALTER TABLE public.talent_contact
  ADD CONSTRAINT uk_talent_contact_profile UNIQUE (talent_profile_id),
  ADD CONSTRAINT fk_talent_contact__talent_profile
    FOREIGN KEY (talent_profile_id) REFERENCES public.talent_profile(id);

----------------------------
-- 4) MEDIA  (profile_media → talent_media)
----------------------------
ALTER TABLE public.profile_media
  DROP CONSTRAINT IF EXISTS fkiy97kv2mpud9y5i0cg4w5e1rx,  -- FK profile_id → profile
  DROP CONSTRAINT IF EXISTS ukeyw2dx2yxbydvyvmsb27a4xf;    -- UQ(profile_id)

ALTER TABLE public.profile_media RENAME TO talent_media;
ALTER TABLE public.talent_media RENAME COLUMN profile_id TO talent_profile_id;

ALTER TABLE public.talent_media
  ADD CONSTRAINT uk_talent_media_profile UNIQUE (talent_profile_id),
  ADD CONSTRAINT fk_talent_media__talent_profile
    FOREIGN KEY (talent_profile_id) REFERENCES public.talent_profile(id);

-- Imágenes adicionales ligadas a MEDIA
ALTER TABLE public.media_entity_other_pictures_url
  DROP CONSTRAINT IF EXISTS fkoj6b1jfhqwl5xhm7rdcggmwma;  -- FK → profile_media
ALTER TABLE public.media_entity_other_pictures_url
  DROP CONSTRAINT IF EXISTS media_entity_other_pictures_url_pkey;

ALTER TABLE public.media_entity_other_pictures_url RENAME TO talent_media_other_pictures_url;
ALTER TABLE public.talent_media_other_pictures_url
  RENAME COLUMN media_entity_id TO talent_media_id;

ALTER TABLE public.talent_media_other_pictures_url
  ADD CONSTRAINT talent_media_other_pictures_url_pkey
    PRIMARY KEY (talent_media_id, idx);

ALTER TABLE public.talent_media_other_pictures_url
  ADD CONSTRAINT fk_talent_media_other_pictures__talent_media
    FOREIGN KEY (talent_media_id) REFERENCES public.talent_media(id);

----------------------------
-- 5) CHARACTERISTICS (profile_characteristics → talent_characteristics)
----------------------------
-- Quitar UNIQUE globales e FK a profile
ALTER TABLE public.profile_characteristics
  DROP CONSTRAINT IF EXISTS uk9h72dqftit3bssthsmfns3bti,  -- UNIQUE(diet_option_id)
  DROP CONSTRAINT IF EXISTS ukof5nt12u8iraqfbem856g7idd,  -- UNIQUE(eye_color_id)
  DROP CONSTRAINT IF EXISTS ukpc57mbg1qhlb203q9f8xiy7up,  -- UNIQUE(hair_color_id)
  DROP CONSTRAINT IF EXISTS ukh9n1p0psgfb7hwd03p77tqxpp,  -- UNIQUE(profile_id)
  DROP CONSTRAINT IF EXISTS fk8hgt8xpk7qe8o8fbt6b37497j;  -- FK profile_id → profile

ALTER TABLE public.profile_characteristics RENAME TO talent_characteristics;
ALTER TABLE public.talent_characteristics RENAME COLUMN profile_id TO talent_profile_id;

-- Re-crear UNIQUE por perfil y FK a talent_profile
ALTER TABLE public.talent_characteristics
  ADD CONSTRAINT uk_talent_characteristics_profile UNIQUE (talent_profile_id),
  ADD CONSTRAINT fk_talent_characteristics__talent_profile
    FOREIGN KEY (talent_profile_id) REFERENCES public.talent_profile(id);

-- Crear índices (no únicos) para diet/eye/hair
CREATE INDEX IF NOT EXISTS idx_talent_characteristics_diet
  ON public.talent_characteristics(diet_option_id);
CREATE INDEX IF NOT EXISTS idx_talent_characteristics_eye
  ON public.talent_characteristics(eye_color_id);
CREATE INDEX IF NOT EXISTS idx_talent_characteristics_hair
  ON public.talent_characteristics(hair_color_id);

-- (Las FKs a diet_option/color_option ya estaban:
-- fk37v8snusb5d798x7us1hdxavx (diet), fker8oxm5f9s2cdo574gr3h3fo0 (eye), fkl50j6kradcd2uunxsdycm1sg7 (hair))

----------------------------
-- 6) CREDIT (profile_credit → talent_credit)
----------------------------
ALTER TABLE public.profile_credit
  DROP CONSTRAINT IF EXISTS fkfhtrlbi7w94n53h6dt3clt82u; -- FK profile_id → profile

ALTER TABLE public.profile_credit RENAME TO talent_credit;
ALTER TABLE public.talent_credit RENAME COLUMN profile_id TO talent_profile_id;

ALTER TABLE public.talent_credit
  ADD CONSTRAINT fk_talent_credit__talent_profile
    FOREIGN KEY (talent_profile_id) REFERENCES public.talent_profile(id);

-- (FK a production_type fkgha7fqcorbg3u7dmb9b9cxd3c permanece válida)

----------------------------
-- 7) EDUCATION (profile_education → talent_education)
----------------------------
ALTER TABLE public.profile_education
  DROP CONSTRAINT IF EXISTS fknyamm2kg8h69w8hslttg6i2y; -- FK profile_id → profile

ALTER TABLE public.profile_education RENAME TO talent_education;
ALTER TABLE public.talent_education RENAME COLUMN profile_id TO talent_profile_id;

ALTER TABLE public.talent_education
  ADD CONSTRAINT fk_talent_education__talent_profile
    FOREIGN KEY (talent_profile_id) REFERENCES public.talent_profile(id);

----------------------------
-- 8) SOCIAL MEDIA (profile_social_media → talent_social_media)
----------------------------
ALTER TABLE public.profile_social_media
  DROP CONSTRAINT IF EXISTS fk18svgm9sfla1s4yv6grlm99k1, -- FK profile_id → profile
  DROP CONSTRAINT IF EXISTS uk7k03keyece8o8lo0qu9binlog; -- UQ(profile_id)

ALTER TABLE public.profile_social_media RENAME TO talent_social_media;
ALTER TABLE public.talent_social_media RENAME COLUMN profile_id TO talent_profile_id;

ALTER TABLE public.talent_social_media
  ADD CONSTRAINT uk_talent_social_media_profile UNIQUE (talent_profile_id),
  ADD CONSTRAINT fk_talent_social_media__talent_profile
    FOREIGN KEY (talent_profile_id) REFERENCES public.talent_profile(id);

----------------------------
-- 9) SKILL (profile_skill → talent_skill)
----------------------------
ALTER TABLE public.profile_skill
  DROP CONSTRAINT IF EXISTS fkcxbysh527relenn6vd73sj0x8,   -- FK profile_id → profile
  DROP CONSTRAINT IF EXISTS profile_skill_pkey;

ALTER TABLE public.profile_skill RENAME TO talent_skill;
ALTER TABLE public.talent_skill RENAME COLUMN profile_id TO talent_profile_id;

ALTER TABLE public.talent_skill
  ADD CONSTRAINT talent_skill_pkey PRIMARY KEY (talent_profile_id, skill_id),
  ADD CONSTRAINT fk_talent_skill__talent_profile
    FOREIGN KEY (talent_profile_id) REFERENCES public.talent_profile(id);

-- (FK a skills fkox34jgj5yu0xn58roe0b49d8i permanece válida)

----------------------------
-- 10) BASIC_INFO_PROFESSION (basic_info_profession → talent_basic_info_profession)
----------------------------
ALTER TABLE public.basic_info_profession
  DROP CONSTRAINT IF EXISTS fkrsgpihn1djur17i3e9kunu51g,     -- FK basic_info_id → profile_basic_info
  DROP CONSTRAINT IF EXISTS basic_info_profession_pkey;

ALTER TABLE public.basic_info_profession RENAME TO talent_basic_info_profession;
ALTER TABLE public.talent_basic_info_profession
  RENAME COLUMN basic_info_id TO talent_basic_info_id;

ALTER TABLE public.talent_basic_info_profession
  ADD CONSTRAINT talent_basic_info_profession_pkey
    PRIMARY KEY (talent_basic_info_id, profession_id);

ALTER TABLE public.talent_basic_info_profession
  ADD CONSTRAINT fk_talent_basic_info_profession__talent_basic_info
    FOREIGN KEY (talent_basic_info_id) REFERENCES public.talent_basic_info(id);

-- (FK a professions fk2f1asm35gl1jcx29vswi7acbh permanece válida)
