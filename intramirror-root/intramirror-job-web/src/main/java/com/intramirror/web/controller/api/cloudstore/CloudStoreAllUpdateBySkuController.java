package com.intramirror.web.controller.api.cloudstore;

import com.alibaba.fastjson15.JSONArray;
import com.alibaba.fastjson15.JSONObject;
import com.intramirror.product.api.service.stock.IUpdateStockService;
import com.intramirror.web.mapping.api.IProductMapping;
import com.intramirror.web.thread.CommonThreadPool;
import com.intramirror.web.thread.UpdateProductThread;
import com.intramirror.web.util.ApiDataFileUtils;
import com.intramirror.web.util.GetPostRequestUtil;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import javax.annotation.Resource;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pk.shoplus.model.ProductEDSManagement;
import pk.shoplus.parameter.StatusType;
import pk.shoplus.service.request.impl.GetPostRequestService;
import pk.shoplus.util.ExceptionUtils;
import pk.shoplus.util.MapUtils;

@Controller
@RequestMapping("/cloudstore_sku")
public class CloudStoreAllUpdateBySkuController implements InitializingBean {

    private final Logger logger = Logger.getLogger(CloudStoreAllUpdateBySkuController.class);

    // init params
    private Map<String, Object> paramsMap;

    // getpost util
    private static GetPostRequestUtil getPostRequestUtil = new GetPostRequestUtil();

    // create product
    public static ProductEDSManagement productEDSManagement = new ProductEDSManagement();

    // mapping
    @Resource(name = "cloudStoreProductMapping")
    private IProductMapping iProductMapping;

    @Resource(name = "updateStockService")
    private IUpdateStockService iUpdateStockService;

    Integer index = 0;
    Integer step = 0;

