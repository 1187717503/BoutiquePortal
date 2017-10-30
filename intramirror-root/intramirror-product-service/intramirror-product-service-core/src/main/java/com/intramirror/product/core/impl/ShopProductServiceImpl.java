package com.intramirror.product.core.impl;

import com.intramirror.product.api.model.ShopProduct;
import com.intramirror.product.api.model.ShopProductWithBLOBs;
import com.intramirror.product.api.service.ShopProductService;
import com.intramirror.product.core.dao.BaseDao;
import com.intramirror.product.core.mapper.ShopProductMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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

    @Override
    public int deleteByPrimaryKey(Long shopProductId) {
        return shopProductMapper.deleteByPrimaryKey(shopProductId);
    }

    @Override
    public int insert(ShopProductWithBLOBs record) {
        return shopProductMapper.insert(record);
    }

    @Override
    public int insertSelective(ShopProductWithBLOBs record) {
        return shopProductMapper.insertSelective(record);
    }

    @Override
    public int updateByPrimaryKeySelective(ShopProductWithBLOBs record) {
        return shopProductMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKeyWithBLOBs(ShopProductWithBLOBs record) {
        return shopProductMapper.updateByPrimaryKeyWithBLOBs(record);
    }

    @Override
    public int updateByPrimaryKey(ShopProduct record) {
        return shopProductMapper.updateByPrimaryKey(record);
    }

    @Override
    public int updateShopProductByProductId(ShopProduct record) {
        return shopProductMapper.updateShopProductByProductId(record);
    }

    @Override
    public Long insertAndGetId(ShopProductWithBLOBs shopProduct) {
        return shopProductMapper.insertAndGetId(shopProduct);
    }



}
