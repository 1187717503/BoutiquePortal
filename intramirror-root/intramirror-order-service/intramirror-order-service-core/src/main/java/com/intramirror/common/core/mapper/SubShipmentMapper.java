package com.intramirror.common.core.mapper;

import com.intramirror.order.api.model.SubShipment;

import java.util.List;
import java.util.Map;


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
     *
     * @param sub
     * @return
     */
    int insertSubshipmentVO(SubShipment sub);
    int insertSubshipment(Map<String, Object> map);

    /**
     * 根据详情查询ID
     *
     * @param map
     * @return
     */
    Long getSubshipment(Map<String, Object> map);

    /**
     * 根据subShipmentId删除subshipment
     *
     * @param subShipmentId
     * @return
     */
    int deleteSubShipmentByShipmentId(Long subShipmentId);

    List<SubShipment> getSubShipmentByShipmentId(Long shipmentId);
    
    /**
     * 根据shipmentId修改awb
     * @param map
     * @return
     */
    int updateSubShipment(Map<String, Object> map);
    
    /**
	 * 根据containerId查询subShipmentIdList
	 * @param map
	 * @return
	 */
    List<SubShipment> getSubShipmentIdByContainerId(Map<String, Object> map);
    
    /**
     * 根据SubShipmentId修改subshipment关联
     * @param map
     * @return
     */
    int updateSubShipmentById(Map<String, Object> map);

    SubShipment getDHLShipment(Map<String, Object> map);
}