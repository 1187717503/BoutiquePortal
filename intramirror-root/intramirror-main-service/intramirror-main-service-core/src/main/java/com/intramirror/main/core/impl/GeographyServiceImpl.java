package com.intramirror.main.core.impl;


import com.intramirror.main.api.model.Geography;
import com.intramirror.main.api.service.GeographyService;
import com.intramirror.main.core.dao.BaseDao;
import com.intramirror.main.core.mapper.GeographyMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 服务
 */
@Service
public class GeographyServiceImpl extends BaseDao implements GeographyService {

    private GeographyMapper geographyMapper;


    public void init() {
        geographyMapper = this.getSqlSession().getMapper(GeographyMapper.class);
    }

    @Override
    public List<Map<String, Object>> getGeographyList() {
        return geographyMapper.getGeographyList();
    }

    @Override
    public Geography getGeographyById(Long geographyId) {
        return geographyMapper.selectByPrimaryKey(geographyId);
    }

    @Override
    public List<Map<String, Object>> getGeographyGroupList() {
        return geographyMapper.getGeographyGroupList();
    }
}
