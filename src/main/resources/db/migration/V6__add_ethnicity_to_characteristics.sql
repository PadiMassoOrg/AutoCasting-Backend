-- V6__add_ethnicity_to_characteristics.sql
-- Agrega la columna ethnicity_id a talent_characteristics
-- y la relaciona con ethnicity_option

-- 1) Agregar la columna (nullable)
ALTER TABLE talent_characteristics
  ADD COLUMN IF NOT EXISTS ethnicity_id uuid NULL;

-- 2) Foreign key hacia ethnicity_option
-- (Postgres NO soporta "ADD CONSTRAINT IF NOT EXISTS")
DO $$
BEGIN
  -- Comprueba si ya existe una constraint con ese nombre
  IF NOT EXISTS (
    SELECT 1
    FROM pg_constraint
    WHERE conname = 'fk_talent_characteristics__ethnicity'
  ) THEN
    ALTER TABLE talent_characteristics
      ADD CONSTRAINT fk_talent_characteristics__ethnicity
        FOREIGN KEY (ethnicity_id) REFERENCES ethnicity_option(id);
  END IF;
END;
$$;

-- 3) Índice para mejorar filtros por etnia
CREATE INDEX IF NOT EXISTS idx_talent_characteristics_ethnicity
  ON talent_characteristics (ethnicity_id);
