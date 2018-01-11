package com.intramirror.web.controller.api.Magento;

import com.alibaba.fastjson15.JSONArray;
import com.alibaba.fastjson15.JSONObject;
import com.intramirror.common.help.StringUtils;
import com.intramirror.web.controller.api.ApiContants;
import com.intramirror.web.mapping.impl.Matgento.MatgentoSynProductMapping;
import com.intramirror.web.thread.CommonThreadPool;
import com.intramirror.web.thread.UpdateProductThread;
import com.intramirror.web.util.ApiDataFileUtils;
import com.intramirror.web.util.HttpUtils;
import difflib.DiffRow;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import javax.annotation.Resource;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import pk.shoplus.model.ProductEDSManagement;
import pk.shoplus.util.FileUtil;

@Controller
@RequestMapping("/matgento")
public class MatgentoController implements InitializingBean {

    private static Logger logger = Logger.getLogger(MatgentoController.class);

    @Resource(name = "matgentoSynProductMapping")
    private MatgentoSynProductMapping matgentoSynProductMapping;

    public static ProductEDSManagement productEDSManagement = new ProductEDSManagement();

    private Map<String,Object> paramsMap;

    private static final int limit = 100;

    private static final String originlPath = "";
    private static final String revisedPath = "";

    private static final String originName = "origin.txt";
    private static final String revisedName = "revised.txt";

    @GetMapping("/{type}")
    public String execute(@PathVariable("type") String type){
        long start = System.currentTimeMillis();
        try {
            Map<String,Object> param = (Map<String, Object>) paramsMap.get(type);
            String url = param.get("url").toString();
            String vendor_id = param.get("vendor_id").toString();
            int threadNum = Integer.parseInt(param.get("threadNum").toString());
            String eventName = param.get("eventName").toString();
            ThreadPoolExecutor executor = (ThreadPoolExecutor) param.get("executor");
            ApiDataFileUtils fileUtils = (ApiDataFileUtils) param.get("fileUtils");

            if(type.equals(ApiContants.product_all_update)) {
                int page = 1;
                while (true) {
                    int limit1 = (page * limit) - limit;
                    int limit2 = page * limit;

                    String appendUrl = url + "&limit=" + limit1 + "," + limit2;
                    logger.info("MatgentoController,appendUrl:" + appendUrl);

                    String response = HttpUtils.httpGet(appendUrl);
                    if (StringUtils.isBlank(response) || response.equals("[\"NO DATA\"]")) {
                        break;
                    }

                    JSONArray products = JSONArray.parseArray(response);

                    for (int i = 0, len = products.size(); i < len; i++) {
                        JSONObject product = products.getJSONObject(i);
                        Map<String, Object> mqDataMap = new HashMap<>();
                        mqDataMap.put("Data", product);

                        ProductEDSManagement.ProductOptions productOptions = matgentoSynProductMapping.mapping(mqDataMap);
                        ProductEDSManagement.VendorOptions vendorOptions = productEDSManagement.getVendorOptions();
                        vendorOptions.setVendorId(Long.parseLong(vendor_id));

                        logger.info("MatgentoController,mapping,product_all_update,mqDataMap:"+JSONObject.toJSONString(mqDataMap)+",productOptions:"+JSONObject.toJSONString(productOptions)+",page:"+page);
                        CommonThreadPool.execute(eventName,executor,threadNum,new UpdateProductThread(productOptions,vendorOptions,fileUtils,mqDataMap));
                    }
                    page++;
                }
            } else if(type.equals(ApiContants.product_delta_update)) {
                String fullRevisedPath = revisedPath+revisedName;
                String fullOriginPath = originlPath+originName;

                String response = HttpUtils.httpGet(url);
                List<Map<String, Object>> productList = (List<Map<String, Object>>) com.alibaba.fastjson.JSONObject.parse(response);
                String productStrs = converString(productList);
                FileUtil.createFileByType(revisedPath,revisedName,productStrs);
                if(!FileUtil.fileExists(fullOriginPath)) {
                    FileUtil.createFileByType(originlPath,originName,productStrs);
                }

                List<DiffRow> diffRows = FileUtil.CompareTxtByType(fullRevisedPath,fullOriginPath);
                for(DiffRow diffRow : diffRows) {
                    DiffRow.Tag tag = diffRow.getTag();
                    if(tag == DiffRow.Tag.INSERT || tag == DiffRow.Tag.CHANGE) {
                        logger.info("MatgentoController,insertandchange,diffRow:"+ com.alibaba.fastjson.JSONObject.toJSONString(diffRow));
                        String stockMapStr = diffRow.getNewLine().replace("<br>","");

                    } else if(tag == DiffRow.Tag.DELETE){
                        logger.info("MatgentoController,delete,diffRow:"+ com.alibaba.fastjson.JSONObject.toJSONString(diffRow));
                        String stockMapStr = diffRow.getOldLine().replace("<br>","");

                    }
                }
                FileUtil.createFileByType(originlPath,originName,converString(productList));
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("MatgentoController,ErrorMessage:"+e);
        }
        long end = System.currentTimeMillis();
        logger.info("Vendor_Run_Time,matgento,type:"+type+",time:"+(end-start));
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ThreadPoolExecutor matgento_all_update =(ThreadPoolExecutor) Executors.newCachedThreadPool();
        Map<String,Object> product_all_update = new HashMap<>();
        product_all_update.put("url","http://srv1.best-fashion.net/export/apiv2/?t=507167b1d174406d152a4bd24344389c&type=allstock");
        product_all_update.put("vendor_id","100");
        product_all_update.put("threadNum","15");
        product_all_update.put("eventName","product_all_update");
        product_all_update.put("executor",matgento_all_update);
        product_all_update.put("fileUtils",new ApiDataFileUtils("matgento","product_all_update"));

        ThreadPoolExecutor matgento_delta_update =(ThreadPoolExecutor) Executors.newCachedThreadPool();
        Map<String,Object> product_delta_update = new HashMap<>();
        product_delta_update.put("url","http://srv1.best-fashion.net/export/apiv2/?t=507167b1d174406d152a4bd24344389c&type=allstock");
        product_delta_update.put("vendor_id","100");
        product_delta_update.put("threadNum","15");
        product_delta_update.put("eventName","product_delta_update");
        product_delta_update.put("executor",matgento_delta_update);
        product_delta_update.put("fileUtils",new ApiDataFileUtils("matgento","product_delta_update"));

        paramsMap.put("product_all_update",product_all_update);
        paramsMap.put("product_delta_update",product_delta_update);
    }

    public String converString(List<Map<String, Object>> list){
        StringBuffer stringBuffer = new StringBuffer();

        if(list != null && list.size() > 0 ){
            for(Map<String, Object> info : list){
                stringBuffer.append(info.toString()).append("\n");
            }
        }
        return stringBuffer.toString();
    }
}
