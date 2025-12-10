
ALTER TABLE casting_basic_info
  ALTER COLUMN title DROP NOT NULL;

ALTER TABLE casting_role
  ALTER COLUMN role_name DROP NOT NULL;

UPDATE roles
SET
    code = 'TALENT',
    name_string_code = 'role.talent',
    description = 'Usuario que representa un talento'
WHERE code = 'ACTOR'
   OR name_string_code = 'role.actor';

UPDATE roles
SET
    code = 'EMPLOYER',
    name_string_code = 'role.employer',
    description = 'Usuario que busca y gestiona actores'
WHERE code = 'CASTINERA'
   OR name_string_code = 'role.castinera';
