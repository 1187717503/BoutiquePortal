/**
 * 
 */
package com.intramirror.order.core.impl;

import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.intramirror.common.core.mapper.SubShipmentMapper;
import com.intramirror.order.api.common.ContainerType;
import com.intramirror.order.api.model.Shipment;
import com.intramirror.order.api.service.IShipmentService;
import com.intramirror.order.core.dao.BaseDao;
import com.intramirror.order.core.mapper.LogisticProductShipmentMapper;
import com.intramirror.order.core.mapper.ShipmentMapper;

/**
 * 订单装箱service
 * @author yuan
 *
 */
@Service
public class ShipmentServiceImpl extends BaseDao implements IShipmentService{

	private static Logger logger = LoggerFactory.getLogger(ShipmentServiceImpl.class);
	
	private ShipmentMapper shipmentMapper;
	
	private SubShipmentMapper subShipmentMapper;
	
	private LogisticProductShipmentMapper logisticProductShipmentMapper;
	
	@Override
	public void init() {
		shipmentMapper = this.getSqlSession().getMapper(ShipmentMapper.class);
		subShipmentMapper = this.getSqlSession().getMapper(SubShipmentMapper.class);
		logisticProductShipmentMapper = this.getSqlSession().getMapper(LogisticProductShipmentMapper.class);
	}
	
	/**
	 * Confirmed的Order生成Shipment 新的Shipment默认有一个carton
	 */
	@Override
	public String saveShipmentByOrderId(Map<String, Object> map) {
		logger.info("shimentSaveService parameter " + new Gson().toJson(map));
		//如果shipment为null创建新的shipment 如果有直接拿shipment生成SUBshipment
		Long shipmentId = Long.parseLong(map.get("shipmentId")==null?"0":map.get("shipmentId").toString());
		int result = 0;
		Shipment shipment = new Shipment();
		if (shipmentId == 0){
			//获取当前时间
			Date currentDate = new Date();
			//保存对象信息
			shipment.setShipToGeography(map.get("shipToGeography")==null?" ":map.get("shipToGeography").toString());
			Long vendorId = Long.parseLong(map.get("vendor_id").toString());
			String top = shipmentMapper.getVendorCodeById(vendorId);
			Integer maxNo = shipmentMapper.getMaxShipmentNo();
			if (null == maxNo)
				maxNo = 1000001;
			else 
				maxNo ++;
			//生成shipmentNo
			shipment.setShipmentNo(top+"SP"+maxNo);
			shipment.setVendorId(vendorId);
			shipment.setStatus(ContainerType.OPEN);
			shipment.setCreatedAt(currentDate);
			shipment.setUpdatedAt(currentDate);
			logger.info("parameter :" + new Gson().toJson(shipment));
			result = shipmentMapper.saveShipmentByOrderId(shipment);
			logger.info("insert shipment result : " +result);
			if (result == 1){
				//根据段生成subshipment
				Map<String, Object> typeMap = new HashMap<String, Object>();
				typeMap.put("consigner_country_id",Long.parseLong(map.get("consigner_country_id").toString()));	
				typeMap.put("consignee_country_id",Long.parseLong(map.get("consignee_country_id").toString()));
				typeMap.put("vendor_id",vendorId);
				logger.info("getShipmentId :" + new Gson().toJson(typeMap));
				List<Map<String, Object>> listMap = shipmentMapper.getShippmentByType(typeMap);
				logger.info("result shipmentType:" + new Gson().toJson(listMap));
				shipmentId = shipmentMapper.getShipmentId(shipment);
				saveSubShipment(listMap, map,shipmentId,Long.parseLong(
						map.get("logistics_product_id")==null?"0":map.get("logistics_product_id").toString()));
				return ""+shipmentId;
			}
		}else{
			shipment = shipmentMapper.selectShipmentById(map);
			shipmentId = shipment.getShipmentId();
			if (null != shipment){
				Map<String, Object> typeMap = new HashMap<String, Object>();
				typeMap.put("consigner_country_id",Long.parseLong(map.get("consigner_country_id").toString()));	
				typeMap.put("consignee_country_id",Long.parseLong(map.get("consignee_country_id").toString()));
				typeMap.put("vendor_id",shipment.getVendorId());
				logger.info("getShipmentId :" + new Gson().toJson(typeMap));
				List<Map<String, Object>> listMap = shipmentMapper.getShippmentByType(typeMap);
				logger.info("result shipmentType:" + new Gson().toJson(listMap));
				saveSubShipment(listMap, map,shipmentId,Long.parseLong(
						map.get("logistics_product_id")==null?"0":map.get("logistics_product_id").toString()));
				return ""+shipment.getShipmentId();
			}
		}
		return null;
	}

