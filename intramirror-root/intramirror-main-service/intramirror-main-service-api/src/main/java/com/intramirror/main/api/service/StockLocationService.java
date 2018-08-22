package com.intramirror.main.api.service;

import com.intramirror.main.api.model.StockLocation;

import java.util.List;

/**
 * Created by caowei on 2018/4/9.
 */
public interface StockLocationService {

    List<StockLocation> getStockLocation(Long vendorId);

    List<StockLocation> getStockLocationByVendorIds(List<Long> vendorIds);

    StockLocation getShipFromLocation(Long shipmentId);
}
