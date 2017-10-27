package com.intramirror.product.api.service;

import com.intramirror.product.api.model.ShopProductSku;

import java.util.List;
import java.util.Map;

public interface ShopProductSkuService {

    List<Map<String, Object>> getSkuByShopProductId(Long shopProductId);

    ShopProductSku getShopProductSkuById(Long shopProductSkuId);

    Map<String, Object> getSkuBySkuId(Long skuId);

    List<Map<String, Object>> getSkuIdByShopProductSkuId(String[] shopProductSkuIds);

    int insertSelective(ShopProductSku record);

    int updateByShopProductId(ShopProductSku record);

}
