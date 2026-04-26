-- ============================================================
-- DEMO SEED (DEV/TEST)
-- - 1 employer base: asd@asd.com (password: asdasd)
-- - 100 talents: asd1@asd.com ... asd100@asd.com (password: asdasd)
-- - 5 castings del employer base, cada uno con 5 roles
-- - cada role recibe entre 3 y 15 applicants (distribución determinística)
--
-- Diseñado para correr después de HARD_DELETE + boot de backend (Flyway ya aplicado).
--
-- Ejecución segura:
-- 1) Crea procedure
-- 2) Ejecuta en transacción
-- 3) Si falla algo => rollback completo
-- ============================================================

DROP PROCEDURE IF EXISTS public.seed_demo_101_users_5x5();

CREATE PROCEDURE public.seed_demo_101_users_5x5()
LANGUAGE plpgsql
AS $proc$
BEGIN
-- ============================================================
-- DEMO SEED (DEV/TEST)
-- - 1 employer base: asd@asd.com (password: asdasd)
-- - 100 talents: asd1@asd.com ... asd100@asd.com (password: asdasd)
-- - 5 castings del employer base, cada uno con 5 roles
-- - cada role recibe entre 3 y 15 applicants (distribución determinística)
--
-- Diseñado para correr después de HARD_DELETE + boot de backend (Flyway ya aplicado).
-- ============================================================


-- ------------------------------------------------------------
-- 0) Precondiciones metadata (reutiliza Flyway, no inserta)
-- ------------------------------------------------------------
  IF NOT EXISTS (SELECT 1 FROM public.roles WHERE code = 'TALENT') THEN
    RAISE EXCEPTION 'Falta role TALENT. Ejecutá Flyway antes del seed.';
  END IF;
  IF NOT EXISTS (SELECT 1 FROM public.roles WHERE code = 'EMPLOYER') THEN
    RAISE EXCEPTION 'Falta role EMPLOYER. Ejecutá Flyway antes del seed.';
  END IF;
  IF NOT EXISTS (SELECT 1 FROM public."plans" WHERE code = 'FREE') THEN
    RAISE EXCEPTION 'Falta plan FREE. Ejecutá Flyway antes del seed.';
  END IF;
  IF NOT EXISTS (SELECT 1 FROM public.gender_option WHERE string_code = 'sitemetadata.gender.indistinct') THEN
    RAISE EXCEPTION 'Falta gender indistinct. Ejecutá Flyway antes del seed.';
  END IF;
  IF NOT EXISTS (SELECT 1 FROM public.company_type_option WHERE string_code = 'sitemetadata.company_type.company') THEN
    RAISE EXCEPTION 'Falta company type company. Ejecutá Flyway antes del seed.';
  END IF;
  IF NOT EXISTS (SELECT 1 FROM public.professions WHERE string_code = 'sitemetadata.profession.actor') THEN
    RAISE EXCEPTION 'Falta profession actor. Ejecutá Flyway antes del seed.';
  END IF;
  IF NOT EXISTS (SELECT 1 FROM public.skills WHERE string_code = 'sitemetadata.skill.english') THEN
    RAISE EXCEPTION 'Falta skill english. Ejecutá Flyway antes del seed.';
  END IF;
  IF NOT EXISTS (SELECT 1 FROM public.casting_status_option WHERE string_code = 'sitemetadata.casting_status.published') THEN
    RAISE EXCEPTION 'Falta casting_status published. Ejecutá Flyway antes del seed.';
  END IF;
  IF NOT EXISTS (SELECT 1 FROM public.casting_section_status_option WHERE string_code = 'sitemetadata.casting_section_status.completed') THEN
    RAISE EXCEPTION 'Falta section_status completed. Ejecutá Flyway antes del seed.';
  END IF;
  IF NOT EXISTS (SELECT 1 FROM public.project_type_option WHERE string_code = 'sitemetadata.project_type.commercial') THEN
    RAISE EXCEPTION 'Falta project_type commercial. Ejecutá Flyway antes del seed.';
  END IF;
  IF NOT EXISTS (SELECT 1 FROM public.project_type_option WHERE string_code = 'sitemetadata.project_type.digital_content') THEN
    RAISE EXCEPTION 'Falta project_type digital_content. Ejecutá Flyway antes del seed.';
  END IF;
  IF NOT EXISTS (SELECT 1 FROM public.project_type_option WHERE string_code = 'sitemetadata.project_type.music_video') THEN
    RAISE EXCEPTION 'Falta project_type music_video. Ejecutá Flyway antes del seed.';
  END IF;
  IF NOT EXISTS (SELECT 1 FROM public.casting_modality_option WHERE string_code = 'sitemetadata.casting_modality.on_site') THEN
    RAISE EXCEPTION 'Falta casting_modality on_site. Ejecutá Flyway antes del seed.';
  END IF;
  IF NOT EXISTS (SELECT 1 FROM public.casting_modality_option WHERE string_code = 'sitemetadata.casting_modality.autocasting') THEN
    RAISE EXCEPTION 'Falta casting_modality autocasting. Ejecutá Flyway antes del seed.';
  END IF;
  IF NOT EXISTS (SELECT 1 FROM public.role_type_option WHERE string_code = 'sitemetadata.role_type.lead') THEN
    RAISE EXCEPTION 'Falta role_type lead. Ejecutá Flyway antes del seed.';
  END IF;
  IF NOT EXISTS (SELECT 1 FROM public.role_type_option WHERE string_code = 'sitemetadata.role_type.secondary') THEN
    RAISE EXCEPTION 'Falta role_type secondary. Ejecutá Flyway antes del seed.';
  END IF;
  IF NOT EXISTS (SELECT 1 FROM public.role_type_option WHERE string_code = 'sitemetadata.role_type.extra') THEN
    RAISE EXCEPTION 'Falta role_type extra. Ejecutá Flyway antes del seed.';
  END IF;
  IF NOT EXISTS (SELECT 1 FROM public.role_type_option WHERE string_code = 'sitemetadata.role_type.voice') THEN
    RAISE EXCEPTION 'Falta role_type voice. Ejecutá Flyway antes del seed.';
  END IF;
  IF NOT EXISTS (SELECT 1 FROM public.role_type_option WHERE string_code = 'sitemetadata.role_type.host') THEN
    RAISE EXCEPTION 'Falta role_type host. Ejecutá Flyway antes del seed.';
  END IF;
  IF NOT EXISTS (SELECT 1 FROM public.casting_compensation_type_option WHERE string_code = 'sitemetadata.compensation_type.paid') THEN
    RAISE EXCEPTION 'Falta compensation_type paid. Ejecutá Flyway antes del seed.';
  END IF;
  IF NOT EXISTS (SELECT 1 FROM public.casting_compensation_type_option WHERE string_code = 'sitemetadata.compensation_type.collaborative') THEN
    RAISE EXCEPTION 'Falta compensation_type collaborative. Ejecutá Flyway antes del seed.';
  END IF;
  IF NOT EXISTS (SELECT 1 FROM public.pay_rate_type_option WHERE string_code = 'sitemetadata.pay_rate_type.fixed') THEN
    RAISE EXCEPTION 'Falta pay_rate_type fixed. Ejecutá Flyway antes del seed.';
  END IF;
  IF NOT EXISTS (SELECT 1 FROM public.pay_rate_type_option WHERE string_code = 'sitemetadata.pay_rate_type.per_day') THEN
    RAISE EXCEPTION 'Falta pay_rate_type per_day. Ejecutá Flyway antes del seed.';
  END IF;
  IF NOT EXISTS (SELECT 1 FROM public.pay_rate_type_option WHERE string_code = 'sitemetadata.pay_rate_type.per_hour') THEN
    RAISE EXCEPTION 'Falta pay_rate_type per_hour. Ejecutá Flyway antes del seed.';
  END IF;
  IF NOT EXISTS (SELECT 1 FROM public.pay_rate_type_option WHERE string_code = 'sitemetadata.pay_rate_type.collaborative') THEN
    RAISE EXCEPTION 'Falta pay_rate_type collaborative. Ejecutá Flyway antes del seed.';
  END IF;
  IF NOT EXISTS (SELECT 1 FROM public.currency_option WHERE string_code = 'sitemetadata.currency.ars') THEN
    RAISE EXCEPTION 'Falta currency ars. Ejecutá Flyway antes del seed.';
  END IF;
  IF NOT EXISTS (SELECT 1 FROM public.currency_option WHERE string_code = 'sitemetadata.currency.usd') THEN
    RAISE EXCEPTION 'Falta currency usd. Ejecutá Flyway antes del seed.';
  END IF;
  IF NOT EXISTS (SELECT 1 FROM public.casting_application_status_option WHERE string_code = 'sitemetadata.application_status.blank') THEN
    RAISE EXCEPTION 'Falta application_status blank. Ejecutá Flyway antes del seed.';
  END IF;

