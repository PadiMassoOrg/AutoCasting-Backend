-- Rename FK column to match JPA mapping
-- casting_application.application_status_option_id -> casting_application.casting_application_status_option_id

ALTER TABLE casting_application
    RENAME COLUMN application_status_option_id TO casting_application_status_option_id;

-- (Opcional pero recomendado) renombrar también el índice si querés consistencia
-- Si el índice existe con el nombre anterior:
DO
$$
    BEGIN
        IF EXISTS (SELECT 1
                   FROM pg_indexes
                   WHERE schemaname = current_schema()
                     AND indexname = 'idx_casting_application_status') THEN
            ALTER INDEX idx_casting_application_status
                RENAME TO idx_casting_application_casting_application_status;
        END IF;
    END
$$;

-- (Opcional) Si querés que el nombre del FK constraint sea coherente, podés renombrarlo también.
-- OJO: esto depende del nombre exacto existente.
-- Si tu constraint se llama fk_casting_application_status_option:
DO
$$
    BEGIN
        IF EXISTS (SELECT 1
                   FROM pg_constraint
                   WHERE conname = 'fk_casting_application_status_option') THEN
            ALTER TABLE casting_application
                RENAME CONSTRAINT fk_casting_application_status_option
                    TO fk_casting_application_casting_application_status_option;
        END IF;
    END
$$;
