package com.intramirror.order.api.vo;

import com.intramirror.order.api.model.ThirdWarehouse;

/**
 * Created by caowei on 2019/1/7.
 */
public class ThirdWarehouseVO extends ThirdWarehouse {
    private Integer addressCountryId;
    private Integer expressType;
    private String spuImage;


    public String getSpuImage() {
        return spuImage;
    }

    public void setSpuImage(String spuImage) {
        this.spuImage = spuImage;
    }

    public Integer getExpressType() {
        return expressType;
    }

    public void setExpressType(Integer expressType) {
        this.expressType = expressType;
    }

    public Integer getAddressCountryId() {
        return addressCountryId;
    }

    public void setAddressCountryId(Integer addressCountryId) {
        this.addressCountryId = addressCountryId;
    }
}
