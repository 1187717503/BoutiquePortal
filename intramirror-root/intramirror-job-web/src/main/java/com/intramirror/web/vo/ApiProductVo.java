package com.intramirror.web.vo;

import pk.shoplus.model.ProductEDSManagement;

/**
 * Created by dingyifan on 2017/9/12.
 */
public class ApiProductVo {
    private ProductEDSManagement.ProductOptions productOptions;

    private ProductEDSManagement.VendorOptions vendorOptions;

    public ProductEDSManagement.ProductOptions getProductOptions() {
        return productOptions;
    }

    public void setProductOptions(ProductEDSManagement.ProductOptions productOptions) {
        this.productOptions = productOptions;
    }

    public ProductEDSManagement.VendorOptions getVendorOptions() {
        return vendorOptions;
    }

    public void setVendorOptions(ProductEDSManagement.VendorOptions vendorOptions) {
        this.vendorOptions = vendorOptions;
    }
}
