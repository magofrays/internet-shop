--liquibase formatted sql

--changeset magofrays:fill-items-and-categories

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



-- 1. ITEMS (товары электротехники)
INSERT INTO item(id, title, description, image_url, price, discount_price, quantity, created_at, updated_at) VALUES
-- Смартфоны и гаджеты
(CAST('a0000000-0000-0000-0000-000000000001' AS UUID), 'iPhone 15 Pro Max 256GB', 'Флагманский смартфон Apple, 6.7" Super Retina XDR, A17 Pro, 48MP камера, титановый корпус', NULL, 149990.00, 139990.00, 15, current_timestamp, current_timestamp),
(CAST('a0000000-0000-0000-0000-000000000002' AS UUID), 'Samsung Galaxy S24 Ultra 512GB', 'Смартфон с S Pen, 6.8" Dynamic AMOLED 2X, 200MP камера, Snapdragon 8 Gen 3', NULL, 129990.00, 119990.00, 10, current_timestamp, current_timestamp),
(CAST('a0000000-0000-0000-0000-000000000003' AS UUID), 'Xiaomi 14 Pro 256GB', '6.73" AMOLED, Leica камера, Snapdragon 8 Gen 3, 120W зарядка', NULL, 89990.00, 84990.00, 20, current_timestamp, current_timestamp),

-- Ноутбуки
(CAST('a0000000-0000-0000-0000-000000000005' AS UUID), 'MacBook Pro 16" M3 Max', 'Ноутбук Apple, 16" Liquid Retina XDR, M3 Max (16-core CPU, 40-core GPU), 48GB RAM, 1TB SSD', NULL, 499990.00, 479990.00, 5, current_timestamp, current_timestamp),
(CAST('a0000000-0000-0000-0000-000000000006' AS UUID), 'ASUS ROG Strix G18', 'Игровой ноутбук, 18" QHD+ 240Hz, Intel Core i9-14900HX, RTX 4090 16GB, 32GB RAM, 2TB SSD', NULL, 349990.00, 329990.00, 7, current_timestamp, current_timestamp),
(CAST('a0000000-0000-0000-0000-000000000007' AS UUID), 'Dell XPS 15', 'Ноутбук премиум-класса, 15.6" OLED 3.5K, Intel Core i9-13900H, RTX 4070, 32GB RAM, 1TB SSD', NULL, 259990.00, 239990.00, 6, current_timestamp, current_timestamp),
(CAST('a0000000-0000-0000-0000-000000000008' AS UUID), 'Lenovo ThinkPad X1 Carbon', 'Бизнес-ноутбук, 14" WQXGA, Intel Core i7-1365U, 16GB RAM, 512GB SSD, 1.1кг', NULL, 189990.00, 179990.00, 12, current_timestamp, current_timestamp),

-- Планшеты и электронные книги
(CAST('a0000000-0000-0000-0000-000000000009' AS UUID), 'iPad Pro 12.9" M2', 'Планшет Apple, 12.9" Liquid Retina XDR, M2, 256GB, Wi-Fi + Cellular', NULL, 149990.00, 139990.00, 9, current_timestamp, current_timestamp),
(CAST('a0000000-0000-0000-0000-000000000010' AS UUID), 'Samsung Galaxy Tab S9 Ultra', 'Планшет, 14.6" Super AMOLED 120Hz, Snapdragon 8 Gen 2, 512GB, S Pen в комплекте', NULL, 119990.00, 109990.00, 7, current_timestamp, current_timestamp),
(CAST('a0000000-0000-0000-0000-000000000011' AS UUID), 'PocketBook InkPad 4', 'Электронная книга, 7.8" E-Ink, 32GB, подсветка, влагозащита', NULL, 34990.00, 29990.00, 25, current_timestamp, current_timestamp),

