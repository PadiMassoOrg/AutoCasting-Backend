-- ============================================================
-- DEMO SEED (DEV/TEST)
-- - 3 employers: asd@asd.com / asd10@asd.com / asd20@asd.com (password: asdasd)
--   * asd@asd.com   -> ASD Studios (company)
--   * asd10@asd.com -> Agencia Vértice Talentos (talent_agency)
--   * asd20@asd.com -> Sur Content Producciones (producer)
-- - 30 talents: asd1@asd.com ... asd30@asd.com (password: asdasd)
-- - 6 castings (2 por employer: 1 draft + 1 published), cada uno con 5 roles (30 roles totales)
-- - cada role de casting PUBLISHED recibe entre 3 y 15 applicants (distribución determinística);
--   roles de castings DRAFT no reciben applicants (aún no publicados)
-- - talent gender/ethnicity/hair/eye/diet/height/weight/measurements/tattoo/passport/driving_license
--   y casting_role gender/ethnicity/pay_rate_type varían de forma determinística (hash-based) para
--   habilitar pruebas reales de los filtros de búsqueda de talent-database y casting-database
-- - legal_acceptances para current TERMS + PRIVACY (locale=es) en los usuarios seed
--
-- Reejecutable (idempotente) en cualquier momento, con backend levantado o no.
-- Requiere schema y metadata al día (Flyway aplicado).
--
-- Ejecución segura:
-- 1) Crea procedure
-- 2) Ejecuta en transacción
-- 3) Si falla algo => rollback completo
-- ============================================================

DROP PROCEDURE IF EXISTS public.seed_demo_30_users_3_employers();

CREATE PROCEDURE public.seed_demo_30_users_3_employers()
LANGUAGE plpgsql
AS $proc$
BEGIN
-- ============================================================
-- DEMO SEED (DEV/TEST)
-- - 3 employers: asd@asd.com / asd10@asd.com / asd20@asd.com (password: asdasd)
-- - 30 talents: asd1@asd.com ... asd30@asd.com (password: asdasd)
-- - 6 castings (2 por employer: 1 draft + 1 published), cada uno con 5 roles
-- - cada role de casting PUBLISHED recibe entre 3 y 15 applicants (distribución determinística)
-- - legal_acceptances para current TERMS + PRIVACY (locale=es) en los usuarios seed
--
-- Reejecutable (idempotente) en cualquier momento, con backend levantado o no.
-- ============================================================

  -- Evita ejecuciones concurrentes del seed en la misma base.
  PERFORM pg_advisory_xact_lock(hashtext('seed_demo_30_users_3_employers')::bigint);


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
  IF (SELECT COUNT(*) FROM public.gender_option WHERE string_code LIKE 'sitemetadata.gender.%') < 6 THEN
    RAISE EXCEPTION 'Faltan gender_option metadata (se requieren 6). Ejecutá Flyway antes del seed.';
  END IF;
  IF (SELECT COUNT(*) FROM public.ethnicity_option WHERE string_code LIKE 'sitemetadata.ethnicity.%') < 8 THEN
    RAISE EXCEPTION 'Faltan ethnicity_option metadata (se requieren 8). Ejecutá Flyway antes del seed.';
  END IF;
  IF (SELECT COUNT(*) FROM public.color_option WHERE category_string_code = 'sitemetadata.category.hair_color') < 11 THEN
    RAISE EXCEPTION 'Faltan color_option (hair_color) metadata (se requieren 11). Ejecutá Flyway antes del seed.';
  END IF;
  IF (SELECT COUNT(*) FROM public.color_option WHERE category_string_code = 'sitemetadata.category.eye_color') < 9 THEN
    RAISE EXCEPTION 'Faltan color_option (eye_color) metadata (se requieren 9). Ejecutá Flyway antes del seed.';
  END IF;
  IF (SELECT COUNT(*) FROM public.diet_option WHERE string_code LIKE 'sitemetadata.diet.%') < 11 THEN
    RAISE EXCEPTION 'Faltan diet_option metadata (se requieren 11). Ejecutá Flyway antes del seed.';
  END IF;
  IF NOT EXISTS (SELECT 1 FROM public.company_type_option WHERE string_code = 'sitemetadata.company_type.company') THEN
    RAISE EXCEPTION 'Falta company type company. Ejecutá Flyway antes del seed.';
  END IF;
  IF NOT EXISTS (SELECT 1 FROM public.company_type_option WHERE string_code = 'sitemetadata.company_type.talent_agency') THEN
    RAISE EXCEPTION 'Falta company type talent_agency. Ejecutá Flyway antes del seed.';
  END IF;
  IF NOT EXISTS (SELECT 1 FROM public.company_type_option WHERE string_code = 'sitemetadata.company_type.producer') THEN
    RAISE EXCEPTION 'Falta company type producer. Ejecutá Flyway antes del seed.';
  END IF;
  IF (SELECT COUNT(*) FROM public.professions WHERE string_code LIKE 'sitemetadata.profession.%') < 8 THEN
    RAISE EXCEPTION 'Faltan professions metadata. Ejecutá Flyway antes del seed.';
  END IF;
  IF (SELECT COUNT(*) FROM public.skills WHERE string_code LIKE 'sitemetadata.skill.%') < 45 THEN
    RAISE EXCEPTION 'Faltan skills metadata (se requieren >=45). Ejecutá Flyway antes del seed.';
  END IF;
  IF NOT EXISTS (SELECT 1 FROM public.casting_status_option WHERE string_code = 'sitemetadata.casting_status.published') THEN
    RAISE EXCEPTION 'Falta casting_status published. Ejecutá Flyway antes del seed.';
  END IF;
  IF NOT EXISTS (SELECT 1 FROM public.casting_status_option WHERE string_code = 'sitemetadata.casting_status.draft') THEN
    RAISE EXCEPTION 'Falta casting_status draft. Ejecutá Flyway antes del seed.';
  END IF;
  IF NOT EXISTS (SELECT 1 FROM public.project_type_option WHERE string_code = 'sitemetadata.project_type.commercial') THEN
    RAISE EXCEPTION 'Falta project_type commercial. Ejecutá Flyway antes del seed.';
  END IF;
  IF NOT EXISTS (SELECT 1 FROM public.project_type_option WHERE string_code = 'sitemetadata.project_type.digital_content') THEN
    RAISE EXCEPTION 'Falta project_type digital_content. Ejecutá Flyway antes del seed.';
  END IF;
  IF NOT EXISTS (SELECT 1 FROM public.project_type_option WHERE string_code = 'sitemetadata.project_type.documentary') THEN
    RAISE EXCEPTION 'Falta project_type documentary. Ejecutá Flyway antes del seed.';
  END IF;
  IF NOT EXISTS (SELECT 1 FROM public.project_type_option WHERE string_code = 'sitemetadata.project_type.short_film') THEN
    RAISE EXCEPTION 'Falta project_type short_film. Ejecutá Flyway antes del seed.';
  END IF;
  IF NOT EXISTS (SELECT 1 FROM public.casting_modality_option WHERE string_code = 'sitemetadata.casting_modality.on_site') THEN
    RAISE EXCEPTION 'Falta casting_modality on_site. Ejecutá Flyway antes del seed.';
  END IF;
  IF NOT EXISTS (SELECT 1 FROM public.casting_modality_option WHERE string_code = 'sitemetadata.casting_modality.autocasting') THEN
    RAISE EXCEPTION 'Falta casting_modality autocasting. Ejecutá Flyway antes del seed.';
  END IF;
  IF (SELECT COUNT(*) FROM public.role_type_option WHERE string_code LIKE 'sitemetadata.role_type.%') < 8 THEN
    RAISE EXCEPTION 'Faltan role_type_option metadata (se requieren 8). Ejecutá Flyway antes del seed.';
  END IF;
  IF NOT EXISTS (SELECT 1 FROM public.pay_rate_type_option WHERE string_code = 'sitemetadata.pay_rate_type.unpaid') THEN
    RAISE EXCEPTION 'Falta pay_rate_type unpaid. Ejecutá Flyway antes del seed.';
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
  IF NOT EXISTS (SELECT 1 FROM public.pay_rate_type_option WHERE string_code = 'sitemetadata.pay_rate_type.per_week') THEN
    RAISE EXCEPTION 'Falta pay_rate_type per_week. Ejecutá Flyway antes del seed.';
  END IF;
  IF NOT EXISTS (SELECT 1 FROM public.pay_rate_type_option WHERE string_code = 'sitemetadata.pay_rate_type.to_be_agreed') THEN
    RAISE EXCEPTION 'Falta pay_rate_type to_be_agreed. Ejecutá Flyway antes del seed.';
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
FROM generate_series(1, 30) AS gs(i);

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
  'COMPLETED',
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

