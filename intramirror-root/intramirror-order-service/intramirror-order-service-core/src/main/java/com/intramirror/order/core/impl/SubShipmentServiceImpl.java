/**
 * 
 */
package com.intramirror.order.core.impl;

import com.google.gson.Gson;
import com.intramirror.common.core.mapper.SubShipmentMapper;
import com.intramirror.order.api.common.ContainerType;
import com.intramirror.order.api.model.SubShipment;
import com.intramirror.order.api.service.ISubShipmentService;
import com.intramirror.order.core.dao.BaseDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单装箱service
 * @author yuan
 *
 */
@Service
public class SubShipmentServiceImpl extends BaseDao implements ISubShipmentService{

	private static Logger logger = LoggerFactory.getLogger(SubShipmentServiceImpl.class);
	
	private SubShipmentMapper subShipmentMapper;
	
	@Override
	public void init() {
		subShipmentMapper = this.getSqlSession().getMapper(SubShipmentMapper.class);
	}

	/**
	 * 保存subShipment
	 */
	@Override
	public int insertSubshipment(Map<String, Object> map) {
		logger.info("param : " + new Gson().toJson(map));
		Map<String, Object> beanMap = new HashMap<String, Object>();
		beanMap.put("consignee", map.get("consignee").toString());
		beanMap.put("segmentSequence", Long.parseLong(map.get("segmentSequence").toString()));
		beanMap.put("shippingSegmentId", Long.parseLong(map.get("shippingSegmentId").toString()));
		beanMap.put("shipToAddr", map.get("shipToAddr").toString());
		beanMap.put("shipToCity", map.get("shipToCity").toString());
		beanMap.put("shipToCountry", map.get("shipToCountry").toString());
		beanMap.put("shipToDistrict", map.get("shipToDistrict").toString());
		beanMap.put("shipToProvince", map.get("shipToProvince").toString());
		beanMap.put("shipmentId", 1);
		beanMap.put("status", ContainerType.RECEIVED);
		Date currentDate = new Date();
		beanMap.put("updatedAt", currentDate);
		beanMap.put("createdAt", currentDate);
		return subShipmentMapper.insertSubshipment(beanMap);
	}

	/**
	 * 查询subShipmentID
	 */
	@Override
	public Long getSubshipment(Map<String, Object> map) {
		return subShipmentMapper.getSubshipment(map);
	}

	@Override
	public int deleteSubShipmentByShipmentId(Map<String, Object> map) {
		return subShipmentMapper.deleteSubShipmentByShipmentId(map);
	}

	@Override
	public List<SubShipment> getSubShipmentByShipmentId(Long shipmentId) {
		return subShipmentMapper.getSubShipmentByShipmentId(shipmentId);
	}

	@Override
	public int deleteByPrimaryKey(Long subShipmentId) {
		// TODO Auto-generated method stub
		return subShipmentMapper.deleteByPrimaryKey(subShipmentId);
	}


}
