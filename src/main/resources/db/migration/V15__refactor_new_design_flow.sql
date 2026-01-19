-- VXX__tune_casting_role_subentities.sql
-- Ajustes de sub-entidades de Roles según nuevo flujo de UX

-- ===============================================
-- 1) CASTING_ROLE: eliminar flag is_complete obsoleto
--    (todos los campos core son NOT NULL, el rol ya nace "completo")
-- ===============================================
ALTER TABLE casting_role
DROP COLUMN IF EXISTS is_complete;


-- ===============================================
-- 2) CASTING_REQUIREMENT: eliminar is_complete
--    (el requirement se crea desde un modal con campos requeridos)
-- ===============================================
ALTER TABLE casting_requirement
DROP COLUMN IF EXISTS is_complete;


-- Reafirmamos que un requirement siempre pertenece a un Role concreto
-- (esto ya debería estar hecho en migraciones previas, pero lo dejamos
-- idempotente por si acaso)
ALTER TABLE casting_requirement
    ALTER COLUMN casting_role_id SET NOT NULL;


-- ===============================================
-- 3) CASTING_ROLE_REMUNERATION: dejar claro el modelo "No remunerado"
--    a nivel de defaults (sub-entidad de Role, 1:1 con casting_role)
-- ===============================================
ALTER TABLE casting_role_remuneration
    ALTER COLUMN is_unpaid SET NOT NULL,
ALTER COLUMN is_unpaid SET DEFAULT true,
  ALTER COLUMN is_complete SET NOT NULL,
  ALTER COLUMN is_complete SET DEFAULT false;

-- Nota de dominio (no DDL):
-- - is_unpaid = true  => dropdown "No remunerado" para ese Role.
-- - is_unpaid = false => se espera pay_rate_type_option_id, currency_option_id y amount.
-- - is_complete se utilizará a nivel de servicio para saber si la remuneración
--   del Role está lista para publicar el casting.
