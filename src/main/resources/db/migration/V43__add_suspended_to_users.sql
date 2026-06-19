alter table users
    add column if not exists suspended boolean not null default false;
