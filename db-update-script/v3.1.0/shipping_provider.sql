/*
Navicat MySQL Data Transfer

Source Server         : staging
Source Server Version : 50634
Source Host           : rm-uf66f4a55b173k4cwo.mysql.rds.aliyuncs.com:3306
Source Database       : ger_sh_staging_db

Target Server Type    : MYSQL
Target Server Version : 50634
File Encoding         : 65001

Date: 2018-04-27 11:21:24
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for shipping_provider
-- ----------------------------
DROP TABLE IF EXISTS `shipping_provider`;
CREATE TABLE `shipping_provider` (
  `shipping_provider_id` bigint(20) NOT NULL,
  `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `service_type` varchar(5) DEFAULT NULL,
  `transfer_consignee` varchar(64) DEFAULT NULL COMMENT '收件人',
  `person_name` varchar(64) DEFAULT NULL,
  `transfer_addr` varchar(256) DEFAULT NULL COMMENT '收件详细地址',
  `transfer_addr2` varchar(255) DEFAULT NULL,
  `transfer_addr3` varchar(255) DEFAULT NULL,
  `emailAddress` varchar(255) DEFAULT NULL,
  `transfer_contact` varchar(255) DEFAULT NULL COMMENT '联系方式',
  `transfer_piva` varchar(255) DEFAULT NULL COMMENT '税号',
  `addr_district` varchar(256) DEFAULT NULL COMMENT '街道',
  `addr_city` varchar(64) DEFAULT NULL COMMENT '城市',
  `addr_province` varchar(64) DEFAULT NULL COMMENT '省',
  `addr_country` varchar(64) DEFAULT NULL COMMENT '国家',
  `country_code` varchar(5) DEFAULT NULL COMMENT '国家编号',
  `zip_code` varchar(10) DEFAULT NULL COMMENT '邮编',
  PRIMARY KEY (`shipping_provider_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of shipping_provider
-- ----------------------------
INSERT INTO `shipping_provider` VALUES ('1', 'DHL', 'P', 'JOAN CHONG', 'cheng', 'NO.22 SZE SHAN STREET YAU TONG', 'MAI TONG INDUSTIRAL BUILDING', 'FLAT B, 2F', 'arthur@intramirror.com', '1545632655', '21321354654', null, 'KOWLOON', null, 'HK Hong Kong', 'HK', '999079');
INSERT INTO `shipping_provider` VALUES ('2', 'VENTANA SERRA SPA', null, 'Rene Marrazzo', 'cheng', 'VIA F.L. MILES 9 10', null, null, null, null, null, null, 'CAVENAGO DI BRIANZA', null, 'Italy', null, '20873');
INSERT INTO `shipping_provider` VALUES ('3', 'ZHANG CHENGCHENG', null, 'JOAN CHONG', 'cheng', 'FLAT B, 2F,MAI TONG INDUSTIRAL BUILDING NO.22 SZE SHAN STREET YAU TONG,KOWLOON', null, null, null, null, null, null, 'KOWLOON', null, 'HK Hong Kong', null, null);
INSERT INTO `shipping_provider` VALUES ('4', 'ZSY', null, 'JOAN CHONG', 'cheng', 'FLAT B, 2F,MAI TONG INDUSTIRAL BUILDING NO.22 SZE SHAN STREET YAU TONG,KOWLOON', null, null, null, null, null, null, 'KOWLOON', null, 'HK Hong Kong', null, null);
INSERT INTO `shipping_provider` VALUES ('5', 'DHL', 'N', 'ZHANG', 'Li Pengfei', 'via Maria Montessori ,18', '', '', 'pengfei.li@intramirror.com', '+39 3914891338', '03384730549', '', 'Matassino (Reggello)', 'Firenze', 'Italy', 'IT', '50066');
INSERT INTO `shipping_provider` VALUES ('6', 'DHL', 'U', 'Acme Inc', 'arthur', '500 Hunt Valley Road', null, null, 'jackie.chan@eei.com', '88347346643', null, null, 'Paris', null, null, 'FR', '75002');




UPDATE `shipping_segment` SET `shipping_provider_id`='5' WHERE (`shipping_segment_id`='208') LIMIT 1;

UPDATE shipping_segment ss,shipping_routing sr,address_country ac SET ss.shipping_provider_id = 6
WHERE ss.shipping_routing_id = sr.shipping_routing_id and ac.address_country_id = sr.consignee_country_id
and ac.geography_id = 3 and ss.segment_sequence = 1  and shipping_segment_id != 1;