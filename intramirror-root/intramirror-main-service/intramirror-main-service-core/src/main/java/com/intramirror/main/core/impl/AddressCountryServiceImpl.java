package com.intramirror.main.core.impl;


import com.intramirror.main.api.model.AddressCountry;
import com.intramirror.main.api.service.AddressCountryService;
import com.intramirror.main.core.dao.BaseDao;
import com.intramirror.main.core.mapper.AddressCountryMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 服务
 */
@Service
public class AddressCountryServiceImpl extends BaseDao implements AddressCountryService {

    private AddressCountryMapper addressCountryMapper;


    public void init() {
        addressCountryMapper = this.getSqlSession().getMapper(AddressCountryMapper.class);
    }

    @Override
    public AddressCountry getAddressCountryByName(String name) {
        return addressCountryMapper.getAddressCountryByName(name);
    }

    @Override
    public List<Map<String, Object>> getAddressCountryByGeographyId(Long geographyId) {

        return addressCountryMapper.getAddressCountryByGeographyId(geographyId);
    }

    @Override
    public List<AddressCountry> getList() {
        List<AddressCountry> list = addressCountryMapper.getList();
        return list;
    }

    @Override
    public AddressCountry getAddressCountryByCountryCode(String countryCode) {
        return addressCountryMapper.getAddressCountryByCountryCode(countryCode);
    }
}
