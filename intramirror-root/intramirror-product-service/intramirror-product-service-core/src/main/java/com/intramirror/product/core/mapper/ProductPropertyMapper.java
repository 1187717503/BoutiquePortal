package com.intramirror.product.core.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ProductPropertyMapper {

    List<Map<String, Object>> getProductPropertyByBrandIDAndColorCode(@Param("brandID") String brandID, @Param("colorCode") String colorCode);
}