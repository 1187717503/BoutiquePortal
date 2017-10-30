DELIMITER // 
CREATE PROCEDURE proc_del_duplicate_sku_property()
BEGIN
 	/*清理sku_property中重复数据*/
	DROP TABLE IF EXISTS tmp_duplicate_size_tofix;
	CREATE TABLE tmp_duplicate_size_tofix AS (
	SELECT t.product_sku_property_key_id,t.value, COUNT(*)
	FROM product_sku_property_value t
	WHERE t.enabled = 1
	GROUP BY t.product_sku_property_key_id,t.value
	HAVING COUNT(*) > 1);
	
	/*把重复的ID中的最大值作为保留*/
	DROP TABLE IF EXISTS tmp_valueid_tosave;
	CREATE TABLE tmp_valueid_tosave AS(
	SELECT MAX(pv.product_sku_property_value_id) AS product_sku_property_value_id,pv.product_sku_property_key_id,pv.value
	FROM product_sku_property_value pv
	INNER JOIN tmp_duplicate_size_tofix tfix ON (pv.product_sku_property_key_id = tfix.product_sku_property_key_id AND pv.value = tfix.value AND pv.value IS NOT NULL AND pv.value <> '')
	GROUP BY pv.product_sku_property_key_id);
	
	/*把所有要删除的记录保存，原因是mysql的delete不支持in的参数里有复杂的sql*/
	DROP TABLE IF EXISTS tmp_valueid_todel;
	CREATE TABLE tmp_valueid_todel AS(
	SELECT sp.sku_property_id
	FROM sku_property sp
	INNER JOIN tmp_valueid_tosave tv ON (sp.product_sku_property_key_id = tv.product_sku_property_key_id AND sp.product_sku_property_value_id <> tv.product_sku_property_value_id));
	/*删除重复数据*/
	DELETE
	FROM sku_property
	WHERE sku_property_id IN (
	SELECT t.sku_property_id
	FROM tmp_valueid_todel t);

	DROP TABLE IF EXISTS tmp_duplicate_size_tofix;
	DROP TABLE IF EXISTS tmp_valueid_tosave;
	DROP TABLE IF EXISTS tmp_valueid_todel;

END //
DELIMITER ;