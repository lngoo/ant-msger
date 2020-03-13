CREATE TABLE `topic` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `topic_id` varchar(32) NOT NULL,
  app_key varchar (64) not null comment '应用注册码',
  topic_name varchar(64) not null comment '主题名称',
  biz_id varchar (32) comment '业务ID',
  biz_type varchar (32) comment '业务类型',
  time_type int(1) default 0 comment '类型：1临时 0永久',
  `expire_time` datetime DEFAULT NULL comment '到期时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique-index` (`topic_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

CREATE TABLE `topic_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `topic_id` varchar(32) NOT NULL,
  `user_id` varchar(32) NOT NULL comment '用户id',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique-index` (`topic_id`,`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;