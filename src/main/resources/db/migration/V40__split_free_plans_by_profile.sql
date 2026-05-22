INSERT INTO plans (
  id,
  code,
  name_string_code,
  description,
  allows_custom_slug,
  created_at,
  created_by,
  modified_at,
  modified_by,
  deleted
)
VALUES
  (gen_random_uuid(), 'TALENT_FREE', 'plan.talent_free', 'Plan gratuito por defecto para perfiles Talent', false, NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
  (gen_random_uuid(), 'EMPLOYER_FREE', 'plan.employer_free', 'Plan gratuito por defecto para perfiles Employer', false, NOW(), 'SYSTEM', NOW(), 'SYSTEM', false)
ON CONFLICT (code) DO UPDATE SET
  name_string_code = EXCLUDED.name_string_code,
  description = EXCLUDED.description,
  allows_custom_slug = EXCLUDED.allows_custom_slug,
  deleted = false,
  modified_at = NOW(),
  modified_by = 'FLYWAY_V40';

UPDATE talent_profile tp
SET plan_id = talent_free.id,
    modified_at = NOW(),
    modified_by = 'FLYWAY_V40'
FROM plans legacy_free,
     plans talent_free
WHERE legacy_free.code = 'FREE'
  AND talent_free.code = 'TALENT_FREE'
  AND tp.plan_id = legacy_free.id;

UPDATE employer_profile ep
SET plan_id = employer_free.id,
    modified_at = NOW(),
    modified_by = 'FLYWAY_V40'
FROM plans legacy_free,
     plans employer_free
WHERE legacy_free.code = 'FREE'
  AND employer_free.code = 'EMPLOYER_FREE'
  AND ep.plan_id = legacy_free.id;

UPDATE plans
SET deleted = true,
    modified_at = NOW(),
    modified_by = 'FLYWAY_V40'
WHERE code = 'FREE'
  AND deleted = false;
