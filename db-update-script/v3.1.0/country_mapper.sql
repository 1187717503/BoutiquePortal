/*
Navicat MySQL Data Transfer

Source Server         : staging
Source Server Version : 50634
Source Host           : rm-uf66f4a55b173k4cwo.mysql.rds.aliyuncs.com:3306
Source Database       : ger_sh_staging_db

Target Server Type    : MYSQL
Target Server Version : 50634
File Encoding         : 65001

Date: 2018-04-28 17:46:20
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for country_mapper
-- ----------------------------
DROP TABLE IF EXISTS `country_mapper`;
CREATE TABLE `country_mapper` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `country` varchar(20) DEFAULT NULL,
  `english_name` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of country_mapper
-- ----------------------------
INSERT INTO `country_mapper` VALUES ('1', '中国', 'China');
INSERT INTO `country_mapper` VALUES ('2', '中国台湾', 'Taiwan');
INSERT INTO `country_mapper` VALUES ('3', '中国大陆', 'China Mainland');
INSERT INTO `country_mapper` VALUES ('4', '中国澳门', 'Macau');
INSERT INTO `country_mapper` VALUES ('5', '中国香港', 'Hong Kong');
INSERT INTO `country_mapper` VALUES ('6', '俄罗斯', 'Russia');
INSERT INTO `country_mapper` VALUES ('7', '加拿大', 'Canada');
INSERT INTO `country_mapper` VALUES ('8', '匈牙利', 'Hungary');
INSERT INTO `country_mapper` VALUES ('9', '奥地利', 'Austria');
INSERT INTO `country_mapper` VALUES ('10', '德国', 'Germany');
INSERT INTO `country_mapper` VALUES ('11', '意大利', 'Italy');
INSERT INTO `country_mapper` VALUES ('12', '新西兰', 'New Zealand');
INSERT INTO `country_mapper` VALUES ('13', '日本', 'Japan');
INSERT INTO `country_mapper` VALUES ('14', '法国', 'France');
INSERT INTO `country_mapper` VALUES ('15', '澳大利亚', 'Australia');
INSERT INTO `country_mapper` VALUES ('16', '美国', 'America');
INSERT INTO `country_mapper` VALUES ('17', '英国', 'Britain');
INSERT INTO `country_mapper` VALUES ('18', '西班牙', 'Spain');
INSERT INTO `country_mapper` VALUES ('19', '韩国', 'Korea');