CREATE TEMP TABLE tmp_seed_user_ids ON COMMIT DROP AS
SELECT u.id AS user_id, u.email, s.is_base, s.stage_name
FROM public.users u
JOIN tmp_seed_users s ON s.email = u.email;

-- Employer personas (3): base + 2 upgrades sobre usuarios talent existentes
CREATE TEMP TABLE tmp_employer_personas (
  email text PRIMARY KEY,
  company_type_code text NOT NULL,
  company_name text NOT NULL,
  tax_number text NOT NULL,
  address text NOT NULL,
  website_url text NOT NULL,
  about text NOT NULL
) ON COMMIT DROP;

INSERT INTO tmp_employer_personas (email, company_type_code, company_name, tax_number, address, website_url, about) VALUES
('asd@asd.com',   'sitemetadata.company_type.company',       'ASD Studios',               'ASD-0001', 'Buenos Aires, AR', 'https://autocasting.app',        'Productora demo para QA de castings y aplicaciones.'),
('asd10@asd.com', 'sitemetadata.company_type.talent_agency', 'Agencia Vértice Talentos',   'AVT-0010', 'Rosario, AR',       'https://verticetalentos.com.ar', 'Agencia de representación de talento para publicidad, cine y streaming.'),
('asd20@asd.com', 'sitemetadata.company_type.producer',      'Sur Content Producciones',   'SCP-0020', 'Córdoba, AR',       'https://surcontent.com.ar',      'Productora audiovisual independiente especializada en documentales y ficción.');

-- ------------------------------------------------------------
-- 1.5) Aceptaciones legales seed (current TERMS + PRIVACY, locale es)
-- ------------------------------------------------------------

CREATE TEMP TABLE tmp_current_legal_docs ON COMMIT DROP AS
SELECT d.id, d.type, d.content_hash
FROM (
  SELECT
    ld.id,
    ld.type,
    ld.content_hash,
    row_number() OVER (PARTITION BY ld.type ORDER BY ld.effective_at DESC, ld.created_at DESC) AS rn
  FROM public.legal_documents ld
  WHERE ld.deleted = false
    AND ld.status = 'PUBLISHED'
    AND ld.locale = 'es'
    AND ld.type IN ('TERMS', 'PRIVACY')
    AND ld.effective_at <= NOW()
) d
WHERE d.rn = 1;

IF (SELECT COUNT(*) FROM tmp_current_legal_docs) <> 2 THEN
  RAISE EXCEPTION 'Faltan documentos legales actuales (TERMS/PRIVACY, locale=es). Ejecutá Flyway release legal antes del seed.';
END IF;

DELETE FROM public.legal_acceptances la
USING public.legal_documents ld, tmp_seed_user_ids su
WHERE la.legal_document_id = ld.id
  AND la.user_id = su.user_id
  AND ld.type IN ('TERMS', 'PRIVACY');

INSERT INTO public.legal_acceptances (
  id, user_id, legal_document_id, accepted_at, ip, user_agent, content_hash,
  created_at, created_by, modified_at, modified_by, deleted
)
SELECT
  gen_random_uuid(),
  su.user_id,
  d.id,
  NOW() - interval '5 minutes',
  '127.0.0.1',
  'SEED_DEMO_30_USERS_3_EMPLOYERS',
  d.content_hash,
  NOW(), 'SEED_DEMO', NOW(), 'SEED_DEMO', false
FROM tmp_seed_user_ids su
CROSS JOIN tmp_current_legal_docs d;

-- ------------------------------------------------------------
-- 2) Profiles Talent + Employer y datos mínimos onboarding
-- ------------------------------------------------------------

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

-- Gender pool (6 valores)
CREATE TEMP TABLE tmp_gender_pool (
  idx int PRIMARY KEY,
  string_code text NOT NULL
) ON COMMIT DROP;

INSERT INTO tmp_gender_pool (idx, string_code) VALUES
  (1, 'sitemetadata.gender.male'),
  (2, 'sitemetadata.gender.female'),
  (3, 'sitemetadata.gender.male_trans'),
  (4, 'sitemetadata.gender.female_trans'),
  (5, 'sitemetadata.gender.non_binary'),
  (6, 'sitemetadata.gender.indistinct');

-- Talent basic info (gender + birth_date con variedad determinística)
INSERT INTO public.talent_basic_info (
  id, created_at, created_by, deleted, modified_at, modified_by,
  stage_name, gender_id, birth_date, talent_profile_id
)
SELECT
  gen_random_uuid(), NOW(), 'SEED_DEMO', false, NOW(), 'SEED_DEMO',
  t.stage_name,
  go.id,
  make_date(
    (EXTRACT(YEAR FROM NOW())::int - 18 - (abs(hashtext(t.email || ':birth_year')) % 42)),
    (1 + (abs(hashtext(t.email || ':birth_month')) % 12)),
    (1 + (abs(hashtext(t.email || ':birth_day')) % 28))
  ),
  tp.id
FROM tmp_seed_user_ids t
JOIN public.talent_profile tp ON tp.user_id = t.user_id
LEFT JOIN public.talent_basic_info tbi ON tbi.talent_profile_id = tp.id
JOIN tmp_gender_pool gp ON gp.idx = ((abs(hashtext(t.email || ':gender')) % 6) + 1)
JOIN public.gender_option go ON go.string_code = gp.string_code
WHERE tbi.id IS NULL;

UPDATE public.talent_basic_info tbi
SET stage_name = t.stage_name,
    gender_id = go.id,
    birth_date = make_date(
      (EXTRACT(YEAR FROM NOW())::int - 18 - (abs(hashtext(t.email || ':birth_year')) % 42)),
      (1 + (abs(hashtext(t.email || ':birth_month')) % 12)),
      (1 + (abs(hashtext(t.email || ':birth_day')) % 28))
    ),
    modified_at = NOW(),
    modified_by = 'SEED_DEMO'
FROM public.talent_profile tp
JOIN tmp_seed_user_ids t ON t.user_id = tp.user_id
JOIN tmp_gender_pool gp ON gp.idx = ((abs(hashtext(t.email || ':gender')) % 6) + 1)
JOIN public.gender_option go ON go.string_code = gp.string_code
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

-- Talent media (catálogo/aplicaciones: headshot + full body) -- SIN CAMBIOS respecto al pool original
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

CREATE TEMP TABLE tmp_seed_profession_pool (
  idx int PRIMARY KEY,
  string_code text NOT NULL
) ON COMMIT DROP;

INSERT INTO tmp_seed_profession_pool (idx, string_code) VALUES
  (1, 'sitemetadata.profession.actor'),
  (2, 'sitemetadata.profession.dancer'),
  (3, 'sitemetadata.profession.singer'),
  (4, 'sitemetadata.profession.influencer'),
  (5, 'sitemetadata.profession.model'),
  (6, 'sitemetadata.profession.musician'),
  (7, 'sitemetadata.profession.standup'),
  (8, 'sitemetadata.profession.voice_talent');

-- Skill pool ampliado: las 49 skills elegibles del catálogo (excluye spanish_pe/spanish_ve, soft-deleted)
CREATE TEMP TABLE tmp_seed_skill_pool (
  idx int PRIMARY KEY,
  string_code text NOT NULL,
  category_string_code text NOT NULL
) ON COMMIT DROP;

