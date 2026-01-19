-- Vxxx__remove_is_unpaid_and_pay_same_for_all_roles.sql

-- 1) Insertar PayRateType "unpaid" (si no existe)
INSERT INTO pay_rate_type_option (id,
                                  string_code,
                                  category_string_code,
                                  created_at,
                                  created_by,
                                  modified_at,
                                  modified_by,
                                  deleted)
SELECT gen_random_uuid(),
       'sitemetadata.pay_rate_type.unpaid',
       'sitemetadata.pay_rate_type',
       NOW(),
       'SYSTEM',
       NOW(),
       'SYSTEM',
       false
WHERE NOT EXISTS (SELECT 1
                  FROM pay_rate_type_option
                  WHERE string_code = 'sitemetadata.pay_rate_type.unpaid'
                    AND deleted = false);

-- 2) Migrar: donde is_unpaid = true, setear pay_rate_type_option_id = (unpaid)
UPDATE casting_role_remuneration rr
SET pay_rate_type_option_id = (SELECT id
                               FROM pay_rate_type_option
                               WHERE string_code = 'sitemetadata.pay_rate_type.unpaid'
                                 AND deleted = false
                               LIMIT 1)
WHERE rr.is_unpaid = true;

-- 3) Normalizar: si pay_rate_type = unpaid -> currency/amount = null (opcional pero recomendado)
UPDATE casting_role_remuneration rr
SET currency_option_id = NULL,
    amount             = NULL
WHERE rr.pay_rate_type_option_id = (SELECT id
                                    FROM pay_rate_type_option
                                    WHERE string_code = 'sitemetadata.pay_rate_type.unpaid'
                                      AND deleted = false
                                    LIMIT 1);

-- 4) Recalcular is_complete para TODAS las rows (según tus reglas)
--    - unpaid => true
--    - no unpaid => (pay_rate_type != null AND currency != null AND amount != null)
UPDATE casting_role_remuneration rr
SET is_complete = CASE
                      WHEN rr.pay_rate_type_option_id = (SELECT id
                                                         FROM pay_rate_type_option
                                                         WHERE string_code = 'sitemetadata.pay_rate_type.unpaid'
                                                           AND deleted = false
                                                         LIMIT 1) THEN true
                      ELSE (
                          rr.pay_rate_type_option_id IS NOT NULL
                              AND rr.currency_option_id IS NOT NULL
                              AND rr.amount IS NOT NULL
                          )
    END;

-- 5) Dropear columnas ya obsoletas
ALTER TABLE casting_role_remuneration
    DROP COLUMN IF EXISTS is_unpaid;

ALTER TABLE casting_remuneration
    DROP COLUMN IF EXISTS pay_same_for_all_roles;
