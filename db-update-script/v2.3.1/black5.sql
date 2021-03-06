DROP TABLE IF EXISTS t_promotion_exclude;
CREATE TABLE `t_promotion_exclude` (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`vendor_id` BIGINT(64) NOT NULL COMMENT '供货商ID',
	`brand_id` BIGINT(64) NOT NULL COMMENT '商品品牌id',
	`category_level` BIGINT(64) NULL DEFAULT NULL COMMENT '类目等级',
	`category_id` BIGINT(64) NOT NULL COMMENT '商品类目 叶子 id',
	`product_id` BIGINT(64) NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	INDEX `IDX_VENDOR_ID` (`vendor_id`),
	INDEX `IDX_BRAND_ID` (`brand_id`),
	INDEX `IDX_CATEGORY_ID` (`category_id`)
)
COMMENT='商品类目表'
COLLATE='utf8_general_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1
;

truncate table t_promotion_exclude;
/*2017-12-6 排除 Nugnes 1920 ****品牌的包*/
INSERT INTO t_promotion_exclude(vendor_id,brand_id,category_level,category_id)
SELECT v.vendor_id,b.brand_id,2,c.category_id
FROM category c,vendor v,brand b
WHERE c.parent_id IN(
	SELECT c1.category_id
	FROM category c1
	WHERE c1.name = 'Bags' AND c1.enabled = 1) 
AND v.vendor_name = 'Nugnes 1920' AND b.english_name IN ('Celine','Gucci','Saint Laurent');

SELECT * FROM t_promotion_exclude;


/*2017-12-7 排除 Base Blu 所有 Saint Laurent 品牌的包*/
INSERT INTO t_promotion_exclude(vendor_id,brand_id,category_level,category_id)
SELECT v.vendor_id,b.brand_id,2,c.category_id
FROM category c,vendor v,brand b
WHERE c.parent_id IN(
	SELECT c1.category_id
	FROM category c1
	WHERE c1.name = 'Bags' AND c1.enabled = 1) 
AND v.vendor_name = ('Base Blu') AND b.english_name IN ('Saint Laurent');

/*2017-12-7 排除 Base Blu ***品牌的所有商品*/
INSERT INTO t_promotion_exclude(vendor_id,brand_id,category_level,category_id)
SELECT distinct v.vendor_id,b.brand_id,1,-1
FROM vendor v,brand b
WHERE v.vendor_name = ('Base Blu') AND b.english_name 
IN ('Bottega Veneta','Antipast','Oamc','Marc Jacobs','Philipp Plein','Philipp Plein Sport','Puma','Calvin Klein','Sacai')
and b.enabled = 1;