INSERT INTO tmp_seed_skill_pool (idx, string_code, category_string_code) VALUES
  -- sport (13)
  (1,  'sitemetadata.skill.athletics', 'sitemetadata.category.sport'),
  (2,  'sitemetadata.skill.basketball', 'sitemetadata.category.sport'),
  (3,  'sitemetadata.skill.boxing', 'sitemetadata.category.sport'),
  (4,  'sitemetadata.skill.cycling', 'sitemetadata.category.sport'),
  (5,  'sitemetadata.skill.football', 'sitemetadata.category.sport'),
  (6,  'sitemetadata.skill.gymnastics', 'sitemetadata.category.sport'),
  (7,  'sitemetadata.skill.handball', 'sitemetadata.category.sport'),
  (8,  'sitemetadata.skill.hockey', 'sitemetadata.category.sport'),
  (9,  'sitemetadata.skill.swimming', 'sitemetadata.category.sport'),
  (10, 'sitemetadata.skill.padel', 'sitemetadata.category.sport'),
  (11, 'sitemetadata.skill.rugby', 'sitemetadata.category.sport'),
  (12, 'sitemetadata.skill.tennis', 'sitemetadata.category.sport'),
  (13, 'sitemetadata.skill.volleyball', 'sitemetadata.category.sport'),
  -- physical (19)
  (14, 'sitemetadata.skill.acrobatics', 'sitemetadata.category.physical'),
  (15, 'sitemetadata.skill.aerial_acrobatics', 'sitemetadata.category.physical'),
  (16, 'sitemetadata.skill.martial_arts', 'sitemetadata.category.physical'),
  (17, 'sitemetadata.skill.capoeira', 'sitemetadata.category.physical'),
  (18, 'sitemetadata.skill.stage_combat', 'sitemetadata.category.physical'),
  (19, 'sitemetadata.skill.contortion', 'sitemetadata.category.physical'),
  (20, 'sitemetadata.skill.tightrope_slackline', 'sitemetadata.category.physical'),
  (21, 'sitemetadata.skill.horseback_riding', 'sitemetadata.category.physical'),
  (22, 'sitemetadata.skill.climbing', 'sitemetadata.category.physical'),
  (23, 'sitemetadata.skill.stage_fencing', 'sitemetadata.category.physical'),
  (24, 'sitemetadata.skill.juggling', 'sitemetadata.category.physical'),
  (25, 'sitemetadata.skill.pantomime', 'sitemetadata.category.physical'),
  (26, 'sitemetadata.skill.physical_theater', 'sitemetadata.category.physical'),
  (27, 'sitemetadata.skill.parkour', 'sitemetadata.category.physical'),
  (28, 'sitemetadata.skill.skating', 'sitemetadata.category.physical'),
  (29, 'sitemetadata.skill.stunts', 'sitemetadata.category.physical'),
  (30, 'sitemetadata.skill.aerial_skills', 'sitemetadata.category.physical'),
  (31, 'sitemetadata.skill.harness_wirework', 'sitemetadata.category.physical'),
  (32, 'sitemetadata.skill.stilts', 'sitemetadata.category.physical'),
  -- language (8)
  (33, 'sitemetadata.skill.german', 'sitemetadata.category.language'),
  (34, 'sitemetadata.skill.chinese_mandarin', 'sitemetadata.category.language'),
  (35, 'sitemetadata.skill.spanish_arg', 'sitemetadata.category.language'),
  (36, 'sitemetadata.skill.french', 'sitemetadata.category.language'),
  (37, 'sitemetadata.skill.english', 'sitemetadata.category.language'),
  (38, 'sitemetadata.skill.italian', 'sitemetadata.category.language'),
  (39, 'sitemetadata.skill.portuguese_br', 'sitemetadata.category.language'),
  (40, 'sitemetadata.skill.russian', 'sitemetadata.category.language'),
  -- accent (9, incluye spanish_arg duplicado con distinta category_string_code)
  (41, 'sitemetadata.skill.spanish_arg', 'sitemetadata.category.accent'),
  (42, 'sitemetadata.skill.spanish_es', 'sitemetadata.category.accent'),
  (43, 'sitemetadata.skill.spanish_neutral', 'sitemetadata.category.accent'),
  (44, 'sitemetadata.skill.spanish_co', 'sitemetadata.category.accent'),
  (45, 'sitemetadata.skill.spanish_ch', 'sitemetadata.category.accent'),
  (46, 'sitemetadata.skill.spanish_mx', 'sitemetadata.category.accent'),
  (47, 'sitemetadata.skill.english_us', 'sitemetadata.category.accent'),
  (48, 'sitemetadata.skill.english_uk', 'sitemetadata.category.accent');

INSERT INTO public.talent_media (
  id, created_at, created_by, deleted, modified_at, modified_by,
  full_body_image_url, headshot_image_url, introduction_video_url, show_reel_video_url, talent_profile_id
)
SELECT
  gen_random_uuid(), NOW(), 'SEED_DEMO', false, NOW(), 'SEED_DEMO',
  split_part(fbp.url, '?', 1) || '?auto=compress&cs=tinysrgb&fit=max&w=900&h=1400&dpr=1',
  split_part(hp.url, '?', 1) || '?auto=compress&cs=tinysrgb&fit=crop&w=480&h=640&dpr=1',
  NULL,
  NULL,
  m.talent_profile_id
FROM (
  SELECT
    tp.id AS talent_profile_id,
    ((abs(hashtext(t.email || ':headshot')) % 65) + 1) AS headshot_idx,
    ((abs(hashtext(t.email || ':fullbody')) % 65) + 1) AS full_body_idx
  FROM public.talent_profile tp
  JOIN tmp_seed_user_ids t ON t.user_id = tp.user_id
) m
JOIN tmp_headshot_pool hp ON hp.idx = m.headshot_idx
JOIN tmp_headshot_pool fbp ON fbp.idx = m.full_body_idx
LEFT JOIN public.talent_media tm ON tm.talent_profile_id = m.talent_profile_id
WHERE tm.id IS NULL;

UPDATE public.talent_media tm
SET headshot_image_url = split_part(hp.url, '?', 1) || '?auto=compress&cs=tinysrgb&fit=crop&w=480&h=640&dpr=1',
    full_body_image_url = split_part(fbp.url, '?', 1) || '?auto=compress&cs=tinysrgb&fit=max&w=900&h=1400&dpr=1',
    modified_at = NOW(),
    modified_by = 'SEED_DEMO'
FROM (
  SELECT
    tp.id AS talent_profile_id,
    ((abs(hashtext(t.email || ':headshot')) % 65) + 1) AS headshot_idx,
    ((abs(hashtext(t.email || ':fullbody')) % 65) + 1) AS full_body_idx
  FROM public.talent_profile tp
  JOIN tmp_seed_user_ids t ON t.user_id = tp.user_id
) m
JOIN tmp_headshot_pool hp ON hp.idx = m.headshot_idx
JOIN tmp_headshot_pool fbp ON fbp.idx = m.full_body_idx
WHERE tm.talent_profile_id = m.talent_profile_id;

-- Ethnicity / hair_color / eye_color / diet pools
CREATE TEMP TABLE tmp_ethnicity_pool (
  idx int PRIMARY KEY,
  string_code text NOT NULL
) ON COMMIT DROP;

INSERT INTO tmp_ethnicity_pool (idx, string_code) VALUES
  (1, 'sitemetadata.ethnicity.afro_descendant'),
  (2, 'sitemetadata.ethnicity.asian'),
  (3, 'sitemetadata.ethnicity.white_caucasian'),
  (4, 'sitemetadata.ethnicity.indigenous_native'),
  (5, 'sitemetadata.ethnicity.latino_hispanic'),
  (6, 'sitemetadata.ethnicity.middle_east_north_africa'),
  (7, 'sitemetadata.ethnicity.mixed'),
  (8, 'sitemetadata.ethnicity.prefer_not_to_say');

CREATE TEMP TABLE tmp_hair_color_pool (
  idx int PRIMARY KEY,
  string_code text NOT NULL,
  category_string_code text NOT NULL
) ON COMMIT DROP;

INSERT INTO tmp_hair_color_pool (idx, string_code, category_string_code) VALUES
  (1, 'sitemetadata.color.black', 'sitemetadata.category.hair_color'),
  (2, 'sitemetadata.color.dark_brown', 'sitemetadata.category.hair_color'),
  (3, 'sitemetadata.color.light_brown', 'sitemetadata.category.hair_color'),
  (4, 'sitemetadata.color.brown', 'sitemetadata.category.hair_color'),
  (5, 'sitemetadata.color.blonde', 'sitemetadata.category.hair_color'),
  (6, 'sitemetadata.color.dark_blonde', 'sitemetadata.category.hair_color'),
  (7, 'sitemetadata.color.light_blonde', 'sitemetadata.category.hair_color'),
  (8, 'sitemetadata.color.red', 'sitemetadata.category.hair_color'),
  (9, 'sitemetadata.color.gray', 'sitemetadata.category.hair_color'),
  (10, 'sitemetadata.color.white', 'sitemetadata.category.hair_color'),
  (11, 'sitemetadata.color.no_hair', 'sitemetadata.category.hair_color');

CREATE TEMP TABLE tmp_eye_color_pool (
  idx int PRIMARY KEY,
  string_code text NOT NULL,
  category_string_code text NOT NULL
) ON COMMIT DROP;

