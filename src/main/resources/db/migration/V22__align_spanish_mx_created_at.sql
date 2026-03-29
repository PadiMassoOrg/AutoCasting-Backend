-- Align created_at for spanish_mx with spanish_neutral.
UPDATE skills
SET created_at = TIMESTAMP '2025-10-07 19:21:02.970'
WHERE string_code = 'sitemetadata.skill.spanish_mx';
