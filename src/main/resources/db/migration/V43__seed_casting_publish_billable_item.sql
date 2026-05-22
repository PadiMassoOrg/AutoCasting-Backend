-- ============================================================
-- Billing Module - Seed default catalog item for casting publish
-- Creates/updates CASTING_PUBLISH and its base price (USD 1.00)
-- with open-ended validity (valid_to = NULL).
-- ============================================================

INSERT INTO billable_item (code, string_code, description, billing_type, active)
VALUES (
    'CASTING_PUBLISH',
    'billing.item.casting_publish',
    'Cobro por publicación de un casting en el catálogo de AutoCasting.',
    'ONE_TIME',
    true
)
ON CONFLICT (code) DO UPDATE
SET string_code = EXCLUDED.string_code,
    description = EXCLUDED.description,
    billing_type = EXCLUDED.billing_type,
    active = true,
    deleted = false,
    modified_at = now(),
    modified_by = 'SYSTEM';

INSERT INTO billable_item_audience (billable_item_id, audience)
SELECT id, 'EMPLOYER'
FROM billable_item
WHERE code = 'CASTING_PUBLISH'
ON CONFLICT (billable_item_id, audience) DO NOTHING;

WITH item AS (
    SELECT id
    FROM billable_item
    WHERE code = 'CASTING_PUBLISH'
)
UPDATE billable_item_price p
SET amount_minor = 100,
    active = true,
    deleted = false,
    valid_to = NULL,
    modified_at = now(),
    modified_by = 'SYSTEM'
FROM item i
WHERE p.billable_item_id = i.id
  AND p.currency_code = 'USD'
  AND p.valid_to IS NULL;

INSERT INTO billable_item_price (billable_item_id, currency_code, amount_minor, valid_from, valid_to, active)
SELECT i.id, 'USD', 100, now(), NULL, true
FROM billable_item i
WHERE i.code = 'CASTING_PUBLISH'
  AND NOT EXISTS (
      SELECT 1
      FROM billable_item_price p
      WHERE p.billable_item_id = i.id
        AND p.currency_code = 'USD'
        AND p.deleted = false
        AND p.valid_to IS NULL
  );