-- ------------------------------------------------------------
-- 1) Usuarios seed
-- ------------------------------------------------------------

CREATE TEMP TABLE tmp_seed_users (
  email text PRIMARY KEY,
  is_base boolean NOT NULL,
  stage_name text NOT NULL
) ON COMMIT DROP;

INSERT INTO tmp_seed_users (email, is_base, stage_name)
VALUES ('asd@asd.com', true, 'Dario Sombra');

INSERT INTO tmp_seed_users (email, is_base, stage_name)
SELECT
  format('asd%s@asd.com', gs.i) AS email,
  false AS is_base,
  trim(
    (ARRAY[
      'Luna','Gael','Mara','Bruno','Selene','Ivo','Ciro','Aitana','Nina','Tiziano',
      'Vera','Dante','Uma','Elio','Nora','Thiago','Alma','Renzo','Mila','Axel'
    ])[((gs.i - 1) % 20) + 1]
    || ' ' ||
    (ARRAY[
      'Montenegro','Rivas','Del Mar','Quintero','Salvatierra','Vidal','Acuña','Ferrer','Ortega','Mistral',
      'Larreta','Noguera','Pizarro','Valente','Soria','Del Río','Maldini','Carrizo','Balmaceda','Serrat'
    ])[(((gs.i - 1) * 7) % 20) + 1]
  ) AS stage_name
FROM generate_series(1, 100) AS gs(i);

-- Upsert users con password común: asdasd
INSERT INTO public.users (
  id, created_at, created_by, deleted, modified_at, modified_by,
  email, password, user_account_provider, active_mode,
  talent_onboarding_status, employer_onboarding_status
)
SELECT
  gen_random_uuid(), NOW(), 'SEED_DEMO', false, NOW(), 'SEED_DEMO',
  s.email,
  crypt('asdasd', gen_salt('bf', 10)),
  'LOCAL',
  CASE WHEN s.is_base THEN 'EMPLOYER' ELSE 'TALENT' END,
  CASE WHEN s.is_base THEN 'NOT_STARTED' ELSE 'COMPLETED' END,
  CASE WHEN s.is_base THEN 'COMPLETED' ELSE 'NOT_STARTED' END
FROM tmp_seed_users s
ON CONFLICT (email) DO UPDATE SET
  password = crypt('asdasd', gen_salt('bf', 10)),
  user_account_provider = 'LOCAL',
  active_mode = EXCLUDED.active_mode,
  talent_onboarding_status = EXCLUDED.talent_onboarding_status,
  employer_onboarding_status = EXCLUDED.employer_onboarding_status,
  deleted = false,
  modified_at = NOW(),
  modified_by = 'SEED_DEMO';

-- Roles TALENT + EMPLOYER para todos los seed users
INSERT INTO public.user_roles (user_id, role_id)
SELECT u.id, r.id
FROM public.users u
JOIN tmp_seed_users s ON s.email = u.email
JOIN public.roles r ON r.code IN ('TALENT', 'EMPLOYER')
ON CONFLICT (user_id, role_id) DO NOTHING;

-- ------------------------------------------------------------
-- 2) Profiles Talent + Employer y datos mínimos onboarding
-- ------------------------------------------------------------

CREATE TEMP TABLE tmp_seed_user_ids ON COMMIT DROP AS
SELECT u.id AS user_id, u.email, s.is_base, s.stage_name
FROM public.users u
JOIN tmp_seed_users s ON s.email = u.email;

-- Talent profile
INSERT INTO public.talent_profile (
  id, created_at, created_by, deleted, modified_at, modified_by,
  default_slug, premium_slug, plan_id, user_id
)
SELECT
  gen_random_uuid(), NOW(), 'SEED_DEMO', false, NOW(), 'SEED_DEMO',
  'AC-' || substr(replace(t.user_id::text, '-', ''), 1, 12),
  NULL,
  p.id,
  t.user_id
FROM tmp_seed_user_ids t
JOIN public."plans" p ON p.code = 'FREE'
LEFT JOIN public.talent_profile tp ON tp.user_id = t.user_id
WHERE tp.id IS NULL;

-- Employer profile
INSERT INTO public.employer_profile (
  id, created_at, created_by, deleted, modified_at, modified_by,
  default_slug, premium_slug, plan_id, user_id
)
SELECT
  gen_random_uuid(), NOW(), 'SEED_DEMO', false, NOW(), 'SEED_DEMO',
  'AC-' || substr(replace(t.user_id::text, '-', ''), 1, 12) || '-E',
  NULL,
  p.id,
  t.user_id
FROM tmp_seed_user_ids t
JOIN public."plans" p ON p.code = 'FREE'
LEFT JOIN public.employer_profile ep ON ep.user_id = t.user_id
WHERE ep.id IS NULL;

-- Talent basic info
INSERT INTO public.talent_basic_info (
  id, created_at, created_by, deleted, modified_at, modified_by,
  stage_name, gender_id, birth_date, talent_profile_id
)
SELECT
  gen_random_uuid(), NOW(), 'SEED_DEMO', false, NOW(), 'SEED_DEMO',
  t.stage_name,
  go.id,
  make_date(
    (1988 + ((row_number() OVER (ORDER BY t.email) % 12))::int),
    (1 + ((row_number() OVER (ORDER BY t.email) % 12))::int),
    (1 + ((row_number() OVER (ORDER BY t.email) % 27))::int)
  ),
  tp.id
FROM tmp_seed_user_ids t
JOIN public.talent_profile tp ON tp.user_id = t.user_id
LEFT JOIN public.talent_basic_info tbi ON tbi.talent_profile_id = tp.id
CROSS JOIN LATERAL (
  SELECT id
  FROM public.gender_option
  WHERE string_code = 'sitemetadata.gender.indistinct'
  ORDER BY created_at NULLS LAST
  LIMIT 1
) go
WHERE tbi.id IS NULL;

UPDATE public.talent_basic_info tbi
SET stage_name = t.stage_name,
    modified_at = NOW(),
    modified_by = 'SEED_DEMO'
FROM public.talent_profile tp
JOIN tmp_seed_user_ids t ON t.user_id = tp.user_id
WHERE tbi.talent_profile_id = tp.id;

