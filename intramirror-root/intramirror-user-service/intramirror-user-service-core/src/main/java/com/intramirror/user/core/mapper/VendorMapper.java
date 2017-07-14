package com.intramirror.user.core.mapper;

import com.intramirror.user.api.model.Vendor;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface VendorMapper {
    List<Vendor> getVendorListByUserIdAndEnabled(@Param("userId") Long userId, @Param("enabled") Boolean enabled);
}