	/**
	 * 根据订单号查询shipment基本信息
	 * @param map
	 * @return map
	 */
	@Override
	public Map<String, Object> selectShipmentByOrder(Map<String, Object> map) {
		return shipmentMapper.selectShipmentByOrder(map);
	}
	/**
	 * 根据订单号查询shipmentt TYPE
	 * @param map
	 * @return map
	 */
	@Override
	public List<Map<String, Object>> getShippmentByType(Map<String, Object> map) {
		return shipmentMapper.getShippmentByType(map);
	}

	public void saveSubShipment(List<Map<String, Object>> map, Map<String, Object> lastMap, Long shipmentId, Long logisticProductId){
		//打印入参段
		logger.info("pararmeter " + new Gson().toJson(map));
		Date currentDate = new Date();
		//根据不同段生成不同sub-shipment
		if (null != map && 0 < map.size()){
			//每条路线生成不同sub-shipment
			if (map.size() == 1){
				Long segmentSequence = Long.parseLong(map.get(0).get("segment_sequence").toString());
				//插入物流订单关联查询是否已有记录
				Long subShipmentId1 = subShipmentMapper.getSubshipment(saveBean(lastMap, currentDate, shipmentId, segmentSequence));
				if (null == subShipmentId1){
					
					subShipmentMapper.insertSubshipment(saveBean(lastMap, currentDate, shipmentId, segmentSequence));
					Map<String, Object> lpsMap = new HashMap<>();
					Long subShipmentId = subShipmentMapper.getSubshipment(saveBean(lastMap, currentDate, shipmentId, segmentSequence));
					lpsMap.put("logisticProductId", logisticProductId);
					lpsMap.put("subShipmentId", subShipmentId);
					logisticProductShipmentMapper.insertlpShipment(lpsMap);
				}
			}
			if (map.size() == 2){
				Map<String, Object> thisMap = map.get(1);
				Long segmentSequence = Long.parseLong(map.get(0).get("segment_sequence").toString());
				//如果该段没有数据生成
				Long subShipmentId1 = subShipmentMapper.getSubshipment(saveBeanByMap(thisMap, currentDate, shipmentId , segmentSequence));
				if (null == subShipmentId1){
					subShipmentMapper.insertSubshipment(saveBeanByMap(thisMap, currentDate, shipmentId, segmentSequence));
					Map<String, Object> lpsMap = new HashMap<>();
					Long subShipmentId = subShipmentMapper.getSubshipment(saveBeanByMap(thisMap, currentDate, shipmentId, segmentSequence));
					lpsMap.put("logisticProductId", logisticProductId);
					lpsMap.put("subShipmentId", subShipmentId);
					logisticProductShipmentMapper.insertlpShipment(lpsMap);
				}
				Long segmentSequence1 = Long.parseLong(map.get(1).get("segment_sequence").toString());
				//生成最后段
				Long subShipmentId2 = subShipmentMapper.getSubshipment(saveBean(lastMap, currentDate, shipmentId, segmentSequence1));
				if (null == subShipmentId2){
					subShipmentMapper.insertSubshipment(saveBean(lastMap, currentDate, shipmentId, segmentSequence1));
					Map<String, Object> lpsMap = new HashMap<>();
					Long subShipmentId = subShipmentMapper.getSubshipment(saveBean(lastMap, currentDate, shipmentId, segmentSequence1));
					lpsMap.put("logisticProductId", logisticProductId);
					lpsMap.put("subShipmentId", subShipmentId);
					logisticProductShipmentMapper.insertlpShipment(lpsMap);
				}
			}
			if (map.size() == 3){
				Map<String, Object> oneMap = map.get(1);
				Map<String, Object> twoMap = map.get(2);
				Long segmentSequence1 = Long.parseLong(map.get(0).get("segment_sequence").toString());
				Long segmentSequence2 = Long.parseLong(map.get(1).get("segment_sequence").toString());
				Long segmentSequence3 = Long.parseLong(map.get(2).get("segment_sequence").toString());
				Long subShipmentId1 = subShipmentMapper.getSubshipment(saveBeanByMap(oneMap, currentDate, shipmentId, segmentSequence1));
				if (null == subShipmentId1){
					subShipmentMapper.insertSubshipment(saveBeanByMap(oneMap, currentDate, shipmentId, segmentSequence1));
					Map<String, Object> lpsMap = new HashMap<>();
					Long subShipmentId = subShipmentMapper.getSubshipment(saveBeanByMap(oneMap, currentDate, shipmentId, segmentSequence1));
					lpsMap.put("logisticProductId", logisticProductId);
					lpsMap.put("subShipmentId", subShipmentId);
					logisticProductShipmentMapper.insertlpShipment(lpsMap);
				}
				Long subShipmentId2 = subShipmentMapper.getSubshipment(saveBeanByMap(twoMap, currentDate, shipmentId, segmentSequence2));
				if (null == subShipmentId2){
					subShipmentMapper.insertSubshipment(saveBeanByMap(twoMap, currentDate, shipmentId, segmentSequence2));
					Map<String, Object> lpsMap = new HashMap<>();
					Long subShipmentId = subShipmentMapper.getSubshipment(saveBeanByMap(twoMap, currentDate, shipmentId, segmentSequence2));
					lpsMap.put("logisticProductId", logisticProductId);
					lpsMap.put("subShipmentId", subShipmentId);
					logisticProductShipmentMapper.insertlpShipment(lpsMap);
				}
				Long subShipmentId3 = subShipmentMapper.getSubshipment(saveBean(lastMap, currentDate, shipmentId, segmentSequence3));
				if (null == subShipmentId3){
					subShipmentMapper.insertSubshipment(saveBean(lastMap, currentDate, shipmentId, segmentSequence3));
					Map<String, Object> lpsMap = new HashMap<>();
					Long subShipmentId = subShipmentMapper.getSubshipment(saveBean(lastMap, currentDate, shipmentId, segmentSequence3));
					lpsMap.put("logisticProductId", logisticProductId);
					lpsMap.put("subShipmentId", subShipmentId);
					logisticProductShipmentMapper.insertlpShipment(lpsMap);
				}
				
			}
		}
	}
	
