CREATE TABLE `member_points_error_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_line_num` varchar(32) DEFAULT NULL COMMENT '订单号',
  `request_body` varchar(1000) DEFAULT NULL COMMENT '会员积分，请求参数',
  `response_body` varchar(1000) DEFAULT NULL COMMENT '接口返回报文',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` int(1) DEFAULT '0' COMMENT '是否删除 0：未删除 1：已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

