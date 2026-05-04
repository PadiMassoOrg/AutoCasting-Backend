-- V37__admin_notes_foundation.sql
-- Base inicial para notas internas del panel admin.
-- Objetivo:
--   - Permitir contexto operativo reusable entre dominios.
--   - Adjuntar notas a cualquier entidad de negocio sin acoplar el esquema a un solo módulo.
--   - Mantener autoría, timestamps y soft delete desde el inicio.
--   - Soportar histórico consolidado por user sin joins complejos.
--   - Dejar espacio para casuísticas futuras sin multiplicar migraciones tempranas.

SET search_path = public;

CREATE TABLE public.admin_note (
    id               uuid PRIMARY KEY      DEFAULT gen_random_uuid(),
    entity_type      varchar(64)  NOT NULL,
    entity_id        uuid         NOT NULL,
    related_user_id  uuid         NULL,
    author_user_id   uuid         NOT NULL,
    note_type        varchar(32)  NOT NULL DEFAULT 'GENERAL',
    source           varchar(32)  NOT NULL DEFAULT 'ADMIN',
    title            varchar(255) NULL,
    body             text         NOT NULL,
    metadata         jsonb        NOT NULL DEFAULT '{}'::jsonb,
    pinned           boolean      NOT NULL DEFAULT false,
    created_at       timestamp    NOT NULL DEFAULT now(),
    created_by       varchar(255) NOT NULL DEFAULT 'SYSTEM',
    modified_at      timestamp    NOT NULL DEFAULT now(),
    modified_by      varchar(255) NOT NULL DEFAULT 'SYSTEM',
    deleted          boolean      NOT NULL DEFAULT false,

    CONSTRAINT fk_admin_note_author_user
        FOREIGN KEY (author_user_id) REFERENCES public.users(id),

    CONSTRAINT fk_admin_note_related_user
        FOREIGN KEY (related_user_id) REFERENCES public.users(id),

    CONSTRAINT chk_admin_note_body_not_blank
        CHECK (length(btrim(body)) > 0),

    CONSTRAINT chk_admin_note_type
        CHECK (note_type IN ('GENERAL', 'SUPPORT', 'MANUAL_CHANGE', 'WARNING', 'FOLLOW_UP')),

    CONSTRAINT chk_admin_note_source
        CHECK (source IN ('ADMIN', 'SYSTEM'))
);

-- Índice principal de lectura por entidad.
CREATE INDEX IF NOT EXISTS idx_admin_note_entity_lookup
    ON public.admin_note (entity_type, entity_id, deleted, pinned, created_at DESC);

-- Índice para histórico consolidado por user.
CREATE INDEX IF NOT EXISTS idx_admin_note_related_user_lookup
    ON public.admin_note (related_user_id, deleted, pinned, created_at DESC);

-- Índice auxiliar para revisar notas por autor.
CREATE INDEX IF NOT EXISTS idx_admin_note_author_user
    ON public.admin_note (author_user_id, deleted, created_at DESC);

-- Índice útil para feeds o filtros por tipo.
CREATE INDEX IF NOT EXISTS idx_admin_note_type_lookup
    ON public.admin_note (note_type, deleted, created_at DESC);

COMMENT ON TABLE public.admin_note IS
    'Notas internas administrativas reutilizables entre users, talent, employers, castings y otras entidades.';

COMMENT ON COLUMN public.admin_note.entity_type IS
    'Tipo lógico de entidad objetivo. Ejemplos esperados: USER, TALENT_PROFILE, EMPLOYER_PROFILE, CASTING, APPLICATION.';

COMMENT ON COLUMN public.admin_note.entity_id IS
    'UUID de la entidad objetivo dentro del dominio indicado por entity_type.';

COMMENT ON COLUMN public.admin_note.related_user_id IS
    'User raíz relacionado con la nota. Facilita histórico consolidado por user incluso si la note vive sobre casting, application o perfil.';

COMMENT ON COLUMN public.admin_note.author_user_id IS
    'Usuario administrador autor de la nota.';

COMMENT ON COLUMN public.admin_note.note_type IS
    'Clasificación operativa simple de la nota.';

COMMENT ON COLUMN public.admin_note.source IS
    'Origen de la nota. ADMIN para carga manual; SYSTEM para futuras notas automáticas.';

COMMENT ON COLUMN public.admin_note.title IS
    'Título corto opcional para escaneo rápido en timelines o listados.';

COMMENT ON COLUMN public.admin_note.body IS
    'Contenido libre de la nota interna.';

COMMENT ON COLUMN public.admin_note.metadata IS
    'JSON opcional para contexto estructurado futuro: canal de soporte, before/after, motivo, ids relacionados, etc.';

COMMENT ON COLUMN public.admin_note.pinned IS
    'Permite destacar notas operativamente importantes dentro de una entidad.';