INSERT INTO tmp_eye_color_pool (idx, string_code, category_string_code) VALUES
  (1, 'sitemetadata.color.amber', 'sitemetadata.category.eye_color'),
  (2, 'sitemetadata.color.hazel', 'sitemetadata.category.eye_color'),
  (3, 'sitemetadata.color.blue', 'sitemetadata.category.eye_color'),
  (4, 'sitemetadata.color.light_blue', 'sitemetadata.category.eye_color'),
  (5, 'sitemetadata.color.gray', 'sitemetadata.category.eye_color'),
  (6, 'sitemetadata.color.brown', 'sitemetadata.category.eye_color'),
  (7, 'sitemetadata.color.black', 'sitemetadata.category.eye_color'),
  (8, 'sitemetadata.color.green', 'sitemetadata.category.eye_color'),
  (9, 'sitemetadata.color.heterochromia', 'sitemetadata.category.eye_color');

CREATE TEMP TABLE tmp_diet_pool (
  idx int PRIMARY KEY,
  string_code text NOT NULL
) ON COMMIT DROP;

INSERT INTO tmp_diet_pool (idx, string_code) VALUES
  (1, 'sitemetadata.diet.omnivore'),
  (2, 'sitemetadata.diet.flexitarian'),
  (3, 'sitemetadata.diet.vegetarian'),
  (4, 'sitemetadata.diet.lacto_ovo_vegetarian'),
  (5, 'sitemetadata.diet.vegan'),
  (6, 'sitemetadata.diet.pescatarian'),
  (7, 'sitemetadata.diet.ketogenic'),
  (8, 'sitemetadata.diet.gluten_free'),
  (9, 'sitemetadata.diet.lactose_free'),
  (10, 'sitemetadata.diet.kosher'),
  (11, 'sitemetadata.diet.halal');

-- Talent characteristics: variedad completa (altura, peso, medidas, talles, etnia, color pelo/ojos, dieta, tattoo/passport/dl)
INSERT INTO public.talent_characteristics (
  id, created_at, created_by, deleted, modified_at, modified_by,
  height_cm, weight_kg, chest_cm, waist_cm, hip_cm,
  shirt_size, pant_size, dress_size, shoe_size,
  ethnicity_id, hair_color_id, eye_color_id, diet_option_id,
  tattoo, passport, driving_license, talent_profile_id
)
SELECT
  gen_random_uuid(), NOW(), 'SEED_DEMO', false, NOW(), 'SEED_DEMO',
  150 + (abs(hashtext(t.email || ':height')) % 51),
  48 + (abs(hashtext(t.email || ':weight')) % 63),
  (80 + (abs(hashtext(t.email || ':chest')) % 41))::text,
  (60 + (abs(hashtext(t.email || ':waist')) % 41))::text,
  (80 + (abs(hashtext(t.email || ':hip')) % 41))::text,
  (ARRAY['XS','S','M','L','XL','XXL'])[(abs(hashtext(t.email || ':shirt')) % 6) + 1],
  (ARRAY['36','38','40','42','44','46','48'])[(abs(hashtext(t.email || ':pant')) % 7) + 1],
  (ARRAY['34','36','38','40','42','44'])[(abs(hashtext(t.email || ':dress')) % 6) + 1],
  (ARRAY['35','36','37','38','39','40','41','42','43','44','45'])[(abs(hashtext(t.email || ':shoe')) % 11) + 1],
  eth.id,
  hc.id,
  ec.id,
  diet.id,
  ((abs(hashtext(t.email || ':tattoo')) % 100) < 35),
  ((abs(hashtext(t.email || ':passport')) % 100) < 55),
  ((abs(hashtext(t.email || ':driving_license')) % 100) < 60),
  tp.id
FROM public.talent_profile tp
JOIN tmp_seed_user_ids t ON t.user_id = tp.user_id
LEFT JOIN public.talent_characteristics tc ON tc.talent_profile_id = tp.id
JOIN tmp_ethnicity_pool eth_p ON eth_p.idx = ((abs(hashtext(t.email || ':ethnicity')) % 8) + 1)
JOIN public.ethnicity_option eth ON eth.string_code = eth_p.string_code
JOIN tmp_hair_color_pool hc_p ON hc_p.idx = ((abs(hashtext(t.email || ':hair')) % 11) + 1)
JOIN public.color_option hc ON hc.string_code = hc_p.string_code AND hc.category_string_code = hc_p.category_string_code
JOIN tmp_eye_color_pool ec_p ON ec_p.idx = ((abs(hashtext(t.email || ':eye')) % 9) + 1)
JOIN public.color_option ec ON ec.string_code = ec_p.string_code AND ec.category_string_code = ec_p.category_string_code
JOIN tmp_diet_pool diet_p ON diet_p.idx = ((abs(hashtext(t.email || ':diet')) % 11) + 1)
JOIN public.diet_option diet ON diet.string_code = diet_p.string_code
WHERE tc.id IS NULL;

UPDATE public.talent_characteristics tc
SET height_cm = 150 + (abs(hashtext(t.email || ':height')) % 51),
    weight_kg = 48 + (abs(hashtext(t.email || ':weight')) % 63),
    chest_cm = (80 + (abs(hashtext(t.email || ':chest')) % 41))::text,
    waist_cm = (60 + (abs(hashtext(t.email || ':waist')) % 41))::text,
    hip_cm = (80 + (abs(hashtext(t.email || ':hip')) % 41))::text,
    shirt_size = (ARRAY['XS','S','M','L','XL','XXL'])[(abs(hashtext(t.email || ':shirt')) % 6) + 1],
    pant_size = (ARRAY['36','38','40','42','44','46','48'])[(abs(hashtext(t.email || ':pant')) % 7) + 1],
    dress_size = (ARRAY['34','36','38','40','42','44'])[(abs(hashtext(t.email || ':dress')) % 6) + 1],
    shoe_size = (ARRAY['35','36','37','38','39','40','41','42','43','44','45'])[(abs(hashtext(t.email || ':shoe')) % 11) + 1],
    ethnicity_id = eth.id,
    hair_color_id = hc.id,
    eye_color_id = ec.id,
    diet_option_id = diet.id,
    tattoo = ((abs(hashtext(t.email || ':tattoo')) % 100) < 35),
    passport = ((abs(hashtext(t.email || ':passport')) % 100) < 55),
    driving_license = ((abs(hashtext(t.email || ':driving_license')) % 100) < 60),
    modified_at = NOW(),
    modified_by = 'SEED_DEMO'
FROM public.talent_profile tp
JOIN tmp_seed_user_ids t ON t.user_id = tp.user_id
JOIN tmp_ethnicity_pool eth_p ON eth_p.idx = ((abs(hashtext(t.email || ':ethnicity')) % 8) + 1)
JOIN public.ethnicity_option eth ON eth.string_code = eth_p.string_code
JOIN tmp_hair_color_pool hc_p ON hc_p.idx = ((abs(hashtext(t.email || ':hair')) % 11) + 1)
JOIN public.color_option hc ON hc.string_code = hc_p.string_code AND hc.category_string_code = hc_p.category_string_code
JOIN tmp_eye_color_pool ec_p ON ec_p.idx = ((abs(hashtext(t.email || ':eye')) % 9) + 1)
JOIN public.color_option ec ON ec.string_code = ec_p.string_code AND ec.category_string_code = ec_p.category_string_code
JOIN tmp_diet_pool diet_p ON diet_p.idx = ((abs(hashtext(t.email || ':diet')) % 11) + 1)
JOIN public.diet_option diet ON diet.string_code = diet_p.string_code
WHERE tc.talent_profile_id = tp.id;

-- Relation profession por talent
DELETE FROM public.talent_basic_info_profession tbip
USING public.talent_basic_info tbi
JOIN public.talent_profile tp ON tp.id = tbi.talent_profile_id
JOIN tmp_seed_user_ids t ON t.user_id = tp.user_id
WHERE tbip.talent_basic_info_id = tbi.id;

INSERT INTO public.talent_basic_info_profession (talent_basic_info_id, profession_id)
SELECT
  talent_professions.talent_basic_info_id,
  p.id
FROM (
  SELECT
    tbi.id AS talent_basic_info_id,
    profession_code
  FROM public.talent_basic_info tbi
  JOIN public.talent_profile tp ON tp.id = tbi.talent_profile_id
  JOIN tmp_seed_user_ids t ON t.user_id = tp.user_id
  CROSS JOIN LATERAL (
    SELECT profession_code
    FROM (
      SELECT spp1.string_code AS profession_code
      FROM tmp_seed_profession_pool spp1
      WHERE spp1.idx = ((abs(hashtext(t.email || ':profession:1')) % 8) + 1)

      UNION ALL

      SELECT spp2.string_code AS profession_code
      FROM tmp_seed_profession_pool spp2
      WHERE (abs(hashtext(t.email || ':profession:count')) % 100) < 45
        AND spp2.idx = ((abs(hashtext(t.email || ':profession:2')) % 8) + 1)
        AND spp2.string_code <> (
          SELECT spp_first.string_code
          FROM tmp_seed_profession_pool spp_first
          WHERE spp_first.idx = ((abs(hashtext(t.email || ':profession:1')) % 8) + 1)
        )
    ) chosen_professions
  ) talent_professions
) talent_professions
JOIN public.professions p ON p.string_code = talent_professions.profession_code
ON CONFLICT (talent_basic_info_id, profession_id) DO NOTHING;

