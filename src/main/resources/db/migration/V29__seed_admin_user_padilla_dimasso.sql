INSERT INTO public.users (
  id,
  created_at,
  created_by,
  deleted,
  modified_at,
  modified_by,
  email,
  password,
  user_account_provider,
  active_mode,
  talent_onboarding_status,
  employer_onboarding_status
)
VALUES (
  gen_random_uuid(),
  NOW(),
  'FLYWAY_V29',
  false,
  NOW(),
  'FLYWAY_V29',
  'padilla.dimasso@gmail.com',
  '$2y$10$zrAoan12Zu4kDqxFXyOhnu4yz0OM2L8jpRmytJU1nrUx9Ipb7ImEG',
  'LOCAL',
  NULL,
  'NOT_STARTED',
  'NOT_STARTED'
)
ON CONFLICT (email) DO UPDATE SET
  password = EXCLUDED.password,
  user_account_provider = 'LOCAL',
  deleted = false,
  modified_at = NOW(),
  modified_by = 'FLYWAY_V29';

INSERT INTO public.user_roles (user_id, role_id)
SELECT u.id, r.id
FROM public.users u
JOIN public.roles r ON r.code = 'ADMIN'
WHERE u.email = 'padilla.dimasso@gmail.com'
ON CONFLICT (user_id, role_id) DO NOTHING;
