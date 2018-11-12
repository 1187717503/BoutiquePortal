package com.intramirror.main.api.service;


import com.intramirror.main.api.model.Tax;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface TaxService {

    List<Map<String, Object>> getTaxRateListById(String fromCountryId, String taxType);

    List<Map<String, Object>> getTaxByCategoryId(String taxType, String[] categoryIds);

    Tax getTaxByAddressCountryId(Long addressCountryId);

    /**
     * 根据订单获取相关税率
     * @param orderLineNum
     * @return
     */
    BigDecimal calculateTax(String orderLineNum);

    /**
     * 获取折扣税率
     * @param orderLineNum
     * @return
     */
    Tax calculateDiscountTax(String orderLineNum);
}
