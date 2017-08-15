package com.intramirror.common.core.mapper;

import java.util.Map;

import com.intramirror.order.api.model.SubShipment;


public interface SubShipmentMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sub_shipment
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Long subShipmentId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sub_shipment
     *
     * @mbggenerated
     */
    int insert(SubShipment record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sub_shipment
     *
     * @mbggenerated
     */
    int insertSelective(SubShipment record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sub_shipment
     *
     * @mbggenerated
     */
    SubShipment selectByPrimaryKey(Long subShipmentId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sub_shipment
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(SubShipment record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sub_shipment
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(SubShipment record);
    
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
}