-- Наушники и аудио
(CAST('a0000000-0000-0000-0000-000000000012' AS UUID), 'Sony WH-1000XM5', 'Беспроводные наушники с шумоподавлением, 30 часов работы, LDAC', NULL, 44990.00, 39990.00, 30, current_timestamp, current_timestamp),
(CAST('a0000000-0000-0000-0000-000000000013' AS UUID), 'Apple AirPods Pro 2', 'Беспроводные наушники с активным шумоподавлением, USB-C', NULL, 24990.00, 22990.00, 50, current_timestamp, current_timestamp),
(CAST('a0000000-0000-0000-0000-000000000014' AS UUID), 'JBL Charge 5', 'Портативная колонка, 20 часов работы, PowerBank, влагозащита IP67', NULL, 14990.00, 12990.00, 40, current_timestamp, current_timestamp),
(CAST('a0000000-0000-0000-0000-000000000015' AS UUID), 'Bose QuietComfort Ultra', 'Премиальные наушники с пространственным аудио, иммерсивный звук', NULL, 49990.00, 45990.00, 15, current_timestamp, current_timestamp),

-- Умные часы и фитнес-трекеры
(CAST('a0000000-0000-0000-0000-000000000016' AS UUID), 'Apple Watch Ultra 2', 'Умные часы для экстремальных условий, 49mm, титан, GPS + Cellular', NULL, 99990.00, 94990.00, 12, current_timestamp, current_timestamp),
(CAST('a0000000-0000-0000-0000-000000000017' AS UUID), 'Samsung Galaxy Watch6 Classic', 'Умные часы с вращающимся безелем, LTE, 47mm, мониторинг здоровья', NULL, 39990.00, 37990.00, 18, current_timestamp, current_timestamp),
(CAST('a0000000-0000-0000-0000-000000000018' AS UUID), 'Garmin Fenix 7 Pro', 'Многофункциональные часы для спорта и активного отдыха, Sapphire Solar', NULL, 89990.00, 84990.00, 8, current_timestamp, current_timestamp),

-- Аксессуары и периферия
(CAST('a0000000-0000-0000-0000-000000000019' AS UUID), 'Logitech MX Master 3S', 'Беспроводная мышь для работы, тихие клики, 8K DPI, USB-C', NULL, 9990.00, 8990.00, 60, current_timestamp, current_timestamp),
(CAST('a0000000-0000-0000-0000-000000000020' AS UUID), 'Keychron Q1 Pro', 'Механическая клавиатура, 75%, hot-swap, RGB, Bluetooth 5.1', NULL, 19990.00, 17990.00, 25, current_timestamp, current_timestamp),
(CAST('a0000000-0000-0000-0000-000000000021' AS UUID), 'Samsung 980 Pro 2TB', 'SSD NVMe M.2, PCIe 4.0, скорости чтения/записи до 7000/5100 МБ/с', NULL, 19990.00, 17990.00, 35, current_timestamp, current_timestamp),
(CAST('a0000000-0000-0000-0000-000000000022' AS UUID), 'WD My Cloud Home 4TB', 'Персональное облачное хранилище, автоматическое резервное копирование', NULL, 24990.00, 22990.00, 12, current_timestamp, current_timestamp),

-- Роутеры и сетевое оборудование
(CAST('a0000000-0000-0000-0000-000000000023' AS UUID), 'TP-Link Archer AXE300', 'Wi-Fi 6E роутер, три-диапазонный, скорость до 10.8 Гбит/с', NULL, 29990.00, 27990.00, 14, current_timestamp, current_timestamp),
(CAST('a0000000-0000-0000-0000-000000000024' AS UUID), 'ASUS ROG Rapture GT-AXE16000', 'Игровой роутер, Wi-Fi 6E, два 10G порта, оптимизация игрового трафика', NULL, 59990.00, 54990.00, 6, current_timestamp, current_timestamp),

-- Кабели и адаптеры
(CAST('a0000000-0000-0000-0000-000000000025' AS UUID), 'Apple USB-C - Lightning кабель 2м', 'Оригинальный кабель для зарядки и синхронизации', NULL, 2990.00, 2490.00, 100, current_timestamp, current_timestamp),
(CAST('a0000000-0000-0000-0000-000000000026' AS UUID), 'Anker USB-C Hub 7-в-1', 'Мультихаб с HDMI, USB-A 3.0, SD/TF картридер, зарядка PD', NULL, 4990.00, 4490.00, 80, current_timestamp, current_timestamp),

