/**
 * 
 */
package com.intramirror.main.core.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.intramirror.main.api.service.SkuPropertyService;
import com.intramirror.main.core.dao.BaseDao;
import com.intramirror.main.core.mapper.SkuPropertyMapping;

/**
 * @author yml
 *
 */
@Service
public class SkuPropertyMappingServiceImpl extends BaseDao implements SkuPropertyService{

	private SkuPropertyMapping skuPropertyMapping;
	
	@Override
	public void init() {
		skuPropertyMapping = this.getSqlSession().getMapper(SkuPropertyMapping.class);
	}
	
	@Override
	public Map<String, Object> getSizeValue(Map<String, Object> map) {
		List<Map<String, Object>> list = skuPropertyMapping.getSizeValue(map);
		if (null != list && 0 < list.size()){
			return list.get(0);
		}
		return null;
	}


}
