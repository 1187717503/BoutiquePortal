package com.intramirror.order.api.service;

import java.util.List;
import java.util.Map;

public interface IOrderService {
	
	/**
	 * 获取订单列表信息
	 * @return
	 */
	List<Map<String,Object>> getOrderList(int status);

	/**
	 * 根据orderNumber 和 订单状态获取订单列表
	 * @return
	 */
	List<Map<String,Object>> getOrderListByOrderNumber(String numbers,int status);
	
	
	/**
	 * 根据logisticsProductId 获取支付相关信息
	 * @param logisticsProductId
	 * @return
	 */
	List<Map<String,Object>> getOrderPaymentByLogisProductId(Long logisticsProductId);
}
