package com.intramirror.order.core.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.intramirror.order.api.service.IOrderService;
import com.intramirror.order.core.dao.BaseDao;
import com.intramirror.order.core.mapper.OrderMapper;

@Service
public class OrderServiceImpl extends BaseDao implements IOrderService{
	
    private static Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private OrderMapper orderMapper;

    public void init() {
    	orderMapper = this.getSqlSession().getMapper(OrderMapper.class);
    }

    public Map<String,Object> getOrderList() {
    	Map<String,Object> result = orderMapper.getOrderList();
        logger.info("result:{}",new Gson().toJson(result));
        return result;
    }
}