-- Игровые приставки
(CAST('a0000000-0000-0000-0000-000000000027' AS UUID), 'Sony PlayStation 5 Slim', 'Игровая приставка, 1TB SSD, два беспроводных контроллера DualSense', NULL, 69990.00, 64990.00, 8, current_timestamp, current_timestamp),
(CAST('a0000000-0000-0000-0000-000000000028' AS UUID), 'Microsoft Xbox Series X', 'Игровая приставка, 1TB SSD, 12 TFLOPS, 4K Blu-ray', NULL, 64990.00, 59990.00, 7, current_timestamp, current_timestamp),
(CAST('a0000000-0000-0000-0000-000000000029' AS UUID), 'Nintendo Switch OLED', 'Игровая приставка, 7" OLED экран, 64GB, док-станция', NULL, 34990.00, 32990.00, 12, current_timestamp, current_timestamp),

-- Электроприборы для дома
(CAST('a0000000-0000-0000-0000-000000000030' AS UUID), 'Dyson V15 Detect Absolute', 'Беспроводной пылесос с лазерной подсветкой, 60 минут работы, HEPA фильтр', NULL, 79990.00, 74990.00, 6, current_timestamp, current_timestamp),
(CAST('a0000000-0000-0000-0000-000000000031' AS UUID), 'Xiaomi Mi Robot Vacuum-Mop 2 Pro', 'Робот-пылесос с функцией влажной уборки, лазерная навигация', NULL, 29990.00, 27990.00, 15, current_timestamp, current_timestamp),
(CAST('a0000000-0000-0000-0000-000000000032' AS UUID), 'KitchenAid 5KSM150', 'Планетарный миксер, 4.8л, 10 скоростей, стальная чаша', NULL, 69990.00, 64990.00, 5, current_timestamp, current_timestamp);

-- 2. CATEGORIES (категории)
INSERT INTO category(id, title, description, parent_catalogue_id, created_at, updated_at) VALUES
-- Корневые категории
(CAST('b0000000-0000-0000-0000-000000000001' AS UUID), 'Смартфоны и гаджеты', 'Мобильные телефоны, умные часы и аксессуары', NULL, current_timestamp, current_timestamp),
(CAST('b0000000-0000-0000-0000-000000000002' AS UUID), 'Компьютеры и ноутбуки', 'Ноутбуки, ПК, мониторы и комплектующие', NULL, current_timestamp, current_timestamp),
(CAST('b0000000-0000-0000-0000-000000000003' AS UUID), 'Аудио и видео', 'Наушники, колонки, гарнитуры', NULL, current_timestamp, current_timestamp),
(CAST('b0000000-0000-0000-0000-000000000004' AS UUID), 'Игры и развлечения', 'Игровые приставки, игры, аксессуары', NULL, current_timestamp, current_timestamp),
(CAST('b0000000-0000-0000-0000-000000000005' AS UUID), 'Техника для дома', 'Пылесосы, миксеры, кухонная техника', NULL, current_timestamp, current_timestamp),

-- Подкатегории смартфонов
(CAST('b1000000-0000-0000-0000-000000000001' AS UUID), 'Смартфоны Apple', 'iPhone и аксессуары', CAST('b0000000-0000-0000-0000-000000000001' AS UUID), current_timestamp, current_timestamp),
(CAST('b1000000-0000-0000-0000-000000000002' AS UUID), 'Смартфоны Samsung', 'Galaxy и аксессуары', CAST('b0000000-0000-0000-0000-000000000001' AS UUID), current_timestamp, current_timestamp),
(CAST('b1000000-0000-0000-0000-000000000003' AS UUID), 'Смартфоны Xiaomi', 'Xiaomi, Redmi и POCO', CAST('b0000000-0000-0000-0000-000000000001' AS UUID), current_timestamp, current_timestamp),
(CAST('b1000000-0000-0000-0000-000000000004' AS UUID), 'Умные часы', 'Apple Watch, Samsung Watch, Garmin', CAST('b0000000-0000-0000-0000-000000000001' AS UUID), current_timestamp, current_timestamp),

