ALTER TABLE `geography`
ADD COLUMN `boutique_use`  int(1) NULL DEFAULT 0 COMMENT '买手店发货使用大区选择' AFTER `warehouse_use`;

