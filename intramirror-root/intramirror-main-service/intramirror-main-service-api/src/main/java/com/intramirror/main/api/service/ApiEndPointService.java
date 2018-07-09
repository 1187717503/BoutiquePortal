package com.intramirror.main.api.service;

import java.util.List;
import java.util.Map;

public interface ApiEndPointService {
	
	/**
	 * 根据系统跟接口名称   获取相关信息
	 * @param system name
	 * @return
	 */
	List<Map<String,Object>> getapiEndpointInfoByCondition(Map<String,Object> param);

}
