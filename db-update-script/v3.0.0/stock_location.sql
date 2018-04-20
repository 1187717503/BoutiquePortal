/*
Navicat MySQL Data Transfer

Source Server         : test
Source Server Version : 50634
Source Host           : rm-uf66f4a55b173k4cwo.mysql.rds.aliyuncs.com:3306
Source Database       : sha-testing-db

Target Server Type    : MYSQL
Target Server Version : 50634
File Encoding         : 65001

Date: 2018-04-19 19:28:51
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for stock_location
-- ----------------------------
DROP TABLE IF EXISTS `stock_location`;
CREATE TABLE `stock_location` (
  `location_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `stock_location` varchar(255) DEFAULT NULL,
  `vendor_id` bigint(20) DEFAULT NULL,
  `create_at` datetime DEFAULT NULL,
  `update_at` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `enabled` bit(1) DEFAULT NULL,
  PRIMARY KEY (`location_id`)
) ENGINE=InnoDB AUTO_INCREMENT=70 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of stock_location
-- ----------------------------
INSERT INTO `stock_location` VALUES ('1', 'tony_location1', '16', '2018-04-09 14:00:52', '2018-04-09 14:01:03', '');
INSERT INTO `stock_location` VALUES ('2', 'tony_location2', '16', '2018-04-09 14:01:18', '2018-04-19 19:20:11', '\0');
INSERT INTO `stock_location` VALUES ('3', 'Dante_location1', '10', '2018-04-09 14:02:37', '2018-04-09 14:02:40', '');
INSERT INTO `stock_location` VALUES ('4', 'Dante_location2', '10', '2018-04-09 14:03:02', '2018-04-09 14:03:09', '');
INSERT INTO `stock_location` VALUES ('5', 'Julian_location1', '26', '2018-04-09 14:03:53', '2018-04-09 14:03:55', '');
INSERT INTO `stock_location` VALUES ('6', 'tony_location3', '16', '2018-04-09 14:04:22', '2018-04-19 19:20:09', '\0');
INSERT INTO `stock_location` VALUES ('7', 'Boutique Ops', '7', null, null, '');
INSERT INTO `stock_location` VALUES ('8', 'Luciana Bari', '8', null, null, '');
INSERT INTO `stock_location` VALUES ('9', 'Nugnes 1920', '9', null, null, '');
INSERT INTO `stock_location` VALUES ('10', 'Dante 5', '10', null, null, '');
INSERT INTO `stock_location` VALUES ('11', 'I Cinque Fiori', '11', null, null, '');
INSERT INTO `stock_location` VALUES ('12', 'Mimma Ninni', '12', null, null, '');
INSERT INTO `stock_location` VALUES ('13', 'Di Pierro', '13', null, null, '');
INSERT INTO `stock_location` VALUES ('14', 'Gisa Boutique', '14', null, null, '');
INSERT INTO `stock_location` VALUES ('15', 'Anna Virgili', '15', null, null, '');
INSERT INTO `stock_location` VALUES ('16', 'Tony Boutique', '16', null, null, '');
INSERT INTO `stock_location` VALUES ('17', 'Filippo M.', '17', null, null, '');
INSERT INTO `stock_location` VALUES ('18', 'Wise Boutique', '18', null, null, '');
INSERT INTO `stock_location` VALUES ('19', 'Quadra', '19', null, null, '');
INSERT INTO `stock_location` VALUES ('20', 'The Apartment Cosenza', '20', null, null, '');
INSERT INTO `stock_location` VALUES ('21', 'Base Blu', '21', null, null, '');
INSERT INTO `stock_location` VALUES ('22', 'Al Duca Daosta', '22', null, null, '');
INSERT INTO `stock_location` VALUES ('23', 'Sugar Boutique', '23', null, null, '');
INSERT INTO `stock_location` VALUES ('24', 'Coltorti', '24', null, null, '');
INSERT INTO `stock_location` VALUES ('25', 'Gaudenzi', '25', null, null, '');
INSERT INTO `stock_location` VALUES ('26', 'Julian', '26', null, null, '');
INSERT INTO `stock_location` VALUES ('27', 'Valenti', '27', null, null, '');
INSERT INTO `stock_location` VALUES ('28', 'Divo', '28', null, null, '');
INSERT INTO `stock_location` VALUES ('29', 'AND Boutique', '29', null, null, '');
INSERT INTO `stock_location` VALUES ('30', 'IntraMirror Boutique', '30', null, null, '');
INSERT INTO `stock_location` VALUES ('31', 'Bagheera boutique', '31', null, null, '');
INSERT INTO `stock_location` VALUES ('32', 'AMR Fashion', '32', null, null, '');
INSERT INTO `stock_location` VALUES ('33', 'Lungolivigno', '33', null, null, '');
INSERT INTO `stock_location` VALUES ('34', 'Leam', '34', null, null, '');
INSERT INTO `stock_location` VALUES ('35', 'O Boutique', '35', null, null, '');
INSERT INTO `stock_location` VALUES ('36', 'Tricot', '36', null, null, '');
INSERT INTO `stock_location` VALUES ('37', 'Dolci Trame', '37', null, null, '');
INSERT INTO `stock_location` VALUES ('38', 'Spinnaker', '38', null, null, '');
INSERT INTO `stock_location` VALUES ('39', 'Mimma Ninni Due', '39', null, null, '');
INSERT INTO `stock_location` VALUES ('40', 'Spinnaker Sanremo', '40', null, null, '');
INSERT INTO `stock_location` VALUES ('41', 'Spinnaker Portofino', '41', null, null, '');
INSERT INTO `stock_location` VALUES ('42', 'Eleonora Bonucci', '42', null, null, '');
INSERT INTO `stock_location` VALUES ('43', 'Tony 2', '43', null, null, '');
INSERT INTO `stock_location` VALUES ('44', 'Gibot', '44', null, null, '');
INSERT INTO `stock_location` VALUES ('45', 'Lino Ricci', '45', null, null, '');
INSERT INTO `stock_location` VALUES ('46', 'Satu', '46', null, null, '');
INSERT INTO `stock_location` VALUES ('47', 'Suit', '47', null, null, '');
INSERT INTO `stock_location` VALUES ('48', 'Gallery', '48', null, null, '');
INSERT INTO `stock_location` VALUES ('49', 'Carofiglio', '49', null, null, '');
INSERT INTO `stock_location` VALUES ('50', 'Zita Fabiani', '50', null, null, '');
INSERT INTO `stock_location` VALUES ('51', 'Bruna Rosso', '51', null, null, '');
INSERT INTO `stock_location` VALUES ('52', 'Gente', '52', null, null, '');
INSERT INTO `stock_location` VALUES ('53', 'Mancini Junior', '63', null, null, '');
