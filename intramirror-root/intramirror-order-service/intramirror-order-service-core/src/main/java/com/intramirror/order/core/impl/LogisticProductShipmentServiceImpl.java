package com.intramirror.order.core.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.intramirror.order.api.model.LogisticProductShipment;
import com.intramirror.order.api.service.ILogisticProductShipmentService;
import com.intramirror.order.core.dao.BaseDao;
import com.intramirror.order.core.mapper.LogisticProductShipmentMapper;

@Service
public class LogisticProductShipmentServiceImpl extends BaseDao implements ILogisticProductShipmentService{

	private static Logger logger = LoggerFactory.getLogger(SubShipmentServiceImpl.class);
	
	private LogisticProductShipmentMapper logisticProductShipmentMapper;
	
	@Override
	public void init() {
		logisticProductShipmentMapper = this.getSqlSession().getMapper(LogisticProductShipmentMapper.class);
	}

	/**
	 * 新增
	 */
	@Override
	public int insertlpShipment(Map<String, Object> map) {
		logger.info("parameter Info :" +new Gson().toJson(map));
		return logisticProductShipmentMapper.insertlpShipment(map);
	}

	@Override
	public List<LogisticProductShipment> selectById(Map<String, Object> map) {
		return logisticProductShipmentMapper.selectById(map);
	}

	@Override
	public int deleteById(Long subShipmentId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("subShipmentId", subShipmentId);
		return logisticProductShipmentMapper.deleteById(map);
	}

	@Override
	public int updateBysubShipmentId(Map<String, Object> map) {
		return logisticProductShipmentMapper.updateBysubShipmentId(map);
	}
	
}
