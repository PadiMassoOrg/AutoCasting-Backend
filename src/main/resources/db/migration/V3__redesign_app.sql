-- V3__redesign_app.sql

-- Nullable stage_name
ALTER TABLE talent_basic_info
    ALTER COLUMN stage_name DROP NOT NULL;
