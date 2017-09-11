package com.intramirror.product.core.mapper;

import com.intramirror.product.api.model.Sku;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Map;

public interface SkuMapper {

    Sku getSkuBySkuCodeAndEnabled(@Param("skuCode") String skuCode, @Param("enabled") Boolean enabled);

    Map<String, BigDecimal> getSkuInfoBySkuId(String shopProductSkuId);

    Sku getSkuById(@Param("skuId") Long skuId);
}