package com.intramirror.logistics.core.impl;

import com.intramirror.logistics.api.model.Tax;
import com.intramirror.logistics.api.service.TaxService;
import com.intramirror.logistics.core.dao.BaseDao;
import com.intramirror.logistics.core.mapper.TaxMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TaxServiceImpl extends BaseDao implements TaxService{
	
	private static Logger logger = LoggerFactory.getLogger(TaxServiceImpl.class);
	
	private TaxMapper taxMapper;
	
	@Override
	public void init() {
		taxMapper = this.getSqlSession().getMapper(TaxMapper.class);
	}


	@Override
	public Tax getTaxByAddressCountryId(Long addressCountryId) {
		return taxMapper.getTaxByAddressCountryId(addressCountryId);
	}
}
