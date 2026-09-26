-- Proposals (core): content prepared on behalf of a third party and handed over via a unique link.
-- Type-specific target FKs (e.g. casting_id) and the per-type CHECK are added by each type's migration.

CREATE TABLE proposals (
  id                   uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  type                 varchar(50)  NOT NULL,
  token                varchar(64)  NOT NULL,   -- 32 random bytes, base64url, stored in clear for "Copy link"
  status               varchar(20)  NOT NULL DEFAULT 'PENDING',

  -- Internal reference (never exposed publicly)
  contact_name         varchar(255),
  contact_email        varchar(255),
  contact_whatsapp     varchar(50),
  company_name_hint    varchar(255),
  internal_notes       text,

  -- Tracking
  first_opened_at      timestamp,
  claimed_by_user_id   uuid,
  claimed_at           timestamp,

  created_at           timestamp NOT NULL DEFAULT now(),
  created_by           varchar(255) NOT NULL DEFAULT 'SYSTEM',
  modified_at          timestamp NOT NULL DEFAULT now(),
  modified_by          varchar(255) NOT NULL DEFAULT 'SYSTEM',
  deleted              boolean NOT NULL DEFAULT false,

  CONSTRAINT uk_proposals_token UNIQUE (token),
  CONSTRAINT chk_proposals_status CHECK (status IN ('PENDING', 'CLAIMED', 'REVOKED')),
  CONSTRAINT fk_proposals_claimed_by_user
    FOREIGN KEY (claimed_by_user_id) REFERENCES users (id)
);

CREATE INDEX idx_proposals_status_type ON proposals (status, type);

-- Users who started the flow with a proposal's link (created an account / are completing the requirement).
-- Composite PK so two people opening the same link never overwrite each other.
CREATE TABLE proposal_attachments (
  proposal_id          uuid NOT NULL,
  user_id              uuid NOT NULL,
  attached_at          timestamp NOT NULL DEFAULT now(),

  CONSTRAINT pk_proposal_attachments PRIMARY KEY (proposal_id, user_id),
  CONSTRAINT fk_proposal_attachments_proposal
    FOREIGN KEY (proposal_id) REFERENCES proposals (id) ON DELETE CASCADE,
  CONSTRAINT fk_proposal_attachments_user
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_proposal_attachments_user_id ON proposal_attachments (user_id);
