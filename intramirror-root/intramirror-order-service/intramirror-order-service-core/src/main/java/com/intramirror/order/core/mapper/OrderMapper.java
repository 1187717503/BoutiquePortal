package com.intramirror.order.core.mapper;

import java.util.List;
import java.util.Map;

import com.intramirror.order.api.model.Shipment;

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
	
	
	/**
	 * 根据logisticsProductId 获取支付相关信息
	 * @param logisticsProductId
	 * @return
	 */
	List<Map<String,Object>> getOrderPaymentByLogisProductId(Long logisticsProductId);
	
	/**
	 * 根据status统计各个状态的订单数量
	 * @param status
	 * @return
	 */
	int getOrderByIsvalidCount(Map<String,Object> param);
	
	/**
	 * 根据订单号查询物流ID
	 * @param orderId
	 * @return map
	 */
	Map<String,Object> getOrderPaymentInfoByOrderId(int orderId);
	
	/**
	 * 根据订单号查询支付信息
	 * @param orderId
	 * @return
	 */
	List<Map<String, Object>> getPaymentInfoByOrderId(int orderId);
	
	/**
	 * 根据订单获取Shipment 
	 * @param orderId
	 * @return
	 */
	Shipment getOrderByShipment(int logisticsProductId);
	

	/**
	 * 根据 订单状态获取子订单列表
	 * @param orderNumber status
	 * @return
	 */
	List<Map<String,Object>> getOrderListByStatus(Map<String, Object> conditionMap);
	
	
	/**
	 * 根据 订单状态获取子订单列表
	 * @param orderNumber status
	 * @return
	 */
	List<Map<String,Object>> getOrderListByStatusAndContainerId(Map<String, Object> conditionMap);
	
	
	/**
	 * 根据 条件获取子订单信息
	 * @param orderNumber status vendor Id 
	 * @return
	 */
	List<Map<String,Object>> getOrderInfoByCondition(Map<String, Object> conditionMap);
	

}