package com.intramirror.main.api.service;

import com.intramirror.main.api.model.StockLocation;
import com.intramirror.main.api.vo.StockLocationVO;

import java.util.List;
import java.util.Map;

/**
 * Created by caowei on 2018/4/9.
 */
public interface StockLocationService {

    List<StockLocation> getStockLocations(Long vendorId);

    List<StockLocationVO> getStockLocationByVendorIds(List<Long> vendorIds);

    StockLocation getShipFromLocation(Long shipmentId);

    StockLocation getStockLocation(Long locationId);
}