-- Relation skill por talent (3 garantizadas + 2 con gate, pool ampliado a 48 skills)
DELETE FROM public.talent_skill ts
USING public.talent_profile tp
JOIN tmp_seed_user_ids t ON t.user_id = tp.user_id
WHERE ts.talent_profile_id = tp.id;

INSERT INTO public.talent_skill (talent_profile_id, skill_id)
SELECT DISTINCT
  talent_skills.talent_profile_id,
  s.id
FROM (
  SELECT
    tp.id AS talent_profile_id,
    skill_code
  FROM public.talent_profile tp
  JOIN tmp_seed_user_ids t ON t.user_id = tp.user_id
  CROSS JOIN LATERAL (
    SELECT DISTINCT skill_code
    FROM (
      SELECT ssp1.string_code AS skill_code
      FROM tmp_seed_skill_pool ssp1
      WHERE ssp1.idx = ((abs(hashtext(t.email || ':skill:1')) % 48) + 1)

      UNION ALL

      SELECT ssp2.string_code AS skill_code
      FROM tmp_seed_skill_pool ssp2
      WHERE ssp2.idx = ((abs(hashtext(t.email || ':skill:2')) % 48) + 1)

      UNION ALL

      SELECT ssp3.string_code AS skill_code
      FROM tmp_seed_skill_pool ssp3
      WHERE ssp3.idx = ((abs(hashtext(t.email || ':skill:3')) % 48) + 1)

      UNION ALL

      SELECT ssp4.string_code AS skill_code
      FROM tmp_seed_skill_pool ssp4
      WHERE (abs(hashtext(t.email || ':skill:count4')) % 100) < 40
        AND ssp4.idx = ((abs(hashtext(t.email || ':skill:4')) % 48) + 1)

      UNION ALL

      SELECT ssp5.string_code AS skill_code
      FROM tmp_seed_skill_pool ssp5
      WHERE (abs(hashtext(t.email || ':skill:count5')) % 100) < 25
        AND ssp5.idx = ((abs(hashtext(t.email || ':skill:5')) % 48) + 1)
    ) chosen_skills
  ) talent_skills
) talent_skills
JOIN public.skills s ON s.string_code = talent_skills.skill_code
ON CONFLICT (talent_profile_id, skill_id) DO NOTHING;

-- Employer basic info: 3 personas con datos reales, resto queda con stub mínimo
INSERT INTO public.employer_basic_info (
  id, created_at, created_by, deleted, modified_at, modified_by,
  company_name, tax_number, company_type_id, company_email, image_url, address, website_url, about, employer_profile_id
)
SELECT
  gen_random_uuid(), NOW(), 'SEED_DEMO', false, NOW(), 'SEED_DEMO',
  ep_persona.company_name,
  ep_persona.tax_number,
  cto.id,
  t.email,
  'https://qmtzkcmnmhvmaerqhaex.supabase.co/storage/v1/object/public/profile-media-develop/autocasting/b6adeb92-127e-4fc3-a82b-1bbcdf2d50ec.png',
  ep_persona.address,
  ep_persona.website_url,
  ep_persona.about,
  ep.id
FROM public.employer_profile ep
JOIN tmp_seed_user_ids t ON t.user_id = ep.user_id
LEFT JOIN public.employer_basic_info ebi ON ebi.employer_profile_id = ep.id
LEFT JOIN tmp_employer_personas ep_persona ON ep_persona.email = t.email
JOIN public.company_type_option cto
  ON cto.string_code = COALESCE(ep_persona.company_type_code, 'sitemetadata.company_type.company')
WHERE ebi.id IS NULL;

UPDATE public.employer_basic_info ebi
SET company_name = COALESCE(ep_persona.company_name, ebi.company_name),
    tax_number = COALESCE(ep_persona.tax_number, ebi.tax_number),
    company_type_id = COALESCE(cto.id, ebi.company_type_id),
    company_email = t.email,
    image_url = COALESCE(ebi.image_url, 'https://qmtzkcmnmhvmaerqhaex.supabase.co/storage/v1/object/public/profile-media-develop/autocasting/b6adeb92-127e-4fc3-a82b-1bbcdf2d50ec.png'),
    address = COALESCE(ep_persona.address, ebi.address),
    website_url = COALESCE(ep_persona.website_url, ebi.website_url),
    about = COALESCE(ep_persona.about, ebi.about),
    modified_at = NOW(),
    modified_by = 'SEED_DEMO'
FROM public.employer_profile ep
JOIN tmp_seed_user_ids t ON t.user_id = ep.user_id
LEFT JOIN tmp_employer_personas ep_persona ON ep_persona.email = t.email
LEFT JOIN public.company_type_option cto ON cto.string_code = ep_persona.company_type_code
WHERE ebi.employer_profile_id = ep.id;

-- ------------------------------------------------------------
-- 3) Castings demo de los 3 employers (2 castings x employer, 5 roles c/u)
-- ------------------------------------------------------------

CREATE TEMP TABLE tmp_casting_employers ON COMMIT DROP AS
SELECT p.email, ep.id AS employer_profile_id
FROM tmp_employer_personas p
JOIN public.users u ON u.email = p.email
JOIN public.employer_profile ep ON ep.user_id = u.id;

-- Limpieza de castings previos de los 3 employers (idempotente)
DELETE FROM public.casting_application ca
WHERE ca.casting_role_id IN (
  SELECT cr.id
  FROM public.casting_role cr
  JOIN public.casting c ON c.id = cr.casting_id
  WHERE c.employer_profile_id IN (SELECT employer_profile_id FROM tmp_casting_employers)
);

DELETE FROM public.casting
WHERE employer_profile_id IN (SELECT employer_profile_id FROM tmp_casting_employers);

CREATE TEMP TABLE tmp_casting_seed (
  casting_seq int PRIMARY KEY,
  employer_email text NOT NULL,
  title text NOT NULL,
  project_type_code text NOT NULL,
  modality_code text NOT NULL,
  status_code text NOT NULL,
  payment_model text NOT NULL,
  location_text text,
  has_wardrobe_fitting boolean NOT NULL,
  wardrobe_fitting_text text,
  description text
) ON COMMIT DROP;

INSERT INTO tmp_casting_seed (
  casting_seq, employer_email, title, project_type_code, modality_code, status_code, payment_model,
  location_text, has_wardrobe_fitting, wardrobe_fitting_text, description
) VALUES
(1, 'asd@asd.com',   'Campaña Urbana: Voces de Ciudad',       'sitemetadata.project_type.commercial',      'sitemetadata.casting_modality.on_site',     'sitemetadata.casting_status.published', 'paid',   'Buenos Aires', true,  'Prueba de vestuario dos días antes del rodaje.',       'Casting completo con datos extendidos para validar dashboard, details y employer card.'),
(2, 'asd@asd.com',   'Microserie Vertical: Medianoche 3AM',    'sitemetadata.project_type.digital_content', 'sitemetadata.casting_modality.autocasting', 'sitemetadata.casting_status.draft',     'unpaid', NULL,           false, NULL,                                                    NULL),
(3, 'asd10@asd.com', 'Cortometraje: Piel de Papel',             'sitemetadata.project_type.short_film',      'sitemetadata.casting_modality.autocasting', 'sitemetadata.casting_status.published', 'unpaid', NULL,           false, NULL,                                                    'Casting con convocatoria abierta gestionada por agencia de talentos.'),
(4, 'asd10@asd.com', 'Convocatoria Abierta: Nuevos Rostros',   'sitemetadata.project_type.commercial',      'sitemetadata.casting_modality.on_site',     'sitemetadata.casting_status.draft',     'paid',   'Rosario',      true,  'Vestuario provisto por la agencia el día del casting.', NULL),
(5, 'asd20@asd.com', 'Documental: Ríos del Sur',                'sitemetadata.project_type.documentary',     'sitemetadata.casting_modality.on_site',     'sitemetadata.casting_status.published', 'paid',   'Córdoba',      true,  'Vestuario coordinado por producción en locación.',      'Casting completo con locación presencial y datos extendidos del employer.'),
(6, 'asd20@asd.com', 'Serie Web: Estación Sur',                 'sitemetadata.project_type.digital_content', 'sitemetadata.casting_modality.autocasting', 'sitemetadata.casting_status.draft',     'unpaid', NULL,           false, NULL,                                                    NULL);

