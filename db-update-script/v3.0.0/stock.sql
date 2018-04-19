INSERT stock_location (stock_location,vendor_id,enabled) 
(SELECT vendor_name,vendor_id,1 FROM vendor WHERE enabled =1)