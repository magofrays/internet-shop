--liquibase formatted sql

--changeset magofrays:init

create table profile
(
    id uuid primary key DEFAULT gen_random_uuid(),
    first_name varchar(128) not null,
    last_name varchar(128) not null,
    email varchar(128) not null unique,
    password varchar(128) not null,
    role_id int not null,
    created_at timestamptz not null default current_timestamp,
    updated_at timestamptz not null default current_timestamp
);

create table item(
    id uuid primary key DEFAULT gen_random_uuid(),
    title varchar(128) not null unique,
    description text,
    image_url varchar(255),
    price decimal(10, 2) not null,
    discount_price decimal(10, 2),
    quantity bigint not null default 1,
    created_at timestamptz not null default current_timestamp,
    updated_at timestamptz not null default current_timestamp,
    added_by_id uuid not null references profile(id)
);

create table category(
    id uuid primary key DEFAULT gen_random_uuid(),
    title varchar(128) not null,
    description text,
    parent_catalogue_id uuid references category(id), --if null means root dir
    created_at timestamptz not null default current_timestamp,
    updated_at timestamptz not null default current_timestamp,
    created_by_id uuid not null references profile(id)
);

create table category_item(
    category_id uuid references category(id),
    item_id uuid references item(id)
);

create table cart(
    id uuid primary key DEFAULT gen_random_uuid(),
    profile_id uuid not null references profile(id) unique
    -- more information to save
);

create table cart_item(
    id uuid primary key default gen_random_uuid(),
    cart_id uuid not null references cart(id),
    item_id uuid not null references item(id),
    added_at timestamptz not null default current_timestamp
);

create table orders(
    id uuid primary key default gen_random_uuid(),
    created_by_id uuid references profile(id),
    discount_cost decimal(10, 2),
    total_cost decimal(10, 2) not null,
    order_status int not null,
    currency varchar(8),
    created_at timestamptz not null default current_timestamp,
    updated_at timestamptz not null default current_timestamp
);

create table order_item(
    id uuid primary key default gen_random_uuid(),
    order_id uuid not null references orders(id),
    item_id uuid not null references item(id),
    cost decimal(10, 2) not null,
    discount_cost decimal(10, 2),
    created_at timestamptz not null default current_timestamp
);
