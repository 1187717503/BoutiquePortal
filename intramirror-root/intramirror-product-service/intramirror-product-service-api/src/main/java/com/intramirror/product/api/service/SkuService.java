package com.intramirror.product.api.service;

import com.intramirror.product.api.model.Sku;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface SkuService {

    Sku getSkuBySkuCode(String skuCode);

    Sku getSkuById(Long skuId);

    Map<String, BigDecimal> getSkuInfoBySkuId(String shopProductSkuIds);

    Map<String, Object> getSkuInfoBySkuCode(Map<String, Object> condition);

    List<Sku> listSkuInfoByProductId(Long productId);

    List<Map<String, Object>> listPriceByProductList(List<Map<String, Object>> products);
}
