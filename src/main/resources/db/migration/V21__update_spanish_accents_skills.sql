-- Soft delete old accent skills to preserve FK integrity on skills.id references.
UPDATE skills
SET
    deleted = true,
    modified_at = NOW(),
    modified_by = 'SYSTEM'
WHERE string_code IN (
    'sitemetadata.skill.spanish_pe',
    'sitemetadata.skill.spanish_ve'
);

-- If it already exists (even deleted), reactivate and normalize metadata.
UPDATE skills
SET
    deleted = false,
    category_string_code = 'sitemetadata.category.accent',
    modified_at = NOW(),
    modified_by = 'SYSTEM'
WHERE string_code = 'sitemetadata.skill.spanish_mx';

-- If it does not exist yet, create it with standard audit/category values.
INSERT INTO skills (
    id,
    string_code,
    created_at,
    created_by,
    modified_at,
    modified_by,
    deleted,
    category_string_code
)
SELECT
    gen_random_uuid(),
    'sitemetadata.skill.spanish_mx',
    NOW(),
    'SYSTEM',
    NOW(),
    'SYSTEM',
    false,
    'sitemetadata.category.accent'
WHERE NOT EXISTS (
    SELECT 1
    FROM skills
    WHERE string_code = 'sitemetadata.skill.spanish_mx'
);
