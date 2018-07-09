package com.intramirror.user.api.service.vendor;

import com.intramirror.user.api.model.Vendor;

import java.util.List;
import java.util.Map;

/**
 * Created by dingyifan on 2017/7/20.
 */
public interface IQueryVendorService {
    List<Map<String,Object>> queryAllVendor(Map<String,Object> params) throws Exception;
    List<Map<String,Object>> queryRuleVendor(Map<String,Object> params) throws Exception;
    Vendor queryVendorByVendorId(Map<String,Object> params) throws Exception;
}
