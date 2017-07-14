package com.intramirror.product.api.service;


import com.intramirror.product.api.model.Sku;

public interface SkuService {

    Sku getSkuBySkuCode(String skuCode);
}
