package com.intramirror.main.api.service;


import java.util.List;
import java.util.Map;

public interface ExchangeRateService {

    List<Map<String, Object>> getShipFeeByCityId(String from_country, String to_country);
}
