insert into users(login, password, registration_date)
values ('admin', '$2a$06$znGHACLbOg2NwHMIEDRHguhR9jgTvOxEeWZFreoz37DyeayEVIJzC', now()),
       ('technic', '$2a$06$znGHACLbOg2NwHMIEDRHguhR9jgTvOxEeWZFreoz37DyeayEVIJzC', now()),
       ('seller', '$2a$06$znGHACLbOg2NwHMIEDRHguhR9jgTvOxEeWZFreoz37DyeayEVIJzC', now()),
       ('customer', '$2a$06$znGHACLbOg2NwHMIEDRHguhR9jgTvOxEeWZFreoz37DyeayEVIJzC', now())
on conflict do nothing; -- пароль: 0000

insert into authorities(user_id, role)
values (1, 'Administrator'),
       (2, 'Technic'),
       (3, 'Seller'),
       (4, 'UnverifiedUser')
on conflict do nothing;

insert into profiles(id, first_name, last_name, patronymic)
values (1, 'Иван', 'Иванов', 'Иванович')
on conflict do nothing;

insert into engine_types(name)
values ('Дизельный'),
       ('Электродвигатель'),
       ('Газовый'),
       ('Бензиновый')
on conflict do nothing;

insert into engines(model, volume, engine_type_id)
values ('KDR8М23', 100, 1)
on conflict do nothing;