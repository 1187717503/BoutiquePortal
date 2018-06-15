ALTER TABLE `tag` ADD COLUMN `tag_type`  int(2) DEFAULT 1 COMMENT 'tag类型 1: tag 2:product group 3:爆款   默认1' AFTER `tag_name`;

ALTER TABLE `tag_product_rel` ADD COLUMN `tag_type` int(2) DEFAULT 1 COMMENT 'tag类型 1: tag 2:product group 3:爆款   默认1' AFTER `product_id`;

ALTER TABLE `tag` CHANGE COLUMN `tag_type` `tag_type` int(2) DEFAULT 1 COMMENT 'tag类型 1: tag 2:product group 3:爆款（im product group）   默认1', ADD COLUMN `vendor_id` bigint COMMENT '买手店id，type为2的时候使用' AFTER `tag_type`;


INSERT INTO `tag` (`tag_name`, `tag_type`, `vendor_id`, `enabled`) VALUES('爆款','3','-2','');