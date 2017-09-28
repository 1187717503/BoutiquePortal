package com.intramirror.order.core.mapper;

import java.util.List;
import java.util.Map;

import com.intramirror.order.api.model.LogisticProductShipment;

public interface LogisticProductShipmentMapper {
	
	/**
	 * 新增物流订单关联
	 * @param map
	 * @return
	 */
	int insertlpShipment(Map<String, Object> map);
	
	/**
	 * 根据ID查询关联表
	 * @param map
	 * @return
	 */
	List<LogisticProductShipment> selectById(Map<String, Object> map);
	
	
	/**
	 * 
	 * @param a
	 * @return
	 */
	int deleteById(Map<String, Object> map);
	
	int updateBysubShipmentId(Map<String, Object> map);
	
	int deleteBylogisticProductId(Map<String, Object> map);
}
