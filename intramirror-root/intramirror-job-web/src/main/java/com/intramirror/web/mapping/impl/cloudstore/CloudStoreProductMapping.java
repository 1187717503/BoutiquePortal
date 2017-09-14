package com.intramirror.web.mapping.impl.cloudstore;

import com.alibaba.fastjson15.JSONArray;
import com.alibaba.fastjson15.JSONObject;
import com.google.gson.Gson;
import com.intramirror.web.mapping.api.IDataMapping;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sql2o.Connection;
import pk.shoplus.DBConnector;
import pk.shoplus.model.ProductEDSManagement;
import pk.shoplus.service.MappingCategoryService;
import pk.shoplus.util.ExceptionUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service(value = "cloudStoreProductMapping")
public class CloudStoreProductMapping implements IDataMapping {

    private static Logger logger = Logger.getLogger(CloudStoreProductMapping.class);

    @Override
    public ProductEDSManagement.ProductOptions mapping(Map<String,Object> bodyDataMap) {
        return null;
    }
}
