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
insert into tag(tag_name,enabled) values ("16FW",1);
insert into tag(tag_name,enabled) values ("16SS",1);
insert into tag(tag_name,enabled) values ("17FW",1);
insert into tag(tag_name,enabled) values ("17SS",1);
insert into tag(tag_name,enabled) values ("18SS",1);
insert into tag(tag_name,enabled) values ("18FW",1);

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
	`create_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`modified_at`timestamp NOT NULL DEFAULT NULL,
	`content` BLOB NULL,
	`bk_color` CHAR(32) NULL DEFAULT NULL COMMENT 'background color',
	`sort_order` INT(11) NULL DEFAULT NULL,
	`subtitle` VARCHAR(256) NULL DEFAULT NULL,
	`title_english` VARCHAR(256) NULL DEFAULT NULL,
	`enabled` BIT(1) NOT NULL,
	PRIMARY KEY (`block_id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
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