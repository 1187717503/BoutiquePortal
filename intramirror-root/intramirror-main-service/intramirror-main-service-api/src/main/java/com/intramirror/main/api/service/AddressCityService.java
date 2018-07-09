package com.intramirror.main.api.service;


import java.util.List;
import java.util.Map;

public interface AddressCityService {

    List<Map<String,Object>> getAddressCityByProvinceId(Long provinceId);
}
