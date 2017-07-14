package com.intramirror.order.core.mapper;

import java.util.List;
import java.util.Map;

public interface OrderMapper {
	
	/**
	 * 根据子订单状态获取 order 列表
	 * @param status 
	 * @return
	 */
	List<Map<String,Object>> getOrderList(Map<String,Object> param);
	
	
	/**
	 * 根据orderNumber 获取子订单列表
	 * @param orderNumber status
	 * @return
	 */
	List<Map<String,Object>> getOrderListByOrderNumber(Map<String,Object> param);

}