-- Castings
INSERT INTO public.casting (
  id, created_at, created_by, deleted, modified_at, modified_by,
  employer_profile_id, default_code, casting_status_option_id,
  title, project_type_option_id, casting_modality_option_id, location_text,
  application_deadline, has_wardrobe_fitting, wardrobe_fitting_text,
  shooting_start_date, shooting_end_date, description
)
SELECT
  gen_random_uuid(), NOW(), 'SEED_DEMO', false, NOW(), 'SEED_DEMO',
  ce.employer_profile_id,
  format('C-DEMO-%s', LPAD(cs.casting_seq::text, 2, '0')),
  cso.id,
  cs.title,
  pto.id,
  cmo.id,
  cs.location_text,
  CASE
    WHEN cs.casting_seq = 1 THEN (NOW() + INTERVAL '1 day')::date
    ELSE (NOW() + ((10 + (abs(hashtext(cs.title)) % 6))::text || ' days')::interval)::date
  END,
  cs.has_wardrobe_fitting,
  cs.wardrobe_fitting_text,
  (NOW() + ((20 + cs.casting_seq)::text || ' days')::interval)::date,
  (NOW() + ((25 + cs.casting_seq)::text || ' days')::interval)::date,
  cs.description
FROM tmp_casting_seed cs
JOIN tmp_casting_employers ce ON ce.email = cs.employer_email
JOIN public.casting_status_option cso ON cso.string_code = cs.status_code
JOIN public.project_type_option pto ON pto.string_code = cs.project_type_code
JOIN public.casting_modality_option cmo ON cmo.string_code = cs.modality_code;

-- Roles seed (30 nombres: 25 originales + 5 nuevos)
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
  role_description text,
  requirement_description text,
  tattoo boolean,
  passport boolean,
  driving_license boolean,
  include_skill boolean NOT NULL,
  include_ethnicity boolean NOT NULL,
  PRIMARY KEY (casting_seq, role_pos)
) ON COMMIT DROP;

INSERT INTO tmp_role_seed (
  casting_seq, role_pos, role_name, role_type_code, age_min, age_max,
  requires_audio, requires_video, pay_rate_code, currency_code, amount, remuneration_notes,
  role_description, requirement_description, tattoo, passport, driving_license, include_skill, include_ethnicity
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
    'sitemetadata.role_type.host',
    'sitemetadata.role_type.creator',
    'sitemetadata.role_type.guest',
    'sitemetadata.role_type.other'
  ])[(abs(hashtext(c.casting_seq::text || ':' || r.role_pos::text || ':role_type')) % 8) + 1],
  (18 + r.role_pos + c.casting_seq)::smallint,
  (30 + r.role_pos * 3 + c.casting_seq)::smallint,
  (r.role_pos IN (2, 4)),
  (r.role_pos IN (1, 5)),
  CASE
    WHEN c.payment_model = 'unpaid' THEN 'sitemetadata.pay_rate_type.unpaid'
    ELSE (ARRAY[
      'sitemetadata.pay_rate_type.fixed',
      'sitemetadata.pay_rate_type.per_hour',
      'sitemetadata.pay_rate_type.per_day',
      'sitemetadata.pay_rate_type.per_week',
      'sitemetadata.pay_rate_type.to_be_agreed',
      'sitemetadata.pay_rate_type.collaborative'
    ])[(abs(hashtext(c.casting_seq::text || ':' || r.role_pos::text || ':pay_rate')) % 6) + 1]
  END,
  CASE
    WHEN c.payment_model = 'unpaid' THEN NULL
    WHEN c.casting_seq % 2 = 0 THEN 'sitemetadata.currency.usd'
    ELSE 'sitemetadata.currency.ars'
  END,
  CASE
    WHEN c.payment_model = 'unpaid' THEN NULL
    ELSE (35000 + (c.casting_seq * 5000) + (r.role_pos * 3500))::numeric
  END,
  CASE
    WHEN c.payment_model = 'unpaid'
      THEN CASE WHEN c.casting_seq IN (2, 4, 6) AND r.role_pos >= 3 THEN NULL ELSE 'Rol no remunerado con material final para reel.' END
    ELSE CASE WHEN c.casting_seq IN (2, 4, 6) AND r.role_pos >= 3 THEN NULL ELSE 'Remuneración variable según rol y disponibilidad.' END
  END,
  CASE
    WHEN c.casting_seq IN (2, 4, 6) AND r.role_pos >= 3 THEN NULL
    ELSE 'Rol generado por seed para pruebas de filtros, cards y applicants.'
  END,
  CASE
    WHEN r.role_pos IN (1, 2) THEN 'Enviar material de referencia según requerimientos del rol.'
    WHEN r.role_pos IN (4, 5) THEN 'Material adicional valorado, no excluyente.'
    ELSE NULL
  END,
  CASE
    WHEN c.casting_seq IN (2, 4, 6) AND r.role_pos >= 3 THEN NULL
    ELSE (r.role_pos % 3 = 0)
  END,
  CASE
    WHEN c.casting_seq IN (2, 4, 6) AND r.role_pos >= 3 THEN NULL
    ELSE (r.role_pos % 2 = 0)
  END,
  CASE
    WHEN c.casting_seq IN (2, 4, 6) AND r.role_pos >= 3 THEN NULL
    ELSE (r.role_pos % 4 = 0)
  END,
  NOT (c.casting_seq IN (2, 4, 6) AND r.role_pos >= 3),
  ((abs(hashtext(c.casting_seq::text || ':' || r.role_pos::text || ':include_ethnicity')) % 100) < 60)
FROM tmp_casting_seed c
CROSS JOIN generate_series(1, 5) AS r(role_pos)
CROSS JOIN LATERAL (
  SELECT ARRAY[
    'Luna Arce','Gael Moreno','Mara Solís','Bruno Fariña','Selene Prado',
    'Ivo Salvat','Ciro Alvear','Aitana Bosch','Nina Caro','Tiziano Valdés',
    'Vera Cifuentes','Dante Roldán','Uma Ferrer','Elio Varela','Nora Ledesma',
    'Thiago Montal','Alma Quiroga','Renzo Soria','Mila Céspedes','Axel Verona',
    'Iris Calderón','Noam Lucero','Bianca Meza','Tomás Repetto','Ambar Duarte',
    'Simón Achával','Delfina Roca','Nahuel Yrigoyen','Catalina Bustos','Lautaro Peralta'
  ] AS role_names
) rn;

-- Gender / ethnicity pool para roles (reutiliza tmp_gender_pool / tmp_ethnicity_pool ya creados)

-- Insert roles
INSERT INTO public.casting_role (
  id, casting_id, role_name, role_type_option_id, gender_option_id, ethnicity_id,
  age_min, age_max, description,
  pay_rate_type_option_id, currency_option_id, amount, remuneration_notes,
  requires_audio, requires_video, requirement_description,
  tattoo, passport, driving_license,
  created_at, created_by, modified_at, modified_by, deleted
)
SELECT
  gen_random_uuid(),
  c.id,
  rs.role_name,
  rto.id,
  go.id,
  eth.id,
  rs.age_min,
  rs.age_max,
  rs.role_description,
  pr.id,
  co.id,
  rs.amount,
  rs.remuneration_notes,
  rs.requires_audio,
  rs.requires_video,
  rs.requirement_description,
  rs.tattoo,
  rs.passport,
  rs.driving_license,
  NOW(), 'SEED_DEMO', NOW(), 'SEED_DEMO', false
FROM tmp_role_seed rs
JOIN public.casting c ON c.default_code = format('C-DEMO-%s', LPAD(rs.casting_seq::text, 2, '0'))
JOIN public.role_type_option rto ON rto.string_code = rs.role_type_code
JOIN tmp_gender_pool gp ON gp.idx = ((abs(hashtext(rs.casting_seq::text || ':' || rs.role_pos::text || ':role_gender')) % 6) + 1)
JOIN public.gender_option go ON go.string_code = gp.string_code
LEFT JOIN tmp_ethnicity_pool eth_pool ON eth_pool.idx = ((abs(hashtext(rs.casting_seq::text || ':' || rs.role_pos::text || ':role_ethnicity')) % 8) + 1)
LEFT JOIN public.ethnicity_option eth ON rs.include_ethnicity AND eth.string_code = eth_pool.string_code
JOIN public.pay_rate_type_option pr ON pr.string_code = rs.pay_rate_code
LEFT JOIN LATERAL (
  SELECT id FROM public.currency_option
  WHERE rs.currency_code IS NOT NULL AND string_code = rs.currency_code
  ORDER BY created_at NULLS LAST
  LIMIT 1
) co ON true;

