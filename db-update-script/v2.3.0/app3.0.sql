DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag` (
	`tag_id` BIGINT(64) NOT NULL AUTO_INCREMENT,
	`tag_name` VARCHAR(190) NOT NULL COMMENT 'Tag名字',
	`created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`enabled` BIT(1) NOT NULL,
	PRIMARY KEY (`tag_id`),
	UNIQUE INDEX `UNIQUE_TAG_NAME` (`tag_name`, `enabled`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1
;

DROP TABLE IF EXISTS `tag_product_rel`;
CREATE TABLE `tag_product_rel` (
 `tag_product_id` BIGINT(64) NOT NULL AUTO_INCREMENT,
 `tag_id` BIGINT(64) NOT NULL,
 `product_id` BIGINT(64) NOT NULL,
 `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
 `sort_num` BIGINT(64) NOT NULL COMMENT '排序', 
 PRIMARY KEY (`tag_product_id`), 
 INDEX `IDX_TAG_ID` (`tag_id`), 
 INDEX `IDX_PRODUCT_ID` (`product_id`)
) COLLATE='utf8_general_ci' ENGINE=InnoDB AUTO_INCREMENT=1
;

DROP TABLE IF EXISTS `block`;
CREATE TABLE `block` (
	`block_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`block_name` VARCHAR(190) NOT NULL,
	`status` TINYINT(4) NOT NULL COMMENT '0:inactive;1:active',
	`title` VARCHAR(256) NULL DEFAULT NULL,
	`title_english` VARCHAR(256) NULL DEFAULT NULL,
	`subtitle` VARCHAR(256) NULL DEFAULT NULL,
	`content` BLOB NULL,
	`bg_color` CHAR(32) NULL DEFAULT NULL COMMENT 'background color',
	`sort_order` INT(11) NULL DEFAULT NULL,
	`create_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`modified_at` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00',
	`enabled` BIT(1) NOT NULL,
	`cover_img` VARCHAR(1024) NULL DEFAULT NULL,
	PRIMARY KEY (`block_id`),
	UNIQUE INDEX `UNIQUE_BLOCK_NAME` (`block_name`, `enabled`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1
;

DROP TABLE IF EXISTS `block_tag_rel`;
CREATE TABLE `block_tag_rel` (
	`block_tag_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`tag_id` BIGINT(20) NOT NULL,
	`block_id` BIGINT(20) NULL DEFAULT NULL,
	`create_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (`block_tag_id`),
	INDEX `block_tag_rel_tag_id_index` (`tag_id`),
	INDEX `block_tag_rel_block_id_index` (`block_id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;


alter table `product`  add `preview_im_price` decimal(16,4) DEFAULT  NULL COMMENT '预热售价' ;

alter table `price_change_rule`  add `preview_status` int(2) default 0 COMMENT  '0 非活动折扣 1 活动折扣';

alter table `logistics_product`  add `retail_price` decimal(16,4) DEFAULT  NULL COMMENT '商城售价' ;
update `logistics_product`  lp inner join `shop_product_sku`  sps on(lp.`shop_product_sku_id` = sps.`shop_product_sku_id`  and sps.`enabled`  = 1 and lp.`enabled`  = 1 )
inner join `sku`  s on(s.`sku_id` = sps.`sku_id`  and s.`enabled`  = 1 )
set lp.`retail_price` = s.`price`  ;

alter table `product`  add `max_im_price` decimal(16,4) DEFAULT NULL COMMENT 'sku.im_price冗余字段';
alter table `product`  add `min_im_price` decimal(16,4) DEFAULT NULL COMMENT 'sku.im_price冗余字段';
update `product`  p,sku set p.`min_im_price` = sku.`im_price`,p.`max_im_price` = sku.`im_price`
 where p.`enabled`  = 1 and sku.`enabled`  = 1 and p.`product_id`  = sku.`product_id` ;





