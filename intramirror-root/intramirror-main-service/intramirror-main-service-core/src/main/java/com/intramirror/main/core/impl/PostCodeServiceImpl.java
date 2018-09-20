package com.intramirror.main.core.impl;

import com.intramirror.main.api.model.PostCode;
import com.intramirror.main.api.service.PostCodeService;
import com.intramirror.main.core.dao.BaseDao;
import com.intramirror.main.core.mapper.PostCodeMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PostCodeServiceImpl extends BaseDao implements PostCodeService {

    private PostCodeMapper postCodeMapper;

    public void init() {
        postCodeMapper = this.getSqlSession().getMapper(PostCodeMapper.class);
    }

    @Override
    public List<Map<String,String>> getCityNameByCountryIdAndCityName(Long countryId, String cityName) {
        return postCodeMapper.getCityNameByCountryIdAndCityName(countryId,cityName);
    }

    @Override
    public List<Map<String, String>> getCityNameByCountryCodeAndCityName(String code, String cityName) {
        return postCodeMapper.getCityNameByCountryCodeAndCityName(code,cityName);
    }
}
