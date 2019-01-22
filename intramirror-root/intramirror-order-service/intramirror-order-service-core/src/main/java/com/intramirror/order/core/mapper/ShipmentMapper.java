/**
 * 
 */
package com.intramirror.order.core.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.intramirror.order.api.model.Shipment;
import com.intramirror.order.api.vo.LogisticsProductVO;
import org.apache.ibatis.annotations.Param;

/**
 * @author 123
 *
 */
public interface ShipmentMapper {

	/**
	 * 根据Confirmed的Order生成Shipment 新的Shipment默认有一个carton
	 * @param shipment
	 * @return
	 */
	int saveShipmentByOrderId(Shipment shipment);
	
	/**
	 * 根据状态国家查询shipment
	 * @param map
	 * @return map
	 */
	Map<String, Object> selectShipmentByOrder(Map<String, Object> map);
	
	
	/**
	 * 根据订单号查询shipment基本信息
	 * @param map
	 * @return map
	 */
	List<Map<String, Object>> getShippmentByType(Map<String, Object> map);
	
	/**
	 * 根据shipment状态大区查询shipment
	 * @param map
	 * @return
	 */
	List<Map<String,Object>> getShipmentByStatus(Map<String, Object> map);
	
	/**
	 * 根据vendorId大区查询shipment open的列表 如果可以返回第一段
	 * @return
	 */
	List<Map<String, Object>> getShipmentsByVendor(Map<String, Object> map);
	
	/**
	 * 根据shipmentID查询shipmentType
	 * @param map
	 * @return
	 */
	Map<String,Object> getShipmentTypeById(Map<String, Object> map);
	
	
	/**
	 * 根据shipmentID查询shipment相关信息
	 * @param map
	 * @return
	 */
	Map<String,Object> getShipmentInfoById(Map<String, Object> map);
	
	
	
	/**
	 * 根据vendorId查询vendorCode
	 * @param vendorId
	 * @return
	 */
	String getVendorCodeById(Long vendorId);
	
	/**
	 * 根据条件查询shipment
	 * @param shipment
	 * @return
	 */
	Long getShipmentId(Shipment shipment);
	
	/**
	 * 生成shipmentNo码
	 * @return
	 */
	Integer getMaxShipmentNo(Map<String, Object> map);
	
	/**
	 * 修改shipment状态
	 * @param map
	 * @return
	 */
	int updateShipmentStatus(Map<String, Object> map);
	
	/**
	 * 根据shipmentId查询shipment
	 * @param map
	 * @return
	 */
	Shipment selectShipmentById(Map<String, Object> map);
	
	/**
	 * 根据containerId查询对应的shipment
	 * @param map
	 * @return
	 */
	List<Shipment> selectContainerId(Map<String, Object> map);
	
	/**
	 * 根据shipmentId删除shipment
	 * @param map
	 * @return
	 */
	int deleteShipmentById(Map<String, Object> map);
	
	/**
	 * 根据shipmentId查询shipmentNo
	 * @param map
	 * @return
	 */
	String getShipmentNoById(Map<String, Object> map);

	List<Shipment> getShipmentList(Map<String, Object> map);

	BigDecimal getCustomValue(Map<String, Object> map);

	List<LogisticsProductVO> getlogisticsMilestone(Long shipmentId);

	void saveMilestone(LogisticsProductVO logisticsProductVO);

	void deleteMilestone(String awbNo);

	List<Shipment> getShipmentListByAwbNo(List<String> awbNos);

	String queryShipmentAwbUrlByAwbNum(String awbNum);

	int updatePrintStep(Map<String, Object> updateStepMap);

	void updateShipmentAwbAdvance(Map<String, Object> awbAdvanceMap);

	/**
	 * 根据订单号获取shipment
	 * @param orderLineNum
	 * @return
	 */
	Shipment getShipmentByOrderLineNum(String orderLineNum);

	List<String> getOrderLineNumsByShipment(Long shipmentId);

	List<Map<String,Object>> getCartoonType(@Param("shipmentId") Long shipmentId);

	int doRepeatShipmentCode(@Param("logisticsProductId") Long logisticsProductId,@Param("shipmentCode") String shipmentCode);

}
