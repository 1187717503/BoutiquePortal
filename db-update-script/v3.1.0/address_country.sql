/*
Navicat MySQL Data Transfer

Source Server         : staging
Source Server Version : 50634
Source Host           : rm-uf66f4a55b173k4cwo.mysql.rds.aliyuncs.com:3306
Source Database       : ger_sh_staging_db

Target Server Type    : MYSQL
Target Server Version : 50634
File Encoding         : 65001

Date: 2018-04-23 12:07:47
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for address_country
-- ----------------------------
DROP TABLE IF EXISTS `address_country`;
CREATE TABLE `address_country` (
  `address_country_id` bigint(20) NOT NULL COMMENT '国家id',
  `name` varchar(64) NOT NULL COMMENT '国家名称',
  `country_code` varchar(5) DEFAULT NULL COMMENT '国家编码',
  `geography_id` bigint(64) NOT NULL,
  PRIMARY KEY (`address_country_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='地址国家类目表';

-- ----------------------------
-- Records of address_country
-- ----------------------------
INSERT INTO `address_country` VALUES ('1', '巴基斯坦', 'PK', '4');
INSERT INTO `address_country` VALUES ('2', '中国', 'CN', '1');
INSERT INTO `address_country` VALUES ('3', '中国香港', 'HK', '2');
INSERT INTO `address_country` VALUES ('4', '中国澳门', 'MO', '2');
INSERT INTO `address_country` VALUES ('5', '奥地利', 'AT', '3');
INSERT INTO `address_country` VALUES ('6', '比利时', 'BE', '3');
INSERT INTO `address_country` VALUES ('7', '保加利亚', 'BG', '3');
INSERT INTO `address_country` VALUES ('8', '克罗地亚', 'HR', '3');
INSERT INTO `address_country` VALUES ('9', '塞浦路斯', 'CY', '3');
INSERT INTO `address_country` VALUES ('10', '捷克', 'CZ', '3');
INSERT INTO `address_country` VALUES ('11', '丹麦', 'DK', '3');
INSERT INTO `address_country` VALUES ('12', '爱沙尼亚', 'EE', '3');
INSERT INTO `address_country` VALUES ('13', '芬兰', 'FI', '3');
INSERT INTO `address_country` VALUES ('14', '法国', 'FR', '3');
INSERT INTO `address_country` VALUES ('15', 'Germany ', 'DE', '3');
INSERT INTO `address_country` VALUES ('16', '希腊', 'GR', '3');
INSERT INTO `address_country` VALUES ('17', '匈牙利', 'HU', '3');
INSERT INTO `address_country` VALUES ('18', '爱尔兰', 'IE', '3');
INSERT INTO `address_country` VALUES ('19', '意大利', 'IT', '3');
INSERT INTO `address_country` VALUES ('20', '拉脱维亚', 'LV', '3');
INSERT INTO `address_country` VALUES ('21', '立陶宛', 'LT', '3');
INSERT INTO `address_country` VALUES ('22', '卢森堡', 'LU', '3');
INSERT INTO `address_country` VALUES ('23', '马耳他', 'MT', '3');
INSERT INTO `address_country` VALUES ('24', '荷兰', 'NL', '3');
INSERT INTO `address_country` VALUES ('25', '波兰', 'PL', '3');
INSERT INTO `address_country` VALUES ('26', '葡萄牙', 'PT', '3');
INSERT INTO `address_country` VALUES ('27', '罗马尼亚', 'RO', '3');
INSERT INTO `address_country` VALUES ('28', '斯洛伐克', 'SK', '3');
INSERT INTO `address_country` VALUES ('29', '斯洛文尼亚', 'SI', '3');
INSERT INTO `address_country` VALUES ('30', '西班牙', 'ES', '3');
INSERT INTO `address_country` VALUES ('31', '瑞典', 'SE', '3');
INSERT INTO `address_country` VALUES ('32', '英国', 'GB', '3');
INSERT INTO `address_country` VALUES ('40', '美国', 'US', '5');
INSERT INTO `address_country` VALUES ('41', '俄罗斯', 'RU', '5');
INSERT INTO `address_country` VALUES ('42', '加拿大', 'CA', '5');
INSERT INTO `address_country` VALUES ('43', '澳大利亚', 'AU', '5');
INSERT INTO `address_country` VALUES ('44', '新西兰', 'NZ', '5');
INSERT INTO `address_country` VALUES ('45', '中国台湾', 'TW', '5');
INSERT INTO `address_country` VALUES ('46', '菲律宾', 'PH', '5');
INSERT INTO `address_country` VALUES ('47', '韩国', 'KR', '5');
INSERT INTO `address_country` VALUES ('48', '马来西亚', 'MY', '5');
INSERT INTO `address_country` VALUES ('49', '日本', 'JP', '5');
INSERT INTO `address_country` VALUES ('50', '泰国', 'TH', '5');
INSERT INTO `address_country` VALUES ('51', '新加坡', 'SG', '5');
INSERT INTO `address_country` VALUES ('52', 'Transit Warehouse', 'IT', '6');
