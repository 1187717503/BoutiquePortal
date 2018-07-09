package com.intramirror.web.distributed.utils;

import com.alibaba.fastjson15.JSONArray;
import com.intramirror.web.distributed.vo.ProductImageVo;
import pk.shoplus.model.ProductEDSManagement;

public class DispersalUtils {

    public ProductImageVo getProductImageVo(ProductEDSManagement.ProductOptions productOptions, Long vendorId) {
        ProductImageVo productImageVo = new ProductImageVo();
        productImageVo.setProductCode(productOptions.getCode());
        productImageVo.setImages(JSONArray.parseArray(productOptions.getCoverImg(), String.class));
        productImageVo.setVendorId(vendorId);
        productOptions.setCoverImg("[]");
        return productImageVo;
    }

}