	public Map<String, Object> saveBean(Map<String, Object> map, Date currentDate, Long shipmentId,Long segmentSequence){
		Map<String, Object> beanMap = new HashMap<String, Object>();
		beanMap.put("consignee", map.get("consignee")==null?" ":map.get("consignee").toString());
		beanMap.put("segmentSequence", segmentSequence);
		beanMap.put("shippingSegmentId", Long.parseLong(map.get("shippingSegmentId")==null?"0":map.get("shipping_segment_id").toString()));
		beanMap.put("shipToAddr", map.get("shipToAddr")==null?" ":map.get("shipToAddr").toString());
		beanMap.put("shipToCity", map.get("shipToCity")==null?" ":map.get("shipToCity").toString());
		beanMap.put("shipToCountry", map.get("shipToCountry")==null?" ":map.get("shipToCountry").toString());
		beanMap.put("shipToDistrict", map.get("shipToDistrict")==null?" ":map.get("shipToDistrict").toString());
		beanMap.put("shipToProvince", map.get("shipToProvince")==null?" ":map.get("shipToProvince").toString());
		beanMap.put("shipmentId", shipmentId);
		beanMap.put("status", ContainerType.RECEIVED);
		beanMap.put("updatedAt", currentDate);
		beanMap.put("createdAt", currentDate);
		return beanMap;
	}
	
