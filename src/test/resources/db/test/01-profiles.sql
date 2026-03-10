--liquibase formatted sql
--changeset magofrays:test

insert into profile(id, email, first_name, last_name, password, role_id)
values ('2e94fadf-f5cb-4c06-a777-17dbaab59aa6', 'dmitry@mail.ru', 'Dmitry', 'Ivanov', '111qqw2', 0),
       ('34a4f119-3e49-4b1c-9e53-b6f86b801665', 'alexey@gmail.com','Alexey', 'Petrov', '112qq23', 1),
       ('b227cb79-1317-48c5-acbd-2e1a17c9a72a', 'matvey@yandex.ru','Matvey', 'Chensky', '12qaw12', 0);
