create table if not exists public.engine_types
(
    id   serial
        primary key,
    name varchar(20) not null
        constraint uk_enginetype_name
            unique
);

alter table public.engine_types
    owner to postgres;

create table if not exists public.engines
(
    id             serial
        primary key,
    model          varchar(30)      not null,
    volume         double precision not null
        constraint engines_volume_check
            check ((volume <= (1000)::double precision) AND (volume >= (5)::double precision)),
    engine_type_id integer          not null
        constraint fk_engine_enginetype
            references public.engine_types
);

alter table public.engines
    owner to postgres;

create table if not exists public.motorcycles
(
    id        serial
        primary key,
    model     varchar(20)      not null,
    price     double precision not null
        constraint motorcycles_price_check
            check ((price <= (1000000000)::double precision) AND (price >= (10)::double precision)),
    engine_id integer          not null
        constraint fk_motorcycle_engine
            references public.engines
);

alter table public.motorcycles
    owner to postgres;

create table if not exists public.images
(
    id            serial
        primary key,
    data          bytea        not null,
    name          varchar(255) not null,
    motorcycle_id integer
        constraint fk_image_motorcycle
            references public.motorcycles
);

alter table public.images
    owner to postgres;

create table if not exists public.users
(
    id                serial
        primary key,
    login             varchar(25)  not null
        constraint uk_user_login
            unique,
    password          varchar(255) not null,
    registration_date timestamp    not null
);

alter table public.users
    owner to postgres;

create table if not exists public.authorities
(
    user_id integer      not null
        constraint fk_authority_user
            references public.users,
    role    varchar(255) not null,
    primary key (user_id, role)
);

alter table public.authorities
    owner to postgres;

create table if not exists public.profiles
(
    id         integer not null
        primary key
        constraint fk_profile_user
            references public.users,
    first_name varchar(255),
    last_name  varchar(255),
    patronymic varchar(255)
);

alter table public.profiles
    owner to postgres;

create table if not exists public.purchased_motorcycles
(
    profile_id    integer not null
        constraint fk_purchasedmotorcycles_profile
            references public.profiles,
    motorcycle_id integer not null
        constraint fk_purchasedmotorcycles_motorcycle
            references public.motorcycles
);

alter table public.purchased_motorcycles
    owner to postgres;

