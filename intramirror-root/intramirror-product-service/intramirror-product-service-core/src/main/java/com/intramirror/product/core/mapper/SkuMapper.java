package com.intramirror.product.core.mapper;

import com.intramirror.product.api.model.Sku;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

public interface SkuMapper {

    Sku getSkuBySkuCodeAndEnabled(@Param("skuCode") String skuCode, @Param("enabled") Boolean enabled);

    List<Map<String, Object>> getSkuInfoBySkuId(String shopProductSkuId);

    Sku getSkuById(@Param("skuId") Long skuId);

    Map<String, Object> getSkuInfoBySkuCode(Map<String, Object> condition);

    List<Sku> listSkuInfoByProductId(Long productId);

    List<Sku> listSkuInfoByProductIds(List<Long> productIds);

}