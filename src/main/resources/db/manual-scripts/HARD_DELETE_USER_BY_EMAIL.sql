-- ⚠️ DEV/TEST ONLY
-- Hard-deletes a single user and everything associated with them (talent profile,
-- employer profile, and every child table down the tree), so the same email can be
-- re-registered from scratch to re-test flows like onboarding completion.
--
-- Unlike HARD_DELETE.sql (which wipes the entire public schema for a full Flyway
-- replay), this only removes one user's data tree — safe to run repeatedly against
-- develop without affecting other seeded/test users.
--
-- Usage: edit v_email below, then run this whole script against the target DB.

DO $$
DECLARE
  v_email varchar := 'padillatomasp@gmail.com';
  v_user_id uuid;
  v_talent_profile_id uuid;
  v_employer_profile_id uuid;
BEGIN
  SELECT u.id
  INTO v_user_id
  FROM public.users u
  WHERE u.email = v_email
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
      -- Eliminar aplicaciones (de cualquier talento) a roles de castings de este employer.
      DELETE FROM public.casting_application ca
      WHERE ca.casting_role_id IN (
        SELECT cr.id
        FROM public.casting_role cr
        JOIN public.casting c ON c.id = cr.casting_id
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

    -- Historial de auditoría asociado (polimórfico, sin FK — limpieza manual).
    DELETE FROM public.entity_history eh
    WHERE (eh.entity_type = 'USER' AND eh.entity_id = v_user_id)
       OR (v_talent_profile_id IS NOT NULL AND eh.entity_type = 'TALENT_PROFILE' AND eh.entity_id = v_talent_profile_id)
       OR (v_employer_profile_id IS NOT NULL AND eh.entity_type = 'EMPLOYER_PROFILE' AND eh.entity_id = v_employer_profile_id)
       OR (v_employer_profile_id IS NOT NULL AND eh.entity_type = 'CASTING' AND eh.entity_id IN (
             SELECT c.id FROM public.casting c WHERE c.employer_profile_id = v_employer_profile_id
           ));

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

    -- refresh_tokens tiene ON DELETE CASCADE desde users, pero se borra explícito por claridad.
    DELETE FROM public.refresh_tokens
    WHERE user_id = v_user_id;

    DELETE FROM public.user_roles
    WHERE user_id = v_user_id;

    DELETE FROM public.users
    WHERE id = v_user_id;
  END IF;
END $$;
