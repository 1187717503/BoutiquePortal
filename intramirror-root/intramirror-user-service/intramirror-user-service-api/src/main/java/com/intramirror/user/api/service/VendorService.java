package com.intramirror.user.api.service;


import com.intramirror.user.api.model.Vendor;
import com.intramirror.user.api.model.VendorApplication;

import java.util.List;
import java.util.Map;

public interface VendorService {

    VendorApplication getVendorApplicationByUserId(Long userId) throws Exception;

    Vendor getVendorByUserId(Long userId) throws Exception;

    List<Vendor> getVendorsByUserId(Long userId) throws Exception;

    Vendor getVendorByVendorId(Map<String, Object> params);

    Map<String, String> getProductSkuVendorIdMap(String[] shopProductSkuIds);

    List<Map<String, Object>> getAllVendorCountryById(String[] vendorIds);

    List<Vendor> getVendorByIds(List<Long> ids);

    void updateByPrimaryKeySelective(Vendor vendor);
}
