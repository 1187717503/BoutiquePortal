package com.intramirror.product.core.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.intramirror.product.api.model.Product;
import com.intramirror.product.api.model.ProductWithBLOBs;
import com.intramirror.product.api.service.IProductService;
import com.intramirror.product.core.dao.BaseDao;
import com.intramirror.product.core.mapper.ProductMapper;

import java.util.List;
import java.util.Map;

@Service
public class ProductServiceImpl extends BaseDao implements IProductService {

    private final static Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);

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
    public List<Map<String, Object>> getVendorCodeBySku(Long shopProductSkuId) {
        return productMapper.getVendorCodeBySku(shopProductSkuId);
    }

    @Override
    public int batchUpdateProductStatus(int status, List<Long> productIds) {
        return productMapper.batchUpdateProductStatus(status, productIds);
    }

    @Override
    public List<ProductWithBLOBs> listProductByProductIds(List<Long> productIds) {
        return productMapper.listProductByProductIds(productIds);
    }

    @Override
    public List<Map<String, Object>> selectDayNoUpdateSum(Map<String, Object> params) {
        return productMapper.selectDayNoUpdateSum(params);
    }

    @Override
    public List<Map<String, Object>> selectDayUpdateSum(Map<String, Object> params) {
        return productMapper.selectDayUpdateSum(params);
    }

    @Override
    public void init() {
        productMapper = this.getSqlSession().getMapper(ProductMapper.class);
    }

}
