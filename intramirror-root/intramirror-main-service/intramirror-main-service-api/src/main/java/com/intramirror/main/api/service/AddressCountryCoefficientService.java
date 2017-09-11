package com.intramirror.main.api.service;


import java.util.List;
import java.util.Map;

public interface AddressCountryCoefficientService {

    List<Map<String, Object>> getAddressCountryCoefficientByCountry(Integer fromCountry, Integer toCountry);
}
