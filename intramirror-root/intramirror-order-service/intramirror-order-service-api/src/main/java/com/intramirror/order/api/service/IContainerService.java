/**
 * 
 */
package com.intramirror.order.api.service;

import java.util.List;
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
	 * 查询箱子
	 * @param ContainerId
	 * @return Container
	 */
	Container selectContainerById(Map<String, Object> map);
	
	
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
	
	/**
	 * 删除箱子
	 * @param map
	 * @return
	 */
	int deleteContainerById(Map<String, Object> map);
	
	/**
	 * 获取条形码
	 * @return int
	 */
	Integer getMaxBarcode();
	
	/**
	 * 修改箱子信息
	 * @param map
	 * @return int
	 */
	int updateContainerByCondition(Map<String, Object> map);
	
	/**
	 * 获取Container 列表
	 * @param map
	 * @return
	 */
	List<Map<String,Object>> getContainerList(Map<String, Object> map);
	
	/**
	 * 根据shipmentid获取Container 列表
	 * @param map
	 * @return
	 */
	List<Map<String,Object>> getShipmentList(Map<String, Object> map);
	
	/**
	 * 根据vendorId查询数量
	 * @param map
	 * @return
	 */
	Integer getContainerCount(Map<String, Object> map);
	
	/**
	 * 修改container下面的subshipment
	 * @param map
	 * @return
	 */
	int updateContainerShipment(Map<String, Object> map);
}
