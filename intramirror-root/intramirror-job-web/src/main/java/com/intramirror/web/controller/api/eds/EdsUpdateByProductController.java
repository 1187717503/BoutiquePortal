package com.intramirror.web.controller.api.eds;

import com.alibaba.fastjson15.JSONObject;
import com.google.gson.Gson;
import com.intramirror.common.parameter.StatusType;
import com.intramirror.common.utils.DateUtils;
import com.intramirror.web.contants.RedisKeyContants;
import com.intramirror.web.mapping.api.IProductMapping;
import com.intramirror.web.thread.CommonThreadPool;
import com.intramirror.web.thread.UpdateProductThread;
import com.intramirror.web.util.GetPostRequestUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pk.shoplus.model.EDSProduct;
import pk.shoplus.model.ProductEDSManagement;
import pk.shoplus.model.Result;
import pk.shoplus.service.RedisService;
import pk.shoplus.service.request.impl.GetPostRequestService;
import pk.shoplus.util.ExceptionUtils;
import pk.shoplus.util.MapUtils;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Controller
@RequestMapping("/eds_product")
public class EdsUpdateByProductController implements InitializingBean {

    // logger
    private static final Logger logger = Logger.getLogger(EdsUpdateByProductController.class);

    // getpost util
    private static GetPostRequestUtil getPostRequestUtil = new GetPostRequestUtil();

    // create product
    public static ProductEDSManagement productEDSManagement = new ProductEDSManagement();

    // redis
    private static RedisService redisService = RedisService.getInstance();

    // mapping
    @Resource(name = "edsUpdateByProductMapping")
    private IProductMapping iProductMapping;

    // init params
    private Map<String,Object> paramsMap;

