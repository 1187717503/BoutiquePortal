package com.intramirror.order.core.mapper;

import java.util.List;
import java.util.Map;

import com.intramirror.order.api.model.LogisticsProduct;
import org.apache.ibatis.annotations.Param;

public interface LogisticsProductMapper {

    /**
     * 根据logistics_product_id 修改相关信息
     * @param LogisticsProduct
     * @return
     */
    int updateByLogisticsProduct(LogisticsProduct logisticsProduct);

    /**
     * 根据containerId 修改相关信息
     * @param invoice
     *         id  containerId 数组
     * @return
     */
    int updateByContainerId(Map<String, Object> conditionMap);

    /**
     * 根据 logistics_product_id 查询详情
     * @param logistics_product_id
     * @return
     */
    LogisticsProduct selectById(Long logistics_product_id);

    /**
     * 根据 logistics_product_id 查询关联logistic_product_shipment详情
     * @param logistics_product_id
     * @return
     */
    List<Map<String, Object>> selectLogisProShipmentById(Long logistics_product_id);

    /**
     * 根据 logistics_product_id vendorID查询关联logistic_product详情
     * @param logistics_product_id
     *         vendorId
     * @return
     */
    Map<String, Object> getOrderInfoByVendorId(Map<String, Object> conditionMap);

    /**
     * 根据condition map 查询详情
     * @param conditionMap
     * @return
     * @throws Exception
     */
    List<LogisticsProduct> selectByCondition(Map<String, Object> conditionMap);

    /**
     * 根据condition map 来获取 OrderLogistics list
     * @param conditionMap
     * @return
     * @throws Exception
     */
    List<LogisticsProduct> getLogisticsProductListByCondition(Map<String, Object> conditionMap);

    /**
     * 根据Id修改container关联
     * @param LogisticsProduct
     * @return
     */
    int updateContainerById(Long order_logistics_id);

    int createLogisticsProduct(LogisticsProduct logisticsProduct);

    /**
     * 更新订单状态为失效
     * @param logisticsProductId
     * @return
     */
    int invalidOrderById(Long logisticsProductId);

    void addStockLocation(Map<String,Object> map);

    List<LogisticsProduct> getLogisticsProductByShipment(Long shipmentId);

    Map<String,Object> getShopProductSku(Long logisticsProductId);

    LogisticsProduct selectByOrderLineNum(@Param("orderLineNum") String orderLineNum);

    List<Map<String, Object>> queryLogisticProductConfirm(Long logisticsProductId);

    Long getVendorAddressCountryIdBylogisticsProductId(Long logisticsProductId);

    int getExpressTypeBylogisticsProductId(Long logisticsProductId);


}
