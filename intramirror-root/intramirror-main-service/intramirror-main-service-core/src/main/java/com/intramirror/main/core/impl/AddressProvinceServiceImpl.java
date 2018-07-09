package com.intramirror.main.core.impl;


import com.intramirror.main.api.service.AddressProvinceService;
import com.intramirror.main.core.dao.BaseDao;
import com.intramirror.main.core.mapper.AddressProvinceMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 服务
 */
@Service
public class AddressProvinceServiceImpl extends BaseDao implements AddressProvinceService {

    private AddressProvinceMapper addressProvinceMapper;


    public void init() {
        addressProvinceMapper = this.getSqlSession().getMapper(AddressProvinceMapper.class);
    }



    @Override
    public List<Map<String, Object>> getAddressProvinceByCountryId(Long countryId) {

        return addressProvinceMapper.getAddressProvinceByCountryId(countryId);
    }
}