    @RequestMapping("/syn_eds_product")
    @ResponseBody
    public Map<String,Object> execute(@Param(value = "name")String name) {
        MapUtils mapUtils = new MapUtils(new HashMap<String, Object>());
        logger.info("EdsProductAllProducerControllerExecute,startExecute,name:"+name);

        // check name
        if(StringUtils.isBlank(name)) {
            return mapUtils.putData("status",StatusType.FAILURE).putData("info","name is null !!!").getMap();
        }

        // get params
        Map<String,Object> param = (Map<String, Object>) paramsMap.get(name);
        String url = param.get("url").toString();
        String store_code = param.get("store_code").toString();
        String vendor_id = param.get("vendor_id").toString();
        int limit = Integer.parseInt(param.get("limit").toString());
        int offset = Integer.parseInt(param.get("offset").toString());
        int threadNum = Integer.parseInt(param.get("threadNum").toString());
        String eventName = param.get("eventName").toString();
        ThreadPoolExecutor nugnesExecutor = (ThreadPoolExecutor) param.get("nugnesExecutor");
        String datetime = param.get("datetime")==null?"":param.get("datetime").toString();
        String nugnes_product_offset = param.get(RedisKeyContants.nugnes_product_offset)==null?"":param.get(RedisKeyContants.nugnes_product_offset).toString();

        try {
            // start 获取增量更新offset
            if(StringUtils.isNotBlank(nugnes_product_offset)) {
                String redis_nugnes_product_offset = redisService.getKey(RedisKeyContants.nugnes_product_offset);
                logger.info("EdsProductAllProducerControllerExecute,redis_nugnes_product_offset:"+redis_nugnes_product_offset);
                if(StringUtils.isNotBlank(redis_nugnes_product_offset)){
                    offset = Integer.parseInt(redis_nugnes_product_offset);
                    logger.info("EdsProductAllProducerControllerExecute,offset,nugnes_product_offset:"+offset);
                }
            }
            // end 获取增量更新offset

            int sum = 0;
            while (true) {
                // 拼接URL
                String appendUrl = url + "?storeCode=" + store_code + "&limit=" + limit +"&offset=" + offset;
                if(StringUtils.isNotBlank(datetime)){appendUrl+="&datetime="+DateUtils.getStrDate(new Date());}

                // 获取数据
                logger.info("EdsProductAllProducerControllerExecute,startRequestMethod,appendUrl:"+appendUrl);
                String responseData = getPostRequestUtil.requestMethod(GetPostRequestService.HTTP_GET,appendUrl,null);
                if(StringUtils.isBlank(responseData)) {
                    logger.info("EdsProductAllProducerControllerExecute,whileEnd,responseData is null,appendUrl:"+appendUrl);
                    break;
                }
                logger.info("EdsProductAllProducerControllerExecute,endRequestMethod,appendUrl:"+appendUrl+",responseData:"+responseData);

                // 解析数据
                Map<String, Result> map = (Map<String, Result>) JSONObject.parse(responseData);
                Map<String, Object> mapResult = (Map<String, Object>) map.get("results");
                List<EDSProduct> edsProductList = (List<EDSProduct>) mapResult.get("items");
                if(edsProductList == null || edsProductList.size() == 0) {
                    logger.info("EdsProductAllProducerControllerExecute,whileEnd,edsProductList is null,appendUrl:"+appendUrl);
                    break;
                }

                for(int i = 0,len = edsProductList.size();i<len;i++){
                    sum++;
                    // 获取数据
                    Map<String,Object> mqDataMap = new HashMap<String,Object>();
                    mqDataMap.put("reqCode", mapResult.get("reqCode"));
                    mqDataMap.put("count", mapResult.get("count"));
                    mqDataMap.put("product", edsProductList.get(i));
                    mqDataMap.put("vendor_id", vendor_id);
                    mqDataMap.put("full_update_product","1");
                    logger.info("EdsProductAllProducerControllerExecute,mqDataMap:"+new Gson().toJson(mqDataMap)+",eventName:"+eventName);

                    // 映射数据 封装VO
                    ProductEDSManagement.ProductOptions productOptions = iProductMapping.mapping(mqDataMap);
                    ProductEDSManagement.VendorOptions vendorOptions = productEDSManagement.getVendorOptions();
                    vendorOptions.setVendorId(Long.parseLong(vendor_id));
                    logger.info("EdsProductAllProducerControllerExecute,initParam,productOptions:"+new Gson().toJson(productOptions)+",vendorOptions:"+new Gson().toJson(vendorOptions)+",eventName:"+eventName);

                    // 线程池
                    logger.info("EdsProductAllProducerControllerExecute,execute,startDate:"+DateUtils.getStrDate(new Date())+",productOptions:"+new Gson().toJson(productOptions)+",vendorOptions:"+new Gson().toJson(vendorOptions)+",eventName:"+eventName);
                    CommonThreadPool.execute(eventName,nugnesExecutor,threadNum,new UpdateProductThread(productOptions,vendorOptions));
                    logger.info("EdsProductAllProducerControllerExecute,execute,endDate:"+DateUtils.getStrDate(new Date())+",productOptions:"+new Gson().toJson(productOptions)+",vendorOptions:"+new Gson().toJson(vendorOptions)+",eventName:"+eventName);
                }

                offset = offset + edsProductList.size();
            }

            // start 记录增量更新offset
            if(StringUtils.isNotBlank(nugnes_product_offset)) {
                logger.info("EdsProductAllProducerControllerExecute,currentOffset:"+offset+",sum:"+sum);
                redisService.setKey(RedisKeyContants.nugnes_product_offset,offset+"");
                logger.info("EdsProductAllProducerControllerExecute,putAfterOffset:"+offset+",sum:"+sum);
            }
            // end 记录增量更新offset

            logger.info("EdsProductAllProducerControllerExecute,executeEnd,offset:"+offset+",limit:"+limit+",url:"+url+",store_code:"+store_code+",sum:"+sum+",param:"+new Gson().toJson(param)+",eventName:"+eventName);
            mapUtils.putData("status",StatusType.SUCCESS).putData("info","success");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("EdsProductAllProducerControllerExecute,errorMessage:"+ ExceptionUtils.getExceptionDetail(e));
            mapUtils.putData("status",StatusType.FAILURE).putData("info",ExceptionUtils.getExceptionDetail(e));
        }

        logger.info("EdsProductAllProducerControllerExecute,endExecute,mapUtils:"+new Gson().toJson(mapUtils));
        return mapUtils.getMap();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // nugnes_all_updateproduct
        ThreadPoolExecutor nugnes_all_updateproduct_executor =(ThreadPoolExecutor) Executors.newCachedThreadPool();
        Map<String,Object> nugnes_all_updateproduct = new HashMap<>();
        nugnes_all_updateproduct.put("url","http://nugnes.edstema.it/api/v3.0/products/condensed");
        nugnes_all_updateproduct.put("vendor_id","9");
        nugnes_all_updateproduct.put("store_code","X3ZMV");
        nugnes_all_updateproduct.put("limit","500");
        nugnes_all_updateproduct.put("offset","0");
        nugnes_all_updateproduct.put("threadNum","10");
        nugnes_all_updateproduct.put("nugnesExecutor",nugnes_all_updateproduct_executor);
        nugnes_all_updateproduct.put("eventName","nugnes全量更新商品");

        // nugnes_day_updateproduct
        ThreadPoolExecutor nugnes_day_updateproduct_executor =(ThreadPoolExecutor) Executors.newCachedThreadPool();
        Map<String,Object> nugnes_day_updateproduct = new HashMap<>();
        nugnes_day_updateproduct.put("url","http://nugnes.edstema.it/api/v3.0/products/date");
        nugnes_day_updateproduct.put("vendor_id","9");
        nugnes_day_updateproduct.put("store_code","X3ZMV");
        nugnes_day_updateproduct.put("limit","500");
        nugnes_day_updateproduct.put("offset","0");
        nugnes_day_updateproduct.put("threadNum","10");
        nugnes_day_updateproduct.put("nugnesExecutor",nugnes_day_updateproduct_executor);
        nugnes_day_updateproduct.put("eventName","nugnes更新当日商品");
        nugnes_day_updateproduct.put("datetime",DateUtils.getStrDate(new Date()));

        // nugnes_increment_updateproduct
        ThreadPoolExecutor nugnes_increment_updateproduct_executor =(ThreadPoolExecutor) Executors.newCachedThreadPool();
        Map<String,Object> nugnes_increment_updateproduct = new HashMap<>();
        nugnes_increment_updateproduct.put("url","http://nugnes.edstema.it/api/v3.0/products/condensed");
        nugnes_increment_updateproduct.put("vendor_id","9");
        nugnes_increment_updateproduct.put("store_code","X3ZMV");
        nugnes_increment_updateproduct.put("limit","500");
        nugnes_increment_updateproduct.put("offset","0");
        nugnes_increment_updateproduct.put("threadNum","10");
        nugnes_increment_updateproduct.put("nugnesExecutor",nugnes_increment_updateproduct_executor);
        nugnes_increment_updateproduct.put("eventName","nugnes增量更新商品");
        nugnes_increment_updateproduct.put(RedisKeyContants.nugnes_product_offset,"0");

        // put data
        paramsMap = new HashMap<>();
        paramsMap.put("nugnes_all_updateproduct",nugnes_all_updateproduct);
        paramsMap.put("nugnes_day_updateproduct",nugnes_day_updateproduct);
        paramsMap.put("nugnes_increment_updateproduct",nugnes_increment_updateproduct);
    }
}
