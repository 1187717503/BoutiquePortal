/**
 * 
 */
package com.intramirror.order.core.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.intramirror.order.api.common.ContainerType;
import com.intramirror.order.api.model.Shipment;
import com.intramirror.order.api.model.SubShipment;
import com.intramirror.order.api.service.IShipmentService;
import com.intramirror.order.core.dao.BaseDao;
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
	
	@Override
	public void init() {
		shipmentMapper = this.getSqlSession().getMapper(ShipmentMapper.class);
	}
	
	/**
	 * Confirmed的Order生成Shipment 新的Shipment默认有一个carton
	 */
	@Override
	public int saveShipmentByOrderId(Map<String, Object> map) {
		logger.info("shimentSaveService parameter " + new Gson().toJson(map));
		Shipment shipment = new Shipment();
		Date currentDate = new Date();
		shipment.setShipToGeography(map.get("shipToGeography").toString());
		shipment.setStatus(ContainerType.OPEN);
		shipment.setCreatedAt(currentDate);
		shipment.setUpdatedAt(currentDate);
		logger.info("parameter :" + new Gson().toJson(shipment));
		//根据段生成subshipment
		Map<String, Object> typeMap = new HashMap<String, Object>();
		typeMap.put("consigner_country_id",Long.parseLong(map.get("consigner_country_id").toString()));	
		typeMap.put("consignee_country_id",Long.parseLong(map.get("consignee_country_id").toString()));
		typeMap.put("vendor_id",Long.parseLong(map.get("vendor_id").toString()));	
		List<Map<String, Object>> listMap = shipmentMapper.getShippmentByType(typeMap);
		logger.info("parameter :" + new Gson().toJson(listMap));
		Long shipmentId =null;
		saveSubShipment(listMap, map,shipmentId);
		return shipmentMapper.saveShipmentByOrderId(shipment);
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

	public void saveSubShipment(List<Map<String, Object>> map, Map<String, Object> lastMap, Long shipmentId){
		//打印入参段
		logger.info("pararmeter " + new Gson().toJson(map));
		Date currentDate = new Date();
		//根据不同段生成不同sub-shipment
		if (null != map && 0 < map.size()){
			//每条路线生成不同sub-shipment
			if (map.size() == 1){
				//最终到达段sub_shipment
				shipmentMapper.saveSubShipment(saveBean(lastMap, currentDate, shipmentId));
			}
			if (map.size() == 2){
				Map<String, Object> thisMap = map.get(1);
				SubShipment sub = saveBeanByMap(thisMap, currentDate, shipmentId);
				shipmentMapper.saveSubShipment(sub);
				shipmentMapper.saveSubShipment(saveBean(lastMap, currentDate, shipmentId));
			}
			if (map.size() == 3){
				Map<String, Object> oneMap = map.get(1);
				Map<String, Object> twoMap = map.get(2);
				shipmentMapper.saveSubShipment(saveBeanByMap(oneMap, currentDate, shipmentId));
				shipmentMapper.saveSubShipment(saveBeanByMap(twoMap, currentDate, shipmentId));
				shipmentMapper.saveSubShipment(saveBean(lastMap, currentDate, shipmentId));
			}
		}
	}
	
	public SubShipment saveBean(Map<String, Object> map, Date currentDate, Long shipmentId){
		SubShipment subShipment = new SubShipment();
		subShipment.setConsignee(map.get("consignee").toString());
		subShipment.setSegmentSequence(Long.parseLong(map.get("consignee").toString()));
		subShipment.setShippingSegmentId(map.get("shippingSegmentId").toString());
		subShipment.setShipToAddr(map.get("shipToAddr").toString());
		subShipment.setShipToCity(map.get("shipToCity").toString());
		subShipment.setShipToCountry(map.get("shipToCountry").toString());
		subShipment.setShipToDistrict(map.get("shipToDistrict").toString());
		subShipment.setShipToProvince(map.get("shipToProvince").toString());
		subShipment.setShipmentId(shipmentId);
		subShipment.setStatus(ContainerType.OPEN);
		subShipment.setUpdatedAt(currentDate);
		subShipment.setCreatedAt(currentDate);
		return subShipment;
	}
	
	public SubShipment saveBeanByMap(Map<String, Object> map, Date currentDate, Long shipmentId){
		SubShipment subShipment = new SubShipment();
		subShipment.setConsignee(map.get("transfer_consignee")==null?" ":map.get("transfer_consignee").toString());
		subShipment.setSegmentSequence(Long.parseLong(map.get("segmentSequence")==null?"0":map.get("segmentSequence").toString()));
		subShipment.setShippingSegmentId(map.get("shippingSegmentId")==null?" ":map.get("shippingSegmentId").toString());
		subShipment.setShipToAddr(map.get("transfer_addr")==null?" ":map.get("transfer_addr").toString());
		subShipment.setShipToDistrict(map.get("addr_district")==null?" ":map.get("addr_district").toString());
		subShipment.setShipToCity(map.get("addr_city")==null?" ":map.get("addr_city").toString());
		subShipment.setShipToProvince(map.get("addr_province")==null?" ":map.get("addr_province").toString());
		subShipment.setShipToCountry(map.get("addr_country")==null?" ":map.get("addr_country").toString());
		subShipment.setShipmentId(shipmentId);
		subShipment.setStatus(ContainerType.OPEN);
		subShipment.setUpdatedAt(currentDate);
		subShipment.setCreatedAt(currentDate);
		return subShipment;
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
	 * 新增subspment
	 */
	@Override
	public int saveSubShipment(SubShipment sub) {
		return shipmentMapper.saveSubShipment(sub);
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

}