-- Talent contact
INSERT INTO public.talent_contact (
  id, created_at, created_by, deleted, modified_at, modified_by,
  email, phone_number, talent_profile_id
)
SELECT
  gen_random_uuid(), NOW(), 'SEED_DEMO', false, NOW(), 'SEED_DEMO',
  t.email,
  '+549110000' || LPAD((row_number() OVER (ORDER BY t.email))::text, 4, '0'),
  tp.id
FROM tmp_seed_user_ids t
JOIN public.talent_profile tp ON tp.user_id = t.user_id
LEFT JOIN public.talent_contact tc ON tc.talent_profile_id = tp.id
WHERE tc.id IS NULL;

UPDATE public.talent_contact tc
SET email = t.email,
    modified_at = NOW(),
    modified_by = 'SEED_DEMO'
FROM public.talent_profile tp
JOIN tmp_seed_user_ids t ON t.user_id = tp.user_id
WHERE tc.talent_profile_id = tp.id;

-- Talent media (mínimo onboarding: stageName + headshot)
CREATE TEMP TABLE tmp_headshot_pool (
  idx int PRIMARY KEY,
  url text NOT NULL
) ON COMMIT DROP;

INSERT INTO tmp_headshot_pool (idx, url) VALUES
  (1,  'https://images.pexels.com/photos/30120612/pexels-photo-30120612.jpeg?cs=srgb&dl=pexels-hao-peng-2148478861-30120612.jpg&fm=jpg'),
  (2,  'https://images.pexels.com/photos/16958117/pexels-photo-16958117.jpeg?cs=srgb&dl=pexels-luiz-woellner-fotografia-557028708-16958117.jpg&fm=jpg'),
  (3,  'https://images.pexels.com/photos/13259277/pexels-photo-13259277.jpeg?cs=srgb&dl=pexels-connorscottmcmanus-13259277.jpg&fm=jpg'),
  (4,  'https://images.pexels.com/photos/8870738/pexels-photo-8870738.jpeg?cs=srgb&dl=pexels-vikkirillova-8870738.jpg&fm=jpg'),
  (5,  'https://images.pexels.com/photos/5311135/pexels-photo-5311135.jpeg?cs=srgb&dl=pexels-juan-vargas-1955119-5311135.jpg&fm=jpg'),
  (6,  'https://images.pexels.com/photos/32721688/pexels-photo-32721688.jpeg?cs=srgb&dl=pexels-shootsaga-32721688.jpg&fm=jpg'),
  (7,  'https://images.pexels.com/photos/30930814/pexels-photo-30930814.jpeg?cs=srgb&dl=pexels-darkshadephotos-30930814.jpg&fm=jpg'),
  (8,  'https://images.pexels.com/photos/4869348/pexels-photo-4869348.jpeg?cs=srgb&dl=pexels-skyler-ewing-266953-4869348.jpg&fm=jpg'),
  (9,  'https://images.pexels.com/photos/22867971/pexels-photo-22867971.jpeg?cs=srgb&dl=pexels-shahinkhalaji-22867971.jpg&fm=jpg'),
  (10, 'https://images.pexels.com/photos/17247995/pexels-photo-17247995.jpeg?cs=srgb&dl=pexels-46792860-17247995.jpg&fm=jpg'),
  (11, 'https://images.pexels.com/photos/3534962/pexels-photo-3534962.jpeg?cs=srgb&dl=pexels-tubarones-3534962.jpg&fm=jpg'),
  (12, 'https://images.pexels.com/photos/3953843/pexels-photo-3953843.jpeg?cs=srgb&dl=pexels-itfeelslikefilm-3953843.jpg&fm=jpg'),
  (13, 'https://images.pexels.com/photos/14589344/pexels-photo-14589344.jpeg?cs=srgb&dl=pexels-amirsaeiddehghan-14589344.jpg&fm=jpg'),
  (14, 'https://images.pexels.com/photos/5608917/pexels-photo-5608917.jpeg?cs=srgb&dl=pexels-aviz-5608917.jpg&fm=jpg'),
  (15, 'https://images.pexels.com/photos/37148308/pexels-photo-37148308.jpeg?cs=srgb&dl=pexels-vincent-santamaria-194760512-37148308.jpg&fm=jpg'),
  (16, 'https://images.pexels.com/photos/7200637/pexels-photo-7200637.jpeg?cs=srgb&dl=pexels-cottonbro-7200637.jpg&fm=jpg'),
  (17, 'https://images.pexels.com/photos/18466015/pexels-photo-18466015.jpeg?cs=srgb&dl=pexels-oluseyi-18466015.jpg&fm=jpg'),
  (18, 'https://images.pexels.com/photos/36200745/pexels-photo-36200745.jpeg?cs=srgb&dl=pexels-y-glmmes-2147904764-36200745.jpg&fm=jpg'),
  (19, 'https://images.pexels.com/photos/5131547/pexels-photo-5131547.jpeg?cs=srgb&dl=pexels-harrisonhaines-5131547.jpg&fm=jpg'),
  (20, 'https://images.pexels.com/photos/33220961/pexels-photo-33220961.jpeg?cs=srgb&dl=pexels-quachtungduong-33220961.jpg&fm=jpg'),
  (21, 'https://images.pexels.com/photos/11091224/pexels-photo-11091224.jpeg?cs=srgb&dl=pexels-rupinder-singh-2744173-11091224.jpg&fm=jpg'),
  (22, 'https://images.pexels.com/photos/10453027/pexels-photo-10453027.jpeg?cs=srgb&dl=pexels-ron-lach-10453027.jpg&fm=jpg'),
  (23, 'https://images.pexels.com/photos/31972438/pexels-photo-31972438.jpeg?cs=srgb&dl=pexels-mayaramombellifotografias-31972438.jpg&fm=jpg'),
  (24, 'https://images.pexels.com/photos/30004323/pexels-photo-30004323.jpeg?cs=srgb&dl=pexels-prolificpeople-30004323.jpg&fm=jpg'),
  (25, 'https://images.pexels.com/photos/30004315/pexels-photo-30004315.jpeg?cs=srgb&dl=pexels-prolificpeople-30004315.jpg&fm=jpg'),
  (26, 'https://images.pexels.com/photos/30004325/pexels-photo-30004325.jpeg?cs=srgb&dl=pexels-prolificpeople-30004325.jpg&fm=jpg'),
  (27, 'https://images.pexels.com/photos/37148339/pexels-photo-37148339.jpeg?cs=srgb&dl=pexels-vincent-santamaria-194760512-37148339.jpg&fm=jpg'),
  (28, 'https://images.pexels.com/photos/10919461/pexels-photo-10919461.jpeg?cs=srgb&dl=pexels-mengmedia-10919461.jpg&fm=jpg'),
  (29, 'https://images.pexels.com/photos/32721690/pexels-photo-32721690.jpeg?cs=srgb&dl=pexels-shootsaga-32721690.jpg&fm=jpg'),
  (30, 'https://images.pexels.com/photos/30258585/pexels-photo-30258585.jpeg?cs=srgb&dl=pexels-adara-cox-2148751755-30258585.jpg&fm=jpg'),
  (31, 'https://images.pexels.com/photos/30468636/pexels-photo-30468636.jpeg?cs=srgb&dl=pexels-augustocarneirojr-30468636.jpg&fm=jpg'),
  (32, 'https://images.pexels.com/photos/29852895/pexels-photo-29852895.jpeg?cs=srgb&dl=pexels-ifeyinkastudios-29852895.jpg&fm=jpg'),
  (33, 'https://images.pexels.com/photos/30496625/pexels-photo-30496625.jpeg?cs=srgb&dl=pexels-kingcyrusstudios-30496625.jpg&fm=jpg'),
  (34, 'https://images.pexels.com/photos/29852852/pexels-photo-29852852.jpeg?cs=srgb&dl=pexels-ifeyinkastudios-29852852.jpg&fm=jpg'),
  (35, 'https://images.pexels.com/photos/35129364/pexels-photo-35129364.jpeg?cs=srgb&dl=pexels-moh-dikko-photography-2151327861-35129364.jpg&fm=jpg'),
  (36, 'https://images.pexels.com/photos/31880922/pexels-photo-31880922.jpeg?cs=srgb&dl=pexels-wasinpirom-31880922.jpg&fm=jpg'),
  (37, 'https://images.pexels.com/photos/26872232/pexels-photo-26872232.jpeg?cs=srgb&dl=pexels-luis-angel-alejos-espinoza-538962425-26872232.jpg&fm=jpg'),
  (38, 'https://images.pexels.com/photos/18032391/pexels-photo-18032391.jpeg?cs=srgb&dl=pexels-salvador-olague-682304070-18032391.jpg&fm=jpg'),
  (39, 'https://images.pexels.com/photos/31869537/pexels-photo-31869537.jpeg?cs=srgb&dl=pexels-finn-gruber-2147533269-31869537.jpg&fm=jpg'),
  (40, 'https://images.pexels.com/photos/11302135/pexels-photo-11302135.jpeg?cs=srgb&dl=pexels-jayb-11302135.jpg&fm=jpg'),
  (41, 'https://images.pexels.com/photos/33202132/pexels-photo-33202132.jpeg?cs=srgb&dl=pexels-gabriela-brasiliano-515209300-33202132.jpg&fm=jpg'),
  (42, 'https://images.pexels.com/photos/12311581/pexels-photo-12311581.jpeg?cs=srgb&dl=pexels-josepheulo-nyc-12311581.jpg&fm=jpg'),
  (43, 'https://images.pexels.com/photos/11000156/pexels-photo-11000156.jpeg?cs=srgb&dl=pexels-vincent-b-170981268-11000156.jpg&fm=jpg'),
  (44, 'https://images.pexels.com/photos/5906433/pexels-photo-5906433.jpeg?cs=srgb&dl=pexels-luisbecerrafotografo-5906433.jpg&fm=jpg'),
  (45, 'https://images.pexels.com/photos/4183527/pexels-photo-4183527.jpeg?cs=srgb&dl=pexels-docta-ulimwengu-2601920-4183527.jpg&fm=jpg'),
  (46, 'https://images.pexels.com/photos/26728099/pexels-photo-26728099.jpeg?cs=srgb&dl=pexels-hanuman-photo-studio-564865561-26728099.jpg&fm=jpg'),
  (47, 'https://images.pexels.com/photos/14585727/pexels-photo-14585727.jpeg?cs=srgb&dl=pexels-amirsaeiddehghan-14585727.jpg&fm=jpg'),
  (48, 'https://images.pexels.com/photos/15780889/pexels-photo-15780889.jpeg?cs=srgb&dl=pexels-joelsantosfotografias-15780889.jpg&fm=jpg'),
  (49, 'https://images.pexels.com/photos/12616225/pexels-photo-12616225.jpeg?cs=srgb&dl=pexels-fabricio-moraes-cunha-259139555-12616225.jpg&fm=jpg'),
  (50, 'https://images.pexels.com/photos/13331367/pexels-photo-13331367.jpeg?cs=srgb&dl=pexels-marcos-felipe-177641462-13331367.jpg&fm=jpg'),
  (51, 'https://images.pexels.com/photos/10417390/pexels-photo-10417390.jpeg?cs=srgb&dl=pexels-jordan-bergendahl-2628960-10417390.jpg&fm=jpg'),
  (52, 'https://images.pexels.com/photos/32844866/pexels-photo-32844866.jpeg?cs=srgb&dl=pexels-kooldark-32844866.jpg&fm=jpg'),
  (53, 'https://images.pexels.com/photos/26834972/pexels-photo-26834972.jpeg?cs=srgb&dl=pexels-bhandari-law-and-partners-1470175070-26834972.jpg&fm=jpg'),
  (54, 'https://images.pexels.com/photos/8872492/pexels-photo-8872492.jpeg?cs=srgb&dl=pexels-mikhail-nilov-8872492.jpg&fm=jpg'),
  (55, 'https://images.pexels.com/photos/36733301/pexels-photo-36733301.jpeg?cs=srgb&dl=pexels-silverkblack-36733301.jpg&fm=jpg'),
  (56, 'https://images.pexels.com/photos/33539340/pexels-photo-33539340.jpeg?cs=srgb&dl=pexels-lucretius-mooka-2554524-33539340.jpg&fm=jpg'),
  (57, 'https://images.pexels.com/photos/29811345/pexels-photo-29811345.jpeg?cs=srgb&dl=pexels-kevinshrmasc-29811345.jpg&fm=jpg'),
  (58, 'https://images.pexels.com/photos/13111213/pexels-photo-13111213.jpeg?cs=srgb&dl=pexels-sandro-tavares-260503371-13111213.jpg&fm=jpg'),
  (59, 'https://images.pexels.com/photos/29995688/pexels-photo-29995688.jpeg?cs=srgb&dl=pexels-kooldark-29995688.jpg&fm=jpg'),
  (60, 'https://images.pexels.com/photos/1520760/pexels-photo-1520760.jpeg?cs=srgb&dl=pexels-doquyen-1520760.jpg&fm=jpg'),
  (61, 'https://images.pexels.com/photos/30386124/pexels-photo-30386124.jpeg?cs=srgb&dl=pexels-boko-shots-812604874-30386124.jpg&fm=jpg'),
  (62, 'https://images.pexels.com/photos/30975982/pexels-photo-30975982.jpeg?cs=srgb&dl=pexels-hao-peng-2148478861-30975982.jpg&fm=jpg'),
  (63, 'https://images.pexels.com/photos/1181686/pexels-photo-1181686.jpeg?cs=srgb&dl=pexels-divinetechygirl-1181686.jpg&fm=jpg'),
  (64, 'https://images.pexels.com/photos/1188971/pexels-photo-1188971.jpeg?cs=srgb&dl=pexels-krivitskiy-1188971.jpg&fm=jpg'),
  (65, 'https://images.pexels.com/photos/34461415/pexels-photo-34461415.jpeg?cs=srgb&dl=pexels-2mephoto-34461415.jpg&fm=jpg');

