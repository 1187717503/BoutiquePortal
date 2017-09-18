package com.intramirror.main.api.service;


import com.intramirror.main.api.model.Tax;

import java.util.List;
import java.util.Map;

public interface TaxService {

    List<Map<String, Object>> getTaxRateListById(String fromCountryId, String taxType);

    List<Map<String, Object>> getTaxByCategoryId(String taxType, String[] categoryIds);

    Tax getTaxByAddressCountryId(Long addressCountryId);
}
