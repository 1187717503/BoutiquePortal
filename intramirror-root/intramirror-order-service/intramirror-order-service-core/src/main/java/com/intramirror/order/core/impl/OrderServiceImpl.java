package com.intramirror.order.core.impl;

import java.util.HashMap;
import java.util.List;
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

    public List<Map<String,Object>> getOrderList(int status) {
    	Map<String,Object> param = new HashMap<String, Object>();
    	param.put("status",status);
    	List<Map<String,Object>> result = orderMapper.getOrderList(param);
        logger.info("getOrderList result:{}",new Gson().toJson(result));
        return result;
    }

    
    
	public List<Map<String, Object>> getOrderListByOrderNumber(String numbers,int status) {
    	Map<String,Object> param = new HashMap<String, Object>();
    	param.put("orderNumbers", numbers.split(","));
    	param.put("status", status);
    	List<Map<String,Object>> result = orderMapper.getOrderListByOrderNumber(param);
        logger.info("getOrderListByOrderNumber result:{}",new Gson().toJson(result));
        return result;
	}

	
	public List<Map<String, Object>> getOrderPaymentByLogisProductId(
			Long logisticsProductId) {
		return orderMapper.getOrderPaymentByLogisProductId(logisticsProductId);
	}
}
