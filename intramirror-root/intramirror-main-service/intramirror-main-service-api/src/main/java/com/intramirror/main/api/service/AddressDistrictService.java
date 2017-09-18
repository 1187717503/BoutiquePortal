package com.intramirror.main.api.service;


import java.util.List;
import java.util.Map;

public interface AddressDistrictService {

    List<Map<String,Object>> getAddressDistrictByCityId(Long cityId);
}
