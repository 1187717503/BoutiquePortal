alter table `product`  add `min_boutique_price` decimal(16,4) DEFAULT  NULL COMMENT 'sku.in_price冗余字段' ;
alter table `product`  add `max_boutique_price` decimal(16,4) DEFAULT  NULL COMMENT 'sku.in_price冗余字段' ;
update `product`  p,sku set p.`min_boutique_price` = sku.`in_price`,p.`max_boutique_price` = sku.`in_price`
where p.`enabled`  = 1 and sku.`enabled`  = 1 and p.`product_id`  = sku.`product_id` ;