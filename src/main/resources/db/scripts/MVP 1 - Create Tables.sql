-- ================================================
-- ================================================
--	DROP SCHEMA public cascade ;
-- ================================================
-- ================================================


-- public.color_option definition

-- Drop table

-- DROP TABLE public.color_option;

CREATE TABLE public.color_option (
	id uuid NOT NULL,
	created_at timestamp(6) NULL,
	created_by varchar(255) NULL,
	deleted bool NOT NULL,
	modified_at timestamp(6) NULL,
	modified_by varchar(255) NULL,
	category_string_code varchar(255) NULL,
	string_code varchar(255) NOT NULL,
	CONSTRAINT color_option_pkey PRIMARY KEY (id)
);


-- public.diet_option definition

-- Drop table

-- DROP TABLE public.diet_option;

CREATE TABLE public.diet_option (
	id uuid NOT NULL,
	created_at timestamp(6) NULL,
	created_by varchar(255) NULL,
	deleted bool NOT NULL,
	modified_at timestamp(6) NULL,
	modified_by varchar(255) NULL,
	category_string_code varchar(255) NULL,
	string_code varchar(255) NOT NULL,
	CONSTRAINT diet_option_pkey PRIMARY KEY (id)
);


-- public.flyway_schema_history definition

-- Drop table

-- DROP TABLE public.flyway_schema_history;

CREATE TABLE public.flyway_schema_history (
	installed_rank int4 NOT NULL,
	"version" varchar(50) NULL,
	description varchar(200) NOT NULL,
	"type" varchar(20) NOT NULL,
	script varchar(1000) NOT NULL,
	checksum int4 NULL,
	installed_by varchar(100) NOT NULL,
	installed_on timestamp DEFAULT now() NOT NULL,
	execution_time int4 NOT NULL,
	success bool NOT NULL,
	CONSTRAINT flyway_schema_history_pk PRIMARY KEY (installed_rank)
);
CREATE INDEX flyway_schema_history_s_idx ON public.flyway_schema_history USING btree (success);


-- public.gender_option definition

-- Drop table

-- DROP TABLE public.gender_option;

CREATE TABLE public.gender_option (
	id uuid NOT NULL,
	created_at timestamp(6) NULL,
	created_by varchar(255) NULL,
	deleted bool NOT NULL,
	modified_at timestamp(6) NULL,
	modified_by varchar(255) NULL,
	category_string_code varchar(255) NULL,
	string_code varchar(255) NOT NULL,
	CONSTRAINT gender_option_pkey PRIMARY KEY (id)
);


-- public.legal_documents definition

-- Drop table

-- DROP TABLE public.legal_documents;

