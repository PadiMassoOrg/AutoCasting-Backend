-- VXX__update_casting_schema_new_flow.sql
-- ===============================================
-- Actualización del dominio de Castings para nuevo flujo de UX
-- ===============================================


-- ===============================================
-- 1) Reforzar requisitos de CASTING_ROLE
--    Todos los campos core NOT NULL, excepto description
-- ===============================================

ALTER TABLE casting_role
  ALTER COLUMN role_name SET NOT NULL,
  ALTER COLUMN role_type_option_id SET NOT NULL,
  ALTER COLUMN gender_option_id SET NOT NULL,
  ALTER COLUMN age_min SET NOT NULL,
  ALTER COLUMN age_max SET NOT NULL;


-- ===============================================
-- 2) Renombrar sección de ACTING -> REQUIREMENTS SECTION
-- ===============================================

-- 2.1) Renombrar tabla principal de sección
ALTER TABLE casting_acting
  RENAME TO casting_requirements_section;

-- 2.2) Eliminar referencia a acting_mode (ya no se usa)
ALTER TABLE casting_requirements_section
  DROP CONSTRAINT fk_casting_acting_mode_option,
  DROP COLUMN casting_acting_mode_option_id;


-- ===============================================
-- 3) Transformar CASTING_ACTING_REQUIREMENT -> CASTING_REQUIREMENT
--    y adaptar a nuevo modelo (role + audio/video + description)
-- ===============================================

-- 3.1) Renombrar tabla de requisitos
ALTER TABLE casting_acting_requirement
  RENAME TO casting_requirement;

-- 3.2) Renombrar FK hacia la sección de requerimientos
ALTER TABLE casting_requirement
  RENAME COLUMN casting_acting_id TO casting_requirements_section_id;

-- 3.3) Un requirement siempre referencia a un rol concreto
ALTER TABLE casting_requirement
  ALTER COLUMN casting_role_id SET NOT NULL;

-- 3.4) Nuevo modelo de media requerida: audio / video (checkboxes en UI)
ALTER TABLE casting_requirement
  ADD COLUMN requires_audio boolean NOT NULL DEFAULT false,
  ADD COLUMN requires_video boolean NOT NULL DEFAULT false;

-- 3.5) Ya no se usa slots_count en el nuevo flujo
ALTER TABLE casting_requirement
  DROP COLUMN slots_count;


-- ===============================================
-- 4) Eliminar tabla de CASTING_ACTING_MODE_OPTION (ya no existe concepto de Acting Mode)
-- ===============================================

DROP TABLE IF EXISTS casting_acting_mode_option;


-- ===============================================
-- 5) Ajustar SITEMETADATA de COMPENSATION TYPE
--    Mantener solo: paid / collaborative
-- ===============================================

-- Aseguramos que las dos opciones “oficiales” queden activas
UPDATE casting_compensation_type_option
SET deleted = false
WHERE string_code IN (
  'sitemetadata.compensation_type.paid',
  'sitemetadata.compensation_type.collaborative'
);

-- Marcamos como eliminadas (soft delete) todas las demás opciones
UPDATE casting_compensation_type_option
SET deleted = true
WHERE string_code NOT IN (
  'sitemetadata.compensation_type.paid',
  'sitemetadata.compensation_type.collaborative'
);


-- ===============================================
-- 6) Ajustar CASTING_ROLE_REMUNERATION para soportar "No remunerado"
-- ===============================================

-- Nuevo flag para representar explícitamente el estado “No remunerado”
-- Por defecto TRUE para todos los roles nuevos/actuales
ALTER TABLE casting_role_remuneration
  ADD COLUMN is_unpaid boolean NOT NULL DEFAULT true;

-- Nota de dominio (no DDL):
-- - Cuando is_unpaid = true => dropdown muestra "No remunerado"
--   y normalmente amount/currency/pay_rate_type se dejan NULL.
-- - Cuando se cambie el dropdown a una opción pagada =>
--   el backend puede poner is_unpaid = false y rellenar
--   pay_rate_type_option_id, currency_option_id y amount.
--   La validación de consistencia se maneja a nivel de servicio/dominio
--   (ej. al publicar el casting o marcar la sección como completa).
