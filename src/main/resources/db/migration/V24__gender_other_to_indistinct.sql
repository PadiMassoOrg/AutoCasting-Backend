-- Rename gender option from "other" to "indistinct"
-- and move it to the first position by setting created_at
-- to the earliest created_at in the table minus 1 second.
-- Defensive version: do nothing if "indistinct" already exists.

WITH earliest_gender_created_at AS (SELECT COALESCE(MIN(created_at), NOW()) - INTERVAL '1 second' AS new_created_at
                                    FROM public.gender_option)
UPDATE public.gender_option go
SET string_code = 'sitemetadata.gender.indistinct',
    created_at  = egca.new_created_at
FROM earliest_gender_created_at egca
WHERE go.string_code = 'sitemetadata.gender.other'
  AND NOT EXISTS (SELECT 1
                  FROM public.gender_option existing
                  WHERE existing.string_code = 'sitemetadata.gender.indistinct');
