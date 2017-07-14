package com.intramirror.user.core.mapper;

import com.intramirror.user.api.model.VendorApplication;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface VendorApplicationMapper {
    List<VendorApplication> getVendorApplicationListByUserIdAndEnabled(@Param("userId") Long userId, @Param("enabled") Boolean enabled);
}