ALTER TABLE casting_role
    DROP CONSTRAINT IF EXISTS fk_casting_role_hair_color,
    DROP CONSTRAINT IF EXISTS fk_casting_role_eye_color,
    DROP CONSTRAINT IF EXISTS fk_casting_role_diet;

ALTER TABLE casting_role
    DROP COLUMN IF EXISTS height_cm,
    DROP COLUMN IF EXISTS weight_kg,
    DROP COLUMN IF EXISTS hair_color_id,
    DROP COLUMN IF EXISTS eye_color_id,
    DROP COLUMN IF EXISTS chest_cm,
    DROP COLUMN IF EXISTS waist_cm,
    DROP COLUMN IF EXISTS hip_cm,
    DROP COLUMN IF EXISTS shirt_size,
    DROP COLUMN IF EXISTS pant_size,
    DROP COLUMN IF EXISTS dress_size,
    DROP COLUMN IF EXISTS shoe_size,
    DROP COLUMN IF EXISTS diet_option_id;