CREATE TEMP TABLE tmp_created_roles ON COMMIT DROP AS
SELECT
  rs.casting_seq,
  rs.role_pos,
  cr.id AS casting_role_id,
  cs.status_code,
  rs.requires_audio,
  rs.requires_video,
  rs.pay_rate_code,
  rs.currency_code,
  rs.amount,
  rs.remuneration_notes
FROM tmp_role_seed rs
JOIN public.casting c ON c.default_code = format('C-DEMO-%s', LPAD(rs.casting_seq::text, 2, '0'))
JOIN public.casting_role cr ON cr.casting_id = c.id AND cr.role_name = rs.role_name
JOIN tmp_casting_seed cs ON cs.casting_seq = rs.casting_seq;

-- Profession + skill para cada role
DELETE FROM public.casting_role_profession crp
USING tmp_created_roles r
WHERE crp.casting_role_id = r.casting_role_id;

INSERT INTO public.casting_role_profession (casting_role_id, profession_id)
SELECT
  role_professions.casting_role_id,
  p.id
FROM (
  SELECT
    r.casting_role_id,
    profession_code
  FROM tmp_created_roles r
  JOIN tmp_role_seed rs
    ON rs.casting_seq = r.casting_seq
   AND rs.role_pos = r.role_pos
  CROSS JOIN LATERAL (
    SELECT profession_code
    FROM (
      SELECT CASE rs.role_type_code
        WHEN 'sitemetadata.role_type.voice' THEN
          CASE ((abs(hashtext(r.casting_role_id::text || ':role:voice:1')) % 3) + 1)
            WHEN 1 THEN 'sitemetadata.profession.voice_talent'
            WHEN 2 THEN 'sitemetadata.profession.singer'
            ELSE 'sitemetadata.profession.actor'
          END
        WHEN 'sitemetadata.role_type.host' THEN
          CASE ((abs(hashtext(r.casting_role_id::text || ':role:host:1')) % 3) + 1)
            WHEN 1 THEN 'sitemetadata.profession.influencer'
            WHEN 2 THEN 'sitemetadata.profession.actor'
            ELSE 'sitemetadata.profession.singer'
          END
        WHEN 'sitemetadata.role_type.extra' THEN
          CASE ((abs(hashtext(r.casting_role_id::text || ':role:extra:1')) % 3) + 1)
            WHEN 1 THEN 'sitemetadata.profession.actor'
            WHEN 2 THEN 'sitemetadata.profession.model'
            ELSE 'sitemetadata.profession.dancer'
          END
        ELSE
          CASE ((abs(hashtext(r.casting_role_id::text || ':role:default:1')) % 4) + 1)
            WHEN 1 THEN 'sitemetadata.profession.actor'
            WHEN 2 THEN 'sitemetadata.profession.model'
            WHEN 3 THEN 'sitemetadata.profession.singer'
            ELSE 'sitemetadata.profession.dancer'
          END
      END AS profession_code

      UNION ALL

      SELECT CASE rs.role_type_code
        WHEN 'sitemetadata.role_type.voice' THEN 'sitemetadata.profession.actor'
        WHEN 'sitemetadata.role_type.host' THEN 'sitemetadata.profession.influencer'
        WHEN 'sitemetadata.role_type.extra' THEN 'sitemetadata.profession.model'
        ELSE 'sitemetadata.profession.actor'
      END
      WHERE (abs(hashtext(r.casting_role_id::text || ':role:profession:count')) % 100) < 30
    ) chosen_role_professions
  ) role_professions
) role_professions
JOIN public.professions p ON p.string_code = role_professions.profession_code
ON CONFLICT (casting_role_id, profession_id) DO NOTHING;

DELETE FROM public.casting_role_skill crs
USING tmp_created_roles r
WHERE crs.casting_role_id = r.casting_role_id;

INSERT INTO public.casting_role_skill (casting_role_id, skill_id)
SELECT
  role_skills.casting_role_id,
  s.id
FROM (
  SELECT
    r.casting_role_id,
    skill_code
  FROM tmp_created_roles r
  JOIN tmp_role_seed rs
    ON rs.casting_seq = r.casting_seq
   AND rs.role_pos = r.role_pos
  CROSS JOIN LATERAL (
    SELECT DISTINCT skill_code
    FROM (
      SELECT CASE rs.role_type_code
        WHEN 'sitemetadata.role_type.voice' THEN
          CASE ((abs(hashtext(r.casting_role_id::text || ':role:skill:1')) % 4) + 1)
            WHEN 1 THEN 'sitemetadata.skill.english'
            WHEN 2 THEN 'sitemetadata.skill.spanish_neutral'
            WHEN 3 THEN 'sitemetadata.skill.italian'
            ELSE 'sitemetadata.skill.portuguese_br'
          END
        WHEN 'sitemetadata.role_type.host' THEN
          CASE ((abs(hashtext(r.casting_role_id::text || ':role:skill:1')) % 4) + 1)
            WHEN 1 THEN 'sitemetadata.skill.english'
            WHEN 2 THEN 'sitemetadata.skill.french'
            WHEN 3 THEN 'sitemetadata.skill.english_us'
            ELSE 'sitemetadata.skill.spanish_neutral'
          END
        WHEN 'sitemetadata.role_type.extra' THEN
          CASE ((abs(hashtext(r.casting_role_id::text || ':role:skill:1')) % 5) + 1)
            WHEN 1 THEN 'sitemetadata.skill.football'
            WHEN 2 THEN 'sitemetadata.skill.swimming'
            WHEN 3 THEN 'sitemetadata.skill.basketball'
            WHEN 4 THEN 'sitemetadata.skill.skating'
            ELSE 'sitemetadata.skill.volleyball'
          END
        ELSE
          CASE ((abs(hashtext(r.casting_role_id::text || ':role:skill:1')) % 6) + 1)
            WHEN 1 THEN 'sitemetadata.skill.english'
            WHEN 2 THEN 'sitemetadata.skill.stage_combat'
            WHEN 3 THEN 'sitemetadata.skill.martial_arts'
            WHEN 4 THEN 'sitemetadata.skill.acrobatics'
            WHEN 5 THEN 'sitemetadata.skill.tennis'
            ELSE 'sitemetadata.skill.horseback_riding'
          END
      END AS skill_code

      UNION ALL

      SELECT CASE rs.role_type_code
        WHEN 'sitemetadata.role_type.voice' THEN 'sitemetadata.skill.english_uk'
        WHEN 'sitemetadata.role_type.host' THEN 'sitemetadata.skill.portuguese_br'
        WHEN 'sitemetadata.role_type.extra' THEN 'sitemetadata.skill.padel'
        ELSE 'sitemetadata.skill.french'
      END
      WHERE rs.include_skill
        AND (abs(hashtext(r.casting_role_id::text || ':role:skill:count')) % 100) < 45
    ) chosen_role_skills
  ) role_skills
  WHERE rs.include_skill
) role_skills
JOIN public.skills s ON s.string_code = role_skills.skill_code
ON CONFLICT (casting_role_id, skill_id) DO NOTHING;

-- ------------------------------------------------------------
-- 4) Aplicaciones talent -> roles de castings PUBLISHED (draft no recibe applicants)
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
WHERE r.status_code = 'sitemetadata.casting_status.published'
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

-- Application message pool
CREATE TEMP TABLE tmp_application_message_pool (
  idx int PRIMARY KEY,
  message text NOT NULL
) ON COMMIT DROP;

INSERT INTO tmp_application_message_pool (idx, message) VALUES
  (1, 'Me encantaría formar parte de este proyecto, tengo disponibilidad completa para las fechas indicadas.'),
  (2, 'Adjunto mi material de referencia. Cuento con experiencia previa en roles similares.'),
  (3, 'Aplicación demo generada por seed para validar tablero de postulantes.'),
  (4, 'Disponible para pruebas de cámara cuando lo requieran. Muchas gracias por la oportunidad.'),
  (5, 'Vi la convocatoria y me pareció una gran oportunidad para sumar a mi portfolio.'),
  (6, 'Postulación enviada con entusiasmo, quedo atento/a a cualquier consulta adicional.');

