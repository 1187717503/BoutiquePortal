package com.intramirror.order.api.service;

import java.util.List;
import java.util.Map;

import com.intramirror.order.api.model.Shipment;

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
	Map<String,Object> getPaymentInfoByOrderId(int orderId);
	
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
	List<Map<String,Object>> getOrderListByStatus(int status,Long vendorId,String sortByName);
	
	/**
	 * 根据 订单状态获取子订单列表
	 * @param orderNumber status
	 * @return
	 */
	List<Map<String,Object>> getOrderListByStatusAndContainerId(int containerId ,int status,Long vendorId);
	
	
	/**
	 * 根据 条件获取子订单详情
	 * @param orderNumber status vendor id
	 * @return
	 */
	Map<String, Object> getOrderInfoByCondition(Map<String,Object> map);
	
}
