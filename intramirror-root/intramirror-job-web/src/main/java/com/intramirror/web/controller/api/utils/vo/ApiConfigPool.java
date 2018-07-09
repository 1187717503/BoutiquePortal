package com.intramirror.web.controller.api.utils.vo;

import com.intramirror.web.util.ApiDataFileUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by dingyifan on 2018/1/12.
 */
public class ApiConfigPool {

    private Map<String,ApiConfig> configPool;

    public void put(ApiConfig config){
        if(configPool == null) {
            configPool = new HashMap<>();
        }
        config.setFileUtils(new ApiDataFileUtils(config.getVendorName(),config.getEventName()));
        configPool.put(config.getVendorName()+"_"+config.getEventName(),config);
    }

    public ApiConfig get(String vendorName,String updateType) {
        return configPool.get(vendorName+"_"+updateType);
    }

    public static ThreadPoolExecutor getExecutor(){
        return (ThreadPoolExecutor) Executors.newCachedThreadPool();
    }

    // --
    public static final String product_all_update = "product_all_update";
    public static final String product_delta_update = "product_delta_update";


}
