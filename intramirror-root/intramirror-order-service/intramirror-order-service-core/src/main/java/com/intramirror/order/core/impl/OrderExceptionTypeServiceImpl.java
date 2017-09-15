/**
 * 
 */
package com.intramirror.order.core.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.intramirror.order.api.service.IOrderExceptionTypeService;
import com.intramirror.order.core.dao.BaseDao;
import com.intramirror.order.core.mapper.OrderExceptionTypeMapper;

/**
 * @author yml
 *
 */
@Service
public class OrderExceptionTypeServiceImpl extends BaseDao implements IOrderExceptionTypeService {

	private OrderExceptionTypeMapper orderExceptionTypeMapper;
	
	@Override
	public void init() {
		orderExceptionTypeMapper = this.getSqlSession().getMapper(OrderExceptionTypeMapper.class);
	}

	@Override
	public List<Map<String, Object>> getExceptionType(Map<String, Object> map) {
		return orderExceptionTypeMapper.getExceptionType(map);
	}
	


	

}
