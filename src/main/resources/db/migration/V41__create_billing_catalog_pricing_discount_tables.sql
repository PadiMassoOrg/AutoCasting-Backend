-- ============================================================
-- Billing Module - Catalog + Pricing + Discounts (DDL only)
-- No inserts in this migration.
-- ============================================================

CREATE TABLE IF NOT EXISTS billable_item
(
    id               uuid PRIMARY KEY      DEFAULT gen_random_uuid(),
    code             varchar(100) NOT NULL,
    string_code      varchar(255) NOT NULL,
    description      text,

    audience         varchar(32)  NOT NULL,
    billing_type     varchar(32)  NOT NULL DEFAULT 'ONE_TIME',
    active           boolean      NOT NULL DEFAULT true,

    created_at       timestamp    NOT NULL DEFAULT now(),
    created_by       varchar(255) NOT NULL DEFAULT 'SYSTEM',
    modified_at      timestamp    NOT NULL DEFAULT now(),
    modified_by      varchar(255) NOT NULL DEFAULT 'SYSTEM',
    deleted          boolean      NOT NULL DEFAULT false,

    CONSTRAINT uq_billable_item_code UNIQUE (code),
    CONSTRAINT chk_billable_item_audience
        CHECK (audience IN ('TALENT', 'EMPLOYER')),
    CONSTRAINT chk_billable_item_billing_type
        CHECK (billing_type IN ('ONE_TIME', 'RECURRING'))
);

CREATE INDEX IF NOT EXISTS idx_billable_item_active
    ON billable_item (active);

CREATE INDEX IF NOT EXISTS idx_billable_item_deleted
    ON billable_item (deleted);

CREATE INDEX IF NOT EXISTS idx_billable_item_audience
    ON billable_item (audience);


CREATE TABLE IF NOT EXISTS billable_item_price
(
    id               uuid PRIMARY KEY      DEFAULT gen_random_uuid(),
    billable_item_id uuid         NOT NULL,
    currency_code    char(3)      NOT NULL,

    amount_minor     bigint       NOT NULL,
    valid_from       timestamptz  NOT NULL,
    valid_to         timestamptz,
    active           boolean      NOT NULL DEFAULT true,

    created_at       timestamp    NOT NULL DEFAULT now(),
    created_by       varchar(255) NOT NULL DEFAULT 'SYSTEM',
    modified_at      timestamp    NOT NULL DEFAULT now(),
    modified_by      varchar(255) NOT NULL DEFAULT 'SYSTEM',
    deleted          boolean      NOT NULL DEFAULT false,

    CONSTRAINT fk_billable_item_price_item
        FOREIGN KEY (billable_item_id) REFERENCES billable_item (id),

    CONSTRAINT uq_billable_item_price_version
        UNIQUE (billable_item_id, currency_code, valid_from),

    CONSTRAINT chk_billable_item_price_amount_non_negative
        CHECK (amount_minor >= 0),

    CONSTRAINT chk_billable_item_price_validity
        CHECK (valid_to IS NULL OR valid_to > valid_from),

    CONSTRAINT chk_billable_item_price_currency_upper
        CHECK (currency_code = upper(currency_code))
);

CREATE INDEX IF NOT EXISTS idx_billable_item_price_item
    ON billable_item_price (billable_item_id);

CREATE INDEX IF NOT EXISTS idx_billable_item_price_item_valid_from
    ON billable_item_price (billable_item_id, valid_from DESC);

CREATE INDEX IF NOT EXISTS idx_billable_item_price_active
    ON billable_item_price (active);

CREATE INDEX IF NOT EXISTS idx_billable_item_price_deleted
    ON billable_item_price (deleted);


