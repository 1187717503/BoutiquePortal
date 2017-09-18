package com.intramirror.product.core.mapper;

import com.intramirror.product.api.model.SkuStore;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface SkuStoreMapper {

    /**
     * 根据SkuId修改库存相关信息
     *
     * @param statusType-订单的状态 skuid
     * @return
     */
    int updateBySkuId(Map<String, Object> param);

    /**
     * 根据shopProductSkuId 查询SKUid
     *
     * @param shopProductSkuId
     * @return
     */
    Long selectSkuIdByShopProductSkuId(Long shopProductSkuId);

    List<Map<String, Object>> getSkuStoreBySkuId(@Param("skuIdString") String[] skuIdString);


    int confirmSkuStore(@Param("skuStoreId") Long skuStoreId);

    int confirmSkuStoreByNegativeStore(@Param("skuStoreId") Long skuStoreId);

    int cancelSkuStore(@Param("shopProductSkuId") Long shopProductSkuId);

    List<SkuStore> selectSkuStoreByShopProductSkuId(@Param("shopProductSkuId") Long shopProductSkuId);

    void batchExecuteBySql(@Param("reservedTwo") BigDecimal reservedTwo, @Param("skuId") Long skuId);
}
