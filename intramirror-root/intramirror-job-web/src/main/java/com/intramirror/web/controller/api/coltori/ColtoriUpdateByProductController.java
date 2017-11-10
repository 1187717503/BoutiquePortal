package com.intramirror.web.controller.api.coltori;

import com.google.gson.Gson;
import com.intramirror.common.help.ExceptionUtils;
import com.intramirror.common.help.StringUtils;
import com.intramirror.common.utils.DateUtils;
import com.intramirror.web.contants.RedisKeyContants;
import com.intramirror.web.mapping.api.IProductMapping;
import com.intramirror.web.thread.CommonThreadPool;
import com.intramirror.web.thread.UpdateProductThread;
import com.intramirror.web.util.ApiDataFileUtils;
import com.intramirror.web.util.GetPostRequestUtil;
import com.intramirror.web.util.HttpUtils;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import javax.annotation.Resource;
import net.sf.json.JSONObject;
import org.apache.ibatis.annotations.Param;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pk.shoplus.model.ProductEDSManagement;
import pk.shoplus.service.RedisService;

/**
 * Created by dingyifan on 2017/11/1.
 */
@Controller
@RequestMapping("/coltori_product")
public class ColtoriUpdateByProductController implements InitializingBean {

    private static final Logger logger = Logger.getLogger(ColtoriUpdateByProductController.class);

    private static GetPostRequestUtil getPostRequestUtil = new GetPostRequestUtil();

    private Map<String,Object> paramsMap;

    private static RedisService redisService = RedisService.getInstance();

    @Resource(name = "coltoriProductMapping")
    private IProductMapping iProductMapping;

