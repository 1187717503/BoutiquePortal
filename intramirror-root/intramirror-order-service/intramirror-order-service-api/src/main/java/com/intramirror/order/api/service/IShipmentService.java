/**
 * 
 */
package com.intramirror.order.api.service;

import java.util.List;
import java.util.Map;

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
	 * 根据订单号查询shipment基本信息
	 * @param map
	 * @return map
	 */
	List<Map<String, Object>> selectShipmentByOrder(Map<String, Object> map);
	
	/**
	 * 根据订单号查询shipment基本信息
	 * @param map
	 * @return map
	 */
	List<Map<String, Object>> getShippmentByType(Map<String, Object> map);
}
