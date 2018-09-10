-- add by yaods
alter table price_change_rule_product add column `designer_id` varchar(256) DEFAULT NULL COMMENT 'designer id';
alter table price_change_rule_product add column `color_code` varchar(256) DEFAULT NULL COMMENT 'color code';