    @RequestMapping("/syn_product")
    @ResponseBody
    public Map<String, Object> execute(@Param(value = "name") String name) {
        try {
            if(StringUtils.isBlank(name) || paramsMap.get(name) == null) {
                return null;
            }

            Map<String,Object> paramMap = (Map<String, Object>) paramsMap.get(name);
            logger.info("ColtoriUpdateByProductController,Execute,inputParams,name:"+name+",paramsMap:"+ JSONObject.fromObject(paramMap));
            ThreadPoolExecutor executor = (ThreadPoolExecutor) paramMap.get("executor");
            ApiDataFileUtils apiDataFileUtils = (ApiDataFileUtils) paramMap.get("fileUtils");
            String get_token_url = paramMap.get("get_token_url").toString();
            String get_product_url = paramMap.get("get_product_url").toString();
            String username = paramMap.get("user").toString();
            String password = paramMap.get("password").toString();
            Long vendor_id = Long.parseLong(paramMap.get("vendor_id").toString());
            String eventName = paramMap.get("eventName").toString();

            String ct_token_url = redisService.getKey(RedisKeyContants.ct_token_url);
            if(name.contains("all")) {
                // get token
                logger.info("ColtoriUpdateByProductController,postAuth,start,get_token_url:"+get_token_url+",username:"+username+",password:"+password);
                String getTokenResponse = getPostRequestUtil.postAuth(get_token_url,username,password);
                logger.info("ColtoriUpdateByProductController,postAuth,end,get_token_url:"+get_token_url+",username:"+username+",password:"+password+",getTokenResponse:"+getTokenResponse);

                JSONObject toKenObject = JSONObject.fromObject(getTokenResponse);
                String access_token = toKenObject.getString("access_token");
                String expires_in = toKenObject.getString("expires_in");
                ct_token_url = "?access_token="+access_token+"&expires_in="+expires_in ;
                redisService.setKey(RedisKeyContants.ct_token_url,ct_token_url);
            }

            get_product_url = get_product_url+ct_token_url;
            if(!name.contains("all")) {
                String since_updated_at = URLEncoder.encode(DateUtils.getTimeByMinute(5));
                String until_updated_at = URLEncoder.encode(DateUtils.getTimeByMinute(0));
                get_product_url = get_product_url+"&since_updated_at="+ since_updated_at+"&until_updated_at="+until_updated_at;
            }

            logger.info("ColtoriUpdateByProductController,requestMethod,start,get_product_url:"+get_product_url);
            String responseData = HttpUtils.httpGet(get_product_url);
            logger.info("ColtoriUpdateByProductController,requestMethod,end,get_product_url:"+get_product_url+",responseData:"+responseData);
            apiDataFileUtils.bakPendingFile("responseData",responseData);

            if(StringUtils.isNotBlank(responseData) && responseData.contains("no results matching your query")) {
                return null;
            }

            ProductEDSManagement.VendorOptions vendorOptions = new ProductEDSManagement.VendorOptions();
            vendorOptions.setVendorId(vendor_id);
            JSONObject products = JSONObject.fromObject(responseData);
            Iterator<String> iterator = products.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                JSONObject product = products.getJSONObject(key);
                Map<String,Object> mqDataMap = new HashMap<>();
                mqDataMap.put("key",key);
                mqDataMap.put("product",product);
                mqDataMap.put("vendor_id",vendor_id);

                logger.info("ColtoriUpdateByProductController,start,mapping,mqDataMap:"+JSONObject.fromObject(mqDataMap));
                ProductEDSManagement.ProductOptions productOptions = iProductMapping.mapping(mqDataMap);
                logger.info("ColtoriUpdateByProductController,end,mapping,mqDataMap:"+JSONObject.fromObject(mqDataMap)+",productOptions:"+JSONObject.fromObject(productOptions));

                logger.info("ColtoriUpdateByProductController,execute,startDate:"+DateUtils.getStrDate(new Date())+",productOptions:"+new Gson().toJson(productOptions)+",vendorOptions:"+new Gson().toJson(vendorOptions)+",eventName:"+eventName);
                CommonThreadPool.execute(eventName,executor,5,new UpdateProductThread(productOptions,vendorOptions,apiDataFileUtils,mqDataMap));
                logger.info("ColtoriUpdateByProductController,execute,endDate:"+DateUtils.getStrDate(new Date())+",productOptions:"+new Gson().toJson(productOptions)+",vendorOptions:"+new Gson().toJson(vendorOptions)+",eventName:"+eventName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("ColtoriUpdateByProductController,Execute,errorMessage:"+ ExceptionUtils.getExceptionDetail(e));
        }
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ThreadPoolExecutor product_all_update_executor =(ThreadPoolExecutor) Executors.newCachedThreadPool();
        Map<String,Object> product_all_update = new HashMap<>();
        product_all_update.put("get_token_url","https://api.orderlink.it/v2/user/token");
        product_all_update.put("get_product_url","https://api.orderlink.it/v2/products");
        product_all_update.put("user","TEST");
        product_all_update.put("password","1k0nic");
        product_all_update.put("vendor_id", "24");
        product_all_update.put("eventName","product_all_update");
        product_all_update.put("fileUtils",new ApiDataFileUtils("coltori","product_all_update"));
        product_all_update.put("executor",product_all_update_executor);

        ThreadPoolExecutor product_delta_update_executor =(ThreadPoolExecutor) Executors.newCachedThreadPool();
        Map<String,Object> product_delta_update = new HashMap<>();
        product_delta_update.put("get_token_url","https://api.orderlink.it/v2/user/token");
        product_delta_update.put("get_product_url","https://api.orderlink.it/v2/products");
        product_delta_update.put("user","TEST");
        product_delta_update.put("password","1k0nic");
        product_delta_update.put("vendor_id", "24");
        product_delta_update.put("eventName","product_delta_update");
        product_delta_update.put("fileUtils",new ApiDataFileUtils("coltori","product_delta_update"));
        product_delta_update.put("executor",product_delta_update_executor);

        paramsMap = new HashMap<>();
        paramsMap.put("product_all_update",product_all_update);
        paramsMap.put("product_delta_update",product_delta_update);

    }
}