CREATE TABLE public.legal_documents (
	id uuid NOT NULL,
	"type" varchar(32) NOT NULL,
	locale varchar(8) NOT NULL,
	"version" varchar(32) NOT NULL,
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
CREATE INDEX idx_doc_lookup ON public.legal_documents USING btree (type, locale, status, effective_at);


-- public."plans" definition

-- Drop table

-- DROP TABLE public."plans";

CREATE TABLE public."plans" (
	id uuid NOT NULL,
	created_at timestamp(6) NULL,
	created_by varchar(255) NULL,
	deleted bool NOT NULL,
	modified_at timestamp(6) NULL,
	modified_by varchar(255) NULL,
	allows_custom_slug bool NOT NULL,
	code varchar(255) NOT NULL,
	description varchar(255) NULL,
	name_string_code varchar(255) NOT NULL,
	CONSTRAINT plans_pkey PRIMARY KEY (id),
	CONSTRAINT ukbsiq2g7uq9l49v27bsijicsys UNIQUE (code)
);


-- public.production_type definition

-- Drop table

-- DROP TABLE public.production_type;

CREATE TABLE public.production_type (
	id uuid NOT NULL,
	created_at timestamp(6) NULL,
	created_by varchar(255) NULL,
	deleted bool NOT NULL,
	modified_at timestamp(6) NULL,
	modified_by varchar(255) NULL,
	category_string_code varchar(255) NULL,
	string_code varchar(255) NOT NULL,
	CONSTRAINT production_type_pkey PRIMARY KEY (id)
);


-- public.professions definition

-- Drop table

-- DROP TABLE public.professions;

CREATE TABLE public.professions (
	id uuid NOT NULL,
	created_at timestamp(6) NULL,
	created_by varchar(255) NULL,
	deleted bool NOT NULL,
	modified_at timestamp(6) NULL,
	modified_by varchar(255) NULL,
	category_string_code varchar(255) NULL,
	string_code varchar(255) NOT NULL,
	CONSTRAINT professions_pkey PRIMARY KEY (id)
);


-- public.roles definition

-- Drop table

-- DROP TABLE public.roles;

CREATE TABLE public.roles (
	id uuid NOT NULL,
	created_at timestamp(6) NULL,
	created_by varchar(255) NULL,
	deleted bool NOT NULL,
	modified_at timestamp(6) NULL,
	modified_by varchar(255) NULL,
	code varchar(255) NOT NULL,
	description varchar(255) NULL,
	name_string_code varchar(255) NOT NULL,
	CONSTRAINT roles_pkey PRIMARY KEY (id),
	CONSTRAINT ukch1113horj4qr56f91omojv8 UNIQUE (code)
);


-- public.skills definition

-- Drop table

-- DROP TABLE public.skills;

CREATE TABLE public.skills (
	id uuid NOT NULL,
	created_at timestamp(6) NULL,
	created_by varchar(255) NULL,
	deleted bool NOT NULL,
	modified_at timestamp(6) NULL,
	modified_by varchar(255) NULL,
	category_string_code varchar(255) NULL,
	string_code varchar(255) NOT NULL,
	CONSTRAINT skills_pkey PRIMARY KEY (id)
);


-- public.legal_acceptances definition

-- Drop table

-- DROP TABLE public.legal_acceptances;

CREATE TABLE public.legal_acceptances (
	id uuid NOT NULL,
	user_id uuid NOT NULL,
	legal_document_id uuid NOT NULL,
	accepted_at timestamptz NOT NULL,
	ip varchar(255) NULL,
	user_agent varchar(512) NULL,
	content_hash varchar(64) NOT NULL,
	created_at timestamp(6) NULL,
	created_by varchar(255) NULL,
	modified_at timestamp(6) NULL,
	modified_by varchar(255) NULL,
	deleted bool NOT NULL,
	CONSTRAINT legal_acceptances_pkey PRIMARY KEY (id),
	CONSTRAINT fk_legal_acc_doc FOREIGN KEY (legal_document_id) REFERENCES public.legal_documents(id)
);
CREATE INDEX idx_acc_doc ON public.legal_acceptances USING btree (legal_document_id);
CREATE INDEX idx_acc_user ON public.legal_acceptances USING btree (user_id);


-- public.users definition

-- Drop table

-- DROP TABLE public.users;

CREATE TABLE public.users (
	id uuid NOT NULL,
	created_at timestamp(6) NULL,
	created_by varchar(255) NULL,
	deleted bool NOT NULL,
	modified_at timestamp(6) NULL,
	modified_by varchar(255) NULL,
	email varchar(255) NOT NULL,
	"password" varchar(255) NULL,
	user_account_provider varchar(255) NOT NULL,
	role_id uuid NOT NULL,
	CONSTRAINT uk6dotkott2kjsp8vw4d0m25fb7 UNIQUE (email),
	CONSTRAINT users_pkey PRIMARY KEY (id),
	CONSTRAINT users_user_account_provider_check CHECK (((user_account_provider)::text = ANY ((ARRAY['LOCAL'::character varying, 'OTHER'::character varying])::text[]))),
	CONSTRAINT fkp56c1712k691lhsyewcssf40f FOREIGN KEY (role_id) REFERENCES public.roles(id)
);


-- public.profile definition

-- Drop table

-- DROP TABLE public.profile;

CREATE TABLE public.profile (
	id uuid NOT NULL,
	created_at timestamp(6) NULL,
	created_by varchar(255) NULL,
	deleted bool NOT NULL,
	modified_at timestamp(6) NULL,
	modified_by varchar(255) NULL,
	default_slug varchar(255) NOT NULL,
	premium_slug varchar(255) NULL,
	plan_id uuid NOT NULL,
	user_id uuid NOT NULL,
	CONSTRAINT profile_pkey PRIMARY KEY (id),
	CONSTRAINT ukc1dkiawnlj6uoe6fnlwd6j83j UNIQUE (user_id),
	CONSTRAINT uks0ihtejjm8ok00dcfow6kmrkf UNIQUE (premium_slug),
	CONSTRAINT uks1vv3awi6krwxfomipnl7o558 UNIQUE (default_slug),
	CONSTRAINT fk2oe2dtxlgbtvd2dsrxhlvgo5p FOREIGN KEY (plan_id) REFERENCES public."plans"(id),
	CONSTRAINT fks14jvsf9tqrcnly0afsv0ngwv FOREIGN KEY (user_id) REFERENCES public.users(id)
);


-- public.profile_basic_info definition

-- Drop table

-- DROP TABLE public.profile_basic_info;

CREATE TABLE public.profile_basic_info (
	id uuid NOT NULL,
	created_at timestamp(6) NULL,
	created_by varchar(255) NULL,
	deleted bool NOT NULL,
	modified_at timestamp(6) NULL,
	modified_by varchar(255) NULL,
	birth_date date NULL,
	stage_name varchar(255) NOT NULL,
	gender_id uuid NULL,
	profile_id uuid NOT NULL,
	CONSTRAINT profile_basic_info_pkey PRIMARY KEY (id),
	CONSTRAINT ukoxl4623wy0efyoxvi1ggmc4h9 UNIQUE (profile_id),
	CONSTRAINT fk3bvsge9vjxpofl2vs8niqswm8 FOREIGN KEY (gender_id) REFERENCES public.gender_option(id),
	CONSTRAINT fko9seovpiuc12dotvwy1qoe41o FOREIGN KEY (profile_id) REFERENCES public.profile(id)
);


-- public.profile_characteristics definition

-- Drop table

-- DROP TABLE public.profile_characteristics;

CREATE TABLE public.profile_characteristics (
	id uuid NOT NULL,
	created_at timestamp(6) NULL,
	created_by varchar(255) NULL,
	deleted bool NOT NULL,
	modified_at timestamp(6) NULL,
	modified_by varchar(255) NULL,
	chest_cm varchar(255) NULL,
	dress_size varchar(255) NULL,
	driving_license bool NULL,
	height_cm int4 NULL,
	hip_cm varchar(255) NULL,
	pant_size varchar(255) NULL,
	passport bool NULL,
	shirt_size varchar(255) NULL,
	shoe_size varchar(255) NULL,
	tattoo bool NULL,
	waist_cm varchar(255) NULL,
	weight_kg int4 NULL,
	diet_option_id uuid NULL,
	eye_color_id uuid NULL,
	hair_color_id uuid NULL,
	profile_id uuid NOT NULL,
	CONSTRAINT profile_characteristics_pkey PRIMARY KEY (id),
	CONSTRAINT uk9h72dqftit3bssthsmfns3bti UNIQUE (diet_option_id),
	CONSTRAINT ukh9n1p0psgfb7hwd03p77tqxpp UNIQUE (profile_id),
	CONSTRAINT ukof5nt12u8iraqfbem856g7idd UNIQUE (eye_color_id),
	CONSTRAINT ukpc57mbg1qhlb203q9f8xiy7up UNIQUE (hair_color_id),
	CONSTRAINT fk37v8snusb5d798x7us1hdxavx FOREIGN KEY (diet_option_id) REFERENCES public.diet_option(id),
	CONSTRAINT fk8hgt8xpk7qe8o8fbt6b37497j FOREIGN KEY (profile_id) REFERENCES public.profile(id),
	CONSTRAINT fker8oxm5f9s2cdo574gr3h3fo0 FOREIGN KEY (eye_color_id) REFERENCES public.color_option(id),
	CONSTRAINT fkl50j6kradcd2uunxsdycm1sg7 FOREIGN KEY (hair_color_id) REFERENCES public.color_option(id)
);


-- public.profile_contact definition

-- Drop table

-- DROP TABLE public.profile_contact;

CREATE TABLE public.profile_contact (
	id uuid NOT NULL,
	created_at timestamp(6) NULL,
	created_by varchar(255) NULL,
	deleted bool NOT NULL,
	modified_at timestamp(6) NULL,
	modified_by varchar(255) NULL,
	email varchar(255) NULL,
	phone_number varchar(255) NULL,
	profile_id uuid NOT NULL,
	CONSTRAINT profile_contact_pkey PRIMARY KEY (id),
	CONSTRAINT ukjfubme5tlmcml2aeg5nvan334 UNIQUE (profile_id),
	CONSTRAINT fk8jelp1wrpm1i0xdctfppihtnu FOREIGN KEY (profile_id) REFERENCES public.profile(id)
);


-- public.profile_credit definition

-- Drop table

-- DROP TABLE public.profile_credit;

CREATE TABLE public.profile_credit (
	id uuid NOT NULL,
	created_at timestamp(6) NULL,
	created_by varchar(255) NULL,
	deleted bool NOT NULL,
	modified_at timestamp(6) NULL,
	modified_by varchar(255) NULL,
	producer_name varchar(255) NULL,
	project_name varchar(255) NULL,
	"role" varchar(255) NULL,
	"year" varchar(255) NULL,
	production_type_id uuid NOT NULL,
	profile_id uuid NOT NULL,
	CONSTRAINT profile_credit_pkey PRIMARY KEY (id),
	CONSTRAINT fkfhtrlbi7w94n53h6dt3clt82u FOREIGN KEY (profile_id) REFERENCES public.profile(id),
	CONSTRAINT fkgha7fqcorbg3u7dmb9b9cxd3c FOREIGN KEY (production_type_id) REFERENCES public.production_type(id)
);


-- public.profile_education definition

-- Drop table

-- DROP TABLE public.profile_education;

CREATE TABLE public.profile_education (
	id uuid NOT NULL,
	created_at timestamp(6) NULL,
	created_by varchar(255) NULL,
	deleted bool NOT NULL,
	modified_at timestamp(6) NULL,
	modified_by varchar(255) NULL,
	course_name varchar(255) NULL,
	graduation_year varchar(255) NULL,
	institution varchar(255) NULL,
	profile_id uuid NOT NULL,
	CONSTRAINT profile_education_pkey PRIMARY KEY (id),
	CONSTRAINT fknyamm2kg8h69w8hslttg6i2y FOREIGN KEY (profile_id) REFERENCES public.profile(id)
);


-- public.profile_media definition

-- Drop table

-- DROP TABLE public.profile_media;

CREATE TABLE public.profile_media (
	id uuid NOT NULL,
	created_at timestamp(6) NULL,
	created_by varchar(255) NULL,
	deleted bool NOT NULL,
	modified_at timestamp(6) NULL,
	modified_by varchar(255) NULL,
	full_body_image_url varchar(255) NULL,
	headshot_image_url varchar(255) NULL,
	introduction_video_url varchar(255) NULL,
	show_reel_video_url varchar(255) NULL,
	profile_id uuid NOT NULL,
	CONSTRAINT profile_media_pkey PRIMARY KEY (id),
	CONSTRAINT ukeyw2dx2yxbydvyvmsb27a4xf UNIQUE (profile_id),
	CONSTRAINT fkiy97kv2mpud9y5i0cg4w5e1rx FOREIGN KEY (profile_id) REFERENCES public.profile(id)
);


-- public.profile_skill definition

-- Drop table

-- DROP TABLE public.profile_skill;

CREATE TABLE public.profile_skill (
	profile_id uuid NOT NULL,
	skill_id uuid NOT NULL,
	CONSTRAINT profile_skill_pkey PRIMARY KEY (profile_id, skill_id),
	CONSTRAINT fkcxbysh527relenn6vd73sj0x8 FOREIGN KEY (profile_id) REFERENCES public.profile(id),
	CONSTRAINT fkox34jgj5yu0xn58roe0b49d8i FOREIGN KEY (skill_id) REFERENCES public.skills(id)
);


-- public.profile_social_media definition

-- Drop table

-- DROP TABLE public.profile_social_media;

CREATE TABLE public.profile_social_media (
	id uuid NOT NULL,
	created_at timestamp(6) NULL,
	created_by varchar(255) NULL,
	deleted bool NOT NULL,
	modified_at timestamp(6) NULL,
	modified_by varchar(255) NULL,
	instagram_url varchar(255) NULL,
	tik_tok_url varchar(255) NULL,
	profile_id uuid NOT NULL,
	CONSTRAINT profile_social_media_pkey PRIMARY KEY (id),
	CONSTRAINT uk7k03keyece8o8lo0qu9binlog UNIQUE (profile_id),
	CONSTRAINT fk18svgm9sfla1s4yv6grlm99k1 FOREIGN KEY (profile_id) REFERENCES public.profile(id)
);


-- public.basic_info_profession definition

-- Drop table

-- DROP TABLE public.basic_info_profession;

CREATE TABLE public.basic_info_profession (
	basic_info_id uuid NOT NULL,
	profession_id uuid NOT NULL,
	CONSTRAINT basic_info_profession_pkey PRIMARY KEY (basic_info_id, profession_id),
	CONSTRAINT fk2f1asm35gl1jcx29vswi7acbh FOREIGN KEY (profession_id) REFERENCES public.professions(id),
	CONSTRAINT fkrsgpihn1djur17i3e9kunu51g FOREIGN KEY (basic_info_id) REFERENCES public.profile_basic_info(id)
);


-- public.media_entity_other_pictures_url definition

-- Drop table

-- DROP TABLE public.media_entity_other_pictures_url;

CREATE TABLE public.media_entity_other_pictures_url (
	media_entity_id uuid NOT NULL,
	url varchar(255) NULL,
	idx int4 NOT NULL,
	CONSTRAINT media_entity_other_pictures_url_pkey PRIMARY KEY (media_entity_id, idx),
	CONSTRAINT fkoj6b1jfhqwl5xhm7rdcggmwma FOREIGN KEY (media_entity_id) REFERENCES public.profile_media(id)
);



-- DROP FUNCTION public.armor(bytea);

CREATE OR REPLACE FUNCTION public.armor(bytea)
 RETURNS text
 LANGUAGE c
 IMMUTABLE PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pg_armor$function$
;

-- DROP FUNCTION public.armor(bytea, _text, _text);

CREATE OR REPLACE FUNCTION public.armor(bytea, text[], text[])
 RETURNS text
 LANGUAGE c
 IMMUTABLE PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pg_armor$function$
;

-- DROP FUNCTION public.crypt(text, text);

CREATE OR REPLACE FUNCTION public.crypt(text, text)
 RETURNS text
 LANGUAGE c
 IMMUTABLE PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pg_crypt$function$
;

-- DROP FUNCTION public.dearmor(text);

CREATE OR REPLACE FUNCTION public.dearmor(text)
 RETURNS bytea
 LANGUAGE c
 IMMUTABLE PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pg_dearmor$function$
;

-- DROP FUNCTION public.decrypt(bytea, bytea, text);

CREATE OR REPLACE FUNCTION public.decrypt(bytea, bytea, text)
 RETURNS bytea
 LANGUAGE c
 IMMUTABLE PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pg_decrypt$function$
;

-- DROP FUNCTION public.decrypt_iv(bytea, bytea, bytea, text);

CREATE OR REPLACE FUNCTION public.decrypt_iv(bytea, bytea, bytea, text)
 RETURNS bytea
 LANGUAGE c
 IMMUTABLE PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pg_decrypt_iv$function$
;

-- DROP FUNCTION public.digest(bytea, text);

CREATE OR REPLACE FUNCTION public.digest(bytea, text)
 RETURNS bytea
 LANGUAGE c
 IMMUTABLE PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pg_digest$function$
;

-- DROP FUNCTION public.digest(text, text);

CREATE OR REPLACE FUNCTION public.digest(text, text)
 RETURNS bytea
 LANGUAGE c
 IMMUTABLE PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pg_digest$function$
;

-- DROP FUNCTION public.encrypt(bytea, bytea, text);

CREATE OR REPLACE FUNCTION public.encrypt(bytea, bytea, text)
 RETURNS bytea
 LANGUAGE c
 IMMUTABLE PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pg_encrypt$function$
;

-- DROP FUNCTION public.encrypt_iv(bytea, bytea, bytea, text);

CREATE OR REPLACE FUNCTION public.encrypt_iv(bytea, bytea, bytea, text)
 RETURNS bytea
 LANGUAGE c
 IMMUTABLE PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pg_encrypt_iv$function$
;

-- DROP FUNCTION public.gen_random_bytes(int4);

CREATE OR REPLACE FUNCTION public.gen_random_bytes(integer)
 RETURNS bytea
 LANGUAGE c
 PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pg_random_bytes$function$
;

-- DROP FUNCTION public.gen_random_uuid();

CREATE OR REPLACE FUNCTION public.gen_random_uuid()
 RETURNS uuid
 LANGUAGE c
 PARALLEL SAFE
AS '$libdir/pgcrypto', $function$pg_random_uuid$function$
;

-- DROP FUNCTION public.gen_salt(text, int4);

CREATE OR REPLACE FUNCTION public.gen_salt(text, integer)
 RETURNS text
 LANGUAGE c
 PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pg_gen_salt_rounds$function$
;

-- DROP FUNCTION public.gen_salt(text);

CREATE OR REPLACE FUNCTION public.gen_salt(text)
 RETURNS text
 LANGUAGE c
 PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pg_gen_salt$function$
;

-- DROP FUNCTION public.hmac(text, text, text);

CREATE OR REPLACE FUNCTION public.hmac(text, text, text)
 RETURNS bytea
 LANGUAGE c
 IMMUTABLE PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pg_hmac$function$
;

-- DROP FUNCTION public.hmac(bytea, bytea, text);

CREATE OR REPLACE FUNCTION public.hmac(bytea, bytea, text)
 RETURNS bytea
 LANGUAGE c
 IMMUTABLE PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pg_hmac$function$
;

-- DROP FUNCTION public.pgp_armor_headers(in text, out text, out text);

CREATE OR REPLACE FUNCTION public.pgp_armor_headers(text, OUT key text, OUT value text)
 RETURNS SETOF record
 LANGUAGE c
 IMMUTABLE PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pgp_armor_headers$function$
;

-- DROP FUNCTION public.pgp_key_id(bytea);

CREATE OR REPLACE FUNCTION public.pgp_key_id(bytea)
 RETURNS text
 LANGUAGE c
 IMMUTABLE PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pgp_key_id_w$function$
;

-- DROP FUNCTION public.pgp_pub_decrypt(bytea, bytea, text);

CREATE OR REPLACE FUNCTION public.pgp_pub_decrypt(bytea, bytea, text)
 RETURNS text
 LANGUAGE c
 IMMUTABLE PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pgp_pub_decrypt_text$function$
;

-- DROP FUNCTION public.pgp_pub_decrypt(bytea, bytea);

CREATE OR REPLACE FUNCTION public.pgp_pub_decrypt(bytea, bytea)
 RETURNS text
 LANGUAGE c
 IMMUTABLE PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pgp_pub_decrypt_text$function$
;

-- DROP FUNCTION public.pgp_pub_decrypt(bytea, bytea, text, text);

CREATE OR REPLACE FUNCTION public.pgp_pub_decrypt(bytea, bytea, text, text)
 RETURNS text
 LANGUAGE c
 IMMUTABLE PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pgp_pub_decrypt_text$function$
;

-- DROP FUNCTION public.pgp_pub_decrypt_bytea(bytea, bytea, text);

CREATE OR REPLACE FUNCTION public.pgp_pub_decrypt_bytea(bytea, bytea, text)
 RETURNS bytea
 LANGUAGE c
 IMMUTABLE PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pgp_pub_decrypt_bytea$function$
;

-- DROP FUNCTION public.pgp_pub_decrypt_bytea(bytea, bytea, text, text);

CREATE OR REPLACE FUNCTION public.pgp_pub_decrypt_bytea(bytea, bytea, text, text)
 RETURNS bytea
 LANGUAGE c
 IMMUTABLE PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pgp_pub_decrypt_bytea$function$
;

-- DROP FUNCTION public.pgp_pub_decrypt_bytea(bytea, bytea);

CREATE OR REPLACE FUNCTION public.pgp_pub_decrypt_bytea(bytea, bytea)
 RETURNS bytea
 LANGUAGE c
 IMMUTABLE PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pgp_pub_decrypt_bytea$function$
;

-- DROP FUNCTION public.pgp_pub_encrypt(text, bytea);

CREATE OR REPLACE FUNCTION public.pgp_pub_encrypt(text, bytea)
 RETURNS bytea
 LANGUAGE c
 PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pgp_pub_encrypt_text$function$
;

-- DROP FUNCTION public.pgp_pub_encrypt(text, bytea, text);

CREATE OR REPLACE FUNCTION public.pgp_pub_encrypt(text, bytea, text)
 RETURNS bytea
 LANGUAGE c
 PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pgp_pub_encrypt_text$function$
;

-- DROP FUNCTION public.pgp_pub_encrypt_bytea(bytea, bytea, text);

CREATE OR REPLACE FUNCTION public.pgp_pub_encrypt_bytea(bytea, bytea, text)
 RETURNS bytea
 LANGUAGE c
 PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pgp_pub_encrypt_bytea$function$
;

-- DROP FUNCTION public.pgp_pub_encrypt_bytea(bytea, bytea);

CREATE OR REPLACE FUNCTION public.pgp_pub_encrypt_bytea(bytea, bytea)
 RETURNS bytea
 LANGUAGE c
 PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pgp_pub_encrypt_bytea$function$
;

-- DROP FUNCTION public.pgp_sym_decrypt(bytea, text);

CREATE OR REPLACE FUNCTION public.pgp_sym_decrypt(bytea, text)
 RETURNS text
 LANGUAGE c
 IMMUTABLE PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pgp_sym_decrypt_text$function$
;

-- DROP FUNCTION public.pgp_sym_decrypt(bytea, text, text);

CREATE OR REPLACE FUNCTION public.pgp_sym_decrypt(bytea, text, text)
 RETURNS text
 LANGUAGE c
 IMMUTABLE PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pgp_sym_decrypt_text$function$
;

-- DROP FUNCTION public.pgp_sym_decrypt_bytea(bytea, text, text);

CREATE OR REPLACE FUNCTION public.pgp_sym_decrypt_bytea(bytea, text, text)
 RETURNS bytea
 LANGUAGE c
 IMMUTABLE PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pgp_sym_decrypt_bytea$function$
;

-- DROP FUNCTION public.pgp_sym_decrypt_bytea(bytea, text);

CREATE OR REPLACE FUNCTION public.pgp_sym_decrypt_bytea(bytea, text)
 RETURNS bytea
 LANGUAGE c
 IMMUTABLE PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pgp_sym_decrypt_bytea$function$
;

-- DROP FUNCTION public.pgp_sym_encrypt(text, text, text);

CREATE OR REPLACE FUNCTION public.pgp_sym_encrypt(text, text, text)
 RETURNS bytea
 LANGUAGE c
 PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pgp_sym_encrypt_text$function$
;

-- DROP FUNCTION public.pgp_sym_encrypt(text, text);

CREATE OR REPLACE FUNCTION public.pgp_sym_encrypt(text, text)
 RETURNS bytea
 LANGUAGE c
 PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pgp_sym_encrypt_text$function$
;

-- DROP FUNCTION public.pgp_sym_encrypt_bytea(bytea, text, text);

CREATE OR REPLACE FUNCTION public.pgp_sym_encrypt_bytea(bytea, text, text)
 RETURNS bytea
 LANGUAGE c
 PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pgp_sym_encrypt_bytea$function$
;

-- DROP FUNCTION public.pgp_sym_encrypt_bytea(bytea, text);

CREATE OR REPLACE FUNCTION public.pgp_sym_encrypt_bytea(bytea, text)
 RETURNS bytea
 LANGUAGE c
 PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pgp_sym_encrypt_bytea$function$
;