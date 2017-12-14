ALTER TABLE `logistics_product`
	CHANGE COLUMN `updated_at` `updated_at` DATETIME NOT NULL DEFAULT NOW() ON UPDATE NOW();