    @RequestMapping("/syn_sku")
    @ResponseBody
    public Map<String, Object> execute(@Param(value = "name") String name) {
        logger.info("------------------------------------------start SynSkuProducerController.populateResult");
        index = 0;
        step = 0;
        MapUtils mapUtils = new MapUtils(new HashMap<String, Object>());
        try {

            // check name
            if (StringUtils.isBlank(name)) {
                return mapUtils.putData("status", StatusType.FAILURE).putData("info", "name is null !!!").getMap();
            }

            // get params
            Map<String, Object> param = (Map<String, Object>) paramsMap.get(name);
            String url = param.get("url").toString();
            String merchantId = param.get("merchantId").toString();
            String token = param.get("token").toString();
            String vendor_id = param.get("vendor_id").toString();
            String eventName = param.get("eventName").toString();
            int threadNum = Integer.parseInt(param.get("threadNum").toString());
            ThreadPoolExecutor executor = (ThreadPoolExecutor) param.get("executor");
            ApiDataFileUtils fileUtils = (ApiDataFileUtils) param.get("fileUtils");

            Map<String, Object> params = new HashMap<>();
            params.put("token", token);
            params.put("merchantId", merchantId);
            int flag = 0;
            while (true) {
                // 获取数据
                logger.info("cloudStoreProductAllProducerControllerExecute,startRequestMethod,appendUrl:" + url + ",index:" + index);
                String responseData = getPostRequestUtil.requestMethod(GetPostRequestService.HTTP_POST, url, JSONObject.toJSONString(params));
                if (StringUtils.isBlank(responseData)) {
                    logger.info("cloudStoreProductAllProducerControllerExecute,whileEnd,responseData is null,appendUrl:" + url);
                    break;
                }
                logger.info("cloudStoreProductAllProducerControllerExecute,endRequestMethod,appendUrl:" + url + ",responseData:" + responseData);
                // 解析数据
                JSONObject jsonObject = JSONObject.parseObject(responseData);
                JSONObject data = jsonObject.getJSONObject("data");
                JSONArray inventorys = data.getJSONArray("inventory");
                Iterator<Object> it = inventorys.iterator();

                if (inventorys == null || inventorys.size() == 0) {
                    logger.info("cloudStoreProductAllProducerControllerExecute,whileEnd,ProductList is null,appendUrl:" + url + ",index:" + index);
                    break;
                }
                if (StringUtils.isBlank(responseData)) {
                    logger.info("cloudStoreProductAllProducerControllerExecute,whileEnd,edsProductList is null,appendUrl:" + url);
                    break;
                } else {
                    fileUtils.bakPendingFile("step" + step, responseData);
                }
                while (it.hasNext()) {
                    index++;
                    flag++;
                    JSONObject inventory = (JSONObject) it.next();
                    Map<String, Object> mqMap = new HashMap<>();
                    mqMap.put("vendor_id", vendor_id);
                    mqMap.put("responseBody", inventory);
                    mqMap.put("full_update_product", "1");
                    // 映射数据 封装VO
                    ProductEDSManagement.ProductOptions productOptions = iProductMapping.mapping(mqMap);
                    ProductEDSManagement.VendorOptions vendorOptions = productEDSManagement.getVendorOptions();
                    vendorOptions.setVendorId(Long.parseLong(vendor_id));
                    logger.info("cloudStoreProductAllProducerControllerExecute,initParam,productOptions:" + JSONObject.toJSONString(productOptions)
                            + ",vendorOptions:" + JSONObject.toJSONString(vendorOptions) + ",eventName:" + eventName);

                    // 线程池
                    logger.info("cloudStoreProductAllProducerControllerExecute,execute,start,mqMap:" + JSONObject.toJSONString(mqMap) + ",productOptions:"
                            + JSONObject.toJSONString(productOptions) + ",vendorOptions:" + JSONObject.toJSONString(vendorOptions) + ",eventName:" + eventName
                            + ",index:" + index);
                    CommonThreadPool.execute(eventName, executor, threadNum, new UpdateProductThread(productOptions, vendorOptions, fileUtils, mqMap));
                    logger.info("cloudStoreProductAllProducerControllerExecute,execute,end,mqMap:" + JSONObject.toJSONString(mqMap) + ",productOptions:"
                            + JSONObject.toJSONString(productOptions) + ",vendorOptions:" + JSONObject.toJSONString(vendorOptions) + ",eventName:" + eventName
                            + ",index:" + index);
                }

                if (jsonObject.get("next_step") != null) {
                    params.put("step", jsonObject.get("next_step"));
                    step++;
                } else {
                    break;
                }
                logger.info("cloudStoreProductAllProducerControllerExecute,stepEnd,step:" + step + ",index:" + index);
            }

            logger.info("cloudStoreProductAllProducerControllerExecute,zeroClearing,flag:" + flag);
            if (eventName.equals("product_all_update") && flag > 100) {
                logger.info("cloudStoreProductAllProducerControllerExecute,zeroClearing,start,vendor_id:" + vendor_id + ",flag:" + flag);
                iUpdateStockService.zeroClearing(Long.parseLong(vendor_id));
                logger.info("cloudStoreProductAllProducerControllerExecute,zeroClearing,end,vendor_id:" + vendor_id + ",flag:" + flag);
            }
            logger.info("cloudStoreProductAllProducerControllerExecute,executeEnd,url:" + url + ",index:" + index + ",param:" + JSONObject.toJSONString(param)
                    + ",eventName:" + eventName);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("cloudStoreSynSkuProducerControllerExceptions : " + ExceptionUtils.getExceptionDetail(e));
            mapUtils.putData("status", StatusType.FAILURE).putData("info", "error message : " + e.getMessage());
        }
        logger.info("------------------------------------------end SynSkuProducerController.populateResult mapUtils : " + JSONObject.toJSONString(mapUtils));
        return mapUtils.getMap();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        Map<String, Object> tony_all_updateProduct = new HashMap<>();
        tony_all_updateProduct.put("url", "http://www.cloudstore.srl/ws/getInventory");
        tony_all_updateProduct.put("limit", "5");
        tony_all_updateProduct.put("offset", "0");
        tony_all_updateProduct.put("merchantId", "55f707f6b49dbbe14ec6354d");
        tony_all_updateProduct.put("token", "e00eb67a3799a9688072d72d76887395");
        tony_all_updateProduct.put("executor", executor);
        tony_all_updateProduct.put("vendor_id", "16");
        tony_all_updateProduct.put("threadNum", "5");
        tony_all_updateProduct.put("eventName", "product_all_update");
        tony_all_updateProduct.put("fileUtils", new ApiDataFileUtils("tony", "product_all_update"));

        Map<String, Object> tony2_all_updateProduct = new HashMap<>();
        tony2_all_updateProduct.put("url", "http://www.cloudstore.srl/ws/getInventory");
        tony2_all_updateProduct.put("limit", "5");
        tony2_all_updateProduct.put("offset", "0");
        tony2_all_updateProduct.put("merchantId", "55f707f6b49dbbe14ec6354d");
        tony2_all_updateProduct.put("token", "7a8f88efd4d0e244cb1c196f1d916cb4");
        tony2_all_updateProduct.put("executor", executor);
        tony2_all_updateProduct.put("vendor_id", "43");
        tony2_all_updateProduct.put("threadNum", "5");
        tony2_all_updateProduct.put("eventName", "product_all_update");
        tony2_all_updateProduct.put("fileUtils", new ApiDataFileUtils("tony2", "product_all_update"));

        // put data
        paramsMap = new HashMap<>();
        paramsMap.put("tony_all_updateProduct", tony_all_updateProduct);
        paramsMap.put("tony2_all_updateProduct", tony2_all_updateProduct);

    }

}
