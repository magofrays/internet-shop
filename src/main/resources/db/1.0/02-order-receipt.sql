--liquibase formatted sql

--changeset magofrays:order-receipt

create table order_receipt(
    order_id uuid primary key references orders(id),
    url varchar(255) not null
)