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
		Map<String, Object> typeMap = new HashMap<String, Object>();
		typeMap.put("consigner_country_id",Long.parseLong(map.get("consigner_country_id").toString()));	
		typeMap.put("consignee_country_id",Long.parseLong(map.get("consignee_country_id").toString()));
		typeMap.put("vendor_id",Long.parseLong(map.get("vendor_id").toString()));	
		List<Map<String, Object>> sequenceMap = shipmentMapper.getShippmentByType(typeMap);
		logger.info("parameter :" + new Gson().toJson(sequenceMap));
		//如果只有一段直接生产shipment 否则生成子shepment
		Shipment shipment = new Shipment();
		shipment.setShipToGeography(map.get("shipToGeography").toString());
		shipment.setConsignee(map.get("consignee").toString());
		shipment.setShipToAddr(map.get("shipToAddr").toString());
		shipment.setShipToDistrict(map.get("shipToDistrict").toString());
		shipment.setShipToCity(map.get("shipToCity").toString());
		shipment.setShipToProvince(map.get("shipToProvince").toString());
		shipment.setShipToCountry(map.get("shipToCountry").toString());
		shipment.setStatus(ContainerType.OPEN);
		Date currentDate = new Date();
		shipment.setCreatedAt(currentDate);
		shipment.setUpdatedAt(currentDate);
		logger.info("parameter :" + new Gson().toJson(shipment));
		return shipmentMapper.saveShipmentByOrderId(shipment);
	}

	/**
	 * 根据订单号查询shipment基本信息
	 * @param map
	 * @return map
	 */
	@Override
	public List<Map<String, Object>> selectShipmentByOrder(Map<String, Object> map) {
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


}
