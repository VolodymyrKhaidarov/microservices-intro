drop table if exists audiofiles cascade;
create table audiofiles (id serial not null, payload bytea, primary key (id));
