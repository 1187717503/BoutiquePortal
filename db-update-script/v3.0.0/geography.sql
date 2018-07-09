/*
Navicat MySQL Data Transfer

Source Server         : staging
Source Server Version : 50634
Source Host           : rm-uf66f4a55b173k4cwo.mysql.rds.aliyuncs.com:3306
Source Database       : ger_sh_staging_db

Target Server Type    : MYSQL
Target Server Version : 50634
File Encoding         : 65001

Date: 2018-04-12 17:50:10
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for geography
-- ----------------------------
DROP TABLE IF EXISTS `geography`;
CREATE TABLE `geography` (
  `geography_id` bigint(64) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `ship_fee` decimal(10,2) DEFAULT NULL,
  `tax_fee` decimal(10,2) DEFAULT NULL,
  `display_order` int(11) DEFAULT NULL,
  `english_name` varchar(256) DEFAULT NULL COMMENT '英文名称',
  `pack_group` int(2) DEFAULT NULL COMMENT '打包组id',
  `pack_english_name` varchar(255) DEFAULT NULL COMMENT '打包组英文名',
  `enabled` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`geography_id`),
  UNIQUE KEY `unique_name` (`name`),
  UNIQUE KEY `unique_display_order` (`display_order`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of geography
-- ----------------------------
INSERT INTO `geography` VALUES ('1', '中国大陆', '30.00', '0.00', '1', 'China Mainland', '1', 'China excl. Taiwan', '1');
INSERT INTO `geography` VALUES ('2', '港澳地区', '24.00', '0.00', '2', 'HongKong', '1', 'China excl. Taiwan', '1');
INSERT INTO `geography` VALUES ('3', '欧盟', '15.00', '0.00', '3', 'European Union', '2', 'European Union', '1');
INSERT INTO `geography` VALUES ('4', '亚洲', '45.00', '0.00', '4', 'Asia', '3', 'Asia', '0');
INSERT INTO `geography` VALUES ('5', '其他地区', '50.00', '0.00', '5', 'Other', '4', 'Other', '1');
INSERT INTO `geography` VALUES ('6', '质检仓', null, null, '6', 'Transit Warehouse', '5', 'Transit Warehouse', '1');
