/**
 * 
 */
package com.intramirror.order.core.impl;

import java.util.Date;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.intramirror.order.api.common.ContainerType;
import com.intramirror.order.api.service.IOrderExceptionService;
import com.intramirror.order.core.dao.BaseDao;
import com.intramirror.order.core.mapper.OrderExceptionMapper;

/**
 * @author yml
 *
 */
@Service
public class OrderExceptionServiceImpl extends BaseDao implements IOrderExceptionService {

	private OrderExceptionMapper orderExceptionMapper;
	
	@Override
	public void init() {
		orderExceptionMapper = this.getSqlSession().getMapper(OrderExceptionMapper.class);
	}
	
	@Override
	public int saveOrderComments(Map<String, Object> map) {
		map.put("logistics_product_id", Long.parseLong(map.get("logistics_product_id").toString()));
		Date currentDate = new Date();
		map.put("order_exception_type_id", Long.parseLong(map.get("reason").toString()));
		map.put("created_at", currentDate);
		map.put("status",ContainerType.PENDINGAUDIT);
		return orderExceptionMapper.saveOrderComments(map);
	}

	@Override
	public int updateOrderExceptionById(Map<String, Object> map) {
		return orderExceptionMapper.updateOrderException(map);
	}

	

}
