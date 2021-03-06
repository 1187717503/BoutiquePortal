package com.intramirror.web.controller.api.shopify;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.intramirror.common.help.ExceptionUtils;
import com.intramirror.common.help.StringUtils;
import com.intramirror.common.utils.DateUtils;
import com.intramirror.product.api.service.stock.IUpdateStockService;
import com.intramirror.web.mapping.impl.shopify.ShopifyProductMapping;
import com.intramirror.web.thread.CommonThreadPool;
import com.intramirror.web.thread.UpdateProductThread;
import com.intramirror.web.util.ApiDataFileUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import javax.annotation.Resource;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pk.shoplus.model.ProductEDSManagement;

/**
 * Created by dingyifan on 2017/12/26.
 */
@Controller
@RequestMapping("/shopify_product")
public class ShopifyProductController  implements InitializingBean {

    private static final Logger logger = Logger.getLogger(ShopifyProductController.class);

    private Map<String,ThreadPoolExecutor> executors = new HashMap<>();

    @Resource(name = "shopifyProductMapping")
    private ShopifyProductMapping shopifyProductMapping;

    @Resource(name = "updateStockService")
    private IUpdateStockService iUpdateStockService;

    public static ProductEDSManagement productEDSManagement = new ProductEDSManagement();

    // basic params
    private static final String product_all_update = "product_all_update";
    private static final String product_delta_update = "product_delta_update";
    private static final int limit = 250;
    private static final String url = "https://37a1a604bfe49d29fad33217c84b7df5:36065b388b4109c56ca3c9b9d4a8a9ad@amr-fashion.myshopify.com/admin/products.json";
    private static final int threadNum = 20;
    private static final Long vendor_id = 32L;

    @RequestMapping("/syn")
    @ResponseBody
    public String execute(@Param("name")String name) {
        ApiDataFileUtils fileUtils = new ApiDataFileUtils("shopiyf",name);
        int page = 1,index = 0;

        String call_url = url + "?limit="+limit;
        ThreadPoolExecutor executor = null;
        switch (name) {
            case product_all_update:
                executor = executors.get(product_all_update);
                break;
            case product_delta_update:
                executor = executors.get(product_delta_update);
                String time = DateUtils.getTimeByMinuteGMT(5);
                call_url=call_url+"&updated_at_min="+time;
                break;
            default:
                return "ERROR";
        }

        try {
            while (true) {
                String newURL = call_url +"&page="+page;

                String responseBody = this.sendGet(newURL);
//                Map<String,Object> map = HttpUtils.requestHttps(newURL,"GET");
//                String responseBody = map.get("resultMessage").toString();
//                String responseBody = HttpUtils.httpGetAmr(newURL);
                fileUtils.bakPendingFile("page"+page,responseBody);

                if(StringUtils.isBlank(responseBody) || responseBody.contains("{\"products\":[]}")) {
                    break;
                }

                JSONObject bodyObj = JSONObject.parseObject(responseBody);
                JSONArray products = bodyObj.getJSONArray("products");
                for(int i = 0,len=products.size();i<len;i++) {
                    JSONObject product = products.getJSONObject(i);
                    Map<String,Object> bodyMap = new HashMap<>();
                    bodyMap.put("product",product);

                    ProductEDSManagement.VendorOptions vendorOptions = productEDSManagement.getVendorOptions();
                    vendorOptions.setVendorId(vendor_id);
                    ProductEDSManagement.ProductOptions productOptions = shopifyProductMapping.mapping(bodyMap);
                    logger.info("shopify_product_update,mapping,bodyMap:"+JSONObject.toJSONString(bodyMap)+",productOptions:"+JSONObject.toJSONString(productOptions)+",page:"+page+",index:"+index);

                    CommonThreadPool.execute(name,executor,threadNum,new UpdateProductThread(productOptions,vendorOptions,fileUtils,bodyMap));
                    index++;
                }
                page ++;
            }

            if(name.equals(product_all_update) && index > 100 ) {
                logger.info("ShopifyProductController,zeroClearing,start,vendor_id:"+vendor_id);
                iUpdateStockService.zeroClearing(vendor_id);
                logger.info("ShopifyProductController,zeroClearing,end,vendor_id:"+vendor_id);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("shopify_product_update,errorMessage:"+ ExceptionUtils.getExceptionDetail(e));
        }
        logger.info("shopify_product_update,end,name:"+name+",page:"+page+",index:"+index+",call_url:"+call_url);
        return "SUCCESS";
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ThreadPoolExecutor all_executor =(ThreadPoolExecutor) Executors.newCachedThreadPool();
        executors.put(product_all_update,all_executor);

        ThreadPoolExecutor delta_executor =(ThreadPoolExecutor) Executors.newCachedThreadPool();
        executors.put(product_delta_update,delta_executor);
    }

    public  String sendGet(String url) throws Exception{
        String result = "";
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();

            HttpGet httpGet = new HttpGet(url);

            RequestConfig requestConfig = RequestConfig.custom()

                    .setConnectTimeout(600000).setConnectionRequestTimeout(600000)

                    .setSocketTimeout(600000).build();

            httpGet.setConfig(requestConfig);

            CloseableHttpResponse response = httpclient.execute(httpGet);

            System.out.println("得到的结果:" + response.getStatusLine());//得到请求结果

            HttpEntity entity = response.getEntity();//得到请求回来的数据
            result = EntityUtils.toString(entity);
            response.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return result;
    }
}
