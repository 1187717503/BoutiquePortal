package com.intramirror.main.api.service;


import com.intramirror.main.api.model.AddressCountry;

import java.util.List;
import java.util.Map;

public interface AddressProvinceService {

    List<Map<String,Object>> getAddressProvinceByCountryId(Long geographyId);
}
