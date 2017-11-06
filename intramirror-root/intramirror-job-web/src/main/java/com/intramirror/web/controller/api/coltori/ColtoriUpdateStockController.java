package com.intramirror.web.controller.api.coltori;

import com.google.gson.Gson;
import com.intramirror.common.help.ExceptionUtils;
import com.intramirror.common.help.StringUtils;
import com.intramirror.common.utils.DateUtils;
import com.intramirror.web.mapping.api.IStockMapping;
import com.intramirror.web.mapping.vo.StockOption;
import com.intramirror.web.thread.CommonThreadPool;
import com.intramirror.web.thread.UpdateStockThread;
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
import pk.shoplus.service.RedisService;

/**
 * Created by dingyifan on 2017/11/2.
 */
@Controller
@RequestMapping("/coltori_stock")
public class ColtoriUpdateStockController  implements InitializingBean {

    private static final Logger logger = Logger.getLogger(ColtoriUpdateStockController.class);

    private static GetPostRequestUtil getPostRequestUtil = new GetPostRequestUtil();

    private Map<String,Object> paramsMap;

    private static RedisService redisService = RedisService.getInstance();

    @Resource(name = "coltoriStockMapping")
    private IStockMapping iStockMapping;

    @RequestMapping("/syn_stock")
    @ResponseBody
    public Map<String, Object> execute(@Param(value = "name") String name) {
        try {
            if(StringUtils.isBlank(name) || paramsMap.get(name) == null) {
                return null;
            }

            Map<String,Object> paramMap = (Map<String, Object>) paramsMap.get(name);
            logger.info("ColtoriUpdateStockController,Execute,inputParams,name:"+name+",paramsMap:"+ JSONObject.fromObject(paramMap));
            ThreadPoolExecutor executor = (ThreadPoolExecutor) paramMap.get("executor");
            ApiDataFileUtils apiDataFileUtils = (ApiDataFileUtils) paramMap.get("fileUtils");
            String get_token_url = paramMap.get("get_token_url").toString();
            String get_stock_url = paramMap.get("get_stock_url").toString();
            String username = paramMap.get("user").toString();
            String password = paramMap.get("password").toString();
            Long vendor_id = Long.parseLong(paramMap.get("vendor_id").toString());
            String eventName = paramMap.get("eventName").toString();

            // get token
            logger.info("ColtoriUpdateStockController,postAuth,start,get_token_url:"+get_token_url+",username:"+username+",password:"+password);
            String getTokenResponse = getPostRequestUtil.postAuth(get_token_url,username,password);
            logger.info("ColtoriUpdateStockController,postAuth,end,get_token_url:"+get_token_url+",username:"+username+",password:"+password+",getTokenResponse:"+getTokenResponse);

            JSONObject toKenObject = JSONObject.fromObject(getTokenResponse);
            String access_token = toKenObject.getString("access_token");
            String expires_in = toKenObject.getString("expires_in");

            get_stock_url = get_stock_url+"?access_token="+access_token+"&expires_in="+expires_in;

            if(!name.contains("all")) {
                String since_updated_at = URLEncoder.encode(DateUtils.getTimeByMinute(5));
                String until_updated_at = URLEncoder.encode(DateUtils.getTimeByMinute(0));
                get_stock_url = get_stock_url+"&since_updated_at="+ since_updated_at+"&until_updated_at="+until_updated_at;
            }

            logger.info("ColtoriUpdateStockController,requestMethod,start,get_stock_url:"+get_stock_url);
            String responseData = HttpUtils.httpGet(get_stock_url);
            logger.info("ColtoriUpdateStockController,requestMethod,end,get_stock_url:"+get_stock_url+",responseData:"+responseData);

            if(StringUtils.isNotBlank(responseData) && responseData.contains("no results matching your query")) {
                return null;
            }

            JSONObject productOpj = JSONObject.fromObject(responseData);
            Iterator<String> product = productOpj.keys();
            while (product.hasNext()) {
                String product_code = product.next();
                JSONObject skusObj = productOpj.getJSONObject(product_code);
                logger.info("ColtoriUpdateStockController,while,skusObj:"+skusObj.toString()+",product_code:"+product_code);
                JSONObject sizesObj = skusObj.getJSONObject("sizes");
                Iterator<String> sizes = sizesObj.keys();
                while(sizes.hasNext()) {
                    String key = sizes.next();
                    JSONObject skuObj = sizesObj.getJSONObject(key);
                    Iterator<String> sku = skuObj.keys();
                    while(sku.hasNext()){
                        String size = sku.next();
                        String stock = skuObj.getString(size);

                        Map<String,Object> stockMap = new HashMap<>();
                        stockMap.put("vendor_id",vendor_id);
                        stockMap.put("product_code",product_code);
                        stockMap.put("size",size);
                        stockMap.put("stock",stock);
                        logger.info("ColtoriUpdateStockController,start,mapping,stockMap:"+JSONObject.fromObject(stockMap));
                        StockOption stockOption = iStockMapping.mapping(stockMap);
                        logger.info("ColtoriUpdateStockController,end,mapping,stockMap:"+JSONObject.fromObject(stockMap)+",stockOption:"+JSONObject.fromObject(stockOption));
                        // 线程池
                        logger.info("ColtoriUpdateStockController,execute,startDate:"+ DateUtils.getStrDate(new Date())+",stockMap:"+new Gson().toJson(stockMap)+",stockOption:"+new Gson().toJson(stockOption)+",eventName:"+eventName);
                        CommonThreadPool.execute(eventName,executor,5,new UpdateStockThread(stockOption,apiDataFileUtils,stockMap));
                        logger.info("ColtoriUpdateStockController,execute,endDate:"+ DateUtils.getStrDate(new Date())+",stockMap:"+new Gson().toJson(stockMap)+",stockOption:"+new Gson().toJson(stockOption)+",eventName:"+eventName);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("ColtoriUpdateByProductController,Execute,errorMessage:"+ ExceptionUtils.getExceptionDetail(e));
        }
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ThreadPoolExecutor stock_all_update_executor =(ThreadPoolExecutor) Executors.newCachedThreadPool();
        Map<String,Object> stock_all_update = new HashMap<>();
        stock_all_update.put("get_token_url","https://api.orderlink.it/v2/user/token");
        stock_all_update.put("get_stock_url","https://api.orderlink.it/v2/stocks");
        stock_all_update.put("user","TEST");
        stock_all_update.put("password","1k0nic");
        stock_all_update.put("vendor_id", "24");
        stock_all_update.put("eventName","stock_all_update");
        stock_all_update.put("fileUtils",new ApiDataFileUtils("coltori","stock_all_update"));
        stock_all_update.put("executor",stock_all_update_executor);

        ThreadPoolExecutor stock_delta_update_executor =(ThreadPoolExecutor) Executors.newCachedThreadPool();
        Map<String,Object> stock_delta_update = new HashMap<>();
        stock_delta_update.put("get_token_url","https://api.orderlink.it/v2/user/token");
        stock_delta_update.put("get_stock_url","https://api.orderlink.it/v2/stocks");
        stock_delta_update.put("user","TEST");
        stock_delta_update.put("password","1k0nic");
        stock_delta_update.put("vendor_id", "24");
        stock_delta_update.put("eventName","stock_delta_update");
        stock_delta_update.put("fileUtils",new ApiDataFileUtils("coltori","stock_delta_update"));
        stock_delta_update.put("executor",stock_delta_update_executor);

        paramsMap = new HashMap<>();
        paramsMap.put("stock_all_update",stock_all_update);
        paramsMap.put("stock_delta_update",stock_delta_update);

    }
}
