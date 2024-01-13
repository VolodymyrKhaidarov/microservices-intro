drop table if exists resource cascade;
create table resource (id serial not null, bucket varchar, resource_key varchar, primary key (id));