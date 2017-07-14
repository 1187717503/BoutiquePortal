package com.intramirror.product.api.service;


import java.util.List;
import java.util.Map;

public interface ProductPropertyService {

    /**
     * 根据BrandID和ColorCode查询
     *
     * @param brandID
     * @param colorCode
     * @return
     */
    List<Map<String, Object>> getProductPropertyByBrandIDAndColorCode(String brandID, String colorCode);
}
