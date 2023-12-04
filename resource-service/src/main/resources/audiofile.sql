drop table if exists audiofile cascade;
create table audiofile (id serial not null, payload bytea, primary key (id));