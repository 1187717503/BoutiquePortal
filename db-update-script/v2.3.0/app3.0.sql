DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag` (
	`tag_id` BIGINT(64) NOT NULL AUTO_INCREMENT,
	`tag_name` VARCHAR(64) NOT NULL COMMENT 'Tag名字',
	`created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`enabled` BIT(1) NOT NULL,
	PRIMARY KEY (`tag_id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;

-- 测试数据
insert into tag(tag_name,enabled) values ("TAG_16FW",1);
insert into tag(tag_name,enabled) values ("TAG_16SS",1);
insert into tag(tag_name,enabled) values ("TAG_17FW",1);
insert into tag(tag_name,enabled) values ("TAG_17SS",1);
insert into tag(tag_name,enabled) values ("TAG_18SS",1);
insert into tag(tag_name,enabled) values ("TAG_18FW",1);

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
	`block_name` VARCHAR(256) NOT NULL,
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
	PRIMARY KEY (`block_id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
AUTO_INCREMENT=8
;


DROP TABLE IF EXISTS `block_tag_rel`;
CREATE TABLE `block_tag_rel` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`tag_id` BIGINT(20) NOT NULL,
	`block_id` BIGINT(20) NULL DEFAULT NULL,
	`create_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (`id`),
	INDEX `block_tag_rel_tag_id_index` (`tag_id`),
	INDEX `block_tag_rel_block_id_index` (`block_id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;


alter table `product`  add `preview_im_price` decimal(16,4) DEFAULT  NULL COMMENT '预热售价' ;

alter table `logistics_product`  add `retail_price` decimal(16,4) DEFAULT  NULL COMMENT '商城售价' ;
update `logistics_product`  lp inner join `shop_product_sku`  sps on(lp.`shop_product_sku_id` = sps.`shop_product_sku_id`  and sps.`enabled`  = 1 and lp.`enabled`  = 1 )
inner join `sku`  s on(s.`sku_id` = sps.`sku_id`  and s.`enabled`  = 1 )
set lp.`retail_price` = s.`price`  ;