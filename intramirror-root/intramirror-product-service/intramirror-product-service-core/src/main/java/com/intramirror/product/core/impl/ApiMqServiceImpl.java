package com.intramirror.product.core.impl;

import com.intramirror.product.api.model.ApiMq;
import com.intramirror.product.api.service.IApiMqService;
import com.intramirror.product.core.dao.BaseDao;
import com.intramirror.product.core.mapper.ApiMqMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by dingyifan on 2017/8/24.
 */
@Service
public class ApiMqServiceImpl extends BaseDao implements IApiMqService{

    private ApiMqMapper apiMqMapper;

    public void init() {
        apiMqMapper = this.getSqlSession().getMapper(ApiMqMapper.class);
    }

    @Override
    public List<ApiMq> getMqs() {
        return apiMqMapper.selectAllActiveData();
    }

    @Override
    public int updateByMqName(ApiMq apiMq) {
        return apiMqMapper.updateByMqName(apiMq);
    }

    @Override
    public List<Map<String, Object>> selectMqByName(String mqName) {
        return apiMqMapper.selectMqByName(mqName);
    }

    @Override
    public List<Map<String, Object>> selectApiInfoByAtelier(Map<String, Object> params) {
        return apiMqMapper.selectApiInfoByAtelier(params);
    }
}
