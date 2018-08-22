package com.intramirror.main.api.vo;

/**
 * Created by caowei on 2018/4/23.
 */
public class StockLocationVO {

    private Long locationId;

    private String stockLocation;

    private Long vendorId;

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public String getStockLocation() {
        return stockLocation;
    }

    public void setStockLocation(String stockLocation) {
        this.stockLocation = stockLocation;
    }

    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }
}
