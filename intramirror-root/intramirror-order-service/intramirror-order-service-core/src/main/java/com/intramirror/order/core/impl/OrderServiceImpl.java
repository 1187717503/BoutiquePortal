package com.intramirror.order.core.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.intramirror.order.api.model.Shipment;
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

	/**
	 * 根据status统计各个状态的订单数量
	 * @param status
	 * @return Integer
	 */
	public int getOrderByIsvalidCount(int status){
		return orderMapper.getOrderByIsvalidCount(status);
	}

	/**
	 * 根据订单号查询物流ID
	 * @param orderId
	 * @return map
	 */
	public Map<String, Object> getOrderPaymentInfoByOrderId(int orderId){
		return orderMapper.getOrderPaymentInfoByOrderId(orderId);
	}

	/**
	 * 根据订单号查询支付信息
	 * @param orderId
	 * @return
	 */
	public Map<String, Object> getPaymentInfoByOrderId(int orderId){
		return orderMapper.getPaymentInfoByOrderId(orderId);
	}

	/**
	 * 根据订单获取Shipment 
	 */
	public Shipment getOrderByShipment(int logisticsProductId) {
		return orderMapper.getOrderByShipment(logisticsProductId);
	}


	/**
	 * 根据 订单状态获取子订单列表
	 * @param orderNumber status
	 * @return
	 */
	@Override
	public List<Map<String, Object>> getOrderListByStatus(int status) {
		return orderMapper.getOrderListByStatus(status);
	}
	
}