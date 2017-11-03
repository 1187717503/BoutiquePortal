DELIMITER $
DROP TRIGGER IF EXISTS `trigger_onoff_sale`;
CREATE  TRIGGER `trigger_onoff_sale`
AFTER UPDATE  ON sku_store
FOR EACH ROW
  BEGIN
    DECLARE l_product_id BIGINT;
    DECLARE l_product_store INT DEFAULT 0;
    DECLARE l_product_status TINYINT DEFAULT 0;
    DECLARE l_soldout_amount INT DEFAULT 0;
    DECLARE const_status_onsale TINYINT DEFAULT 3;
    DECLARE const_status_soldout TINYINT DEFAULT 1;
    DECLARE const_status_stopselling TINYINT DEFAULT 2;
    DECLARE const_product_status_onsale TINYINT DEFAULT 3;
    DECLARE const_shop_product_status_onsale TINYINT DEFAULT 0;
    DECLARE const_shop_product_status_soldout TINYINT DEFAULT 1;
    SET l_product_id = NEW.product_id;
    IF OLD.store > 0 AND NEW.store = 0
    THEN
      SELECT SUM(store)
      INTO l_product_store
      FROM sku_store
      WHERE product_id = l_product_id AND enabled = 1;
      IF l_product_store = 0
      THEN
        UPDATE shop_product
        SET `status` = const_shop_product_status_soldout, updated_at = NOW()
        WHERE product_id = l_product_id AND enabled = 1 AND `status` = const_shop_product_status_onsale;
      END IF;
    ELSEIF OLD.store = 0 AND NEW.store > 0
      THEN
        SELECT COUNT(1)
        INTO l_soldout_amount
        FROM shop_product
        WHERE product_id = l_product_id AND enabled = 1 AND `status` = const_shop_product_status_soldout;
        IF l_soldout_amount > 0
        THEN
          UPDATE shop_product
          SET `status` = const_shop_product_status_onsale, updated_at = NOW()
          WHERE product_id = l_product_id AND enabled = 1 AND `status` = const_shop_product_status_soldout;
        END IF;
    END IF;
  END;
$
DELIMITER;