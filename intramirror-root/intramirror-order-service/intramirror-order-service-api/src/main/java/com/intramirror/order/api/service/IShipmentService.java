/**
 * 
 */
package com.intramirror.order.api.service;

import com.intramirror.order.api.model.Shipment;

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
	Shipment saveShipmentByOrderId(Shipment shipment);
}
