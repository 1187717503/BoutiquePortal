package com.intramirror.order.core.mapper;

import java.util.List;
import java.util.Map;

import com.intramirror.order.api.model.Order;
import com.intramirror.order.api.model.Shipment;
import com.intramirror.order.api.model.CancelOrderVO;

public interface OrderMapper {

    /**
     * 根据子订单状态获取 order 列表
     *
     * @param param
     * @return
     */
    List<Map<String, Object>> getOrderList(Map<String, Object> param);


    /**
     * 根据orderNumber 获取子订单列表
     *
     * @param param
     * @return
     */
    List<Map<String, Object>> getOrderListByOrderNumber(Map<String, Object> param);


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
     * @param param
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
    List<Map<String, Object>> getPaymentInfoByOrderId(int orderId);

    /**
     * 根据订单获取Shipment
     *
     * @param logisticsProductId
     * @return
     */
    Shipment getOrderByShipment(int logisticsProductId);


    /**
     * 根据 订单状态获取子订单列表
     *
     * @param conditionMap
     * @return
     */
    List<Map<String, Object>> getOrderListByStatus(Map<String, Object> conditionMap);


    /**
     * 根据 订单状态获取子订单列表
     *
     * @param conditionMap
     * @return
     */
    List<Map<String, Object>> getOrderListByStatusAndContainerId(Map<String, Object> conditionMap);
    
    List<Map<String,Object>> selectCreateThreeOrderInfo(Map<String,Object> params);
    
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
     * @param conditionMap
     * @return
     */
    List<Map<String, Object>> getOrderListByShipmentId(Map<String, Object> conditionMap);


    /**
     * 根据 条件获取子订单信息
     *
     * @param conditionMap
     * @return
     */
    List<Map<String, Object>> getOrderInfoByCondition(Map<String, Object> conditionMap);

    /**
     * Shipped根据 订单状态获取子订单列表
     *
     * @return
     */
    List<Map<String, Object>> getShippedOrderListByStatus(Map<String, Object> conditionMap);

    /**
     * 根据条件查询订单信息
     *
     * @param conditionMap
     * @return
     */
    List<Map<String, Object>> getShipmentDetails(Map<String, Object> conditionMap);

    /**
     * 统计shipped数量
     *
     * @param map
     * @return
     */
    Integer getShippedCount(Map<String, Object> map);

    int createOrder(Order order);

    void updateById(Order order);
    
    List<Map<String,Object>> atelierSelectOrder(Map<String,Object> conditionMap);
    List<Map<String,Object>> selectOrderInfo(Map<String,Object> conditionMap);

    /**
     * 查询子订单列表
     * @param params
     * @return
     */
    List<CancelOrderVO> getOrderCancelList(Map<String, Object> params);

}