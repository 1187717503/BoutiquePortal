/**
 * 
 */
package com.intramirror.order.api.service;

import com.intramirror.order.api.model.SubShipment;

import java.util.List;
import java.util.Map;

/**
 * @author 123
 *
 */
public interface ISubShipmentService {
	
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sub_shipment
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Long subShipmentId);
    

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
	
	
	/**
	 * 根据shipmentId删除subshipment
	 * @param map
	 * @return
	 */
	int deleteSubShipmentByShipmentId(Map<String, Object> map);

	List<SubShipment> getSubShipmentByShipmentId(Long shipmentId);
	
	/**
     * 根据shipmentId修改subshipment关联
     * @param map
     * @return
     */
    int updateSubShipment(Map<String, Object> map);
}
