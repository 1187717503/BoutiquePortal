package com.intramirror.order.api.service;


import java.util.List;
import java.util.Map;

import com.intramirror.order.api.model.LogisticProductContainer;
import com.intramirror.order.api.model.LogisticsProduct;

public interface ILogisticsProductService {
	
	/**
	 * 根据logistics_product_id 修改相关信息
	 * @param logistics_product_id
	 * @return
	 */
	int updateOrderLogisticsStatusById (Long logistics_product_id,int status);
	
	
	/**
	 * 根据containerId 修改相关信息
	 * @param conditionMap
	 * @return
	 */
	int updateByContainerId (Map<String, Object> conditionMap);
	
	
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
	Map<String,Object> selectLogisProShipmentById(Long logistics_product_id);
	
	
	
	/**
	 * 根据 logistics_product_id vendorID查询关联logistic_product详情
	 * @param conditionMap
	 * @return
	 */
	Map<String,Object> getOrderInfoByVendorId(Map<String, Object> conditionMap);
	
	
	
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
	 * 根据logistics_product_id 修改相关信息
	 * @param logisticsProduct
	 * @return
	 */
	int updateByLogisticsProduct (LogisticsProduct logisticsProduct);
	
	/**
	 * 根据Id修改container关联
	 * @param order_logistics_id
	 * @return
	 */
	int updateContainerById(Long order_logistics_id);


	LogisticsProduct createLogisticsProduct(LogisticsProduct logisticsProduct);

	int invalidOrderById(Long logisticsProductId);

	void addStockLocation(Long logisProductId,String stockLocation);

	/**
	 * 添加carton和订单关联表
	 * @param logisticProductContainer
	 */
	void insertLogisticProductContainer(LogisticProductContainer logisticProductContainer);

	/**
	 * 删除关联表
	 * @param logisticProductContainer
	 */
	void updateLogisticProductContainer(LogisticProductContainer logisticProductContainer);

	/**
	 * 查询carton和订单关联表
	 * @param logisticProductContainer
	 * @return
	 */
	LogisticProductContainer getLogisticProductContainer(LogisticProductContainer logisticProductContainer);

	LogisticsProduct selectByOrderLineNum(String orderLineNum);

}

