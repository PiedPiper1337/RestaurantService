# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table youtube_video (
  id                        bigint auto_increment not null,
  video_id                  varchar(255),
  transcript                LONGTEXT,
  constraint pk_youtube_video primary key (id))
;




# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table youtube_video;

SET FOREIGN_KEY_CHECKS=1;

