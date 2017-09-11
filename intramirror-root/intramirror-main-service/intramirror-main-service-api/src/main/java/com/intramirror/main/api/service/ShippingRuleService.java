package com.intramirror.main.api.service;


import java.util.List;
import java.util.Map;

public interface ShippingRuleService {

    List<Map<String, Object>> getShippingFeeByProductIds (String[] productIds, Integer toCountry);
}
