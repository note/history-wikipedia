# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table background_job (
  id                        bigint auto_increment not null,
  finished                  tinyint(1) default 0,
  category                  varchar(255),
  report                    TEXT,
  constraint pk_background_job primary key (id))
;

create table simple_graph (
  id                        bigint auto_increment not null,
  from_person               bigint,
  to_person                 bigint,
  weight                    bigint,
  from_name                 varchar(255),
  to_name                   varchar(255),
  job_id                    bigint,
  constraint pk_simple_graph primary key (id))
;




# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table background_job;

drop table simple_graph;

SET FOREIGN_KEY_CHECKS=1;

