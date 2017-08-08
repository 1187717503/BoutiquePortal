package com.intramirror.order.api.service;


import java.util.List;
import java.util.Map;

import com.intramirror.order.api.model.LogisticsProduct;

public interface ILogisticsProductService {
	
	/**
	 * 根据logistics_product_id 修改相关信息
	 * @param logistics_product_id
	 * @return
	 */
	int updateOrderLogisticsStatusById (Long logistics_product_id,int status);
	
	
	/**
	 * 根据 logistics_product_id 查询详情
	 * @param logistics_product_id
	 * @return
	 */
	LogisticsProduct selectById(Long logistics_product_id);
	
	/**
	 * 根据condition map 来获取 OrderLogistics list
	 * @param conditionMap
	 * @return 
	 * @throws Exception
	 */
	List<LogisticsProduct> getLogisticsProductListByCondition(Map<String, Object> conditionMap);
	
	/**
	 * 根据logistics_product_id 修改相关信息
	 * @param LogisticsProduct
	 * @return
	 */
	int updateByLogisticsProduct (LogisticsProduct logisticsProduct);
	
}

