DO $$
DECLARE
  v_user_id uuid;
  v_talent_profile_id uuid;
  v_employer_profile_id uuid;
BEGIN
  SELECT u.id
  INTO v_user_id
  FROM public.users u
  WHERE u.email = 'padilla.dimasso@gmail.com'
  LIMIT 1;

  IF v_user_id IS NOT NULL THEN
    SELECT tp.id
    INTO v_talent_profile_id
    FROM public.talent_profile tp
    WHERE tp.user_id = v_user_id
    LIMIT 1;

    SELECT ep.id
    INTO v_employer_profile_id
    FROM public.employer_profile ep
    WHERE ep.user_id = v_user_id
    LIMIT 1;

    IF v_employer_profile_id IS NOT NULL THEN
      -- Eliminar aplicaciones vinculadas a roles de castings de este employer.
      DELETE FROM public.casting_application ca
      WHERE ca.casting_role_id IN (
        SELECT cr.id
        FROM public.casting_role cr
        JOIN public.casting_roles_section crs ON crs.id = cr.casting_roles_section_id
        JOIN public.casting c ON c.id = crs.casting_id
        WHERE c.employer_profile_id = v_employer_profile_id
      );

      -- Eliminar castings (cascading borra basic/roles/requirements/remuneration y subtablas).
      DELETE FROM public.casting
      WHERE employer_profile_id = v_employer_profile_id;

      -- Eliminar links sociales del employer.
      DELETE FROM public.talent_social_media_link
      WHERE employer_basic_info_id IN (
        SELECT ebi.id
        FROM public.employer_basic_info ebi
        WHERE ebi.employer_profile_id = v_employer_profile_id
      );

      DELETE FROM public.employer_basic_info
      WHERE employer_profile_id = v_employer_profile_id;
    END IF;

    IF v_talent_profile_id IS NOT NULL THEN
      -- Eliminar aplicaciones del talento (submissions se borran por ON DELETE CASCADE).
      DELETE FROM public.casting_application
      WHERE talent_profile_id = v_talent_profile_id;

      DELETE FROM public.talent_social_media_link
      WHERE talent_profile_id = v_talent_profile_id;

      DELETE FROM public.talent_skill
      WHERE talent_profile_id = v_talent_profile_id;

      DELETE FROM public.talent_basic_info_profession
      WHERE talent_basic_info_id IN (
        SELECT tbi.id
        FROM public.talent_basic_info tbi
        WHERE tbi.talent_profile_id = v_talent_profile_id
      );

      DELETE FROM public.talent_media_other_pictures_url
      WHERE talent_media_id IN (
        SELECT tm.id
        FROM public.talent_media tm
        WHERE tm.talent_profile_id = v_talent_profile_id
      );

      DELETE FROM public.talent_credit
      WHERE talent_profile_id = v_talent_profile_id;

      DELETE FROM public.talent_education
      WHERE talent_profile_id = v_talent_profile_id;

      DELETE FROM public.talent_contact
      WHERE talent_profile_id = v_talent_profile_id;

      DELETE FROM public.talent_characteristics
      WHERE talent_profile_id = v_talent_profile_id;

      DELETE FROM public.talent_media
      WHERE talent_profile_id = v_talent_profile_id;

      DELETE FROM public.talent_basic_info
      WHERE talent_profile_id = v_talent_profile_id;
    END IF;

    DELETE FROM public.legal_acceptances
    WHERE user_id = v_user_id;

    IF v_talent_profile_id IS NOT NULL THEN
      DELETE FROM public.talent_profile
      WHERE id = v_talent_profile_id;
    END IF;

    IF v_employer_profile_id IS NOT NULL THEN
      DELETE FROM public.employer_profile
      WHERE id = v_employer_profile_id;
    END IF;

    DELETE FROM public.user_roles
    WHERE user_id = v_user_id;

    DELETE FROM public.users
    WHERE id = v_user_id;
  END IF;
END $$;

-- Asegurar existencia/estado del rol ADMIN.
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
  'FLYWAY_V30',
  false,
  NOW(),
  'FLYWAY_V30',
  'ADMIN',
  'Usuario administrador de la plataforma',
  'role.admin'
)
ON CONFLICT (code) DO UPDATE SET
  description = EXCLUDED.description,
  name_string_code = EXCLUDED.name_string_code,
  deleted = false,
  modified_at = NOW(),
  modified_by = 'FLYWAY_V30';

-- Reinsertar usuario admin.
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
  'FLYWAY_V30',
  false,
  NOW(),
  'FLYWAY_V30',
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
  modified_by = 'FLYWAY_V30';

-- Dejar únicamente el rol ADMIN asociado a esta cuenta.
DELETE FROM public.user_roles ur
USING public.users u
WHERE ur.user_id = u.id
  AND u.email = 'padilla.dimasso@gmail.com';

INSERT INTO public.user_roles (user_id, role_id)
SELECT u.id, r.id
FROM public.users u
JOIN public.roles r ON r.code = 'ADMIN'
WHERE u.email = 'padilla.dimasso@gmail.com'
ON CONFLICT (user_id, role_id) DO NOTHING;
