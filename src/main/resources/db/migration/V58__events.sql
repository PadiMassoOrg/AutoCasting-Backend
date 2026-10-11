-- Events module (AI-92), first slice: public catalog of events owned by a user (any mode).
-- Event type and image are intentionally absent; they are added by later migrations.

CREATE TABLE events (
  id           uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id      uuid NOT NULL REFERENCES users (id),
  slug         varchar(32) NOT NULL,
  name         varchar(120) NOT NULL,
  entry_access varchar(20) NOT NULL,
  ticket_url   text NULL,
  start_date   date NOT NULL,
  end_date     date NOT NULL,
  dates_info   varchar(500) NOT NULL,
  location     varchar(255) NULL,
  description  text NULL,
  created_at   timestamp NULL DEFAULT now(),
  created_by   varchar(255) NULL,
  modified_at  timestamp NULL DEFAULT now(),
  modified_by  varchar(255) NULL,
  deleted      boolean NOT NULL DEFAULT false,
  CONSTRAINT ck_events_entry_access CHECK (entry_access IN ('FREE', 'PAY_AT_DOOR', 'ONLINE')),
  CONSTRAINT ck_events_date_range CHECK (end_date >= start_date),
  CONSTRAINT ck_events_ticket_url CHECK ((entry_access = 'ONLINE') = (ticket_url IS NOT NULL))
);

CREATE UNIQUE INDEX ux_events_slug
  ON events (slug);

CREATE INDEX idx_events_user_id
  ON events (user_id);

CREATE INDEX idx_events_dates
  ON events (end_date, start_date)
  WHERE deleted = false;
