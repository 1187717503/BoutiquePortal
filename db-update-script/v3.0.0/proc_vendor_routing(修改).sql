CREATE PROCEDURE `proc_vendor_routing`()
BEGIN
	
declare done int DEFAULT FALSE;
DECLARE vendorId int;
DECLARE shippingRoutingId int;
DECLARE num int;

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
				end IF;
			  FETCH  cur2 into shippingRoutingId; 
		 END WHILE;
		
		CLOSE cur2;

		SET done = FALSE;

    FETCH  cur1 into vendorId;  
END WHILE;
-- 释放游标  
CLOSE cur1;  

END