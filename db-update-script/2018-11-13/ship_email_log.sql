CREATE TABLE `ship_email_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `shipment_no` varchar(16) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'shipment编号',
  `email_body` varchar(2000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'email推送报文信息',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` int(1) DEFAULT '0' COMMENT '是否删除  1. 是 0.不是',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

