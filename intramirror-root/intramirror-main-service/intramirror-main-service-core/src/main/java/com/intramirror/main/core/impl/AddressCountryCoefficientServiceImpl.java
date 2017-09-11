package com.intramirror.main.core.impl;


import com.intramirror.main.api.service.AddressCountryCoefficientService;
import com.intramirror.main.core.dao.BaseDao;
import com.intramirror.main.core.mapper.AddressCountryCoefficientMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 服务
 */
@Service
public class AddressCountryCoefficientServiceImpl extends BaseDao implements AddressCountryCoefficientService {

    private AddressCountryCoefficientMapper addressCountryCoefficientMapper;


    public void init() {
        addressCountryCoefficientMapper = this.getSqlSession().getMapper(AddressCountryCoefficientMapper.class);
    }


    @Override
    public List<Map<String, Object>> getAddressCountryCoefficientByCountry(Integer fromCountry, Integer toCountry) {
        return addressCountryCoefficientMapper.getAddressCountryCoefficientByCountry(fromCountry,toCountry);
    }
}
