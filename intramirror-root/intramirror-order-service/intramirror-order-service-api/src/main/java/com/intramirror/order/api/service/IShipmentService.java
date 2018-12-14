/**
 *
 */
package com.intramirror.order.api.service;

import com.intramirror.order.api.model.LogisticsProduct;
import com.intramirror.order.api.model.Shipment;
import com.intramirror.order.api.model.SubShipment;
import com.intramirror.order.api.vo.LogisticsProductVO;
import com.intramirror.order.api.vo.ShipmentInputVO;
import com.intramirror.order.api.vo.ShipmentSendMailVO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author yuan
 *
 */
public interface IShipmentService {

	/**
	 * 根据Confirmed的Order生成Shipment 新的Shipment默认有一个carton
	 * @return
	 */
	Shipment saveShipmentByOrderId(Map<String, Object> map);


	/**
	 * 根据订单号查询shipment type
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
	 * 根据条件查询shipmentID
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
	 * 创建新的shipment
	 * @param map
	 * @return
	 */
	Long newShipment(Map<String, Object> map);


	/**
	 * 根据shipmentId查询shipmentNo
	 * @param map
	 * @return
	 */
	String getShipmentNoById(Map<String, Object> map);

	/**
	 * @param map
	 * @return
	 */
	List<Shipment> getShipmentList(Map<String, Object> map);

	BigDecimal getCustomValue(Map<String, Object> map);

	/**
	 * 根据shipmentId查询用户收货地址信息
	 * @param shipmentId
	 * @return
	 */
	SubShipment getSubShipmentByShipmentId(Long shipmentId);

	List<LogisticsProductVO> getlogisticsMilestone(Long shipmentId);

	void saveMilestone(LogisticsProductVO vo);

	void deleteMilestone(String awbNo);

	/**
	 * 根据awbNo查询shipment列表
	 * @param awbNos
	 * @return
	 */
	List<Shipment> getShipmentList(List awbNos);

	/**
	 * shipment发货
	 * @param shipment
	 */
	void shipmentToShip(Shipment shipment);

    /**
     * shipped状态发邮件
     * @param shipment
     */
	void sendMailForShipped(ShipmentSendMailVO shipment);

	/**
	 * 查询shipment下的订单
	 * @param shipmentId
	 * @return
	 */
	List<LogisticsProduct> getLogisticsProductByShipment(Long shipmentId);


	void styleroomShip(List<String> list);

	String queryAwbUrlByAwbNum(String awbNum);

	int updatePrintStep(Map<String, Object> updateStepMap);

	void updateShipmentAwbAdvance(Map<String, Object> awbAdvanceMap);

	/**
	 * 根据订单号获取shipment
	 * @param orderLineNum
	 * @return
	 */
	Map<String,Object> getShipmentByOrderLineNum(String orderLineNum);

    void saveAwb(ShipmentInputVO inputVO);

}
