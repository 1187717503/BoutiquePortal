package com.intramirror.order.api.service;


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
}
