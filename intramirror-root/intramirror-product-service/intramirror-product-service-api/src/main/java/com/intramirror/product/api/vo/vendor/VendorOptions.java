package com.intramirror.product.api.vo.vendor;

/**
 * Created by dingyifan on 2017/8/2.
 * 这个是提供给接口用的vendor vo,用于在创建，修改信息的时候,存储该商品创建时的配置
 */
public class VendorOptions {
    public String storeCode;

    public Long vendorId;

    public Long apiConfigurationId;

    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    public Long getApiConfigurationId() {
        return apiConfigurationId;
    }

    public void setApiConfigurationId(Long apiConfigurationId) {
        this.apiConfigurationId = apiConfigurationId;
    }

    public String getStoreCode() {
        return storeCode;
    }

    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode;
    }
}
