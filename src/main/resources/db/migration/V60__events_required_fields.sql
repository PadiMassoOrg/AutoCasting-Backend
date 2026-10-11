-- Events (AI-92): required fields. name, entry_access, start_date, end_date and location are mandatory;
-- ticket_url is mandatory only for ONLINE access (already enforced by ck_events_ticket_url);
-- dates_info is optional.

ALTER TABLE events
  ALTER COLUMN dates_info DROP NOT NULL;

-- Rows created before this migration only exist in dev/demo data (the module is not released yet).
UPDATE events
   SET location = 'Ubicación a confirmar'
 WHERE location IS NULL OR btrim(location) = '';

ALTER TABLE events
  ALTER COLUMN location SET NOT NULL;

ALTER TABLE events
  ADD CONSTRAINT ck_events_name_not_blank CHECK (btrim(name) <> '');

ALTER TABLE events
  ADD CONSTRAINT ck_events_location_not_blank CHECK (btrim(location) <> '');

ALTER TABLE events
  ADD CONSTRAINT ck_events_ticket_url_not_blank CHECK (ticket_url IS NULL OR btrim(ticket_url) <> '');
