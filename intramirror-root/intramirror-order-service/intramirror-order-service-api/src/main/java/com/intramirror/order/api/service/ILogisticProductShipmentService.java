/**
 * 
 */
package com.intramirror.order.api.service;

import java.util.Map;

/**
 * @author 123
 *
 */
public interface ILogisticProductShipmentService {

	/**
	 * 新增物流订单关联
	 * @param map
	 * @return
	 */
	int insertlpShipment(Map<String, Object> map);
}
