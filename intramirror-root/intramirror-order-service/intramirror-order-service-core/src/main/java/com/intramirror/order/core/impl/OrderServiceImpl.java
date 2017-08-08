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
	public int getOrderByIsvalidCount(Map<String,Object> param){
		return orderMapper.getOrderByIsvalidCount(param);
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
		List<Map<String, Object>> orderMapList = orderMapper.getPaymentInfoByOrderId(orderId);
        if (orderMapList.size() > 0) {
            return orderMapList.get(0);
        }
		return null;
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
	public List<Map<String, Object>> getOrderListByStatus(int status,Long vendorId) {
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("status", status);
		conditionMap.put("vendorId", vendorId);
		return orderMapper.getOrderListByStatus(conditionMap);
	}

	/**
	 * 根据 订单状态获 和 container ID取子订单列表
	 * @param orderNumber status
	 * @return
	 */
	@Override
	public List<Map<String, Object>> getOrderListByStatusAndContainerId(
			int containerId, int status,Long vendorId) {
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("status", status);
		conditionMap.put("vendorId", vendorId);
		conditionMap.put("containerId", containerId);
		return orderMapper.getOrderListByStatusAndContainerId(conditionMap);
	}

	@Override
	public Map<String, Object> getOrderInfoByCondition(
			Map<String, Object> map) {
		List<Map<String, Object>> list = orderMapper.getOrderInfoByCondition(map);
		if(list != null && list.size() > 0){
			return list.get(0);
		}
		 return null;
	}
	
}