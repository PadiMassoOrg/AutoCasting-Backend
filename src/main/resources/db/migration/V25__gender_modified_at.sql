UPDATE public.gender_option
SET modified_at = NOW(),
    modified_by = 'SYSTEM'
WHERE string_code = 'sitemetadata.gender.indistinct';
