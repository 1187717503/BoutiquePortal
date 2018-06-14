
USE `ger_sh_staging_db`;

/*Table structure for table `im_price_algorithm` */

DROP TABLE IF EXISTS `im_price_algorithm`;

CREATE TABLE `im_price_algorithm` (
  `im_price_algorithm_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `category_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '1: adult, 2:kids',
  `season_code` varchar(128) NOT NULL DEFAULT '0',
  `name` varchar(128) NOT NULL DEFAULT '0',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `enabled` bit(1) NOT NULL DEFAULT b'1',
  PRIMARY KEY (`im_price_algorithm_id`),
  KEY `type_season_code` (`category_type`,`season_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `im_price_algorithm_rule` */

DROP TABLE IF EXISTS `im_price_algorithm_rule`;

CREATE TABLE `im_price_algorithm_rule` (
  `im_price_algorithm_rule_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `im_price_algorithm_id` bigint(20) NOT NULL,
  `algorithm_rule_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '1: 默认规则，2: brand规则',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `enabled` bit(1) NOT NULL DEFAULT b'1',
  PRIMARY KEY (`im_price_algorithm_rule_id`),
  KEY `im_price_algorithm_id` (`im_price_algorithm_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `im_price_algorithm_rule_brand` */

DROP TABLE IF EXISTS `im_price_algorithm_rule_brand`;

CREATE TABLE `im_price_algorithm_rule_brand` (
  `im_price_algorithm_rule_brand_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `im_price_algorithm_rule_id` bigint(20) NOT NULL DEFAULT '0',
  `brand_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '-1: general , 其他:  brandId',
  `enabled` bit(1) NOT NULL DEFAULT b'1',
  PRIMARY KEY (`im_price_algorithm_rule_brand_id`),
  KEY `im_price_algorithm_rule_id` (`im_price_algorithm_rule_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `im_price_discount_mapping` */

DROP TABLE IF EXISTS `im_price_discount_mapping`;

CREATE TABLE `im_price_discount_mapping` (
  `im_price_discount_mapping_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `im_price_algorithm_rule_id` bigint(20) NOT NULL DEFAULT '0',
  `boutique_discount_off` int(11) NOT NULL DEFAULT '0',
  `im_discount_off` int(11) NOT NULL DEFAULT '0',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `enabled` bit(1) NOT NULL DEFAULT b'1',
  PRIMARY KEY (`im_price_discount_mapping_id`),
  KEY `im_price_algorithm_rule_id` (`im_price_algorithm_rule_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `im_price_discount_model` */

DROP TABLE IF EXISTS `im_price_discount_model`;

CREATE TABLE `im_price_discount_model` (
  `im_price_discount_model` bigint(20) NOT NULL AUTO_INCREMENT,
  `boutique_discount_off` int(11) NOT NULL COMMENT 'boutique折扣',
  `im_discount_off` int(11) NOT NULL COMMENT '默认im折扣',
  PRIMARY KEY (`im_price_discount_model`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `snapshot_price_detail` */

DROP TABLE IF EXISTS `snapshot_price_detail`;

CREATE TABLE `snapshot_price_detail` (
  `snapshot_price_detail_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `snapshot_price_rule_id` bigint(20) NOT NULL DEFAULT '0',
  `product_id` bigint(20) NOT NULL DEFAULT '0',
  `retail_price` decimal(12,4) NOT NULL DEFAULT '0.0000',
  `boutique_price` decimal(12,4) NOT NULL DEFAULT '0.0000',
  `im_price` decimal(12,4) NOT NULL DEFAULT '0.0000',
  `boutique_discount_off` int(11) NOT NULL DEFAULT '0',
  `im_discount_off` int(11) NOT NULL DEFAULT '0',
  `type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '1:boutique code ,2:product group,3:brand-sub category,4: brand-category,5:categroy-default',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `enabled` bit(1) NOT NULL DEFAULT b'1',
  PRIMARY KEY (`snapshot_price_detail_id`),
  KEY `snapshot_price_rule_id_product_id` (`snapshot_price_rule_id`,`product_id`),
  KEY `type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `snapshot_price_rule` */

DROP TABLE IF EXISTS `snapshot_price_rule`;

CREATE TABLE `snapshot_price_rule` (
  `snapshot_price_rule_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `price_change_rule_id` bigint(20) NOT NULL,
  `save_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0: 空闲 1:正在刷新',
  `valid_from` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `enabled` bit(1) NOT NULL DEFAULT b'1',
  PRIMARY KEY (`snapshot_price_rule_id`),
  KEY `price_change_rule_id` (`price_change_rule_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


alter table `price_change_rule` add column `im_price_algorithm_id` bigint(20) DEFAULT NULL;
ALTER TABLE `price_change_rule` ADD INDEX `im_price_algorithm_id` (`im_price_algorithm_id`);