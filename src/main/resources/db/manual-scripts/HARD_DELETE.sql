-- ⚠️ DEV/TEST ONLY
-- Hard reset TOTAL de objetos app en schema public para re-ejecutar todas las migraciones Flyway.
--
-- Qué hace:
-- 1) Elimina vistas/materialized views/tablas/secuencias del schema public (incluye flyway_schema_history).
-- 2) NO elimina el schema public ni extensiones (ej: pgcrypto), para evitar problemas de permisos.
--
-- Después de ejecutar este script:
-- - Levantá el backend.
-- - Flyway recreará all desde V1..Vn automáticamente.

BEGIN;

SET search_path = public;

DO $$
DECLARE
  obj RECORD;
BEGIN
  -- 1) Views
  FOR obj IN
    SELECT schemaname, viewname
    FROM pg_views
    WHERE schemaname = 'public'
  LOOP
    EXECUTE format('DROP VIEW IF EXISTS %I.%I CASCADE', obj.schemaname, obj.viewname);
  END LOOP;

  -- 2) Materialized views
  FOR obj IN
    SELECT schemaname, matviewname
    FROM pg_matviews
    WHERE schemaname = 'public'
  LOOP
    EXECUTE format('DROP MATERIALIZED VIEW IF EXISTS %I.%I CASCADE', obj.schemaname, obj.matviewname);
  END LOOP;

  -- 3) Tables (incluye flyway_schema_history)
  FOR obj IN
    SELECT schemaname, tablename
    FROM pg_tables
    WHERE schemaname = 'public'
  LOOP
    EXECUTE format('DROP TABLE IF EXISTS %I.%I CASCADE', obj.schemaname, obj.tablename);
  END LOOP;

  -- 4) Sequences sueltas
  FOR obj IN
    SELECT sequence_schema, sequence_name
    FROM information_schema.sequences
    WHERE sequence_schema = 'public'
  LOOP
    EXECUTE format('DROP SEQUENCE IF EXISTS %I.%I CASCADE', obj.sequence_schema, obj.sequence_name);
  END LOOP;
END
$$;

COMMIT;
