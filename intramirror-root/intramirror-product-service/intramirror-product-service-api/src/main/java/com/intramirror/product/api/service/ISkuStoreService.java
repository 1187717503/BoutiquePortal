package com.intramirror.product.api.service;


public interface ISkuStoreService {
	
	/**
	 * 根据SkuId修改库存相关信息
	 * @param statusType-订单的状态     skuid 
	 * @return
	 */
	Long updateBySkuId(int statusType,long skuid);
}
