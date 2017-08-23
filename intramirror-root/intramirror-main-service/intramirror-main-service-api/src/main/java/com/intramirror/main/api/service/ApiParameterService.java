package com.intramirror.main.api.service;

import java.util.List;
import java.util.Map;

public interface ApiParameterService {
	
	/**
	 * 根据api_end_point_id 获取相关信息
	 * @param api_end_point_id
	 * @return
	 */
	List<Map<String,Object>> getapiParameterByCondition(Map<String,Object> param);
}
