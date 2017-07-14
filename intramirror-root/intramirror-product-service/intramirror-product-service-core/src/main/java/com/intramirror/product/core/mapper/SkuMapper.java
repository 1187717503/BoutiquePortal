package com.intramirror.product.core.mapper;

import com.intramirror.product.api.model.Sku;
import org.apache.ibatis.annotations.Param;

public interface SkuMapper {

    Sku getSkuBySkuCodeAndEnabled(@Param("skuCode") String skuCode, @Param("enabled") Boolean enabled);
}