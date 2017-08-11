/**
 * 
 */
package com.intramirror.order.core.mapper;

import java.util.List;
import java.util.Map;

import com.intramirror.order.api.model.Shipment;

/**
 * @author 123
 *
 */
public interface ShipmentMapper {

	/**
	 * 根据Confirmed的Order生成Shipment 新的Shipment默认有一个carton
	 * @param shipment
	 * @return
	 */
	int saveShipmentByOrderId(Shipment shipment);
	
	/**
	 * 根据状态国家查询shipment
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
