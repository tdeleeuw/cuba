-- add not null constraints for SYS_CONFIG

alter table SYS_CONFIG alter column NAME varchar(255) not null;
alter table SYS_CONFIG alter column VALUE_ varchar(max) not null;

drop constraint IDX_SYS_CONFIG_UNIQ_NAME;
alter table SYS_CONFIG add constraint IDX_SYS_CONFIG_UNIQ_NAME unique (NAME);