/**
 * 
 */
package com.intramirror.order.api.service;

import java.util.List;
import java.util.Map;

import com.intramirror.order.api.model.SubShipment;

/**
 * @author yuan
 *
 */
public interface IShipmentService {

	/**
	 * 根据Confirmed的Order生成Shipment 新的Shipment默认有一个carton
	 * @param shipment
	 * @return
	 */
	int saveShipmentByOrderId(Map<String, Object> map);
	
	
	/**
	 * 根据订单号查询shipment type
	 * @param map
	 * @return map
	 */
	Map<String, Object> selectShipmentByOrder(Map<String, Object> map);
	
	/**
	 * 根据订单号查询shipment基本信息
	 * @param map
	 * @return map
	 */
	List<Map<String, Object>> getShippmentByType(Map<String, Object> map);
	
	
	/**
	 * 根据shipment状态大区查询shipment
	 * @param map
	 * @return
	 */
	List<Map<String,Object>> getShipmentByStatus(Map<String, Object> map);
	
	/**
	 * 根据shipmentID查询shipment
	 * @param map
	 * @return
	 */
	List<Map<String,Object>> getShipmentById(Map<String, Object> map);
	
	
	/**
	 * 新增subshipment
	 * @param sub
	 * @return
	 */
	int saveSubShipment(SubShipment sub);
}
