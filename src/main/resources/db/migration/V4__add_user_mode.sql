-- Users nuevos attr
ALTER TABLE public.users
  ADD COLUMN active_mode varchar(32),
  ADD COLUMN talent_onboarding_status varchar(32) NOT NULL DEFAULT 'NOT_STARTED',
  ADD COLUMN employer_onboarding_status varchar(32) NOT NULL DEFAULT 'NOT_STARTED';

-- Pasa de users.role_id (ManyToOne) a tabla de unión user_roles (ManyToMany)
-- 1) Crear tabla de unión
CREATE TABLE public.user_roles (
user_id uuid NOT NULL,
role_id uuid NOT NULL,
CONSTRAINT user_roles_pkey PRIMARY KEY (user_id, role_id),
CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES public.users(id),
CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES public.roles(id)
);

-- 2) Volcar datos existentes de users.role_id a la tabla de unión
INSERT INTO public.user_roles (user_id, role_id)
SELECT id, role_id
FROM public.users
WHERE role_id IS NOT NULL;

-- 3) Eliminar la FK antigua y la columna role_id
ALTER TABLE public.users DROP CONSTRAINT fkp56c1712k691lhsyewcssf40f;
ALTER TABLE public.users DROP COLUMN role_id;