CREATE TEMP TABLE tmp_submission_notes_pool (
  idx int PRIMARY KEY,
  notes text NOT NULL
) ON COMMIT DROP;

INSERT INTO tmp_submission_notes_pool (idx, notes) VALUES
  (1, 'Submission demo para pruebas funcionales.'),
  (2, 'Material grabado especialmente para esta convocatoria.'),
  (3, 'Archivo de referencia, disponible material adicional a pedido.'),
  (4, 'Grabación realizada en casa, calidad de audio/video estándar.'),
  (5, 'Reel actualizado, incluye trabajos recientes.');

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
    amp.message,
    NOW(), 'SEED_DEMO', NOW(), 'SEED_DEMO', false
  FROM tmp_role_applicants ra
  JOIN public.casting_application_status_option sao
    ON sao.string_code = 'sitemetadata.application_status.blank'
  JOIN tmp_application_message_pool amp
    ON amp.idx = ((abs(hashtext(ra.talent_profile_id::text || ':' || ra.casting_role_id::text || ':message')) % 6) + 1)
  RETURNING id, casting_role_id, talent_profile_id
)
INSERT INTO tmp_created_applications (id, casting_role_id, talent_profile_id)
SELECT id, casting_role_id, talent_profile_id
FROM inserted_apps;

-- Requirement submissions para cada aplicación (audio/video URL SIN CAMBIOS; notes con variedad)
INSERT INTO public.casting_application_requirement_submission (
  id, application_id, casting_role_id, audio_url, video_url, notes,
  created_at, created_by, modified_at, modified_by, deleted
)
SELECT
  gen_random_uuid(),
  a.id,
  cr.id,
  CASE WHEN cr.requires_audio THEN 'https://www.youtube.com/watch?v=bhagN-pes9Q' ELSE NULL END,
  CASE WHEN cr.requires_video THEN 'https://www.youtube.com/watch?v=bhagN-pes9Q' ELSE NULL END,
  snp.notes,
  NOW(), 'SEED_DEMO', NOW(), 'SEED_DEMO', false
FROM tmp_created_applications a
JOIN public.casting_role cr ON cr.id = a.casting_role_id
JOIN tmp_submission_notes_pool snp
  ON snp.idx = ((abs(hashtext(a.id::text || ':notes')) % 5) + 1);


-- ------------------------------------------------------------
-- 5) Validaciones fuertes post-seed
-- ------------------------------------------------------------
IF (SELECT COUNT(*) FROM public.users WHERE email = 'asd@asd.com') <> 1 THEN
  RAISE EXCEPTION 'Seed inválido: asd@asd.com no quedó en estado esperado';
END IF;

IF (SELECT COUNT(*) FROM public.users WHERE email ~ '^asd[0-9]+@asd\.com$') <> 30 THEN
  RAISE EXCEPTION 'Seed inválido: cantidad de usuarios incrementales distinta de 30';
END IF;

IF (
  SELECT COUNT(*)
  FROM public.employer_basic_info ebi
  JOIN public.employer_profile ep ON ep.id = ebi.employer_profile_id
  JOIN public.users u ON u.id = ep.user_id
  WHERE u.email IN ('asd@asd.com', 'asd10@asd.com', 'asd20@asd.com')
    AND ebi.company_name IS NOT NULL
) <> 3 THEN
  RAISE EXCEPTION 'Seed inválido: no se completaron los 3 employer personas esperados';
END IF;

IF (
  SELECT COUNT(*)
  FROM public.casting c
  WHERE c.employer_profile_id IN (SELECT employer_profile_id FROM tmp_casting_employers)
) <> 6 THEN
  RAISE EXCEPTION 'Seed inválido: cantidad de castings distinta de 6';
END IF;

IF (SELECT COUNT(DISTINCT c.employer_profile_id) FROM public.casting c WHERE c.employer_profile_id IN (SELECT employer_profile_id FROM tmp_casting_employers)) <> 3 THEN
  RAISE EXCEPTION 'Seed inválido: los 6 castings no están distribuidos entre los 3 employers';
END IF;

IF (
  SELECT COUNT(DISTINCT c.casting_status_option_id)
  FROM public.casting c
  WHERE c.employer_profile_id IN (SELECT employer_profile_id FROM tmp_casting_employers)
) < 2 THEN
  RAISE EXCEPTION 'Seed inválido: no hay variedad de casting_status (se esperaba draft + published)';
END IF;

IF (
  SELECT COUNT(*)
  FROM public.casting_role cr
  JOIN public.casting c ON c.id = cr.casting_id
  WHERE c.employer_profile_id IN (SELECT employer_profile_id FROM tmp_casting_employers)
) <> 30 THEN
  RAISE EXCEPTION 'Seed inválido: cantidad de roles distinta de 30';
END IF;

IF EXISTS (
  SELECT 1
  FROM (
    SELECT
      ca.casting_role_id,
      COUNT(*)::int AS applicant_count
    FROM public.casting_application ca
    JOIN public.casting_role cr ON cr.id = ca.casting_role_id
    JOIN public.casting c ON c.id = cr.casting_id
    WHERE c.employer_profile_id IN (SELECT employer_profile_id FROM tmp_casting_employers)
      AND c.casting_status_option_id IN (
        SELECT id FROM public.casting_status_option WHERE string_code = 'sitemetadata.casting_status.published'
      )
    GROUP BY ca.casting_role_id
  ) per_role
  WHERE per_role.applicant_count < 3 OR per_role.applicant_count > 15
) THEN
  RAISE EXCEPTION 'Seed inválido: existe al menos un role de casting published fuera del rango de applicants (3..15)';
END IF;

IF (
  SELECT COUNT(*)
  FROM public.casting_application ca
  JOIN public.casting_role cr ON cr.id = ca.casting_role_id
  JOIN public.casting c ON c.id = cr.casting_id
  WHERE c.employer_profile_id IN (SELECT employer_profile_id FROM tmp_casting_employers)
    AND c.casting_status_option_id IN (
      SELECT id FROM public.casting_status_option WHERE string_code = 'sitemetadata.casting_status.draft'
    )
) <> 0 THEN
  RAISE EXCEPTION 'Seed inválido: existen aplicaciones para roles de castings draft (no deberían tener applicants)';
END IF;

IF (
  SELECT COUNT(*)
  FROM public.casting_application ca
  JOIN public.casting_role cr ON cr.id = ca.casting_role_id
  JOIN public.casting c ON c.id = cr.casting_id
  WHERE c.employer_profile_id IN (SELECT employer_profile_id FROM tmp_casting_employers)
) <> (
  SELECT COALESCE(SUM(target_count), 0)
  FROM tmp_role_application_targets
) THEN
  RAISE EXCEPTION 'Seed inválido: cantidad total de aplicaciones no coincide con el target generado por role';
END IF;

IF (
  SELECT COUNT(*)
  FROM public.legal_acceptances la
  JOIN public.legal_documents ld ON ld.id = la.legal_document_id
  JOIN public.users u ON u.id = la.user_id
  WHERE (u.email = 'asd@asd.com' OR u.email ~ '^asd[0-9]+@asd\.com$')
    AND ld.type IN ('TERMS', 'PRIVACY')
) <> 62 THEN
  RAISE EXCEPTION 'Seed inválido: aceptaciones legales esperadas para usuarios demo (62) no coinciden';
END IF;

IF (
  SELECT COUNT(DISTINCT tbi.gender_id)
  FROM public.talent_basic_info tbi
  JOIN public.talent_profile tp ON tp.id = tbi.talent_profile_id
  JOIN public.users u ON u.id = tp.user_id
  WHERE u.email ~ '^asd[0-9]+@asd\.com$'
) < 4 THEN
  RAISE EXCEPTION 'Seed inválido: variedad de gender insuficiente entre talents seed';
END IF;

IF (
  SELECT COUNT(*)
  FROM public.talent_characteristics tc
  JOIN public.talent_profile tp ON tp.id = tc.talent_profile_id
  JOIN public.users u ON u.id = tp.user_id
  WHERE u.email ~ '^asd[0-9]+@asd\.com$'
    AND (tc.ethnicity_id IS NULL OR tc.hair_color_id IS NULL OR tc.eye_color_id IS NULL OR tc.diet_option_id IS NULL)
) <> 0 THEN
  RAISE EXCEPTION 'Seed inválido: existen talent_characteristics con ethnicity/hair_color/eye_color/diet sin completar';
END IF;

END;
$proc$;

BEGIN;
CALL public.seed_demo_30_users_3_employers();
COMMIT;

DROP PROCEDURE public.seed_demo_30_users_3_employers();
