package com.intramirror.product.api.service;

import java.util.List;
import java.util.Map;

public interface ISkuStoreService {

    /**
     * 根据SkuId修改库存相关信息
     *
     * @param statusType-订单的状态
     *         skuid
     * @return
     */
    int updateBySkuId(int statusType, long skuid);

    int cancelSkuStore(Long shopProductSkuId);

    /**
     * 根据skuId修改确认库存
     *
     * @param shopProductSkuId
     * @return
     */
    void updateConfirmStoreByShopProductSkuId(Long shopProductSkuId) throws Exception;

    /**
     * 根据shopProductSkuId 查询SKUid
     *
     * @param shopProductSkuId
     * @return
     */
    Long selectSkuIdByShopProductSkuId(Long shopProductSkuId);

    void updateSkuStoreReserved(List<String> reservedList, String[] skuIdString, List<Long> skuIds) throws Exception;

    List<Map<String, Object>> listSkuStoreByProductId(Long productId);

    List<Map<String, Object>> listSkuStoreByProductList(List<Map<String, Object>> products);

    List<Map<String, Object>> listStockByProductList(List<Map<String, Object>> products);

    Long getTotalStockByProductId(Long productId);

    List<Map<String, Object>> listTotalStockByProductIds(List<Long> productIds);

}
