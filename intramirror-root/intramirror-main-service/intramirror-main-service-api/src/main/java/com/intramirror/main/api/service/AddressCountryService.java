package com.intramirror.main.api.service;


import com.intramirror.main.api.model.AddressCountry;

import java.util.List;
import java.util.Map;

public interface AddressCountryService {

    AddressCountry getAddressCountryByName(String name);
    List<Map<String,Object>> getAddressCountryByGeographyId(Long geographyId);
    List<AddressCountry> getList();
    AddressCountry getAddressCountryByCountryCode(String countryCode);
}