INSERT INTO public.talent_media (
  id, created_at, created_by, deleted, modified_at, modified_by,
  full_body_image_url, headshot_image_url, introduction_video_url, show_reel_video_url, talent_profile_id
)
SELECT
  gen_random_uuid(), NOW(), 'SEED_DEMO', false, NOW(), 'SEED_DEMO',
  NULL,
  split_part(hp.url, '?', 1) || '?auto=compress&cs=tinysrgb&fit=crop&w=480&h=640&dpr=1',
  NULL,
  NULL,
  m.talent_profile_id
FROM (
  SELECT
    tp.id AS talent_profile_id,
    ((abs(hashtext(t.email)) % 65) + 1) AS headshot_idx
  FROM public.talent_profile tp
  JOIN tmp_seed_user_ids t ON t.user_id = tp.user_id
) m
JOIN tmp_headshot_pool hp ON hp.idx = m.headshot_idx
LEFT JOIN public.talent_media tm ON tm.talent_profile_id = m.talent_profile_id
WHERE tm.id IS NULL;

UPDATE public.talent_media tm
SET headshot_image_url = split_part(hp.url, '?', 1) || '?auto=compress&cs=tinysrgb&fit=crop&w=480&h=640&dpr=1',
    full_body_image_url = NULL,
    modified_at = NOW(),
    modified_by = 'SEED_DEMO'
