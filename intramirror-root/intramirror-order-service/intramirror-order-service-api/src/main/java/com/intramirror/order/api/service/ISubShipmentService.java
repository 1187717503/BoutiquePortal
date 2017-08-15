/**
 * 
 */
package com.intramirror.order.api.service;

import java.util.Map;

/**
 * @author 123
 *
 */
public interface ISubShipmentService {

	 /**
	 * 新增subshipment
	 * @param sub
	 * @return
	 */
	int insertSubshipment(Map<String, Object> map);
	
	/**
	 * 根据详情查询ID
	 * @param map
	 * @return
	 */
	Long getSubshipment(Map<String, Object> map);
}
