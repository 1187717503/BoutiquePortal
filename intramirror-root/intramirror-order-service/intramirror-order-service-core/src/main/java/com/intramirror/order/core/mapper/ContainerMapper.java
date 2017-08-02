/**
 * 
 */
package com.intramirror.order.core.mapper;

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
	Container getContainerByBarcode(String barcode);
	
	/**
	 * 修改箱子
	 * @param container
	 * @return int
	 */
	int updateContainerByBarcode(Container container);
}
