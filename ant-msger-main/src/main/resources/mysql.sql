-- CREATE TABLE `user_device` (
--   `id` int(255) NOT NULL AUTO_INCREMENT,
--   `user_id` varchar(32) NOT NULL,
--   `device_id` varchar(32) NOT NULL,
--   `expire_time` datetime DEFAULT NULL,
--   PRIMARY KEY (`id`)
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `topic_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `topic_id` varchar(32) NOT NULL,
  `user_id` varchar(32) NOT NULL,
  `expire_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique-index` (`topic_id`,`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;