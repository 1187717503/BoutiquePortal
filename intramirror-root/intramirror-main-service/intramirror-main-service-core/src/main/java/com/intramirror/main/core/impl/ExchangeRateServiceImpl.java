package com.intramirror.main.core.impl;


import com.intramirror.main.api.service.AddressDistrictService;
import com.intramirror.main.api.service.ExchangeRateService;
import com.intramirror.main.core.dao.BaseDao;
import com.intramirror.main.core.mapper.AddressDistrictMapper;
import com.intramirror.main.core.mapper.ExchangeRateMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 服务
 */
@Service
public class ExchangeRateServiceImpl extends BaseDao implements ExchangeRateService {

    private ExchangeRateMapper exchangeRateMapper;


    public void init() {
        exchangeRateMapper = this.getSqlSession().getMapper(ExchangeRateMapper.class);
    }

    @Override
    public List<Map<String, Object>> getShipFeeByCityId(String from_country, String to_country) {
        return exchangeRateMapper.getShipFeeByCityId(from_country, to_country);
    }
}
