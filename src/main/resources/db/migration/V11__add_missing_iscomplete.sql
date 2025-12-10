-- ===============================================
-- Add is_complete to casting_acting_requirement
-- ===============================================

ALTER TABLE casting_acting_requirement
  ADD COLUMN is_complete boolean NOT NULL DEFAULT false;


-- ===============================================
-- Add is_complete to casting_role_remuneration
-- ===============================================

ALTER TABLE casting_role_remuneration
  ADD COLUMN is_complete boolean NOT NULL DEFAULT false;
