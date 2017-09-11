package com.intramirror.web.mapping.api;

import pk.shoplus.model.ProductEDSManagement;

import java.util.Map;

/**
 * Created by dingyifan on 2017/9/11.
 */
public interface IDataMapping {
    ProductEDSManagement.ProductOptions mapping(Map<String,Object> bodyDataMap);
}
