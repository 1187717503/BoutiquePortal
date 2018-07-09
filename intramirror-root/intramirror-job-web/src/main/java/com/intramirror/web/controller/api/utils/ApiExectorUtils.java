package com.intramirror.web.controller.api.utils;

import com.alibaba.fastjson.JSONObject;
import com.intramirror.web.controller.api.utils.vo.ApiConfig;
import com.intramirror.web.mapping.api.IProductMapping;
import com.intramirror.web.thread.CommonThreadPool;
import com.intramirror.web.thread.UpdateProductThread;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import pk.shoplus.model.ProductEDSManagement;

/**
 * Created by dingyifan on 2018/1/12.
 */
@Service
public class ApiExectorUtils {

    private static Logger logger = Logger.getLogger(ApiExectorUtils.class);

    public static ProductEDSManagement productEDSManagement = new ProductEDSManagement();

    public static void putThreadPool(ApiConfig apiConfig,Map<String,Object> bodyMap,IProductMapping iProductMapping) throws Exception{
        logger.info("API_JOB_LOG,接口原始数据:"
        +",\nvendor_name:"+apiConfig.getVendorName()
        +",\nmessage_type:"+apiConfig.getEventName()
        +",\nbody:"+ JSONObject.toJSONString(bodyMap)
        +",\nindex:"+apiConfig.getIndex());

        ProductEDSManagement.ProductOptions productOptions = iProductMapping.mapping(bodyMap);
        ProductEDSManagement.VendorOptions vendorOptions = productEDSManagement.getVendorOptions();
        vendorOptions.setVendorId(apiConfig.getVendor_id());

        logger.info("API_JOB_LOG,接口转换后的数据:"
                +",\nvendor_name:"+apiConfig.getVendorName()
                +",\nmessage_type:"+apiConfig.getEventName()
                +",\nbody:"+ JSONObject.toJSONString(bodyMap)
                +",\nproductOptions:"+JSONObject.toJSONString(productOptions));
        CommonThreadPool.execute(apiConfig.getEventName(),apiConfig.getExecutor(),apiConfig.getThreadNum(),
                new UpdateProductThread(productOptions,vendorOptions,apiConfig.getFileUtils(),bodyMap));
    }
}
