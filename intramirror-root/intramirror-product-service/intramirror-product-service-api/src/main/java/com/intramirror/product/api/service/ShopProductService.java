package com.intramirror.product.api.service;

import com.intramirror.product.api.model.ShopProduct;
import com.intramirror.product.api.model.ShopProductWithBLOBs;

public interface ShopProductService {

    ShopProduct getShopProductById(Long shopProductId);

    int deleteByPrimaryKey(Long shopProductId);

    int insert(ShopProductWithBLOBs record);

    int insertSelective(ShopProductWithBLOBs record);

    int updateByPrimaryKeySelective(ShopProductWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(ShopProductWithBLOBs record);

    int updateByPrimaryKey(ShopProduct record);

    int updateShopProductByProductId(ShopProduct record);

    Long insertAndGetId(ShopProductWithBLOBs shopProduct);

}
