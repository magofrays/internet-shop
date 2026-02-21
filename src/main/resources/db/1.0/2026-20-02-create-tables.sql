--liquibase formatted sql

--changeset magofrays:init

create table profile
(
    profile_id uuid primary key DEFAULT gen_random_uuid(),
    first_name varchar(128) not null,
    last_name varchar(128) not null,
    email varchar(128) not null unique,
    password varchar(128) not null,
    role_id int not null,
    created_at timestamptz not null default current_timestamp,
    updated_at timestamptz not null default current_timestamp
);

create table item(
    item_id uuid primary key DEFAULT gen_random_uuid(),
    title varchar(128) not null unique,
    description text,
    image_url varchar(255),
    price decimal(10, 2) not null,
    discount_price decimal(10, 2),
    quantity bigint not null default 1,
    created_at timestamptz not null default current_timestamp,
    updated_at timestamptz not null default current_timestamp,
    added_by uuid not null references profile(profile_id)
);

create table category(
    category_id uuid primary key DEFAULT gen_random_uuid(),
    title varchar(128) not null,
    description text,
    parent_catalogue uuid references category(category_id), --if null means root dir
    created_at timestamptz not null default current_timestamp,
    updated_at timestamptz not null default current_timestamp,
    created_by uuid not null references profile(profile_id)
);

create table cart(
    cart_id uuid primary key DEFAULT gen_random_uuid(),
    profile_id uuid not null references profile(profile_id) unique
    -- more information to save
);

create table cart_item(
    cart_item_id uuid primary key default gen_random_uuid(),
    cart_id uuid not null references cart(cart_id),
    item_id uuid not null references item(item_id),
    added_at timestamptz not null default current_timestamp
);

create table orders(
    order_id uuid primary key default gen_random_uuid(),
    created_by uuid references profile(profile_id),
    discount_cost decimal(10, 2),
    total_cost decimal(10, 2) not null,
    order_status int not null,
    currency varchar(8),
    created_at timestamptz not null default current_timestamp,
    updated_at timestamptz not null default current_timestamp
);

create table order_item(
    order_item_id uuid primary key default gen_random_uuid(),
    order_id uuid not null references orders(order_id),
    cost decimal(10, 2) not null,
    discount_cost decimal(10, 2),
    created_at timestamptz not null default current_timestamp
);
