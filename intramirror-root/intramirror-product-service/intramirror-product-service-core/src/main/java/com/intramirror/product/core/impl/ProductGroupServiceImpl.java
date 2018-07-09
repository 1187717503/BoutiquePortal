package com.intramirror.product.core.impl;

import com.intramirror.product.api.model.ProductGroup;
import com.intramirror.product.api.service.IProductGroupService;
import com.intramirror.product.core.dao.BaseDao;
import com.intramirror.product.core.mapper.ProductGroupMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductGroupServiceImpl extends BaseDao implements IProductGroupService {

    private static Logger logger = LoggerFactory.getLogger(ProductGroupServiceImpl.class);

    private ProductGroupMapper productGroupMapper;

    public void init() {
        productGroupMapper = this.getSqlSession().getMapper(ProductGroupMapper.class);
    }

    @Override
    public List<ProductGroup> getProductGroupListByGroupTypeAndVendorId(String groupType, Long vendorId) {
        return productGroupMapper.getProductGroupListByGroupTypeAndVendorId(groupType, vendorId);
    }
}
