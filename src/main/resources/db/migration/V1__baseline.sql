-- public.color_option definition

-- Drop table

-- DROP TABLE color_option;

CREATE TABLE color_option (
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

-- DROP TABLE diet_option;

CREATE TABLE diet_option (
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


-- public.gender_option definition

-- Drop table

-- DROP TABLE gender_option;

CREATE TABLE gender_option (
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


-- public."plans" definition

-- Drop table

-- DROP TABLE "plans";

CREATE TABLE "plans" (
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

-- DROP TABLE production_type;

CREATE TABLE production_type (
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

-- DROP TABLE professions;

CREATE TABLE professions (
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

-- DROP TABLE roles;

CREATE TABLE roles (
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

-- DROP TABLE skills;

CREATE TABLE skills (
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


-- public.users definition

-- Drop table

-- DROP TABLE users;

CREATE TABLE users (
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
	CONSTRAINT fkp56c1712k691lhsyewcssf40f FOREIGN KEY (role_id) REFERENCES roles(id)
);


-- public.profile definition

-- Drop table

-- DROP TABLE profile;

CREATE TABLE profile (
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
	CONSTRAINT fk2oe2dtxlgbtvd2dsrxhlvgo5p FOREIGN KEY (plan_id) REFERENCES "plans"(id),
	CONSTRAINT fks14jvsf9tqrcnly0afsv0ngwv FOREIGN KEY (user_id) REFERENCES users(id)
);


-- public.profile_basic_info definition

-- Drop table

-- DROP TABLE profile_basic_info;

CREATE TABLE profile_basic_info (
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
	CONSTRAINT fk3bvsge9vjxpofl2vs8niqswm8 FOREIGN KEY (gender_id) REFERENCES gender_option(id),
	CONSTRAINT fko9seovpiuc12dotvwy1qoe41o FOREIGN KEY (profile_id) REFERENCES profile(id)
);


-- public.profile_characteristics definition

-- Drop table

-- DROP TABLE profile_characteristics;

CREATE TABLE profile_characteristics (
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
	CONSTRAINT fk37v8snusb5d798x7us1hdxavx FOREIGN KEY (diet_option_id) REFERENCES diet_option(id),
	CONSTRAINT fk8hgt8xpk7qe8o8fbt6b37497j FOREIGN KEY (profile_id) REFERENCES profile(id),
	CONSTRAINT fker8oxm5f9s2cdo574gr3h3fo0 FOREIGN KEY (eye_color_id) REFERENCES color_option(id),
	CONSTRAINT fkl50j6kradcd2uunxsdycm1sg7 FOREIGN KEY (hair_color_id) REFERENCES color_option(id)
);


-- public.profile_contact definition

-- Drop table

-- DROP TABLE profile_contact;

CREATE TABLE profile_contact (
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
	CONSTRAINT fk8jelp1wrpm1i0xdctfppihtnu FOREIGN KEY (profile_id) REFERENCES profile(id)
);


-- public.profile_credit definition

-- Drop table

-- DROP TABLE profile_credit;

CREATE TABLE profile_credit (
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
	CONSTRAINT fkfhtrlbi7w94n53h6dt3clt82u FOREIGN KEY (profile_id) REFERENCES profile(id),
	CONSTRAINT fkgha7fqcorbg3u7dmb9b9cxd3c FOREIGN KEY (production_type_id) REFERENCES production_type(id)
);


-- public.profile_education definition

-- Drop table

-- DROP TABLE profile_education;

CREATE TABLE profile_education (
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
	CONSTRAINT fknyamm2kg8h69w8hslttg6i2y FOREIGN KEY (profile_id) REFERENCES profile(id)
);


-- public.profile_media definition

-- Drop table

-- DROP TABLE profile_media;

CREATE TABLE profile_media (
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
	CONSTRAINT fkiy97kv2mpud9y5i0cg4w5e1rx FOREIGN KEY (profile_id) REFERENCES profile(id)
);


-- public.profile_skill definition

-- Drop table

-- DROP TABLE profile_skill;

CREATE TABLE profile_skill (
	profile_id uuid NOT NULL,
	skill_id uuid NOT NULL,
	CONSTRAINT profile_skill_pkey PRIMARY KEY (profile_id, skill_id),
	CONSTRAINT fkcxbysh527relenn6vd73sj0x8 FOREIGN KEY (profile_id) REFERENCES profile(id),
	CONSTRAINT fkox34jgj5yu0xn58roe0b49d8i FOREIGN KEY (skill_id) REFERENCES skills(id)
);


-- public.profile_social_media definition

-- Drop table

-- DROP TABLE profile_social_media;

CREATE TABLE profile_social_media (
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
	CONSTRAINT fk18svgm9sfla1s4yv6grlm99k1 FOREIGN KEY (profile_id) REFERENCES profile(id)
);


-- public.basic_info_profession definition

-- Drop table

-- DROP TABLE basic_info_profession;

CREATE TABLE basic_info_profession (
	basic_info_id uuid NOT NULL,
	profession_id uuid NOT NULL,
	CONSTRAINT basic_info_profession_pkey PRIMARY KEY (basic_info_id, profession_id),
	CONSTRAINT fk2f1asm35gl1jcx29vswi7acbh FOREIGN KEY (profession_id) REFERENCES professions(id),
	CONSTRAINT fkrsgpihn1djur17i3e9kunu51g FOREIGN KEY (basic_info_id) REFERENCES profile_basic_info(id)
);


-- public.media_entity_other_pictures_url definition

-- Drop table

-- DROP TABLE media_entity_other_pictures_url;

CREATE TABLE media_entity_other_pictures_url (
	media_entity_id uuid NOT NULL,
	url varchar(255) NULL,
	idx int4 NOT NULL,
	CONSTRAINT media_entity_other_pictures_url_pkey PRIMARY KEY (media_entity_id, idx),
	CONSTRAINT fkoj6b1jfhqwl5xhm7rdcggmwma FOREIGN KEY (media_entity_id) REFERENCES profile_media(id)
);
