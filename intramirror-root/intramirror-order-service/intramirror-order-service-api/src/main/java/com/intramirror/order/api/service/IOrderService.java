package com.intramirror.order.api.service;

import com.intramirror.common.help.Page;
import com.intramirror.common.help.PageUtils;
import com.intramirror.order.api.model.Order;
import com.intramirror.order.api.model.Shipment;
import com.intramirror.order.api.vo.ShippedParam;

import java.util.List;
import java.util.Map;

public interface IOrderService {

    /**
     * 获取订单列表信息
     *
     * @return
     */
    List<Map<String, Object>> getOrderList(int status);

    /**
     * 根据orderNumber 和 订单状态获取订单列表
     *
     * @return
     */
    List<Map<String, Object>> getOrderListByOrderNumber(String numbers, int status);


    /**
     * 根据logisticsProductId 获取支付相关信息
     *
     * @param logisticsProductId
     * @return
     */
    List<Map<String, Object>> getOrderPaymentByLogisProductId(Long logisticsProductId);

    /**
     * 根据status统计各个状态的订单数量
     *
     * @param
     * @return
     */
    int getOrderByIsvalidCount(Map<String, Object> param);

    /**
     * 根据订单号查询物流ID
     *
     * @param orderId
     * @return map
     */
    Map<String, Object> getOrderPaymentInfoByOrderId(int orderId);

    /**
     * 根据订单号查询支付信息
     *
     * @param orderId
     * @return
     */
    Map<String, Object> getPaymentInfoByOrderId(int orderId);

    /**
     * 根据订单获取Shipment
     *
     * @param
     * @return
     */
    Shipment getOrderByShipment(int logisticsProductId);

    /**
     * 根据 订单状态获取子订单列表
     *
     * @param status
     * @return
     */
    List<Map<String, Object>> getOrderListByStatus(int status, Long vendorId, String sortByName);

    
    /**
     * 根据 订单状态获取子订单列表
     *
     * @param status
     * @return
     */
    List<Map<String, Object>> getOrderListByStatusAndContainerId(int containerId, int status, Long vendorId);
    
    
    
    /**
     * 根据 logisticsProductId 获取订单详情
     *
     * @param logisticsProductId
     * @return
     */
     Map<String, Object> getOrderLogisticsInfoByIdWithSql(Long logisticsProductId);
     


    /**
     * 根据 ShipmentId 查询箱子跟订单信息
     *
     * @param
     * @return
     */
    List<Map<String, Object>> getOrderListByShipmentId(Map<String, Object> conditionMap);


    /**
     * 根据 条件获取子订单详情
     *
     * @param
     * @return
     */
    Map<String, Object> getOrderInfoByCondition(Map<String, Object> map);

    /**
     * Shipped根据 订单状态获取子订单列表
     *
     * @param
     * @return
     */
    PageUtils getShippedOrderListByStatus(Page page, Long vendorId, ShippedParam shippedParam);

    /**
     * 根据条件查询订单信息
     *
     * @param conditionMap
     * @return
     */
    Map<String, Object> getShipmentDetails(Map<String, Object> conditionMap);

    /**
     * 统计shipped数量
     *
     * @param map
     * @return
     */
    Integer getShippedCount(Map<String, Object> map);

    Order createOrder(Order order);

    void updateOrder(Order order);

    List<Map<String,Object>> atelierSelectOrder(Map<String,Object> conditionMap);


}
