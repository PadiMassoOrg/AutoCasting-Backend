-- 8) PAY RATE TYPE
INSERT INTO pay_rate_type_option (id,
                                  string_code,
                                  category_string_code,
                                  created_at,
                                  created_by,
                                  modified_at,
                                  modified_by,
                                  deleted)
VALUES (gen_random_uuid(), 'sitemetadata.pay_rate_type.collaborative', 'sitemetadata.pay_rate_type', NOW(), 'SYSTEM',
        NOW(), 'SYSTEM', false);
