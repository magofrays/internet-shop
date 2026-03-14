DELETE FROM order_item;
DELETE FROM orders;
DELETE FROM cart_item;
DELETE FROM cart;
DELETE FROM category_item;
DELETE FROM category;
DELETE FROM item;
DELETE FROM profile;

-- 1. PROFILES (пользователи)
INSERT INTO profile(id, email, first_name, last_name, password, role_id)
VALUES
(CAST('11111111-1111-1111-1111-111111111111' AS UUID), 'dmitry@mail.ru', 'Dmitry', 'Ivanov', '$2a$10$7q5SSPPJBDV1480xB0w5AO9B5LlzSpPnXq6mW43vMg1ayW2khOeA', 0),
(CAST('22222222-2222-2222-2222-222222222222' AS UUID), 'alexey@gmail.com', 'Alexey', 'Petrov', '$2a$10$SKEG6q1.7hT2bCZyJE81p.qMefx5bQVhIn8mYRYIzE9EHbVPufnKW', 1),
(CAST('33333333-3333-3333-3333-333333333333' AS UUID), 'matvey@yandex.ru', 'Matvey', 'Chensky', '$2a$10$kWORibrAnEn9lRaObybof.CaprXbSCRQgRdcgsnPdtX47FLY12y/a', 0);

-- 2. CARTS (корзины для каждого пользователя)
INSERT INTO cart(id, profile_id) VALUES
(CAST('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa' AS UUID), CAST('11111111-1111-1111-1111-111111111111' AS UUID)),
(CAST('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb' AS UUID), CAST('22222222-2222-2222-2222-222222222222' AS UUID)),
(CAST('cccccccc-cccc-cccc-cccc-cccccccccccc' AS UUID), CAST('33333333-3333-3333-3333-333333333333' AS UUID));

-- 3. ITEMS (товары)
INSERT INTO item(id, title, description, image_url, price, discount_price, quantity, created_at, updated_at) VALUES
(CAST('a0000000-0000-0000-0000-000000000001' AS UUID), 'iPhone 15 Pro', '256GB, черный', '/img/iphone.jpg', 999.99, 899.99, 10, current_timestamp, current_timestamp),
(CAST('a0000000-0000-0000-0000-000000000002' AS UUID), 'Samsung S24', '512GB, серый', '/img/samsung.jpg', 899.99, 849.99, 15, current_timestamp, current_timestamp),
(CAST('a0000000-0000-0000-0000-000000000003' AS UUID), 'MacBook Pro', 'M3, 16GB RAM', '/img/macbook.jpg', 1999.99, 1899.99, 5, current_timestamp, current_timestamp),
(CAST('a0000000-0000-0000-0000-000000000004' AS UUID), 'Sony WH-1000XM5', 'Наушники', '/img/sony.jpg', 399.99, 349.99, 20, current_timestamp, current_timestamp),
(CAST('a0000000-0000-0000-0000-000000000005' AS UUID), 'iPad Air', '64GB, розовый', '/img/ipad.jpg', 599.99, 549.99, 8, current_timestamp, current_timestamp);

-- 4. CATEGORIES (категории)
INSERT INTO category(id, title, description, parent_catalogue_id, created_at, updated_at) VALUES
(CAST('b0000000-0000-0000-0000-000000000001' AS UUID), 'Электроника', 'Все для цифры', NULL, current_timestamp, current_timestamp),
(CAST('b0000000-0000-0000-0000-000000000002' AS UUID), 'Одежда', 'Мода и стиль', NULL, current_timestamp, current_timestamp),
(CAST('b0000000-0000-0000-0000-000000000003' AS UUID), 'Смартфоны', 'Мобильные телефоны', CAST('b0000000-0000-0000-0000-000000000001' AS UUID), current_timestamp, current_timestamp),
(CAST('b0000000-0000-0000-0000-000000000004' AS UUID), 'Ноутбуки', 'Портативные ПК', CAST('b0000000-0000-0000-0000-000000000001' AS UUID), current_timestamp, current_timestamp);

-- 5. CATEGORY_ITEM (связь товаров с категориями)
INSERT INTO category_item(category_id, item_id) VALUES
(CAST('b0000000-0000-0000-0000-000000000003' AS UUID), CAST('a0000000-0000-0000-0000-000000000001' AS UUID)),
(CAST('b0000000-0000-0000-0000-000000000003' AS UUID), CAST('a0000000-0000-0000-0000-000000000002' AS UUID)),
(CAST('b0000000-0000-0000-0000-000000000004' AS UUID), CAST('a0000000-0000-0000-0000-000000000003' AS UUID)),
(CAST('b0000000-0000-0000-0000-000000000001' AS UUID), CAST('a0000000-0000-0000-0000-000000000004' AS UUID)),
(CAST('b0000000-0000-0000-0000-000000000001' AS UUID), CAST('a0000000-0000-0000-0000-000000000005' AS UUID));

-- 6. CART_ITEM (товары в корзинах)
INSERT INTO cart_item(id, cart_id, item_id, added_at) VALUES
(CAST('c0000000-0000-0000-0000-000000000001' AS UUID), CAST('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa' AS UUID), CAST('a0000000-0000-0000-0000-000000000001' AS UUID), current_timestamp),
(CAST('c0000000-0000-0000-0000-000000000002' AS UUID), CAST('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa' AS UUID), CAST('a0000000-0000-0000-0000-000000000004' AS UUID), current_timestamp),
(CAST('c0000000-0000-0000-0000-000000000003' AS UUID), CAST('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb' AS UUID), CAST('a0000000-0000-0000-0000-000000000003' AS UUID), current_timestamp),
(CAST('c0000000-0000-0000-0000-000000000004' AS UUID), CAST('cccccccc-cccc-cccc-cccc-cccccccccccc' AS UUID), CAST('a0000000-0000-0000-0000-000000000005' AS UUID), current_timestamp);

-- 7. ORDERS (заказы)
INSERT INTO orders(id, discount_cost, total_cost, order_status, currency, created_at, updated_at, created_by_id) VALUES
(CAST('d0000000-0000-0000-0000-000000000001' AS UUID), 50.00, 949.99, 1, 'USD', current_timestamp, current_timestamp, CAST('11111111-1111-1111-1111-111111111111' AS UUID)),
(CAST('d0000000-0000-0000-0000-000000000002' AS UUID), 0.00, 399.99, 2, 'USD', current_timestamp, current_timestamp, CAST('22222222-2222-2222-2222-222222222222' AS UUID));

-- 8. ORDER_ITEM (товары в заказах)
INSERT INTO order_item(id, order_id, item_id, cost, discount_cost, created_at) VALUES
(CAST('e0000000-0000-0000-0000-000000000001' AS UUID), CAST('d0000000-0000-0000-0000-000000000001' AS UUID), CAST('a0000000-0000-0000-0000-000000000001' AS UUID), 999.99, 50.00, current_timestamp),
(CAST('e0000000-0000-0000-0000-000000000002' AS UUID), CAST('d0000000-0000-0000-0000-000000000002' AS UUID), CAST('a0000000-0000-0000-0000-000000000004' AS UUID), 399.99, 0.00, current_timestamp);