FROM (
  SELECT
    tp.id AS talent_profile_id,
    ((abs(hashtext(t.email)) % 65) + 1) AS headshot_idx
  FROM public.talent_profile tp
  JOIN tmp_seed_user_ids t ON t.user_id = tp.user_id
) m
JOIN tmp_headshot_pool hp ON hp.idx = m.headshot_idx
WHERE tm.talent_profile_id = m.talent_profile_id;

-- Talent characteristics (placeholder)
INSERT INTO public.talent_characteristics (
  id, created_at, created_by, deleted, modified_at, modified_by,
  tattoo, passport, driving_license, talent_profile_id
)
SELECT
  gen_random_uuid(), NOW(), 'SEED_DEMO', false, NOW(), 'SEED_DEMO',
  false, false, false, tp.id
FROM public.talent_profile tp
JOIN tmp_seed_user_ids t ON t.user_id = tp.user_id
LEFT JOIN public.talent_characteristics tc ON tc.talent_profile_id = tp.id
WHERE tc.id IS NULL;

UPDATE public.talent_characteristics tc
SET tattoo = COALESCE(tc.tattoo, false),
    passport = COALESCE(tc.passport, false),
    driving_license = COALESCE(tc.driving_license, false),
    modified_at = NOW(),
    modified_by = 'SEED_DEMO'
FROM public.talent_profile tp
JOIN tmp_seed_user_ids t ON t.user_id = tp.user_id
WHERE tc.talent_profile_id = tp.id;

-- Relation profession por talent
INSERT INTO public.talent_basic_info_profession (talent_basic_info_id, profession_id)
SELECT tbi.id, p.id
FROM public.talent_basic_info tbi
JOIN public.talent_profile tp ON tp.id = tbi.talent_profile_id
JOIN tmp_seed_user_ids t ON t.user_id = tp.user_id
CROSS JOIN LATERAL (
  SELECT id FROM public.professions
  WHERE string_code = 'sitemetadata.profession.actor'
  ORDER BY created_at NULLS LAST
  LIMIT 1
) p
ON CONFLICT (talent_basic_info_id, profession_id) DO NOTHING;

-- Relation skill por talent
INSERT INTO public.talent_skill (talent_profile_id, skill_id)
SELECT tp.id, s.id
FROM public.talent_profile tp
JOIN tmp_seed_user_ids t ON t.user_id = tp.user_id
  CROSS JOIN LATERAL (
  SELECT id FROM public.skills
  WHERE string_code = 'sitemetadata.skill.english'
  ORDER BY created_at NULLS LAST
  LIMIT 1
) s
ON CONFLICT (talent_profile_id, skill_id) DO NOTHING;

-- Employer basic info
INSERT INTO public.employer_basic_info (
  id, created_at, created_by, deleted, modified_at, modified_by,
  company_name, tax_number, company_type_id, company_email, image_url, address, website_url, about, employer_profile_id
)
SELECT
  gen_random_uuid(), NOW(), 'SEED_DEMO', false, NOW(), 'SEED_DEMO',
  CASE WHEN t.is_base THEN 'ASD Studios' ELSE NULL END,
  CASE WHEN t.is_base THEN 'ASD-0001' ELSE NULL END,
  cto.id,
  t.email,
  'https://qmtzkcmnmhvmaerqhaex.supabase.co/storage/v1/object/public/profile-media-develop/autocasting/b6adeb92-127e-4fc3-a82b-1bbcdf2d50ec.png',
  CASE WHEN t.is_base THEN 'Buenos Aires, AR' ELSE NULL END,
  CASE WHEN t.is_base THEN 'https://autocasting.app' ELSE NULL END,
  CASE WHEN t.is_base THEN 'Productora demo para QA de castings y aplicaciones.' ELSE NULL END,
  ep.id
FROM public.employer_profile ep
JOIN tmp_seed_user_ids t ON t.user_id = ep.user_id
LEFT JOIN public.employer_basic_info ebi ON ebi.employer_profile_id = ep.id
CROSS JOIN LATERAL (
  SELECT id FROM public.company_type_option
  WHERE string_code = 'sitemetadata.company_type.company'
  ORDER BY created_at NULLS LAST
  LIMIT 1
) cto
WHERE ebi.id IS NULL;

UPDATE public.employer_basic_info ebi
SET company_name = CASE WHEN t.is_base THEN 'ASD Studios' ELSE ebi.company_name END,
    tax_number = CASE WHEN t.is_base THEN 'ASD-0001' ELSE ebi.tax_number END,
    company_email = t.email,
    image_url = COALESCE(ebi.image_url, 'https://qmtzkcmnmhvmaerqhaex.supabase.co/storage/v1/object/public/profile-media-develop/autocasting/b6adeb92-127e-4fc3-a82b-1bbcdf2d50ec.png'),
    address = CASE WHEN t.is_base THEN 'Buenos Aires, AR' ELSE ebi.address END,
    website_url = CASE WHEN t.is_base THEN 'https://autocasting.app' ELSE ebi.website_url END,
    about = CASE WHEN t.is_base THEN 'Productora demo para QA de castings y aplicaciones.' ELSE ebi.about END,
    modified_at = NOW(),
    modified_by = 'SEED_DEMO'
FROM public.employer_profile ep
JOIN tmp_seed_user_ids t ON t.user_id = ep.user_id
WHERE ebi.employer_profile_id = ep.id;

-- ------------------------------------------------------------
-- 3) Castings demo del employer base (5 castings x 5 roles)
-- ------------------------------------------------------------

CREATE TEMP TABLE tmp_base_employer ON COMMIT DROP AS
SELECT
  u.id AS user_id,
  ep.id AS employer_profile_id
FROM public.users u
JOIN public.employer_profile ep ON ep.user_id = u.id
WHERE u.email = 'asd@asd.com'
LIMIT 1;

-- Limpieza de castings previos del base (idempotente)
DELETE FROM public.casting_application ca
WHERE ca.casting_role_id IN (
  SELECT cr.id
  FROM public.casting_role cr
  JOIN public.casting_roles_section crs ON crs.id = cr.casting_roles_section_id
  JOIN public.casting c ON c.id = crs.casting_id
  WHERE c.employer_profile_id = (SELECT employer_profile_id FROM tmp_base_employer)
);

DELETE FROM public.casting
WHERE employer_profile_id = (SELECT employer_profile_id FROM tmp_base_employer);

CREATE TEMP TABLE tmp_casting_seed (
  casting_seq int PRIMARY KEY,
  title text NOT NULL,
  project_type_code text NOT NULL,
  modality_code text NOT NULL,
  compensation_code text NOT NULL,
  location_text text NOT NULL
) ON COMMIT DROP;

