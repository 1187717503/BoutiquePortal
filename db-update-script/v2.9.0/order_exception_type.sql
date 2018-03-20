/*
Navicat MySQL Data Transfer

Source Server         : staging
Source Server Version : 50634
Source Host           : rm-uf66f4a55b173k4cwo.mysql.rds.aliyuncs.com:3306
Source Database       : ger_sh_staging_db

Target Server Type    : MYSQL
Target Server Version : 50634
File Encoding         : 65001

Date: 2018-03-20 17:41:05
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for order_exception_type
-- ----------------------------
DROP TABLE IF EXISTS `order_exception_type`;
CREATE TABLE `order_exception_type` (
  `order_exception_type_id` bigint(64) NOT NULL AUTO_INCREMENT COMMENT '原因类型ID',
  `description` varchar(100) DEFAULT NULL COMMENT '原因',
  PRIMARY KEY (`order_exception_type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 COMMENT='订单原因类型表';

-- ----------------------------
-- Records of order_exception_type
-- ----------------------------
INSERT INTO `order_exception_type` VALUES ('1', 'Out of stock-Product sold in boutique or other channels shortly before receiving IntraMirror order');
INSERT INTO `order_exception_type` VALUES ('2', 'Unavailable-Product reserved for boutique or other channels');
INSERT INTO `order_exception_type` VALUES ('3', 'Disalignment-Product in inventory system couldn\'t be found physically');
INSERT INTO `order_exception_type` VALUES ('4', 'Boutique order interface error');
INSERT INTO `order_exception_type` VALUES ('5', 'Data error-Product sold in boutique long ago, inconsistent stock');
INSERT INTO `order_exception_type` VALUES ('6', ' Quality issue-Product verified to be defected');
INSERT INTO `order_exception_type` VALUES ('7', 'Operation error');
