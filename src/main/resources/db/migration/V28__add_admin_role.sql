INSERT INTO public.roles (
  id,
  created_at,
  created_by,
  deleted,
  modified_at,
  modified_by,
  code,
  description,
  name_string_code
)
VALUES (
  gen_random_uuid(),
  NOW(),
  'FLYWAY_V28',
  false,
  NOW(),
  'FLYWAY_V28',
  'ADMIN',
  'Usuario administrador de la plataforma',
  'role.admin'
)
ON CONFLICT (code) DO UPDATE SET
  description = EXCLUDED.description,
  name_string_code = EXCLUDED.name_string_code,
  deleted = false,
  modified_at = NOW(),
  modified_by = 'FLYWAY_V28';
