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
