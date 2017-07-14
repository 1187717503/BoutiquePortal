package com.intramirror.product.core.mapper;

import java.util.Map;

public interface SkuStoreMapper {
	
	/**
	 * 根据SkuId修改库存相关信息
	 * @param statusType-订单的状态     skuid 
	 * @return
	 */
	Long updateBySkuId (Map<String,Object> param);
	
	/**
	 * 根据shopProductSkuId 查询SKUid
	 * @param shopProductSkuId
	 * @return
	 */
	Long selectSkuIdByShopProductSkuId(Long shopProductSkuId);
	

}
