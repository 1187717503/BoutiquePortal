package com.intramirror.order.core.impl;

import com.google.gson.Gson;
import com.intramirror.common.help.IPageService;
import com.intramirror.common.help.Page;
import com.intramirror.common.help.PageUtils;
import com.intramirror.order.api.model.Order;
import com.intramirror.order.api.model.Shipment;
import com.intramirror.order.api.service.IOrderService;
import com.intramirror.order.api.vo.ShippedParam;
import com.intramirror.order.core.dao.BaseDao;
import com.intramirror.order.core.mapper.OrderMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl extends BaseDao implements IOrderService, IPageService {

    private static Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private OrderMapper orderMapper;

    public void init() {
        orderMapper = this.getSqlSession().getMapper(OrderMapper.class);
    }

    public List<Map<String, Object>> getOrderList(int status) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("status", status);
        List<Map<String, Object>> result = orderMapper.getOrderList(param);
        logger.info("getOrderList result:{}", new Gson().toJson(result));
        return result;
    }


    public List<Map<String, Object>> getOrderListByOrderNumber(String numbers, int status) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("orderNumbers", numbers.split(","));
        param.put("status", status);
        List<Map<String, Object>> result = orderMapper.getOrderListByOrderNumber(param);
        logger.info("getOrderListByOrderNumber result:{}", new Gson().toJson(result));
        return result;
    }


    public List<Map<String, Object>> getOrderPaymentByLogisProductId(
            Long logisticsProductId) {
        return orderMapper.getOrderPaymentByLogisProductId(logisticsProductId);
    }

    /**
     * 根据status统计各个状态的订单数量
     *
     * @param
     * @return Integer
     */
    public int getOrderByIsvalidCount(Map<String, Object> param) {
        return orderMapper.getOrderByIsvalidCount(param);
    }

    /**
     * 根据订单号查询物流ID
     *
     * @param orderId
     * @return map
     */
    public Map<String, Object> getOrderPaymentInfoByOrderId(int orderId) {
        return orderMapper.getOrderPaymentInfoByOrderId(orderId);
    }

    /**
     * 根据订单号查询支付信息
     *
     * @param orderId
     * @return
     */
    public Map<String, Object> getPaymentInfoByOrderId(int orderId) {
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
     *
     * @param status
     * @return
     */
    @Override
    public List<Map<String, Object>> getOrderListByStatus(int status, Long vendorId, String sortByName) {
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("status", status);
        conditionMap.put("vendorId", vendorId);
        if (sortByName != null && StringUtils.isNoneBlank(sortByName)) {
            conditionMap.put(sortByName, sortByName);
        }
        return orderMapper.getOrderListByStatus(conditionMap);
    }

    /**
     * 根据 订单状态获 和 container ID取子订单列表
     *
     * @param status
     * @return
     */
    @Override
    public List<Map<String, Object>> getOrderListByStatusAndContainerId(
            int containerId, int status, Long vendorId) {
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
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public PageUtils getShippedOrderListByStatus(Page page, Long vendorId, ShippedParam shippedParam) {
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("vendorId", vendorId);
        conditionMap.put("shippedParam", shippedParam);
        PageUtils pageUtils = new PageUtils(page, this, conditionMap);
        return pageUtils;
    }

    @Override
    public List<Map<String, Object>> getOrderListByShipmentId(
            Map<String, Object> conditionMap) {

        return orderMapper.getOrderListByShipmentId(conditionMap);
    }

    @Override
    public Map<String, Object> getShipmentDetails(Map<String, Object> conditionMap) {
        List<Map<String, Object>> list = orderMapper.getShipmentDetails(conditionMap);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public List<Map<String, Object>> getResult(Map<String, Object> params) {
        return orderMapper.getShippedOrderListByStatus(params);
    }

    @Override
    public Integer getShippedCount(Map<String, Object> map) {
        return orderMapper.getShippedCount(map);
    }

    @Override
    public Order createOrder(Order order) {
        orderMapper.createOrder(order);
        // 创建订单编号
        String orderNum = generateOrderNum(order.getUserId(), order.getOrderId());
        order.setOrderNum(orderNum);
        orderMapper.updateById(order);
        return order;
}

    @Override
    public void updateOrder(Order order) {
        orderMapper.updateById(order);
    }

    // 订单号生成规则：
    // 日期（8位）＋ 用户ID（末3位）＋ 订单order ID（末5位）
    //
    // 例如：2014092100400023

    /**
     * 订单号生成规则：
     *
     * @param userId
     * @param orderId
     * @return
     */
    private String generateOrderNum(Long userId, Long orderId) {
        String userStr = "000" + userId;
        String orderStr = "00000" + orderId;
        userStr = userStr.substring(userStr.length() - 3, userStr.length());
        orderStr = orderStr.substring(orderStr.length() - 5, orderStr.length());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        return simpleDateFormat.format(new Date()) + userStr + orderStr;
    }
}