INSERT INTO tmp_casting_seed (casting_seq, title, project_type_code, modality_code, compensation_code, location_text) VALUES
(1, 'Campaña Urbana: Voces de Ciudad', 'sitemetadata.project_type.commercial', 'sitemetadata.casting_modality.on_site', 'sitemetadata.compensation_type.paid', 'Buenos Aires'),
(2, 'Microserie Vertical: Medianoche 3AM', 'sitemetadata.project_type.digital_content', 'sitemetadata.casting_modality.autocasting', 'sitemetadata.compensation_type.collaborative', 'Remoto LATAM'),
(3, 'Videoclip Indie: Luz de Neón', 'sitemetadata.project_type.music_video', 'sitemetadata.casting_modality.on_site', 'sitemetadata.compensation_type.paid', 'CABA'),
(4, 'Contenido de Marca: Tiempo Real', 'sitemetadata.project_type.digital_content', 'sitemetadata.casting_modality.autocasting', 'sitemetadata.compensation_type.collaborative', 'Remoto Global'),
(5, 'Spot Internacional: Puerta 9', 'sitemetadata.project_type.commercial', 'sitemetadata.casting_modality.on_site', 'sitemetadata.compensation_type.paid', 'Montevideo');

-- Root casting
INSERT INTO public.casting (
  id, created_at, created_by, deleted, modified_at, modified_by,
  employer_profile_id, default_code, casting_status_option_id
)
SELECT
  gen_random_uuid(), NOW(), 'SEED_DEMO', false, NOW(), 'SEED_DEMO',
  be.employer_profile_id,
  format('C-ASD-%s', LPAD(cs.casting_seq::text, 2, '0')),
  cso.id
FROM tmp_casting_seed cs
CROSS JOIN tmp_base_employer be
CROSS JOIN LATERAL (
  SELECT id FROM public.casting_status_option
  WHERE string_code = 'sitemetadata.casting_status.published'
  ORDER BY created_at NULLS LAST
  LIMIT 1
) cso;

-- Sections
INSERT INTO public.casting_roles_section (
  id, casting_id, section_status_option_id, notes, created_at, created_by, modified_at, modified_by, deleted
)
SELECT
  gen_random_uuid(), c.id, css.id,
  'Roles listos para pruebas QA y navegación de applicants.',
  NOW(), 'SEED_DEMO', NOW(), 'SEED_DEMO', false
FROM public.casting c
JOIN tmp_casting_seed cs ON c.default_code = format('C-ASD-%s', LPAD(cs.casting_seq::text, 2, '0'))
CROSS JOIN LATERAL (
  SELECT id FROM public.casting_section_status_option
  WHERE string_code = 'sitemetadata.casting_section_status.completed'
  ORDER BY created_at NULLS LAST
  LIMIT 1
) css;

INSERT INTO public.casting_requirements_section (
  id, casting_id, section_status_option_id, created_at, created_by, modified_at, modified_by, deleted
)
SELECT
  gen_random_uuid(), c.id, css.id,
  NOW(), 'SEED_DEMO', NOW(), 'SEED_DEMO', false
FROM public.casting c
JOIN tmp_casting_seed cs ON c.default_code = format('C-ASD-%s', LPAD(cs.casting_seq::text, 2, '0'))
CROSS JOIN LATERAL (
  SELECT id FROM public.casting_section_status_option
  WHERE string_code = 'sitemetadata.casting_section_status.completed'
  ORDER BY created_at NULLS LAST
  LIMIT 1
) css;

INSERT INTO public.casting_remuneration (
  id, casting_id, section_status_option_id, compensation_type_option_id, notes,
  created_at, created_by, modified_at, modified_by, deleted
)
SELECT
  gen_random_uuid(), c.id, css.id, cct.id,
  CASE
    WHEN cs.compensation_code = 'sitemetadata.compensation_type.paid'
      THEN 'Remuneración definida por rol. Se permite variación entre perfiles.'
    ELSE 'Proyecto colaborativo con acreditación y material final.'
  END,
  NOW(), 'SEED_DEMO', NOW(), 'SEED_DEMO', false
FROM public.casting c
JOIN tmp_casting_seed cs ON c.default_code = format('C-ASD-%s', LPAD(cs.casting_seq::text, 2, '0'))
CROSS JOIN LATERAL (
  SELECT id FROM public.casting_section_status_option
  WHERE string_code = 'sitemetadata.casting_section_status.completed'
  ORDER BY created_at NULLS LAST
  LIMIT 1
) css
CROSS JOIN LATERAL (
  SELECT id FROM public.casting_compensation_type_option
  WHERE string_code = cs.compensation_code
  ORDER BY created_at NULLS LAST
  LIMIT 1
) cct;

-- Basic info por casting
INSERT INTO public.casting_basic_info (
  id, casting_id, section_status_option_id,
  title, project_type_option_id, location_text, casting_modality_option_id, casting_modality_text,
  application_deadline, has_wardrobe_fitting, wardrobe_fitting_text, shooting_start_date, shooting_end_date, description,
  created_at, created_by, modified_at, modified_by, deleted
)
SELECT
  gen_random_uuid(),
  c.id,
  css.id,
  cs.title,
  pto.id,
  cs.location_text,
  cmo.id,
  CASE WHEN cs.modality_code = 'sitemetadata.casting_modality.on_site' THEN 'Presencial con callback híbrido' ELSE 'Autocasting 100% remoto' END,
  CASE
    WHEN cs.casting_seq = 1 THEN (NOW() + INTERVAL '1 day')::date
    ELSE (NOW() + ((10 + (abs(hashtext(cs.title)) % 6))::text || ' days')::interval)::date
  END,
  (cs.casting_seq % 2 = 1),
  CASE WHEN cs.casting_seq % 2 = 1 THEN 'Prueba de vestuario opcional en estudio' ELSE NULL END,
  (NOW() + ((20 + cs.casting_seq)::text || ' days')::interval)::date,
  (NOW() + ((25 + cs.casting_seq)::text || ' days')::interval)::date,
  'Casting generado por seed para probar dashboard employer/talent con datos consistentes.',
  NOW(), 'SEED_DEMO', NOW(), 'SEED_DEMO', false
FROM public.casting c
JOIN tmp_casting_seed cs ON c.default_code = format('C-ASD-%s', LPAD(cs.casting_seq::text, 2, '0'))
CROSS JOIN LATERAL (
  SELECT id FROM public.casting_section_status_option
  WHERE string_code = 'sitemetadata.casting_section_status.completed'
  ORDER BY created_at NULLS LAST
  LIMIT 1
) css
CROSS JOIN LATERAL (
  SELECT id FROM public.project_type_option
  WHERE string_code = cs.project_type_code
  ORDER BY created_at NULLS LAST
  LIMIT 1
) pto
CROSS JOIN LATERAL (
  SELECT id FROM public.casting_modality_option
  WHERE string_code = cs.modality_code
  ORDER BY created_at NULLS LAST
  LIMIT 1
) cmo;

-- Roles seed (25 nombres creativos)
CREATE TEMP TABLE tmp_role_seed (
  casting_seq int NOT NULL,
  role_pos int NOT NULL,
  role_name text NOT NULL,
  role_type_code text NOT NULL,
  age_min smallint NOT NULL,
  age_max smallint NOT NULL,
  requires_audio boolean NOT NULL,
  requires_video boolean NOT NULL,
  pay_rate_code text NOT NULL,
  currency_code text,
  amount numeric(12,2),
  remuneration_notes text,
  PRIMARY KEY (casting_seq, role_pos)
) ON COMMIT DROP;

