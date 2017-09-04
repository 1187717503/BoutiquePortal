/**
 * 
 */
package com.intramirror.order.core.impl;

import java.util.Date;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.intramirror.order.api.common.ContainerType;
import com.intramirror.order.api.service.IOrderCommentsService;
import com.intramirror.order.core.dao.BaseDao;
import com.intramirror.order.core.mapper.OrderCommentsMapper;

/**
 * @author yml
 *
 */
@Service
public class OrderCommentsServiceImpl extends BaseDao implements IOrderCommentsService {

	private OrderCommentsMapper orderCommentsMapper;
	
	@Override
	public void init() {
		orderCommentsMapper = this.getSqlSession().getMapper(OrderCommentsMapper.class);
	}
	
	@Override
	public int saveOrderComments(Map<String, Object> map) {
		map.put("logistics_product_id", Long.parseLong(map.get("logistics_product_id").toString()));
		Date currentDate = new Date();
		map.put("order_exception_type_id", Long.parseLong(map.get("reason").toString()));
		map.put("created_at", currentDate);
		map.put("status",ContainerType.PENDINGAUDIT);
		return orderCommentsMapper.saveOrderComments(map);
	}

	

}
