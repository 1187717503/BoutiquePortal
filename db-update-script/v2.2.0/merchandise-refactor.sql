/***************************************************  sku.size 和 product.color_code,product.brandId的处理******************************************************************/

ALTER TABLE `sku` ADD COLUMN `size` VARCHAR(32) NULL DEFAULT NULL COMMENT 'size';
	
--清理sku_property中重复数据
DROP TABLE IF EXISTS tmp_duplicate_size_tofix;
CREATE TABLE tmp_duplicate_size_tofix AS (
SELECT t.product_sku_property_key_id,t.value, COUNT(*)
FROM product_sku_property_value t
WHERE t.enabled = 1
GROUP BY t.product_sku_property_key_id,t.value
HAVING COUNT(*) > 1);
--查询
SELECT *
FROM product_sku_property_value pv
INNER JOIN tmp_duplicate_size_tofix tfix ON (pv.product_sku_property_key_id = tfix.product_sku_property_key_id AND pv.value = tfix.value);

--准备删除多余的value
DROP TABLE IF EXISTS tmp_psp_vid_todel;
CREATE TABLE tmp_psp_vid_todel AS (
SELECT pv.product_sku_property_value_id
FROM product_sku_property_value pv
INNER JOIN tmp_valueid_tosave t ON 
(t.product_sku_property_key_id = pv.product_sku_property_key_id AND t.product_sku_property_value_id <> pv.product_sku_property_value_id AND t.value = pv.value));
--删除多余的value
DELETE
FROM product_sku_property_value
WHERE product_sku_property_value_id IN(
SELECT t.product_sku_property_value_id
FROM tmp_psp_vid_todel t
);

--把重复的ID中的最大值作为保留
DROP TABLE IF EXISTS tmp_valueid_tosave;
CREATE TABLE tmp_valueid_tosave AS(
SELECT MAX(pv.product_sku_property_value_id) AS product_sku_property_value_id,pv.product_sku_property_key_id,pv.value
FROM product_sku_property_value pv
INNER JOIN tmp_duplicate_size_tofix tfix ON (pv.product_sku_property_key_id = tfix.product_sku_property_key_id AND pv.value = tfix.value AND pv.value IS NOT NULL AND pv.value <> '')
GROUP BY pv.product_sku_property_key_id);

--把所有要删除的记录保存，原因是mysql的delete不支持in的参数里有复杂的sql
DROP TABLE IF EXISTS tmp_valueid_todel;
CREATE TABLE tmp_valueid_todel AS(
SELECT sp.sku_property_id
FROM sku_property sp
INNER JOIN tmp_valueid_tosave tv ON (sp.product_sku_property_key_id = tv.product_sku_property_key_id AND sp.product_sku_property_value_id <> tv.product_sku_property_value_id));
--删除重复数据
DELETE
FROM sku_property
WHERE sku_property_id IN (
SELECT t.sku_property_id
FROM tmp_valueid_todel t);

--导出size的中间表
DROP TABLE IF EXISTS tmp_merchandise_refactor;
CREATE TABLE tmp_merchandise_refactor AS(
SELECT p.product_id,s.sku_id,pv.value
FROM sku s
INNER JOIN product p ON (p.product_id = s.product_id AND p.enabled = 1)
INNER JOIN sku_property sp ON (sp.sku_id = s.sku_id AND sp.enabled = 1)
INNER JOIN product_sku_property_value pv ON (sp.product_sku_property_value_id = pv.product_sku_property_value_id AND pv.enabled = 1));
--检查是否有重复数据
SELECT t.product_id,t.sku_id, COUNT(*)
FROM tmp_merchandise_refactor t
GROUP BY t.product_id,t.sku_id
HAVING COUNT(*) > 1;

--原来用这条语句更新，奇慢 update sku s set s.size = (select t.value from tmp_merchandise_refactor t where t.sku_id = s.sku_id and t.product_id = s.product_id);
--效率不错的语句
UPDATE sku s, tmp_merchandise_refactor ts SET s.size = ts.value
WHERE s.sku_id = ts.sku_id AND s.product_id = ts.product_id;
--检查是否有有效数据没有被赋值
SELECT *
FROM (
SELECT *
FROM sku s1
WHERE s1.enabled = 1 AND s1.size = '') AS s
INNER JOIN product p ON (p.product_id = s.product_id AND p.enabled = 1)
INNER JOIN sku_property sp ON (sp.sku_id = s.sku_id AND sp.enabled = 1)
INNER JOIN product_sku_property_value pv ON (sp.product_sku_property_value_id = pv.product_sku_property_value_id AND pv.enabled = 1);

--删除所有临时表
DROP TABLE IF EXISTS tmp_duplicate_size_tofix;
DROP TABLE IF EXISTS tmp_valueid_tosave;
DROP TABLE IF EXISTS tmp_valueid_todel;
DROP TABLE IF EXISTS tmp_merchandise_refactor;

ALTER TABLE `product`
	ADD COLUMN `color_code` VARCHAR(128) NULL DEFAULT NULL COMMENT 'color';
	
ALTER TABLE `product`
	ADD COLUMN `designer_id` VARCHAR(128) NULL DEFAULT NULL COMMENT 'designer_id';

/*分析是否有重复垃圾数据*/
select t.product_id,count(*) from product_property t where t.key_name = 'ColorCode' and t.enabled = 1 group by t.product_id having count(*) > 1;
select t.product_id,count(*) from product_property t where t.key_name = 'BrandID' and t.enabled = 1 group by t.product_id having count(*) > 1;

