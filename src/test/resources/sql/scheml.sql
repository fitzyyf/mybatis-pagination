create table res
(
  id                   varchar(64) not null comment '主键',
  name                 varchar(120) comment '资源名称',
  type                 varchar(120) comment '资源类型',
  path                 varchar(120) comment '资源地址',
  action               varchar(120) comment '资源请求',
  controller           varchar(120) comment '资源控制',
  status               tinyint comment '状态',
  primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;;