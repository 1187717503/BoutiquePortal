package com.intramirror.main.core.impl;


import com.intramirror.main.api.model.AddressCountry;
import com.intramirror.main.api.service.AddressCityService;
import com.intramirror.main.api.service.AddressCountryService;
import com.intramirror.main.core.dao.BaseDao;
import com.intramirror.main.core.mapper.AddressCityMapper;
import com.intramirror.main.core.mapper.AddressCountryMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 服务
 */
@Service
public class AddressCityServiceImpl extends BaseDao implements AddressCityService {

    private AddressCityMapper addressCityMapper;


    public void init() {
        addressCityMapper = this.getSqlSession().getMapper(AddressCityMapper.class);
    }

    @Override
    public List<Map<String, Object>> getAddressCityByProvinceId(Long provinceId) {

        return addressCityMapper.getAddressCityByProvinceId(provinceId);
    }
}
