/**
 * 
 */
package com.intramirror.order.core.mapper;

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
	Shipment saveShipmentByOrderId(Shipment shipment);
}
