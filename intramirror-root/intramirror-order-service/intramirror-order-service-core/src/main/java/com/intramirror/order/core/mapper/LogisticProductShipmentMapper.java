package com.intramirror.order.core.mapper;

import java.util.Map;

public interface LogisticProductShipmentMapper {
	
	/**
	 * 新增物流订单关联
	 * @param map
	 * @return
	 */
	int insertlpShipment(Map<String, Object> map);

}
