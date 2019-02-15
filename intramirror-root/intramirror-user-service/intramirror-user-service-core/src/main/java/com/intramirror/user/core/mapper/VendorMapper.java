package com.intramirror.user.core.mapper;

import com.intramirror.user.api.model.Vendor;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface VendorMapper {
    List<Vendor> getVendorListByUserIdAndEnabled(@Param("userId") Long userId, @Param("enabled") Boolean enabled);

    List<Vendor> getChildVendorListByUserIdAndEnabled(@Param("userId") Long userId, @Param("enabled") Boolean enabled);

    List<Map<String, Object>> queryAllVendor(Map<String, Object> params);

    List<Map<String, Object>> queryRuleVendor(Map<String, Object> params);

    List<Vendor> queryVendorByIds(Map<String, Object> param);

    Vendor queryVendorByVendorId(Map<String, Object> params);

    List<Map<String, Object>> getProductSkuVendorIdMap(@Param("skuIds") String[] skuIds);

    List<Map<String, Object>> getAllVendorCountryById(@Param("vendorIds") String[] vendorIds);

    void updateByPrimaryKeySelective(Vendor vendor);

    Long selectAllowImportProductByVendorId(@Param("vendorId") Long vendorId);
}