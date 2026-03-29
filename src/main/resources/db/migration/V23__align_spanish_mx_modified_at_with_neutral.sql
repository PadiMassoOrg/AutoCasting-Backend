-- Align spanish_mx timestamps with spanish_neutral.
UPDATE skills mx
SET
    created_at = neutral.created_at,
    modified_at = neutral.modified_at,
    modified_by = 'SYSTEM'
FROM skills neutral
WHERE mx.string_code = 'sitemetadata.skill.spanish_mx'
  AND neutral.string_code = 'sitemetadata.skill.spanish_neutral';