	public Map<String, Object> saveBeanByMap(Map<String, Object> map, Date currentDate, Long shipmentId, Long segmentSequence){
		Map<String, Object> beanMap = new HashMap<String, Object>();
		beanMap.put("consignee", map.get("transfer_consignee")==null?" ":map.get("transfer_consignee").toString());
		beanMap.put("segmentSequence",segmentSequence);
		beanMap.put("shippingSegmentId", Long.parseLong(map.get("shipping_segment_id")==null?"0":map.get("shipping_segment_id").toString()));
		beanMap.put("shipToAddr", map.get("transfer_addr")==null?" ":map.get("transfer_addr").toString());
		beanMap.put("shipToDistrict", map.get("addr_district")==null?" ":map.get("addr_district").toString());
		beanMap.put("shipToCity", map.get("addr_city")==null?" ":map.get("addr_city").toString());
		beanMap.put("shipToProvince", map.get("addr_province")==null?" ":map.get("addr_province").toString());
		beanMap.put("shipToCountry", map.get("addr_country")==null?" ":map.get("addr_country").toString());
		beanMap.put("shipmentId", shipmentId);
		beanMap.put("status", ContainerType.RECEIVED);
		beanMap.put("updatedAt", currentDate);
		beanMap.put("createdAt", currentDate);
		return beanMap;
	}

	/**
	 * 根据shipment状态大区查询shipment
	 * @param map
	 * @return
	 */
	@Override
	public List<Map<String, Object>> getShipmentByStatus(Map<String, Object> map) {
		return shipmentMapper.getShipmentByStatus(map);
	}

	/**
	 * 根据shipmentID查询shipmentType
	 * @param map
	 * @return
	 */
	public Map<String, Object> getShipmentTypeById(Map<String, Object> map) {
		return shipmentMapper.getShipmentTypeById(map);
	}
	
	/**
	 * 根据vendorId大区查询shipment open的列表 如果可以返回第一段
	 * @return
	 */
	public List<Map<String, Object>> getShipmentsByVendor(Map<String, Object> map) {
		return shipmentMapper.getShipmentsByVendor(map);
	}
	
	/**
	 * 根据vendorId查询vendorCode
	 * @param map
	 * @return
	 */
	public String getVendorCodeById(Long vendorId) {
		return shipmentMapper.getVendorCodeById(vendorId);
	}
	
	/**
	 * 根据条件查询shipment
	 * @param map
	 * @return
	 */
	public Long getShipmentId(Shipment shipment) {
		return shipmentMapper.getShipmentId(shipment);
	}

	/**
	 * 获取最大shipmentNo
	 */
	public Integer getMaxShipmentNo() {
		return shipmentMapper.getMaxShipmentNo();
	}

	/**
	 * 根据shipmentId修改状态
	 */
	@Override
	public int updateShipmentStatus(Map<String, Object> map) {
		int status = Integer.parseInt(map.get("status").toString());
		//查询当前对象信息
		Shipment shipment = shipmentMapper.selectShipmentById(map);
		System.out.println(shipment.getVendorId());
		int result = 0;
		//修改状态
		if (null != shipment){
			logger.info(MessageFormat.format("当前shipment状态:{0},需要修改后的shipment状态:{1}",shipment.getStatus(),status));
			//状态一直不需要修改
			if(shipment.getStatus() == status){
	        	return result;
	        }
			//获取上一个状态
			int lastStatus= ContainerType.getLastStatus(status);
			//如果一直修改状态
			if (lastStatus == shipment.getStatus()){
				result = shipmentMapper.updateShipmentStatus(map);
			}
			//如果为编辑箱子，修改箱子状态
			if (status == 1){
				result = shipmentMapper.updateShipmentStatus(map);
			}
		}
		return result;
	}

	@Override
	public Shipment selectShipmentById(Map<String, Object> map) {
		return shipmentMapper.selectShipmentById(map);
	}

	@Override
	public List<Shipment> selectContainerId(Map<String, Object> map) {
		return shipmentMapper.selectContainerId(map);
	}

}
