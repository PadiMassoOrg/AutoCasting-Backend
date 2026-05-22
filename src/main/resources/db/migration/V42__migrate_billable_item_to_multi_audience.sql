-- ============================================================
-- Billing Module - Multi audience for billable items
-- Replaces single audience column with relation table.
-- ============================================================

CREATE TABLE IF NOT EXISTS billable_item_audience
(
    billable_item_id uuid        NOT NULL,
    audience         varchar(32) NOT NULL,

    CONSTRAINT pk_billable_item_audience PRIMARY KEY (billable_item_id, audience),
    CONSTRAINT fk_billable_item_audience_item
        FOREIGN KEY (billable_item_id) REFERENCES billable_item (id) ON DELETE CASCADE,
    CONSTRAINT chk_billable_item_audience_value
        CHECK (audience IN ('TALENT', 'EMPLOYER'))
);

INSERT INTO billable_item_audience (billable_item_id, audience)
SELECT id, audience
FROM billable_item
ON CONFLICT (billable_item_id, audience) DO NOTHING;

CREATE INDEX IF NOT EXISTS idx_billable_item_audience_audience
    ON billable_item_audience (audience);

DROP INDEX IF EXISTS idx_billable_item_audience;

ALTER TABLE billable_item
    DROP CONSTRAINT IF EXISTS chk_billable_item_audience;

ALTER TABLE billable_item
    DROP COLUMN IF EXISTS audience;
