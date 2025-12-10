-- VXX__add_default_code_to_casting.sql

-- 1) Agregar columna default_code (slug técnico/public code) a casting
ALTER TABLE casting
  ADD COLUMN default_code varchar(255);

-- 2) Backfill para registros existentes
--    Regla: "AC-C-" + primeras 8 letras del UUID en mayúsculas.
--    Así no rompes nada aunque ya tengas castings creados.
UPDATE casting
SET default_code = CONCAT('C-', UPPER(SUBSTRING(id::text FROM 1 FOR 8)))
WHERE default_code IS NULL;

-- 3) Marcarla como NOT NULL
ALTER TABLE casting
  ALTER COLUMN default_code SET NOT NULL;

-- 4) Índice único para que cada casting tenga un code distinto
CREATE UNIQUE INDEX ux_casting_default_code
  ON casting (default_code);
