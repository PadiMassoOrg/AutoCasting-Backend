-- Normalize historical currency codes in billing tables and avoid bpchar behavior.
-- Goal: always persist ISO-4217 string codes as varchar(3), e.g. USD / ARS.

-- 1) Data cleanup (idempotent)
UPDATE billable_item_price
SET currency_code = 'USD'
WHERE upper(trim(currency_code)) = 'US';

UPDATE billable_item_price
SET currency_code = 'ARS'
WHERE upper(trim(currency_code)) = 'AR';

UPDATE billable_item_price
SET currency_code = upper(trim(currency_code))
WHERE currency_code IS NOT NULL
  AND currency_code <> upper(trim(currency_code));

UPDATE billing_discount
SET currency_code = 'USD'
WHERE currency_code IS NOT NULL
  AND upper(trim(currency_code)) = 'US';

UPDATE billing_discount
SET currency_code = 'ARS'
WHERE currency_code IS NOT NULL
  AND upper(trim(currency_code)) = 'AR';

UPDATE billing_discount
SET currency_code = upper(trim(currency_code))
WHERE currency_code IS NOT NULL
  AND currency_code <> upper(trim(currency_code));

-- 2) Switch from char(3) to varchar(3)
ALTER TABLE billable_item_price
    ALTER COLUMN currency_code TYPE varchar(3)
    USING upper(trim(currency_code));

ALTER TABLE billing_discount
    ALTER COLUMN currency_code TYPE varchar(3)
    USING CASE
        WHEN currency_code IS NULL THEN NULL
        ELSE upper(trim(currency_code))
    END;

-- 3) Tighten checks to enforce ISO 3-letter uppercase representation
ALTER TABLE billable_item_price
    DROP CONSTRAINT IF EXISTS chk_billable_item_price_currency_upper;

ALTER TABLE billable_item_price
    ADD CONSTRAINT chk_billable_item_price_currency_iso3
        CHECK (currency_code ~ '^[A-Z]{3}$');

ALTER TABLE billing_discount
    DROP CONSTRAINT IF EXISTS chk_billing_discount_currency_upper;

ALTER TABLE billing_discount
    ADD CONSTRAINT chk_billing_discount_currency_iso3
        CHECK (currency_code IS NULL OR currency_code ~ '^[A-Z]{3}$');