-- Подкатегории компьютеров
(CAST('b2000000-0000-0000-0000-000000000001' AS UUID), 'Ноутбуки Apple', 'MacBook Air, MacBook Pro', CAST('b0000000-0000-0000-0000-000000000002' AS UUID), current_timestamp, current_timestamp),
(CAST('b2000000-0000-0000-0000-000000000002' AS UUID), 'Игровые ноутбуки', 'ASUS ROG, MSI, Lenovo Legion', CAST('b0000000-0000-0000-0000-000000000002' AS UUID), current_timestamp, current_timestamp),
(CAST('b2000000-0000-0000-0000-000000000003' AS UUID), 'Бизнес-ноутбуки', 'Lenovo ThinkPad, Dell XPS, HP EliteBook', CAST('b0000000-0000-0000-0000-000000000002' AS UUID), current_timestamp, current_timestamp),
(CAST('b2000000-0000-0000-0000-000000000004' AS UUID), 'Планшеты', 'iPad, Samsung Tab, электронные книги', CAST('b0000000-0000-0000-0000-000000000002' AS UUID), current_timestamp, current_timestamp),
(CAST('b2000000-0000-0000-0000-000000000005' AS UUID), 'Комплектующие', 'SSD, RAM, клавиатуры, мыши', CAST('b0000000-0000-0000-0000-000000000002' AS UUID), current_timestamp, current_timestamp),

-- Подкатегории аудио
(CAST('b3000000-0000-0000-0000-000000000001' AS UUID), 'Наушники', 'Внутриканальные, полноразмерные, TWS', CAST('b0000000-0000-0000-0000-000000000003' AS UUID), current_timestamp, current_timestamp),
(CAST('b3000000-0000-0000-0000-000000000002' AS UUID), 'Портативные колонки', 'Bluetooth колонки', CAST('b0000000-0000-0000-0000-000000000003' AS UUID), current_timestamp, current_timestamp),
(CAST('b3000000-0000-0000-0000-000000000003' AS UUID), 'Премиум аудио', 'Bose, Sony, Sennheiser', CAST('b0000000-0000-0000-0000-000000000003' AS UUID), current_timestamp, current_timestamp),

-- Подкатегории игр
(CAST('b4000000-0000-0000-0000-000000000001' AS UUID), 'PlayStation', 'PS5, PS4, аксессуары', CAST('b0000000-0000-0000-0000-000000000004' AS UUID), current_timestamp, current_timestamp),
(CAST('b4000000-0000-0000-0000-000000000002' AS UUID), 'Xbox', 'Xbox Series X/S, Xbox One', CAST('b0000000-0000-0000-0000-000000000004' AS UUID), current_timestamp, current_timestamp),
(CAST('b4000000-0000-0000-0000-000000000003' AS UUID), 'Nintendo', 'Switch, Switch OLED', CAST('b0000000-0000-0000-0000-000000000004' AS UUID), current_timestamp, current_timestamp),

-- Подкатегории техники для дома
(CAST('b5000000-0000-0000-0000-000000000001' AS UUID), 'Пылесосы', 'Роботы-пылесосы, вертикальные, моющие', CAST('b0000000-0000-0000-0000-000000000005' AS UUID), current_timestamp, current_timestamp),
(CAST('b5000000-0000-0000-0000-000000000002' AS UUID), 'Кухонная техника', 'Миксеры, блендеры, мультиварки', CAST('b0000000-0000-0000-0000-000000000005' AS UUID), current_timestamp, current_timestamp);

-- 3. CATEGORY_ITEM (связь товаров с категориями)
INSERT INTO category_item(category_id, item_id) VALUES
-- Смартфоны Apple
(CAST('b1000000-0000-0000-0000-000000000001' AS UUID), CAST('a0000000-0000-0000-0000-000000000001' AS UUID)),
-- Смартфоны Samsung
(CAST('b1000000-0000-0000-0000-000000000002' AS UUID), CAST('a0000000-0000-0000-0000-000000000002' AS UUID)),
-- Смартфоны Xiaomi
(CAST('b1000000-0000-0000-0000-000000000003' AS UUID), CAST('a0000000-0000-0000-0000-000000000003' AS UUID)),

