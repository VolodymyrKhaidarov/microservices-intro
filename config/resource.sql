drop table if exists resource cascade;
create table resource (id serial not null, bucket varchar, path varchar, resource_key varchar, primary key (id));