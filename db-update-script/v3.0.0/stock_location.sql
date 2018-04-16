/*
Navicat MySQL Data Transfer

Source Server         : staging
Source Server Version : 50634
Source Host           : rm-uf66f4a55b173k4cwo.mysql.rds.aliyuncs.com:3306
Source Database       : ger_sh_staging_db

Target Server Type    : MYSQL
Target Server Version : 50634
File Encoding         : 65001

Date: 2018-04-12 19:01:30
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for stock_location
-- ----------------------------
DROP TABLE IF EXISTS `stock_location`;
CREATE TABLE `stock_location` (
  `location_id` bigint(20) NOT NULL,
  `stock_location` varchar(255) DEFAULT NULL,
  `vendor_id` bigint(20) DEFAULT NULL,
  `create_at` datetime DEFAULT NULL,
  `update_at` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `enabled` bit(1) DEFAULT NULL,
  PRIMARY KEY (`location_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of stock_location
-- ----------------------------
INSERT INTO `stock_location` VALUES ('1', 'tony_location1', '16', '2018-04-09 14:00:52', '2018-04-09 14:01:03', '');
INSERT INTO `stock_location` VALUES ('2', 'tony_location2', '16', '2018-04-09 14:01:18', '2018-04-09 14:01:20', '');
INSERT INTO `stock_location` VALUES ('3', 'Dante_location1', '10', '2018-04-09 14:02:37', '2018-04-09 14:02:40', '');
INSERT INTO `stock_location` VALUES ('4', 'Dante_location2', '10', '2018-04-09 14:03:02', '2018-04-09 14:03:09', '');
INSERT INTO `stock_location` VALUES ('5', 'Julian_location1', '26', '2018-04-09 14:03:53', '2018-04-09 14:03:55', '');
INSERT INTO `stock_location` VALUES ('6', 'tony_location3', '16', '2018-04-09 14:04:22', '2018-04-09 14:04:24', '');
