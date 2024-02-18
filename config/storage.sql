drop table if exists storages cascade;
create table storages (id serial not null, storage_type varchar(255), bucket varchar(255), path varchar(255), primary key (id));

insert into storages (id, storage_type, bucket, path)
values (1, 'STAGING', 'staging', '/staging'),
       (2, 'PERMANENT', 'permanent', '/permanent');