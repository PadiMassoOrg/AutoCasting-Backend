-- Log of every email of a tracked type sent to a user, so the admin can see who received which email and
-- when, and bulk sends can skip recent recipients. Plus the per-user unsubscribe token and opt-out date for
-- the marketing (admin bulk) emails.

CREATE TABLE email_deliveries (
  id          uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id     uuid NOT NULL,
  email_type  varchar(64) NOT NULL,
  sent_at     timestamp NOT NULL,
  created_at  timestamp NULL DEFAULT now(),
  created_by  varchar(255) NULL,
  modified_at timestamp NULL DEFAULT now(),
  modified_by varchar(255) NULL,
  deleted     boolean NOT NULL DEFAULT false,
  CONSTRAINT fk_email_deliveries_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_email_deliveries_user_type_sent_at
  ON email_deliveries (user_id, email_type, sent_at DESC);

ALTER TABLE users
  ADD COLUMN IF NOT EXISTS email_unsubscribe_token varchar(64) NULL,
  ADD COLUMN IF NOT EXISTS marketing_emails_unsubscribed_at timestamp NULL;

CREATE UNIQUE INDEX ux_users_email_unsubscribe_token
  ON users (email_unsubscribe_token);
