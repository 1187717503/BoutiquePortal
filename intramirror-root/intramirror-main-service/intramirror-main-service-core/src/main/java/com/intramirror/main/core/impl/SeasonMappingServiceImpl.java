/**
 * 
 */
package com.intramirror.main.core.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.intramirror.main.api.service.SeasonMappingService;
import com.intramirror.main.core.dao.BaseDao;
import com.intramirror.main.core.mapper.SeasonMappingMapping;

/**
 * @author 123
 *
 */
@Service
public class SeasonMappingServiceImpl extends BaseDao implements SeasonMappingService{

	private SeasonMappingMapping seasonMappingMapping;
	
	@Override
	public void init() {
		seasonMappingMapping = this.getSqlSession().getMapper(SeasonMappingMapping.class);
	}
	
	@Override
	public List<Map<String, Object>> getBoutiqueSeasonCode(String vendorId) {
		return seasonMappingMapping.getBoutiqueSeasonCode(vendorId);
	}



}
