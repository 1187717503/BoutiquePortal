ALTER TABLE `logistics_product`
ADD COLUMN `stock_location_id`  int NULL AFTER `retail_price`;

ALTER TABLE `shipment`
ADD COLUMN `stock_location_id`  int NULL AFTER `ship_to_geography`;

ALTER TABLE `container`
ADD COLUMN `stock_location_id`  int NULL AFTER `ship_to_geography`;


ALTER TABLE `sub_shipment`
ADD COLUMN `ship_to_country_code`  varchar(5) NULL COMMENT '国家编号' AFTER `ship_to_country`;
ADD COLUMN `contact`  varchar(64) NULL COMMENT '联系方式' AFTER `consignee`,
ADD COLUMN `piva`  varchar(64) NULL COMMENT '税号' AFTER `contact`;
ADD COLUMN `ship_to_addr2`  varchar(150) NULL AFTER `ship_to_addr`,
ADD COLUMN `ship_to_addr3`  varchar(150) NULL AFTER `ship_to_addr2`,
ADD COLUMN `ship_to_emailAddr`  varchar(100) NULL AFTER `ship_to_addr3`;
ADD COLUMN `postal_code`  varchar(20) NULL COMMENT '邮编' AFTER `ship_to_country_code`;
ADD COLUMN `person_name`  varchar(20) NULL  AFTER `consignee`;


ALTER TABLE `invoice`
ADD COLUMN `ddt_num`  int(5) NULL AFTER `invoice_num`;

ALTER TABLE `stock_location`
ADD COLUMN `address_streetLines2`  varchar(255) NULL AFTER `address_streetLines`,
ADD COLUMN `address_streetLines3`  varchar(255) NULL AFTER `address_streetLines2`;