INSERT INTO tmp_role_seed (
  casting_seq, role_pos, role_name, role_type_code, age_min, age_max,
  requires_audio, requires_video, pay_rate_code, currency_code, amount, remuneration_notes
)
SELECT
  c.casting_seq,
  r.role_pos,
  role_names[((c.casting_seq - 1) * 5 + r.role_pos)],
  (ARRAY[
    'sitemetadata.role_type.lead',
    'sitemetadata.role_type.secondary',
    'sitemetadata.role_type.extra',
    'sitemetadata.role_type.voice',
    'sitemetadata.role_type.host'
  ])[r.role_pos],
  (18 + r.role_pos + c.casting_seq)::smallint,
  (30 + r.role_pos * 3 + c.casting_seq)::smallint,
  (r.role_pos % 2 = 0),
  (r.role_pos % 2 = 1),
  CASE
    WHEN c.compensation_code = 'sitemetadata.compensation_type.collaborative' THEN 'sitemetadata.pay_rate_type.collaborative'
    WHEN r.role_pos IN (1, 5) THEN 'sitemetadata.pay_rate_type.fixed'
    WHEN r.role_pos IN (2, 4) THEN 'sitemetadata.pay_rate_type.per_day'
    ELSE 'sitemetadata.pay_rate_type.per_hour'
  END,
  CASE
    WHEN c.compensation_code = 'sitemetadata.compensation_type.collaborative' THEN NULL
    WHEN c.casting_seq % 2 = 0 THEN 'sitemetadata.currency.usd'
    ELSE 'sitemetadata.currency.ars'
  END,
  CASE
    WHEN c.compensation_code = 'sitemetadata.compensation_type.collaborative' THEN NULL
    ELSE (35000 + (c.casting_seq * 5000) + (r.role_pos * 3500))::numeric
  END,
  CASE
    WHEN c.compensation_code = 'sitemetadata.compensation_type.collaborative'
      THEN 'Participación colaborativa + material final para reel.'
    ELSE 'Remuneración variable según rol y disponibilidad.'
  END
FROM tmp_casting_seed c
CROSS JOIN generate_series(1, 5) AS r(role_pos)
CROSS JOIN LATERAL (
  SELECT ARRAY[
    'Luna Arce','Gael Moreno','Mara Solís','Bruno Fariña','Selene Prado',
    'Ivo Salvat','Ciro Alvear','Aitana Bosch','Nina Caro','Tiziano Valdés',
    'Vera Cifuentes','Dante Roldán','Uma Ferrer','Elio Varela','Nora Ledesma',
    'Thiago Montal','Alma Quiroga','Renzo Soria','Mila Céspedes','Axel Verona',
    'Iris Calderón','Noam Lucero','Bianca Meza','Tomás Repetto','Ambar Duarte'
  ] AS role_names
) rn;

-- Insert roles
INSERT INTO public.casting_role (
  id, casting_roles_section_id, role_name, role_type_option_id, gender_option_id,
  age_min, age_max, description,
  created_at, created_by, modified_at, modified_by, deleted
)
SELECT
  gen_random_uuid(),
  crs.id,
  rs.role_name,
  rto.id,
  go.id,
  rs.age_min,
  rs.age_max,
  'Rol generado por seed para pruebas de filtros, cards y applicants.',
  NOW(), 'SEED_DEMO', NOW(), 'SEED_DEMO', false
FROM tmp_role_seed rs
JOIN public.casting c ON c.default_code = format('C-ASD-%s', LPAD(rs.casting_seq::text, 2, '0'))
JOIN public.casting_roles_section crs ON crs.casting_id = c.id
CROSS JOIN LATERAL (
  SELECT id FROM public.role_type_option
  WHERE string_code = rs.role_type_code
  ORDER BY created_at NULLS LAST
  LIMIT 1
) rto
CROSS JOIN LATERAL (
  SELECT id FROM public.gender_option
  WHERE string_code = 'sitemetadata.gender.indistinct'
  ORDER BY created_at NULLS LAST
  LIMIT 1
) go;

CREATE TEMP TABLE tmp_created_roles ON COMMIT DROP AS
SELECT
  rs.casting_seq,
  rs.role_pos,
  cr.id AS casting_role_id,
  rs.requires_audio,
  rs.requires_video,
  rs.pay_rate_code,
  rs.currency_code,
  rs.amount,
  rs.remuneration_notes
FROM tmp_role_seed rs
JOIN public.casting c ON c.default_code = format('C-ASD-%s', LPAD(rs.casting_seq::text, 2, '0'))
JOIN public.casting_roles_section crs ON crs.casting_id = c.id
JOIN public.casting_role cr ON cr.casting_roles_section_id = crs.id AND cr.role_name = rs.role_name;

-- Profession + skill para cada role
INSERT INTO public.casting_role_profession (casting_role_id, profession_id)
SELECT r.casting_role_id, p.id
FROM tmp_created_roles r
CROSS JOIN LATERAL (
  SELECT id FROM public.professions
  WHERE string_code = 'sitemetadata.profession.actor'
  ORDER BY created_at NULLS LAST
  LIMIT 1
) p
ON CONFLICT (casting_role_id, profession_id) DO NOTHING;

INSERT INTO public.casting_role_skill (casting_role_id, skill_id)
SELECT r.casting_role_id, s.id
FROM tmp_created_roles r
CROSS JOIN LATERAL (
  SELECT id FROM public.skills
  WHERE string_code = 'sitemetadata.skill.english'
  ORDER BY created_at NULLS LAST
  LIMIT 1
) s
ON CONFLICT (casting_role_id, skill_id) DO NOTHING;

-- Characteristics placeholder por role
INSERT INTO public.casting_role_characteristics (
  id, created_at, created_by, deleted, modified_at, modified_by,
  height_cm, passport, tattoo, casting_role_id
)
SELECT
  gen_random_uuid(), NOW(), 'SEED_DEMO', false, NOW(), 'SEED_DEMO',
  (165 + ((r.casting_seq + r.role_pos) % 25)),
  (r.role_pos % 2 = 0),
  (r.role_pos % 3 = 0),
  r.casting_role_id
FROM tmp_created_roles r
ON CONFLICT (casting_role_id) DO NOTHING;

-- Requirements (1 por role)
INSERT INTO public.casting_requirement (
  id, casting_requirements_section_id, casting_role_id, description, requires_audio, requires_video,
  created_at, created_by, modified_at, modified_by, deleted
)
SELECT
  gen_random_uuid(),
  crs.id,
  r.casting_role_id,
  CASE
    WHEN r.requires_audio AND r.requires_video
      THEN 'Enviar self tape (video) + lectura en audio. Referencia: https://www.youtube.com/watch?v=bhagN-pes9Q'
    WHEN r.requires_audio
      THEN 'Enviar sólo lectura en audio. Referencia: https://www.youtube.com/watch?v=bhagN-pes9Q'
    WHEN r.requires_video
      THEN 'Enviar self tape en video. Referencia: https://www.youtube.com/watch?v=bhagN-pes9Q'
    ELSE 'Requirement simple sin media obligatoria.'
  END,
  r.requires_audio,
  r.requires_video,
  NOW(), 'SEED_DEMO', NOW(), 'SEED_DEMO', false
FROM tmp_created_roles r
JOIN public.casting c ON c.default_code = format('C-ASD-%s', LPAD(r.casting_seq::text, 2, '0'))
JOIN public.casting_requirements_section crs ON crs.casting_id = c.id;

-- Remuneration per role
INSERT INTO public.casting_role_remuneration (
  id, casting_role_id, is_complete, pay_rate_type_option_id, currency_option_id, amount, notes,
  created_at, created_by, modified_at, modified_by, deleted
)
SELECT
  gen_random_uuid(),
  r.casting_role_id,
  true,
  pr.id,
  co.id,
  r.amount,
  r.remuneration_notes,
  NOW(), 'SEED_DEMO', NOW(), 'SEED_DEMO', false
