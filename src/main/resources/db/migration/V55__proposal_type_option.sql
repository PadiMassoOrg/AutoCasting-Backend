-- Proposal types become sitemetadata (DB-driven options, like project_type_option) instead of a
-- hardcoded varchar/enum: proposals.type -> proposals.proposal_type_option_id.

-- Tipo de proposal (casting, a futuro evento, etc.)
CREATE TABLE proposal_type_option (
  id                   uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  string_code          varchar(255) NOT NULL,
  category_string_code varchar(255) NOT NULL,
  created_at           timestamp NOT NULL DEFAULT now(),
  created_by           varchar(255) NOT NULL DEFAULT 'SYSTEM',
  modified_at          timestamp NOT NULL DEFAULT now(),
  modified_by          varchar(255) NOT NULL DEFAULT 'SYSTEM',
  deleted              boolean NOT NULL DEFAULT false
);

CREATE UNIQUE INDEX ux_proposal_type_option_string_code
  ON proposal_type_option (string_code);

CREATE INDEX idx_proposal_type_option_category
  ON proposal_type_option (category_string_code);

INSERT INTO proposal_type_option (
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
  (gen_random_uuid(), 'sitemetadata.proposal_type.casting', 'sitemetadata.proposal_type', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false);

-- proposals.type (varchar) -> proposals.proposal_type_option_id (FK), backfilling any existing rows.
ALTER TABLE proposals
  ADD COLUMN proposal_type_option_id uuid;

UPDATE proposals p
   SET proposal_type_option_id = o.id
  FROM proposal_type_option o
 WHERE o.string_code = 'sitemetadata.proposal_type.' || lower(p.type);

DROP INDEX IF EXISTS idx_proposals_status_type;

ALTER TABLE proposals
  ALTER COLUMN proposal_type_option_id SET NOT NULL,
  ADD CONSTRAINT fk_proposals_proposal_type_option
    FOREIGN KEY (proposal_type_option_id) REFERENCES proposal_type_option (id),
  DROP COLUMN type;

CREATE INDEX idx_proposals_status_type_option ON proposals (status, proposal_type_option_id);