update product p,product_property pp set p.color_code = pp.value where p.product_id = pp.product_id and pp.key_name = 'ColorCode';
update product p,product_property pp set p.designer_id = pp.value where p.product_id = pp.product_id and pp.key_name = 'BrandID';

/**********************************************************  创建 商品-库存总和 的view  **************************************************************************/

create or replace view view_prod_store_sum as 
select t.`product_id` AS `product_id`,sum(t.`store`) AS `store` from `sku_store` t where t.store >= 0 and t.enabled = 1 group by t.`product_id`;

/**********************************************************  商品状态机，异常数据状态迁移  **************************************************************************/

/*新建数据处理日志表*/

/*6,1 -> 5,null*/
drop table tmp_data_migration_log;
create table tmp_data_migration_log as
SELECT 
11 AS act_type,
p.product_id,
sp.shop_product_id,
'UPDATE product p,shop_product sp SET p.`status` = 5,sp.enabled = 0
WHERE p.product_id = sp.product_id AND p.enabled = 1 AND sp.enabled = 1 AND p.`status` = 6 AND sp.`status` = 1;' AS act_sql,
'6,1 -> 5,null' as description, 
now() as cur_date
FROM product p,shop_product sp
WHERE p.product_id = sp.product_id AND p.enabled = 1 AND sp.enabled = 0 AND p.`status` = 6 AND sp.`status` = 1;

update product p,shop_product sp set p.`status` = 5,sp.enabled = 0 where p.product_id = sp.product_id and p.enabled = 1 and sp.enabled = 1 and p.`status` = 6 and sp.`status` = 1;

/*4,0 -> 3,2*/
insert into tmp_data_migration_log 
SELECT 
12 AS act_type,
p.product_id,
sp.shop_product_id,
'UPDATE product p,shop_product sp SET p.`status` = 3,sp.`status` = 2
WHERE p.product_id = sp.product_id AND p.enabled = 1 AND sp.enabled = 1 AND p.`status` = 4 AND sp.`status` = 0;' AS act_sql,
'4,0 -> 3,2' as description, 
now() as cur_date
FROM product p,shop_product sp
WHERE p.product_id = sp.product_id AND p.enabled = 1 AND sp.enabled = 1 AND p.`status` = 4 AND sp.`status` = 0;

UPDATE product p,shop_product sp SET p.`status` = 3,sp.`status` = 2
WHERE p.product_id = sp.product_id AND p.enabled = 1 AND sp.enabled = 1 AND p.`status` = 4 AND sp.`status` = 0;

/*6,2 -> 5,2*/
insert into tmp_data_migration_log 
SELECT 
13 AS act_type,
p.product_id,
sp.shop_product_id,
'UPDATE product p,shop_product sp SET p.`status` = 5,sp.`status` = 2
WHERE p.product_id = sp.product_id AND p.enabled = 1 AND sp.enabled = 1 AND p.`status` = 6 AND sp.`status` = 2;' AS act_sql,
'6,2 -> 5,2' as description,
now() as cur_date
FROM product p,shop_product sp
WHERE p.product_id = sp.product_id AND p.enabled = 1 AND sp.enabled = 1 AND p.`status` = 6 AND sp.`status` = 2;

UPDATE product p,shop_product sp SET p.`status` = 5,sp.`status` = 2
WHERE p.product_id = sp.product_id AND p.enabled = 1 AND sp.enabled = 1 AND p.`status` = 6 AND sp.`status` = 2;

/*4,1 -> 3,1*/
insert into tmp_data_migration_log 
SELECT 
14 AS act_type,
p.product_id,
sp.shop_product_id,
'UPDATE product p,shop_product sp SET p.`status` = 3,sp.`status` = 1
WHERE p.product_id = sp.product_id AND p.enabled = 1 AND sp.enabled = 1 AND p.`status` = 4 AND sp.`status` = 1;' AS act_sql,
'4,1 -> 3,1' as description,
now() as cur_date
FROM product p,shop_product sp
WHERE p.product_id = sp.product_id AND p.enabled = 1 AND sp.enabled = 1 AND p.`status` = 4 AND sp.`status` = 1;

UPDATE product p,shop_product sp SET p.`status` = 3,sp.`status` = 1
WHERE p.product_id = sp.product_id AND p.enabled = 1 AND sp.enabled = 1 AND p.`status` = 4 AND sp.`status` = 1;

/*6,null -> 5,null*/
insert into tmp_data_migration_log 
SELECT 
15 AS act_type,
p.product_id,
'' as shop_product_id,
'UPDATE product p SET p.`status` = 5 WHERE p.enabled = 1 AND p.`status` = 6;' AS act_sql,
'6,null -> 5,null' as description,
now() as cur_date
FROM product p
WHERE p.enabled = 1 AND p.`status` = 6;

UPDATE product p SET p.`status` = 5 WHERE p.enabled = 1 AND p.`status` = 6;

select * from tmp_data_migration_log;


/**********************************************************  商品状态机，异常数据状态迁移  **************************************************************************/

SELECT COUNT(*)  FROM `sku` t 
INNER JOIN `sku_store` ss on(
t.`product_id` = ss.`product_id` and t.`sku_id` = ss.`sku_id` and t.`enabled` <> ss.`enabled` );  -- 25043

UPDATE `sku_store` ss,`sku` t set ss.`enabled` = t.`enabled` where t.`product_id` = ss.`product_id` and t.`sku_id` = ss.`sku_id` and t.`enabled` <> ss.`enabled`;