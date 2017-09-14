package com.intramirror.product.core.mapper;

import com.intramirror.product.api.model.SkuStore;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SkuStoreMapper {
	
	/**
	 * 根据SkuId修改库存相关信息
	 * @param param statusType-订单的状态     skuid
	 * @return
	 */
	int updateBySkuId (Map<String,Object> param);
	
	/**
	 * 根据shopProductSkuId 查询SKUid
	 * @param shopProductSkuId
	 * @return
	 */
	Long selectSkuIdByShopProductSkuId(Long shopProductSkuId);

	int confirmSkuStore(@Param("skuStoreId") Long skuStoreId);

	int confirmSkuStoreByNegativeStore(@Param("skuStoreId") Long skuStoreId);

	int cancelSkuStore(@Param("shopProductSkuId") Long shopProductSkuId);

	List<SkuStore> selectSkuStoreByShopProductSkuId (@Param("shopProductSkuId") Long shopProductSkuId);

}
