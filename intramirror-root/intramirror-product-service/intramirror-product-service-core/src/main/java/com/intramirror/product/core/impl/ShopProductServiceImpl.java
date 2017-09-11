package com.intramirror.product.core.impl;

import com.intramirror.product.api.model.ProductGroup;
import com.intramirror.product.api.model.ShopProduct;
import com.intramirror.product.api.service.IProductGroupService;
import com.intramirror.product.api.service.ShopProductService;
import com.intramirror.product.core.dao.BaseDao;
import com.intramirror.product.core.mapper.ProductGroupMapper;
import com.intramirror.product.core.mapper.ShopProductMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShopProductServiceImpl extends BaseDao implements ShopProductService {

    private static Logger logger = LoggerFactory.getLogger(ShopProductServiceImpl.class);

    private ShopProductMapper shopProductMapper;

    public void init() {
        shopProductMapper = this.getSqlSession().getMapper(ShopProductMapper.class);
    }


    @Override
    public ShopProduct getShopProductById(Long shopProductId) {
        return shopProductMapper.selectByPrimaryKey(shopProductId);
    }
}
