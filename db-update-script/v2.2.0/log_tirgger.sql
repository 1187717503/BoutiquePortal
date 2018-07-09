drop table if exists log_trigger;
CREATE TABLE `log_trigger` (
	`act_type` BIGINT(64) NOT NULL DEFAULT '0' COMMENT '100~999是trigger的日志, 101-onoffsale',
	`product_id` BIGINT(64) NOT NULL DEFAULT '0',
	`sku_id` BIGINT(64) NOT NULL DEFAULT '0' COMMENT '商家商品ID',
	`act_sql` VARCHAR(256) NOT NULL DEFAULT '' COLLATE 'utf8mb4_general_ci',
	`description` VARCHAR(256) NOT NULL DEFAULT '' COLLATE 'utf8mb4_general_ci',
	`cur_date` DATETIME NOT NULL DEFAULT '0000-00-00 00:00:00'
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;

select * from log_trigger order by cur_date desc;