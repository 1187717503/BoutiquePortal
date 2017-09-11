package com.intramirror.user.core.mapper;

import com.intramirror.user.api.model.Vendor;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface VendorMapper {
    List<Vendor> getVendorListByUserIdAndEnabled(@Param("userId") Long userId, @Param("enabled") Boolean enabled);

    List<Map<String, Object>> queryAllVendor(Map<String, Object> params);

    List<Map<String, Object>> queryRuleVendor(Map<String, Object> params);

    Vendor queryVendorByVendorId(Map<String, Object> params);

    Map<String, String> getProductSkuVendorIdMap(@Param("skuIds") String[] skuIds);

    List<Map<String, Object>> getAllVendorCountryById(@Param("vendorIds") String[] vendorIds);
}