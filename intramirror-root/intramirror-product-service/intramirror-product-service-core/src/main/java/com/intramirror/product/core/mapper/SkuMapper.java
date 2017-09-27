package com.intramirror.product.core.mapper;

import com.intramirror.product.api.model.Sku;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface SkuMapper {

    Sku getSkuBySkuCodeAndEnabled(@Param("skuCode") String skuCode, @Param("enabled") Boolean enabled);

    List<Map<String, Object>> getSkuInfoBySkuId(String shopProductSkuId);

    Sku getSkuById(@Param("skuId") Long skuId);
    
    Map<String, Object> getSkuInfoBySkuCode(Map<String, Object> condition);
}