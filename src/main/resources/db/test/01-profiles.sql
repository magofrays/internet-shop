--liquibase formatted sql
--changeset magofrays:test

insert into profile(first_name, last_name, password, role_id)
values ('Dmitry', 'Ivanov', '111qqw2', 0),
       ('Alexey', 'Petrov', '112qq23', 1),
       ('Matvey', 'Chensky', '12qaw12', 0);