-- Ноутбуки Apple
(CAST('b2000000-0000-0000-0000-000000000001' AS UUID), CAST('a0000000-0000-0000-0000-000000000005' AS UUID)),
-- Игровые ноутбуки
(CAST('b2000000-0000-0000-0000-000000000002' AS UUID), CAST('a0000000-0000-0000-0000-000000000006' AS UUID)),
-- Бизнес-ноутбуки
(CAST('b2000000-0000-0000-0000-000000000003' AS UUID), CAST('a0000000-0000-0000-0000-000000000007' AS UUID)),
(CAST('b2000000-0000-0000-0000-000000000003' AS UUID), CAST('a0000000-0000-0000-0000-000000000008' AS UUID)),
-- Планшеты
(CAST('b2000000-0000-0000-0000-000000000004' AS UUID), CAST('a0000000-0000-0000-0000-000000000009' AS UUID)),
(CAST('b2000000-0000-0000-0000-000000000004' AS UUID), CAST('a0000000-0000-0000-0000-000000000010' AS UUID)),
(CAST('b2000000-0000-0000-0000-000000000004' AS UUID), CAST('a0000000-0000-0000-0000-000000000011' AS UUID)),

-- Наушники и аудио
(CAST('b3000000-0000-0000-0000-000000000001' AS UUID), CAST('a0000000-0000-0000-0000-000000000012' AS UUID)),
(CAST('b3000000-0000-0000-0000-000000000001' AS UUID), CAST('a0000000-0000-0000-0000-000000000013' AS UUID)),
(CAST('b3000000-0000-0000-0000-000000000002' AS UUID), CAST('a0000000-0000-0000-0000-000000000014' AS UUID)),
(CAST('b3000000-0000-0000-0000-000000000003' AS UUID), CAST('a0000000-0000-0000-0000-000000000015' AS UUID)),

-- Умные часы
(CAST('b1000000-0000-0000-0000-000000000004' AS UUID), CAST('a0000000-0000-0000-0000-000000000016' AS UUID)),
(CAST('b1000000-0000-0000-0000-000000000004' AS UUID), CAST('a0000000-0000-0000-0000-000000000017' AS UUID)),
(CAST('b1000000-0000-0000-0000-000000000004' AS UUID), CAST('a0000000-0000-0000-0000-000000000018' AS UUID)),

-- Комплектующие
(CAST('b2000000-0000-0000-0000-000000000005' AS UUID), CAST('a0000000-0000-0000-0000-000000000019' AS UUID)),
(CAST('b2000000-0000-0000-0000-000000000005' AS UUID), CAST('a0000000-0000-0000-0000-000000000020' AS UUID)),
(CAST('b2000000-0000-0000-0000-000000000005' AS UUID), CAST('a0000000-0000-0000-0000-000000000021' AS UUID)),
(CAST('b2000000-0000-0000-0000-000000000005' AS UUID), CAST('a0000000-0000-0000-0000-000000000022' AS UUID)),

-- Сетевое оборудование
(CAST('b0000000-0000-0000-0000-000000000002' AS UUID), CAST('a0000000-0000-0000-0000-000000000023' AS UUID)),
(CAST('b0000000-0000-0000-0000-000000000002' AS UUID), CAST('a0000000-0000-0000-0000-000000000024' AS UUID)),

-- Аксессуары
(CAST('b2000000-0000-0000-0000-000000000005' AS UUID), CAST('a0000000-0000-0000-0000-000000000025' AS UUID)),
(CAST('b2000000-0000-0000-0000-000000000005' AS UUID), CAST('a0000000-0000-0000-0000-000000000026' AS UUID)),

-- Игровые приставки
(CAST('b4000000-0000-0000-0000-000000000001' AS UUID), CAST('a0000000-0000-0000-0000-000000000027' AS UUID)),
(CAST('b4000000-0000-0000-0000-000000000002' AS UUID), CAST('a0000000-0000-0000-0000-000000000028' AS UUID)),
(CAST('b4000000-0000-0000-0000-000000000003' AS UUID), CAST('a0000000-0000-0000-0000-000000000029' AS UUID)),

-- Техника для дома
(CAST('b5000000-0000-0000-0000-000000000001' AS UUID), CAST('a0000000-0000-0000-0000-000000000030' AS UUID)),
(CAST('b5000000-0000-0000-0000-000000000001' AS UUID), CAST('a0000000-0000-0000-0000-000000000031' AS UUID)),
(CAST('b5000000-0000-0000-0000-000000000002' AS UUID), CAST('a0000000-0000-0000-0000-000000000032' AS UUID));