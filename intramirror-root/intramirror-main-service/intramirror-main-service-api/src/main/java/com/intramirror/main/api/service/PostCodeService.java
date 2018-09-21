package com.intramirror.main.api.service;

import com.intramirror.main.api.model.PostCode;

import java.util.List;
import java.util.Map;

public interface PostCodeService {

    List<Map<String,String>> getCityNameByCountryIdAndCityName(Long countryId, String cityName);

    List<Map<String,String>> getCityNameByCountryCodeAndCityName(String code,String cityName);

    PostCode getByCountryCode(String code, String cityName);

    PostCode getByCountryId(Long countryId,String cityName);
}
