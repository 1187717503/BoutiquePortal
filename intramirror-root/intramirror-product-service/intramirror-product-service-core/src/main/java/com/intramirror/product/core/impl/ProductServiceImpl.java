package com.intramirror.product.core.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.intramirror.product.api.model.Product;
import com.intramirror.product.api.model.ProductWithBLOBs;
import com.intramirror.product.api.service.IProductService;
import com.intramirror.product.core.dao.BaseDao;
import com.intramirror.product.core.mapper.ProductMapper;

@Service
public class ProductServiceImpl extends BaseDao implements IProductService{

    private static Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    private ProductMapper productMapper;
    
	@Override
	public int deleteByPrimaryKey(Long productId) {
		return productMapper.deleteByPrimaryKey(productId);
	}

	@Override
	public int insert(ProductWithBLOBs record) {
		
		return productMapper.insert(record);
	}

	@Override
	public int insertSelective(ProductWithBLOBs record) {
		
		return productMapper.insertSelective(record);
	}

	@Override
	public ProductWithBLOBs selectByPrimaryKey(Long productId) {
		
		return productMapper.selectByPrimaryKey(productId);
	}

	@Override
	public ProductWithBLOBs selectByParameter(ProductWithBLOBs record) {
		
		return productMapper.selectByParameter(record);
	}

	@Override
	public int updateByPrimaryKeySelective(ProductWithBLOBs record) {
		
		return productMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKeyWithBLOBs(ProductWithBLOBs record) {
		
		return productMapper.updateByPrimaryKeyWithBLOBs(record);
	}

	@Override
	public int updateByPrimaryKey(Product record) {
		
		return productMapper.updateByPrimaryKey(record);
	}

	@Override
	public void init() {
		productMapper = this.getSqlSession().getMapper(ProductMapper.class);
	}

}
