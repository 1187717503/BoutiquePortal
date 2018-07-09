package com.intramirror.product.core.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.intramirror.product.api.model.SystemProperty;
import com.intramirror.product.api.service.ISystemPropertyService;
import com.intramirror.product.core.dao.BaseDao;
import com.intramirror.product.core.mapper.SystemPropertyMapper;

import java.util.List;
import java.util.Map;

@Service
public class SystemPropertyServiceImpl extends BaseDao implements ISystemPropertyService{

    private static Logger logger = LoggerFactory.getLogger(SystemPropertyServiceImpl.class);

    private SystemPropertyMapper systemPropertyMapper;
    
	@Override
	public int deleteByPrimaryKey(Long systemPropertyId) {
		
		return systemPropertyMapper.deleteByPrimaryKey(systemPropertyId);
	}

	@Override
	public int insert(SystemProperty record) {
	
		return systemPropertyMapper.insert(record);
	}

	@Override
	public int insertSelective(SystemProperty record) {
		
		return systemPropertyMapper.insertSelective(record);
	}

	@Override
	public SystemProperty selectByPrimaryKey(Long systemPropertyId) {

		return systemPropertyMapper.selectByPrimaryKey(systemPropertyId);
	}

	@Override
	public int updateByPrimaryKeySelective(SystemProperty record) {
		
		return systemPropertyMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(SystemProperty record) {
	
		return systemPropertyMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<Map<String, Object>> selectSystemProperty() throws Exception {
		return systemPropertyMapper.selectSystemProperty();
	}

	@Override
	public void init() {
		systemPropertyMapper = this.getSqlSession().getMapper(SystemPropertyMapper.class);
		
	}

	@Override
	public int updateByNameSelective(SystemProperty record) {
		
		return systemPropertyMapper.updateByNameSelective(record);
	}

}
