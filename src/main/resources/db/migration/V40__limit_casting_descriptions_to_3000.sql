ALTER TABLE casting
    DROP CONSTRAINT IF EXISTS chk_casting_description_max_length,
    ADD CONSTRAINT chk_casting_description_max_length
        CHECK (description IS NULL OR char_length(description) <= 3000);

ALTER TABLE casting_role
    DROP CONSTRAINT IF EXISTS chk_casting_role_requirement_description_max_length,
    ADD CONSTRAINT chk_casting_role_requirement_description_max_length
        CHECK (requirement_description IS NULL OR char_length(requirement_description) <= 3000);
