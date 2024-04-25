create schema if not exists storage authorization postgres;

create table if not exists storage.users (
    id serial primary key ,
    nickname varchar(30) unique ,
    email varchar(30) unique ,
    password varchar(30) not null ,
    role varchar(30) not null
);
create table if not exists storage.files (
    id serial primary key ,
    filename varchar(100) not null unique ,
    date date not null default CURRENT_DATE ,
    size bigint ,
    content bytea not null ,
    user_id int references storage.users (id)
);
