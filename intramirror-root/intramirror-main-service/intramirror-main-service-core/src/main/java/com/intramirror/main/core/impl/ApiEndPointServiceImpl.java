package com.intramirror.main.core.impl;


import com.intramirror.main.api.service.ApiEndPointService;
import com.intramirror.main.core.dao.BaseDao;
import com.intramirror.main.core.mapper.ApiEndPointMapper;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class ApiEndPointServiceImpl extends BaseDao implements ApiEndPointService {

    private ApiEndPointMapper apiEndPointMapper;


    public void init() {
    	apiEndPointMapper = this.getSqlSession().getMapper(ApiEndPointMapper.class);
    }


	@Override
	public List<Map<String, Object>> getapiEndpointInfoByCondition(
			Map<String, Object> param) {
		
		return apiEndPointMapper.getapiEndpointInfoByCondition(param);
	}


}
