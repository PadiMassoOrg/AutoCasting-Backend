CREATE OR REPLACE FUNCTION public.enforce_talent_basic_info_profession_limit()
RETURNS trigger
LANGUAGE plpgsql
AS $$
DECLARE
  profession_count integer;
  max_professions constant integer := 2;
BEGIN
  SELECT count(*)
  INTO profession_count
  FROM public.talent_basic_info_profession
  WHERE talent_basic_info_id = NEW.talent_basic_info_id;

  IF profession_count > max_professions THEN
    RAISE EXCEPTION 'talent.professions_max';
  END IF;

  RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS trg_talent_basic_info_profession_limit ON public.talent_basic_info_profession;

CREATE TRIGGER trg_talent_basic_info_profession_limit
AFTER INSERT OR UPDATE OF talent_basic_info_id, profession_id ON public.talent_basic_info_profession
FOR EACH ROW
EXECUTE FUNCTION public.enforce_talent_basic_info_profession_limit();
