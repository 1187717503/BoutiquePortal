package com.intramirror.order.core.mapper;


import com.intramirror.order.api.model.LogisticsProduct;

public interface LogisticsProductMapper {
	
	/**
	 * 根据logistics_product_id 修改相关信息
	 * @param LogisticsProduct
	 * @return
	 */
	int updateByLogisticsProduct (LogisticsProduct logisticsProduct);
	
	
	/**
	 * 根据 logistics_product_id 查询详情
	 * @param logistics_product_id
	 * @return
	 */
	LogisticsProduct selectById(Long logistics_product_id);
	

}
