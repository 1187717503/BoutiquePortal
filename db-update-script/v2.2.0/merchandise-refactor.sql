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

/**********************************************************  到此，sku.size字段更新完毕  **************************************************************************/
	
ALTER TABLE `product`
	ADD COLUMN `color_code` VARCHAR(128) NULL DEFAULT NULL COMMENT 'color';
	
ALTER TABLE `product`
	ADD COLUMN `designer_id` VARCHAR(128) NULL DEFAULT NULL COMMENT 'designer_id';

/*分析是否有重复垃圾数据*/
select t.product_id,count(*) from product_property t where t.key_name = 'ColorCode' and t.enabled = 1 group by t.product_id having count(*) > 1;
select t.product_id,count(*) from product_property t where t.key_name = 'BrandID' and t.enabled = 1 group by t.product_id having count(*) > 1;

update product p,product_property pp set p.color_code = pp.value where p.product_id = pp.product_id and pp.key_name = 'ColorCode';
update product p,product_property pp set p.designer_id = pp.value where p.product_id = pp.product_id and pp.key_name = 'BrandID';

/**********************************************************  到此，ColorCode, BrandID字段更新完毕  **************************************************************************/

create or replace view view_prod_store_sum as select `sku_store`.`product_id` AS `product_id`,sum(`sku_store`.`store`) AS `store` from `sku_store` group by `sku_store`.`product_id`;
