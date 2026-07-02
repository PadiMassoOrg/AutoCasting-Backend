CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS entity_notes
(
    id           uuid PRIMARY KEY      DEFAULT gen_random_uuid(),

    entity_type  varchar(50)  NOT NULL,
    entity_id    uuid         NOT NULL,
    reason       text         NOT NULL,

    created_at   timestamp    NOT NULL DEFAULT now(),
    created_by   varchar(255) NOT NULL DEFAULT 'SYSTEM',
    modified_at  timestamp    NOT NULL DEFAULT now(),
    modified_by  varchar(255) NOT NULL DEFAULT 'SYSTEM',
    deleted      boolean      NOT NULL DEFAULT false
);

CREATE INDEX IF NOT EXISTS idx_entity_notes_entity
    ON entity_notes (entity_type, entity_id, created_at);

CREATE INDEX IF NOT EXISTS idx_entity_notes_deleted
    ON entity_notes (deleted);
