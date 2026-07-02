ALTER TABLE public.entity_history
    ADD COLUMN IF NOT EXISTS changes text;
