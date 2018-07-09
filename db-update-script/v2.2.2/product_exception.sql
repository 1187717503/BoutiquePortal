drop table if exists product_exception;
CREATE TABLE `product_exception` (
  `id` BIGINT(64) NOT NULL AUTO_INCREMENT COMMENT '产品异常ID',
  `type_id` INT(2) NOT NULL COMMENT '异常类型ID',
  `product_id` BIGINT(64) NOT NULL COMMENT 'Product ID',
  `sku_id` BIGINT(64) NOT NULL COMMENT 'Sku ID',
  `note` VARCHAR(200) NULL DEFAULT NULL COMMENT '备注',
  `status` INT(2) NOT NULL COMMENT '状态 0异常解决 1库存异常',
  `created_at` DATETIME NOT NULL COMMENT '创建时间',
  `created_by_user_id` BIGINT(64) NULL DEFAULT NULL COMMENT '用户Id',
  `modified_at` DATETIME NULL DEFAULT NULL COMMENT '审核时间',
  `modified_by_user_id` BIGINT(64) NULL DEFAULT NULL COMMENT '审核人Id',
  PRIMARY KEY (`id`),
  INDEX `IDX_PRODUCT_ID` (`product_id`),
  INDEX `IDX_SKU_ID` (`sku_id`)
)
  COMMENT='产品异常表'
  COLLATE='utf8_general_ci'
  ENGINE=InnoDB
  AUTO_INCREMENT=1
;