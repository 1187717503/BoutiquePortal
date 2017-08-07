/**
 * 
 */
package com.intramirror.order.core.mapper;

import java.util.Map;

import com.intramirror.order.api.model.Container;

/**
 * @author 袁孟亮
 *
 */
public interface ContainerMapper {

	/**
	 * 新增箱子
	 * @param container
	 * @return int
	 */
	int saveContainerByShipment(Container container);
	
	/**
	 * 查询箱子
	 * @param barcode
	 * @return Container
	 */
	Container selectContainerByBarcode(Map<String, Object> map);
	
	/**
	 * 修改箱子
	 * @param container
	 * @return int
	 */
	int updateContainerByBarcode(Map<String, Object> map);
	
	/**
	 * 修改箱子状态
	 * @param map
	 * @return int
	 */
	int updateContainerBystatus(Map<String, Object> map);
	
	/**
	 * 删除箱子
	 * @param map
	 * @return
	 */
	int deleteContainerById(Map<String, Object> map);
}
