-- V38__admin_user_blocking.sql
-- Primera entrega de User Management:
-- bloqueo reversible de cuenta separado del soft delete.

SET search_path = public;

ALTER TABLE public.users
    ADD COLUMN IF NOT EXISTS blocked boolean NOT NULL DEFAULT false,
    ADD COLUMN IF NOT EXISTS blocked_at timestamp NULL,
    ADD COLUMN IF NOT EXISTS blocked_by varchar(255) NULL;

COMMENT ON COLUMN public.users.blocked IS
    'Bloqueo administrativo reversible. Cuando es true la cuenta no debe autenticarse.';

COMMENT ON COLUMN public.users.blocked_at IS
    'Timestamp del último bloqueo administrativo.';

COMMENT ON COLUMN public.users.blocked_by IS
    'Identificador textual del actor que aplicó el último bloqueo administrativo.';
