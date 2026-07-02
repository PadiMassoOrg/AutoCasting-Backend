DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = 'public'
          AND table_name = 'entity_notes'
    ) AND NOT EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = 'public'
          AND table_name = 'entity_history'
    ) THEN
        ALTER TABLE public.entity_notes RENAME TO entity_history;
    END IF;
END
$$;

ALTER TABLE public.entity_history
    RENAME COLUMN reason TO note;

ALTER TABLE public.entity_history
    ALTER COLUMN note DROP NOT NULL;

ALTER INDEX IF EXISTS idx_entity_notes_entity RENAME TO idx_entity_history_entity;
ALTER INDEX IF EXISTS idx_entity_notes_deleted RENAME TO idx_entity_history_deleted;
