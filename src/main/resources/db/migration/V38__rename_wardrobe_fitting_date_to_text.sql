DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'casting'
          AND column_name = 'wardrobe_fitting_date'
    ) THEN
        IF EXISTS (
            SELECT 1
            FROM information_schema.columns
            WHERE table_schema = 'public'
              AND table_name = 'casting'
              AND column_name = 'wardrobe_fitting_text'
        ) THEN
            EXECUTE '
                UPDATE public.casting
                SET wardrobe_fitting_text = COALESCE(wardrobe_fitting_text, wardrobe_fitting_date)
                WHERE wardrobe_fitting_date IS NOT NULL
            ';
            EXECUTE 'ALTER TABLE public.casting DROP COLUMN wardrobe_fitting_date';
        ELSE
            EXECUTE 'ALTER TABLE public.casting RENAME COLUMN wardrobe_fitting_date TO wardrobe_fitting_text';
        END IF;
    END IF;
END $$;

ALTER TABLE casting
    DROP CONSTRAINT IF EXISTS chk_casting_wardrobe_requires_date,
    DROP CONSTRAINT IF EXISTS chk_casting_wardrobe_requires_text;

ALTER TABLE casting
    ADD CONSTRAINT chk_casting_wardrobe_requires_text
        CHECK (
            has_wardrobe_fitting IS DISTINCT FROM false
            OR wardrobe_fitting_text IS NULL
        );
