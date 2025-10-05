-- =========================
-- V2__legal.sql
-- =========================

-- public.legal_documents definition

-- DROP TABLE legal_documents;

CREATE TABLE legal_documents (
    id uuid NOT NULL,
    type varchar(32) NOT NULL,
    locale varchar(8) NOT NULL,
    version varchar(32) NOT NULL,
    title text NOT NULL,
    slug text NOT NULL,
    status varchar(16) NOT NULL,
    format varchar(16) NOT NULL,
    body_template text NOT NULL,
    content_hash varchar(64) NOT NULL,
    effective_at timestamptz NOT NULL,
    published_at timestamptz NULL,
    created_at timestamp(6) NULL,
    created_by varchar(255) NULL,
    modified_at timestamp(6) NULL,
    modified_by varchar(255) NULL,
    deleted bool NOT NULL,
    CONSTRAINT legal_documents_pkey PRIMARY KEY (id),
    CONSTRAINT uq_doc UNIQUE (type, locale, version)
);

CREATE INDEX idx_doc_lookup
    ON legal_documents (type, locale, status, effective_at);


-- public.legal_acceptances definition

-- DROP TABLE legal_acceptances;
CREATE TABLE legal_acceptances (
    id uuid NOT NULL,
    user_id uuid NOT NULL,
    legal_document_id uuid NOT NULL,
    accepted_at timestamptz NOT NULL,
    ip varchar(255) NULL,                    -- << antes era inet
    user_agent varchar(512) NULL,            -- << antes era text
    content_hash varchar(64) NOT NULL,
    created_at timestamp(6) NULL,
    created_by varchar(255) NULL,
    modified_at timestamp(6) NULL,
    modified_by varchar(255) NULL,
    deleted bool NOT NULL,
    CONSTRAINT legal_acceptances_pkey PRIMARY KEY (id),
    CONSTRAINT fk_legal_acc_doc FOREIGN KEY (legal_document_id) REFERENCES legal_documents(id)
);

CREATE INDEX idx_acc_user ON legal_acceptances (user_id);
CREATE INDEX idx_acc_doc  ON legal_acceptances (legal_document_id);
