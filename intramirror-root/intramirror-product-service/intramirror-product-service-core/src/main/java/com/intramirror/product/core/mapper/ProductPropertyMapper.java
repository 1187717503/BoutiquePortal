package com.intramirror.product.core.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ProductPropertyMapper {

    List<Map<String, Object>> getProductPropertyByBrandIDAndColorCode(@Param("brandID") String brandID, @Param("colorCode") String colorCode);
    
    /**
     * 根据productId 查询brandID colorCode 信息
     * @param productId
     * @return
     */
    List<Map<String, Object>> selectByProductId(Map<String,Object> param);

    List<Map<String, Object>> getProductPropertyValueByBrandIdAndColorCode(@Param("brandID") String brandID, @Param("colorCode") String colorCode);

    List<Map<String, Object>> getProductPropertyValueByProductId(Long productId);
}