BEGIN
	
declare done int DEFAULT FALSE;
DECLARE vendorId int;
DECLARE shippingRoutingId int DEFAULT 1;
DECLARE num int;
declare countNum int DEFAULT 0;

declare cur1 CURSOR FOR SELECT vendor_id from vendor WHERE enabled = 1;
declare cur2 CURSOR FOR SELECT shipping_routing_id from shipping_routing WHERE enabled = 1;
declare continue HANDLER for not found set done = TRUE; 
 
-- 打开游标  
open cur1;  
-- 取游标中的值  
FETCH  cur1 into vendorId;  
WHILE (not done) do
		
		OPEN cur2;
		FETCH  cur2 into shippingRoutingId;  

		 WHILE (not done) do
				SELECT count(1) into num FROM vendor_shipping_routing WHERE vendor_id = vendorId and shipping_routing_id = shippingRoutingId;
				IF num  < 1 THEN
						INSERT vendor_shipping_routing (vendor_id,shipping_routing_id,priority) VALUES (vendorId,shippingRoutingId,1);
						SET countNum = countNum + 1;
				end IF;
			  FETCH  cur2 into shippingRoutingId; 
		 END WHILE;
		
		CLOSE cur2;

		SET done = 0;

    FETCH  cur1 into vendorId;  
END WHILE;
-- 释放游标  
CLOSE cur1;  
SELECT CONCAT('本次执行添加了',countNum,'条路径') as '执行信息';
END