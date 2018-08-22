package com.intramirror.main.core.impl;

import com.intramirror.main.api.model.StockLocation;
import com.intramirror.main.api.service.StockLocationService;
import com.intramirror.main.core.dao.BaseDao;
import com.intramirror.main.core.mapper.StockLocationMapper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caowei on 2018/4/9.
 */
@Service
public class StockLocationServiceImpl extends BaseDao implements StockLocationService {


    private StockLocationMapper stockLocationMapper;

    @Override
    public void init() {
        stockLocationMapper = this.getSqlSession().getMapper(StockLocationMapper.class);
    }

    @Override
    public List<StockLocation> getStockLocation(Long vendorId) {
        List<StockLocation> locationList = stockLocationMapper.getStockLocationByVendorId(vendorId);
        return locationList;
    }

    @Override
    public List<StockLocation> getStockLocationByVendorIds(List<Long> vendorIds) {
        if(CollectionUtils.isEmpty(vendorIds)){
            return null;
        }
        List<StockLocation> locationList = stockLocationMapper.getStockLocationByVendorIds(vendorIds);
        return locationList;
    }

    @Override
    public StockLocation getShipFromLocation(Long shipmentId) {
        return stockLocationMapper.getShipFromLocation(shipmentId);
    }
}