CREATE TABLE IF NOT EXISTS billing_discount
(
    id               uuid PRIMARY KEY      DEFAULT gen_random_uuid(),
    code             varchar(100) NOT NULL,
    string_code      varchar(255) NOT NULL,
    description      text,

    discount_type    varchar(32)  NOT NULL,
    percentage_bps   integer,
    amount_minor     bigint,
    currency_code    char(3),

    stackable        boolean      NOT NULL DEFAULT false,
    active           boolean      NOT NULL DEFAULT true,
    starts_at        timestamptz,
    ends_at          timestamptz,

    created_at       timestamp    NOT NULL DEFAULT now(),
    created_by       varchar(255) NOT NULL DEFAULT 'SYSTEM',
    modified_at      timestamp    NOT NULL DEFAULT now(),
    modified_by      varchar(255) NOT NULL DEFAULT 'SYSTEM',
    deleted          boolean      NOT NULL DEFAULT false,

    CONSTRAINT uq_billing_discount_code UNIQUE (code),

    CONSTRAINT chk_billing_discount_type
        CHECK (discount_type IN ('PERCENTAGE', 'FIXED_AMOUNT')),

    CONSTRAINT chk_billing_discount_validity
        CHECK (ends_at IS NULL OR starts_at IS NULL OR ends_at > starts_at),

    CONSTRAINT chk_billing_discount_currency_upper
        CHECK (currency_code IS NULL OR currency_code = upper(currency_code)),

    CONSTRAINT chk_billing_discount_value_shape
        CHECK (
            (
                discount_type = 'PERCENTAGE'
                AND percentage_bps IS NOT NULL
                AND percentage_bps > 0
                AND percentage_bps <= 10000
                AND amount_minor IS NULL
                AND currency_code IS NULL
            )
            OR
            (
                discount_type = 'FIXED_AMOUNT'
                AND amount_minor IS NOT NULL
                AND amount_minor >= 0
                AND currency_code IS NOT NULL
                AND percentage_bps IS NULL
            )
        )
);

CREATE INDEX IF NOT EXISTS idx_billing_discount_active
    ON billing_discount (active);

CREATE INDEX IF NOT EXISTS idx_billing_discount_deleted
    ON billing_discount (deleted);

CREATE INDEX IF NOT EXISTS idx_billing_discount_dates
    ON billing_discount (starts_at, ends_at);


CREATE TABLE IF NOT EXISTS billable_item_discount
(
    id                  uuid PRIMARY KEY      DEFAULT gen_random_uuid(),
    billable_item_id    uuid         NOT NULL,
    billing_discount_id uuid         NOT NULL,

    valid_from          timestamptz  NOT NULL,
    valid_to            timestamptz,
    priority            smallint     NOT NULL DEFAULT 100,
    active              boolean      NOT NULL DEFAULT true,

    created_at          timestamp    NOT NULL DEFAULT now(),
    created_by          varchar(255) NOT NULL DEFAULT 'SYSTEM',
    modified_at         timestamp    NOT NULL DEFAULT now(),
    modified_by         varchar(255) NOT NULL DEFAULT 'SYSTEM',
    deleted             boolean      NOT NULL DEFAULT false,

    CONSTRAINT fk_billable_item_discount_item
        FOREIGN KEY (billable_item_id) REFERENCES billable_item (id),

    CONSTRAINT fk_billable_item_discount_discount
        FOREIGN KEY (billing_discount_id) REFERENCES billing_discount (id),

    CONSTRAINT uq_billable_item_discount_window
        UNIQUE (billable_item_id, billing_discount_id, valid_from),

    CONSTRAINT chk_billable_item_discount_validity
        CHECK (valid_to IS NULL OR valid_to > valid_from),

    CONSTRAINT chk_billable_item_discount_priority
        CHECK (priority >= 0)
);

CREATE INDEX IF NOT EXISTS idx_billable_item_discount_item
    ON billable_item_discount (billable_item_id);

CREATE INDEX IF NOT EXISTS idx_billable_item_discount_discount
    ON billable_item_discount (billing_discount_id);

CREATE INDEX IF NOT EXISTS idx_billable_item_discount_active
    ON billable_item_discount (active);

CREATE INDEX IF NOT EXISTS idx_billable_item_discount_deleted
    ON billable_item_discount (deleted);
