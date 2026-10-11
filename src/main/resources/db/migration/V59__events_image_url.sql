-- Events (AI-92): every event has a mandatory image (placeholder or user upload).
-- Rows created before this migration only exist in dev/demo data (the module is not released yet);
-- they are backfilled with a demo image so the column can be NOT NULL.

ALTER TABLE events
  ADD COLUMN image_url text NULL;

UPDATE events
   SET image_url = 'https://savubzaypraqdehudynb.supabase.co/storage/v1/object/public/profile-media-public/autocasting/A4351A42-DC7E-4B25-817E-5E9516C5711E_1_105_c.jpeg'
 WHERE image_url IS NULL;

ALTER TABLE events
  ALTER COLUMN image_url SET NOT NULL;

ALTER TABLE events
  ADD CONSTRAINT ck_events_image_url_not_blank CHECK (btrim(image_url) <> '');
