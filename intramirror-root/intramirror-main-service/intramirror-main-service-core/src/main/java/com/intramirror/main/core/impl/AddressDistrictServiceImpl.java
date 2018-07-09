package com.intramirror.main.core.impl;


import com.intramirror.main.api.model.AddressCountry;
import com.intramirror.main.api.service.AddressCountryService;
import com.intramirror.main.api.service.AddressDistrictService;
import com.intramirror.main.core.dao.BaseDao;
import com.intramirror.main.core.mapper.AddressCountryMapper;
import com.intramirror.main.core.mapper.AddressDistrictMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 服务
 */
@Service
public class AddressDistrictServiceImpl extends BaseDao implements AddressDistrictService {

    private AddressDistrictMapper addressDistrictMapper;


    public void init() {
        addressDistrictMapper = this.getSqlSession().getMapper(AddressDistrictMapper.class);
    }

    @Override
    public List<Map<String, Object>> getAddressDistrictByCityId(Long cityId) {

        return addressDistrictMapper.getAddressDistrictByCityId(cityId);
    }
}
