package com.intramirror.product.core.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intramirror.product.api.model.Shop;
import com.intramirror.product.api.service.IShopService;
import com.intramirror.product.core.dao.BaseDao;
import com.intramirror.product.core.mapper.ShopMapper;

public class ShopServiceImpl extends BaseDao implements IShopService{

    private static Logger logger = LoggerFactory.getLogger(ShopServiceImpl.class);

    private ShopMapper shopMapper;
    
	@Override
	public int deleteByPrimaryKey(Long shopId) {
		return shopMapper.deleteByPrimaryKey(shopId);
	}

	@Override
	public int insert(Shop record) {
		return shopMapper.insert(record);
	}

	@Override
	public int insertSelective(Shop record) {
		return shopMapper.insertSelective(record);
	}

	@Override
	public Shop selectByPrimaryKey(Long shopId) {
		return shopMapper.selectByPrimaryKey(shopId);
	}

	@Override
	public Shop selectByParameter(Map<String, Object> map) {
		return shopMapper.selectByParameter(map);
	}

	@Override
	public int updateByPrimaryKeySelective(Shop record) {
		return shopMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKeyWithBLOBs(Shop record) {
		return shopMapper.updateByPrimaryKeyWithBLOBs(record);
	}

	@Override
	public int updateByPrimaryKey(Shop record) {
		return shopMapper.updateByPrimaryKey(record);
	}

	@Override
	public void init() {
		shopMapper = this.getSqlSession().getMapper(ShopMapper.class);
		
	}

}
