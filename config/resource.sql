drop table if exists resource cascade;
create table resource (id serial not null, payload bytea, primary key (id));