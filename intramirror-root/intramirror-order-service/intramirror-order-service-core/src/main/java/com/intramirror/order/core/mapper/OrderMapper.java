package com.intramirror.order.core.mapper;

import java.util.List;
import java.util.Map;

public interface OrderMapper {
	
	List<Map<String,Object>> getOrderList(Map<String,String> param);
	
	List<Map<String,Object>> getOrderListByOrderNumber(Map<String,Object> param);

}
