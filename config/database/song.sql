drop table if exists song cascade;
create table song (id serial not null, artist varchar(255), album varchar(255), length varchar(255), name varchar(255), year varchar(255), resource_id varchar(255), primary key (id));