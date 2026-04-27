WITH target_user AS (
    SELECT id AS user_id FROM users WHERE email = 'asd1@asd.com'
), current_docs AS (
    SELECT id
    FROM (
             SELECT ld.id, ld.type,
                    row_number() OVER (PARTITION BY ld.type ORDER BY ld.effective_at DESC, ld.created_at DESC) rn
             FROM legal_documents ld
             WHERE ld.deleted = false
               AND ld.status = 'PUBLISHED'
               AND ld.locale = 'es'
               AND ld.type IN ('TERMS','PRIVACY')
               AND ld.effective_at <= NOW()
         ) t
    WHERE rn = 1
)
DELETE FROM legal_acceptances la
    USING target_user tu, current_docs cd
WHERE la.user_id = tu.user_id
  AND la.legal_document_id = cd.id;
