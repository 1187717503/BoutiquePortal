package com.intramirror.main.core.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.intramirror.main.api.service.ApiParameterService;
import com.intramirror.main.core.dao.BaseDao;
import com.intramirror.main.core.mapper.ApiParameterMapper;

@Service
public class ApiParameterServiceImpl extends BaseDao implements ApiParameterService{

	private ApiParameterMapper apiParameterMapper;
	 
	@Override
	public void init() {
		
		apiParameterMapper = this.getSqlSession().getMapper(ApiParameterMapper.class); 
	}
		
	@Override
	public List<Map<String, Object>> getapiParameterByCondition(
			Map<String, Object> param) {
		return apiParameterMapper.getapiParameterByCondition(param);
	}



}
