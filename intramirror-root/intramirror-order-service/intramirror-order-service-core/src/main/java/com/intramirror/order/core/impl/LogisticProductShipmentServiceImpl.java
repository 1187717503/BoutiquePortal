package com.intramirror.order.core.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
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
	
}
