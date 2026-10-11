-- Events (AI-92): entry access becomes sitemetadata (DB-driven options, like proposal_type_option)
-- instead of a hardcoded varchar/enum: events.entry_access -> events.entry_access_option_id.

CREATE TABLE entry_access_option (
  id                   uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  string_code          varchar(255) NOT NULL,
  category_string_code varchar(255) NOT NULL,
  created_at           timestamp NOT NULL DEFAULT now(),
  created_by           varchar(255) NOT NULL DEFAULT 'SYSTEM',
  modified_at          timestamp NOT NULL DEFAULT now(),
  modified_by          varchar(255) NOT NULL DEFAULT 'SYSTEM',
  deleted              boolean NOT NULL DEFAULT false
);

CREATE UNIQUE INDEX ux_entry_access_option_string_code
  ON entry_access_option (string_code);

CREATE INDEX idx_entry_access_option_category
  ON entry_access_option (category_string_code);

INSERT INTO entry_access_option (
  id,
  string_code,
  category_string_code,
  created_at,
  created_by,
  modified_at,
  modified_by,
  deleted
)
VALUES
  (gen_random_uuid(), 'sitemetadata.entry_access.free',        'sitemetadata.entry_access', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
  (gen_random_uuid(), 'sitemetadata.entry_access.pay_at_door', 'sitemetadata.entry_access', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
  (gen_random_uuid(), 'sitemetadata.entry_access.online',     'sitemetadata.entry_access', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false);

-- events.entry_access (varchar) -> events.entry_access_option_id (FK), backfilling any existing rows.
ALTER TABLE events
  ADD COLUMN entry_access_option_id uuid;

UPDATE events e
   SET entry_access_option_id = o.id
  FROM entry_access_option o
 WHERE o.string_code = 'sitemetadata.entry_access.' || lower(e.entry_access);

ALTER TABLE events
  ALTER COLUMN entry_access_option_id SET NOT NULL;

ALTER TABLE events
  ADD CONSTRAINT fk_events_entry_access_option
  FOREIGN KEY (entry_access_option_id) REFERENCES entry_access_option (id);

-- The "ticket_url only for ONLINE" rule referenced the old column and cannot be expressed against another
-- table: it is enforced by the Event entity validation (ck_events_ticket_url_not_blank stays).
ALTER TABLE events
  DROP CONSTRAINT ck_events_ticket_url;

ALTER TABLE events
  DROP CONSTRAINT ck_events_entry_access;

ALTER TABLE events
  DROP COLUMN entry_access;
