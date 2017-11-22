DROP TRIGGER IF EXISTS `ger-prd-db`.`trg_sku_store_onoffsale`; 

delimiter //
CREATE  TRIGGER `trg_sku_store_onoffsale` 
AFTER UPDATE  ON sku_store
 FOR EACH ROW  
begin
DECLARE l_product_id bigint;
DECLARE l_sku_id bigint;
DECLARE l_product_store int DEFAULT 0;
DECLARE l_product_count int DEFAULT 0;
DECLARE v_tx_isolation varchar(32);

DECLARE const_product_status_approved tinyint DEFAULT 3 ;
DECLARE const_shop_product_status_onsale tinyint DEFAULT 0;
DECLARE const_shop_product_status_soldout tinyint DEFAULT 1;

SET l_product_id = NEW.product_id;
SET l_sku_id = NEW.sku_id;

IF OLD.store > 0 AND NEW.store <= 0 THEN 
	
	SELECT product_id INTO l_product_id FROM shop_product WHERE product_id = l_product_id AND enabled = 1 FOR UPDATE;
	
    SELECT MAX(store) INTO l_product_store FROM sku_store WHERE product_id = l_product_id AND enabled = 1;

	IF l_product_store <= 0 THEN
        SELECT COUNT(1) into l_product_count FROM product p INNER JOIN shop_product sp 
        	ON (p.product_id = l_product_id and p.product_id = sp.product_id AND p.enabled = 1 AND sp.enabled = 1 
                AND p.`status` = const_product_status_approved AND sp.`status` = const_shop_product_status_onsale);
        IF l_product_count > 0 THEN
        	UPDATE shop_product SET `status` = const_shop_product_status_soldout,`updated_at` = NOW()
				WHERE product_id = l_product_id AND enabled = 1 AND `status` = const_shop_product_status_onsale;
			UPDATE product SET updated_at = NOW() 
				WHERE product_id = l_product_id AND enabled = 1 AND `status` = const_product_status_approved;
			INSERT INTO log_trigger VALUES(101,l_product_id,l_sku_id,'offsale logic of trg_sku_store_onoffsale','3,0 -> 3,1', NOW());
		END IF;
    END IF;
ELSEIF OLD.store <= 0  AND NEW.store > 0 THEN 
    SELECT count(1) INTO l_product_count FROM product p INNER JOIN shop_product sp 
        	ON (p.product_id = l_product_id and p.product_id = sp.product_id AND p.enabled = 1 AND sp.enabled = 1 
                AND p.`status` = const_product_status_approved AND sp.`status` = const_shop_product_status_soldout);
	IF l_product_count > 0 THEN
		UPDATE shop_product SET `status` = const_shop_product_status_onsale,`updated_at` = NOW()
			WHERE product_id = l_product_id AND enabled = 1 AND `status` = const_shop_product_status_soldout;
		UPDATE product SET updated_at = NOW() 
				WHERE product_id = l_product_id AND enabled = 1 AND `status` = const_product_status_approved;
		INSERT INTO log_trigger VALUES(102,l_product_id,l_sku_id,'onsale logic of trg_sku_store_onoffsale','3,1 -> 3,0', NOW());
	END IF;
END IF;
end;  //
delimiter ;
