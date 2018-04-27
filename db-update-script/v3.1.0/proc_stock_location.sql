CREATE DEFINER=`sha_testing_db`@`%` PROCEDURE `proc_stock_location`()
BEGIN

	declare done int DEFAULT FALSE;
	DECLARE vendorId int;
	DECLARE vendorName VARCHAR(64);
	DECLARE num int;
	declare countNum int DEFAULT 0;

	declare cur1 CURSOR FOR SELECT vendor_id,vendor_name from vendor WHERE enabled = 1;
	declare continue HANDLER for not found set done = TRUE; 

-- 打开游标  
open cur1;  
-- 取游标中的值  
FETCH  cur1 into vendorId,vendorName;  
WHILE (not done) do
	
				SELECT count(1) into num FROM stock_location WHERE vendor_id = vendorId;
				IF num  < 1 THEN
						INSERT stock_location (stock_location,vendor_id,ENABLEd) VALUES (vendorName,vendorId,1);
						SET countNum = countNum + 1;
				end IF;
		
    FETCH  cur1 into vendorId,vendorName;  
END WHILE;
-- 释放游标  
CLOSE cur1;  
SELECT CONCAT('本次执行添加了',countNum,'条路径') as '执行信息';

END