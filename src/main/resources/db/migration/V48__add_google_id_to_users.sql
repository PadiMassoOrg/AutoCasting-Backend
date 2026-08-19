ALTER TABLE public.users ADD COLUMN google_id varchar(255) NULL;
ALTER TABLE public.users ADD CONSTRAINT uk_users_google_id UNIQUE (google_id);

ALTER TABLE public.users DROP CONSTRAINT users_user_account_provider_check;
ALTER TABLE public.users ADD CONSTRAINT users_user_account_provider_check CHECK (
  (user_account_provider)::text = ANY (ARRAY['LOCAL','OTHER','GOOGLE']::text[])
);