FROM tmp_created_roles r
CROSS JOIN LATERAL (
  SELECT id FROM public.pay_rate_type_option
  WHERE string_code = r.pay_rate_code
  ORDER BY created_at NULLS LAST
  LIMIT 1
) pr
LEFT JOIN LATERAL (
  SELECT id FROM public.currency_option
  WHERE r.currency_code IS NOT NULL AND string_code = r.currency_code
  ORDER BY created_at NULLS LAST
  LIMIT 1
) co ON true;

-- ------------------------------------------------------------
-- 4) Aplicaciones talent -> roles del employer base
--    objetivo: entre 3 y 15 applicants por role
-- ------------------------------------------------------------

CREATE TEMP TABLE tmp_applicant_talents ON COMMIT DROP AS
SELECT
  row_number() OVER (ORDER BY u.email) AS rn,
  u.email,
  tp.id AS talent_profile_id
FROM public.users u
JOIN public.talent_profile tp ON tp.user_id = u.id
WHERE u.email ~ '^asd[0-9]+@asd\.com$'
ORDER BY u.email;

CREATE TEMP TABLE tmp_target_roles ON COMMIT DROP AS
SELECT
  row_number() OVER (ORDER BY r.casting_seq, r.role_pos) AS rn,
  r.casting_role_id
FROM tmp_created_roles r
ORDER BY r.casting_seq, r.role_pos;

CREATE TEMP TABLE tmp_role_application_targets ON COMMIT DROP AS
SELECT
  tr.rn,
  tr.casting_role_id,
  (3 + ((tr.rn * 7) % 13))::int AS target_count
FROM tmp_target_roles tr;

CREATE TEMP TABLE tmp_role_applicants ON COMMIT DROP AS
SELECT
  rt.casting_role_id,
  at.talent_profile_id
FROM tmp_role_application_targets rt
JOIN LATERAL generate_series(1, rt.target_count) gs(slot) ON true
JOIN tmp_applicant_talents at
  ON at.rn = (((rt.rn * 17 + gs.slot * 11) % (SELECT COUNT(*) FROM tmp_applicant_talents)) + 1);

CREATE TEMP TABLE tmp_created_applications (
  id uuid NOT NULL,
  casting_role_id uuid NOT NULL,
  talent_profile_id uuid NOT NULL
) ON COMMIT DROP;

WITH inserted_apps AS (
  INSERT INTO public.casting_application (
    id, casting_role_id, talent_profile_id, casting_application_status_option_id, message,
    created_at, created_by, modified_at, modified_by, deleted
  )
  SELECT
    gen_random_uuid(),
    ra.casting_role_id,
    ra.talent_profile_id,
    sao.id,
    'Aplicación demo generada por seed para validar tablero de postulantes.',
    NOW(), 'SEED_DEMO', NOW(), 'SEED_DEMO', false
  FROM tmp_role_applicants ra
  CROSS JOIN LATERAL (
    SELECT id FROM public.casting_application_status_option
    WHERE string_code = 'sitemetadata.application_status.blank'
    ORDER BY created_at NULLS LAST
    LIMIT 1
  ) sao
  RETURNING id, casting_role_id, talent_profile_id
)
INSERT INTO tmp_created_applications (id, casting_role_id, talent_profile_id)
SELECT id, casting_role_id, talent_profile_id
FROM inserted_apps;

-- Requirement submissions para cada aplicación
INSERT INTO public.casting_application_requirement_submission (
  id, application_id, casting_requirement_id, audio_url, video_url, notes,
  created_at, created_by, modified_at, modified_by, deleted
)
SELECT
  gen_random_uuid(),
  a.id,
  req.id,
  CASE WHEN req.requires_audio THEN 'https://www.youtube.com/watch?v=bhagN-pes9Q' ELSE NULL END,
  CASE WHEN req.requires_video THEN 'https://www.youtube.com/watch?v=bhagN-pes9Q' ELSE NULL END,
  'Submission demo para pruebas funcionales.',
  NOW(), 'SEED_DEMO', NOW(), 'SEED_DEMO', false
FROM tmp_created_applications a
JOIN public.casting_requirement req ON req.casting_role_id = a.casting_role_id;


-- ------------------------------------------------------------
-- 5) Validaciones fuertes post-seed
-- ------------------------------------------------------------
IF (SELECT COUNT(*) FROM public.users WHERE email = 'asd@asd.com') <> 1 THEN
  RAISE EXCEPTION 'Seed inválido: asd@asd.com no quedó en estado esperado';
END IF;

IF (SELECT COUNT(*) FROM public.users WHERE email ~ '^asd[0-9]+@asd\.com$') <> 100 THEN
  RAISE EXCEPTION 'Seed inválido: cantidad de usuarios incrementales distinta de 100';
END IF;

IF (
  SELECT COUNT(*)
  FROM public.casting c
  JOIN public.employer_profile ep ON ep.id = c.employer_profile_id
  JOIN public.users u ON u.id = ep.user_id
  WHERE u.email = 'asd@asd.com'
) <> 5 THEN
  RAISE EXCEPTION 'Seed inválido: cantidad de castings del employer base distinta de 5';
END IF;

IF (
  SELECT COUNT(*)
  FROM public.casting_role cr
  JOIN public.casting_roles_section crs ON crs.id = cr.casting_roles_section_id
  JOIN public.casting c ON c.id = crs.casting_id
  JOIN public.employer_profile ep ON ep.id = c.employer_profile_id
  JOIN public.users u ON u.id = ep.user_id
  WHERE u.email = 'asd@asd.com'
) <> 25 THEN
  RAISE EXCEPTION 'Seed inválido: cantidad de roles del employer base distinta de 25';
END IF;

IF EXISTS (
  SELECT 1
  FROM (
    SELECT
      ca.casting_role_id,
      COUNT(*)::int AS applicant_count
    FROM public.casting_application ca
    JOIN public.casting_role cr ON cr.id = ca.casting_role_id
    JOIN public.casting_roles_section crs ON crs.id = cr.casting_roles_section_id
    JOIN public.casting c ON c.id = crs.casting_id
    JOIN public.employer_profile ep ON ep.id = c.employer_profile_id
    JOIN public.users u ON u.id = ep.user_id
    WHERE u.email = 'asd@asd.com'
    GROUP BY ca.casting_role_id
  ) per_role
  WHERE per_role.applicant_count < 3 OR per_role.applicant_count > 15
) THEN
  RAISE EXCEPTION 'Seed inválido: existe al menos un role fuera del rango de applicants (3..15)';
END IF;

IF (
  SELECT COUNT(*)
  FROM public.casting_application ca
  JOIN public.casting_role cr ON cr.id = ca.casting_role_id
  JOIN public.casting_roles_section crs ON crs.id = cr.casting_roles_section_id
  JOIN public.casting c ON c.id = crs.casting_id
  JOIN public.employer_profile ep ON ep.id = c.employer_profile_id
  JOIN public.users u ON u.id = ep.user_id
  WHERE u.email = 'asd@asd.com'
) <> (
  SELECT COALESCE(SUM(target_count), 0)
  FROM tmp_role_application_targets
) THEN
  RAISE EXCEPTION 'Seed inválido: cantidad total de aplicaciones no coincide con el target generado por role';
END IF;

END;
$proc$;

BEGIN;
CALL public.seed_demo_101_users_5x5();
COMMIT;

DROP PROCEDURE public.seed_demo_101_users_5x5();
