
/* 2017-11-13 Tony Boutique在shop ready无库存的商品5656件，全部到sold out状态 */
create table tmp_product_toupdate
SELECT p.product_id
FROM `product` p,`shop_product` sp, view_prod_store_sum vs, vendor v
WHERE p.product_id = sp.product_id AND p.enabled = 1 AND sp.enabled = 1 AND p.`status` = 3 AND sp.`status` = 2
	AND vs.product_id = p.product_id and vs.store <= 0
	AND p.vendor_id = v.vendor_id and v.vendor_name = 'Tony Boutique';

UPDATE `product` p,`shop_product` sp, tmp_product_toupdate tmp SET p.`status` = 3,sp.`status` = 1
WHERE p.product_id = tmp.product_id AND sp.product_id = tmp.product_id AND p.`status` = 3 AND sp.`status` = 2 AND p.enabled = 1 AND sp.enabled = 1;

/* 2017-11-13 On Sale的无库存的商品2407件，全部到sold out状态 */
drop TABLE if EXISTS tmp_product_toupdate;
create table tmp_product_toupdate
SELECT p.product_id
FROM `product` p,`shop_product` sp, view_prod_store_sum vs
WHERE p.product_id = sp.product_id AND p.enabled = 1 AND sp.enabled = 1 AND p.`status` = 3 AND sp.`status` = 0
	AND vs.product_id = p.product_id and vs.store <= 0; -- 2407

SELECT COUNT(*) FROM tmp_product_toupdate;

UPDATE `product` p,`shop_product` sp, tmp_product_toupdate tmp SET p.`status` = 3,sp.`status` = 1
WHERE p.product_id = tmp.product_id AND sp.product_id = tmp.product_id AND p.`status` = 3 AND sp.`status` = 0 AND p.enabled = 1 AND sp.enabled = 1;

/* 2017-11-13 Sold Out的无库存的商品19件，全部到On Sale状态 */
drop TABLE if EXISTS tmp_product_toupdate;
create table tmp_product_toupdate
SELECT p.product_id
FROM `product` p,`shop_product` sp, view_prod_store_sum vs
WHERE p.product_id = sp.product_id AND p.enabled = 1 AND sp.enabled = 1 AND p.`status` = 3 AND sp.`status` = 1
	AND vs.product_id = p.product_id and vs.store > 0; -- 19

SELECT COUNT(*) FROM tmp_product_toupdate;

UPDATE `product` p,`shop_product` sp, tmp_product_toupdate tmp SET p.`status` = 3,sp.`status` = 0
WHERE p.product_id = tmp.product_id AND sp.product_id = tmp.product_id AND p.`status` = 3 AND sp.`status` = 1 AND p.enabled = 1 AND sp.enabled = 1;



/* 2017-11-13 Dante 5在shop ready有库存的商品15件，全部到On Sell状态 */
drop TABLE if EXISTS tmp_product_toupdate;
create table tmp_product_toupdate
SELECT p.product_id
FROM `product` p,`shop_product` sp, view_prod_store_sum vs, vendor v
WHERE p.product_id = sp.product_id AND p.enabled = 1 AND sp.enabled = 1 AND p.`status` = 3 AND sp.`status` = 2
	AND vs.product_id = p.product_id and vs.store > 0
	AND p.vendor_id = v.vendor_id and v.vendor_name = 'Dante 5'; -- 15

UPDATE `product` p,`shop_product` sp, tmp_product_toupdate tmp SET p.`status` = 3,sp.`status` = 0
WHERE p.product_id = tmp.product_id AND sp.product_id = tmp.product_id AND p.`status` = 3 AND sp.`status` = 2 AND p.enabled = 1 AND sp.enabled = 1;

SELECT COUNT(*) FROM `product` p,`shop_product` sp, tmp_product_toupdate tmp
WHERE p.product_id = tmp.product_id AND sp.product_id = tmp.product_id AND p.`status` = 3 AND sp.`status` = 2 AND p.enabled = 1 AND sp.enabled = 1;


/* 2017-11-13 shop ready无库存的商品5667件，全部到sold out状态 */
drop TABLE if EXISTS tmp_product_toupdate;
create table tmp_product_toupdate
SELECT p.product_id
FROM `product` p,`shop_product` sp, view_prod_store_sum vs
WHERE p.product_id = sp.product_id AND p.enabled = 1 AND sp.enabled = 1 AND p.`status` = 3 AND sp.`status` = 2
	AND vs.product_id = p.product_id and vs.store <= 0; -- 5667

UPDATE `product` p,`shop_product` sp, tmp_product_toupdate tmp SET p.`status` = 3,sp.`status` = 1
WHERE p.product_id = tmp.product_id AND sp.product_id = tmp.product_id AND p.`status` = 3 AND sp.`status` = 2 AND p.enabled = 1 AND sp.enabled = 1;

SELECT COUNT(*) FROM `product` p,`shop_product` sp, tmp_product_toupdate tmp
WHERE p.product_id = tmp.product_id AND sp.product_id = tmp.product_id AND p.`status` = 3 AND sp.`status` = 2 AND p.enabled = 1 AND sp.enabled = 1;



/* 2017-11-14 on sell状态的商品，没有sale at字段的，全部用同表update_at字段补位 */
DROP TABLE IF EXISTS tmp_product_toupdate;
CREATE TABLE tmp_product_toupdate
SELECT p.product_id
FROM `product` p,`shop_product` sp
WHERE p.product_id = sp.product_id AND p.enabled = 1 AND sp.enabled = 1 AND p.`status` = 3 AND sp.`status` = 0 AND sp.sale_at IS NULL;

SELECT COUNT(*)
FROM `product` p,`shop_product` sp, tmp_product_toupdate tmp
WHERE p.product_id = tmp.product_id AND sp.product_id = tmp.product_id AND p.`status` = 3 AND sp.`status` = 0 AND p.enabled = 1 AND sp.enabled = 1 AND sp.sale_at IS NULL;

UPDATE `product` p,`shop_product` sp, tmp_product_toupdate tmp SET sp.sale_at = sp.updated_at
WHERE p.product_id = tmp.product_id AND sp.product_id = tmp.product_id AND p.`status` = 3 AND sp.`status` = 0 AND p.enabled = 1 AND sp.enabled = 1 AND sp.sale_at IS NULL;