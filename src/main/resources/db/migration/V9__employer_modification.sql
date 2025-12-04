-- 1) Nuevo campo para CUIT / NIF / etc.
ALTER TABLE employer_basic_info
    ADD COLUMN tax_number varchar(255);

-- 2) Renombrar email -> company_email
ALTER TABLE employer_basic_info
    RENAME COLUMN email TO company_email;

-- 3) Renombrar website -> website_url
ALTER TABLE employer_basic_info
    RENAME COLUMN website TO website_url;

-- 4) Vaciar los valores actuales (que eran el email del user)
UPDATE employer_basic_info
SET company_email = NULL;
