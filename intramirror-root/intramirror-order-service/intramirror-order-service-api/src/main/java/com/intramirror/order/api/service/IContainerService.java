/**
 * 
 */
package com.intramirror.order.api.service;

import java.util.Map;

import com.intramirror.order.api.model.Container;

/**
 * @author 袁孟亮
 *
 */
public interface IContainerService {

	/**
	 * 新增箱子
	 * @param container
	 * @return Container
	 */
	int saveContainerByShipment(Map<String, Object> map);
	
	/**
	 * 查询箱子
	 * @param barcode
	 * @return Container
	 */
	Container getContainerBybarcode(Map<String, Object> map);
	
	/**
	 * 修改箱子
	 * @param container
	 * @return int
	 */
	int updateContainerBybarcode(Map<String, Object> map);
	
	/**
	 * 修改箱子状态
	 * @param map
	 * @return int
	 */
	int updateContainerBystatus(Map<String, Object> map);
	
}
