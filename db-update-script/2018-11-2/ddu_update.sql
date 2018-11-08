ALTER TABLE `geography`
ADD COLUMN `boutique_use`  int(1) NULL DEFAULT 0 COMMENT '买手店发货使用大区选择' AFTER `warehouse_use`;

INSERT INTO `geography` (`geography_id`, `name`, `ship_fee`, `tax_fee`, `display_order`, `english_name`, `pack_group`, `pack_english_name`, `description`, `enabled`, `warehouse_use`, `boutique_use`) VALUES ('7', '荷兰邮政', NULL, NULL, '7', 'COMO', '6', 'COMO', NULL, '1', '0', '1');
