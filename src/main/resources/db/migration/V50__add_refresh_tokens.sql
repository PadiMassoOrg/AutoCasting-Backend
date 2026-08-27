CREATE TABLE public.refresh_tokens (
  id uuid NOT NULL,
  user_id uuid NOT NULL,
  token_hash varchar(255) NOT NULL,
  issued_at timestamp(6) NOT NULL,
  expires_at timestamp(6) NOT NULL,
  revoked_at timestamp(6) NULL,
  replaced_by_id uuid NULL,
  created_at timestamp(6) NULL,
  created_by varchar(255) NULL,
  CONSTRAINT refresh_tokens_pkey PRIMARY KEY (id),
  CONSTRAINT uk_refresh_tokens_token_hash UNIQUE (token_hash),
  CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE,
  CONSTRAINT fk_refresh_tokens_replaced_by FOREIGN KEY (replaced_by_id) REFERENCES public.refresh_tokens(id)
);

CREATE INDEX idx_refresh_tokens_user_id ON public.refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_token_hash ON public.refresh_tokens(token_hash);
