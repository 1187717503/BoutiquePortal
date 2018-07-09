package com.intramirror.web.controller.api.Magento;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.intramirror.product.api.service.stock.IUpdateStockService;
import com.intramirror.web.controller.api.utils.ApiExectorUtils;
import com.intramirror.web.controller.api.utils.ApiHttpTools;
import com.intramirror.web.controller.api.utils.vo.ApiConfig;
import com.intramirror.web.controller.api.utils.vo.ApiConfigPool;
import com.intramirror.web.mapping.impl.Matgento.MatgentoSynProductMapping;
import difflib.DiffRow;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import pk.shoplus.service.RedisService;
import pk.shoplus.util.FileUtil;

@Controller
@RequestMapping("/syn")
public class MatgentoController implements InitializingBean {

    private static Logger logger = Logger.getLogger(MatgentoController.class);

    private ApiConfigPool apiConfigPool;

    @Resource(name = "matgentoSynProductMapping")
    private MatgentoSynProductMapping matgentoSynProductMapping;

    @Resource(name = "updateStockService")
    private IUpdateStockService iUpdateStockService;

    private static RedisService redisService = RedisService.getInstance();

    @GetMapping("/{vendorName}/{updateType}")
    public String execute(@PathVariable("vendorName") String name,@PathVariable("updateType") String type){
        long start = System.currentTimeMillis();
        ApiConfig config = apiConfigPool.get(name,type);
        try {

            // product_all_update
            if(config.getEventName().equals(ApiConfigPool.product_all_update)) {

                int index = 0;
                this.page = 1;
                while (true) {
                    String url = this.generatorURL(config.getUrl(),config.getLimit());
                    String response = ApiHttpTools.httpGet(url);
                    logger.info("MatgentoController,httpGet,url:"+url);
                    config.getFileUtils().bakPendingFile(config.getEventName(),response);
                    if (StringUtils.isBlank(response) || response.equals("[\"NO DATA\"]") || response.equals("[\"NO DATA\"]\n")) {
                        break;
                    }

                    JSONArray products = JSONArray.parseArray(response);
                    for (int i = 0, len = products.size(); i < len; i++) {
                        Map<String, Object> mqDataMap = new HashMap<>();
                        mqDataMap.put("Data", products.get(i));
                        ApiExectorUtils.putThreadPool(config.setIndex(index),mqDataMap,matgentoSynProductMapping);
                        index++;
                    }
                }

                if(index >100) {
                    iUpdateStockService.zeroClearing(config.getVendor_id());
                }
            }

            // product_delta_update
            if(config.getEventName().equals(ApiConfigPool.product_delta_update)) {
                // 获取全路径
                String fullOriginPath = config.getOriginPath()+config.getOriginName();
                String fullRevisedPath = config.getRevisedPath()+config.getRevisedName();

                // 保存最新文件
                String response = ApiHttpTools.httpGet(config.getUrl());
                config.getFileUtils().bakPendingFile(config.getEventName(),response);
                List<Map<String, Object>> productList = (List<Map<String, Object>>) JSONObject.parse(response);
                String productStrs = converString(productList);
                FileUtil.createFileByType(config.getRevisedPath(),config.getRevisedName(),productStrs);

                // 如果第一次进来存一份原始文件
                if(!FileUtil.fileExists(fullOriginPath)) {
                    FileUtil.createFileByType(config.getOriginPath(),config.getOriginName(),productStrs);
                }

                List<DiffRow> diffRows = FileUtil.CompareTxtByType(fullOriginPath,fullRevisedPath);
                for(DiffRow diffRow : diffRows) {
                    DiffRow.Tag tag = diffRow.getTag();
                    String product = "";
                    if(tag == DiffRow.Tag.INSERT || tag == DiffRow.Tag.CHANGE) {
                        logger.info("MatgentoController,insertandchange,diffRow:"+ com.alibaba.fastjson.JSONObject.toJSONString(diffRow));
                        product = diffRow.getNewLine().replace("<br>","");
                    } else if(tag == DiffRow.Tag.DELETE){
                        logger.info("MatgentoController,delete,diffRow:"+ com.alibaba.fastjson.JSONObject.toJSONString(diffRow));
                        product = diffRow.getOldLine().replace("<br>","");
                    }

                    if(StringUtils.isNotBlank(product)) {
                        Map<String, Object> mqDataMap = new HashMap<>();
                        mqDataMap.put("Data", product);
                        if(tag == DiffRow.Tag.DELETE) {
                            mqDataMap.put("stock","0");
                        }
                        ApiExectorUtils.putThreadPool(config,mqDataMap,matgentoSynProductMapping);
                    }
                }

                // 结束后，用最新文件覆盖原始文件，准备下次更新
                FileUtil.createFileByType(config.getOriginPath(),config.getOriginName(),productStrs);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("MatgentoController,errorMessage:"+e);
        }
        long end = System.currentTimeMillis();
        logger.info("Job_Run_Time,vendorName:"+name+","+type+",start:"+start+",end:"+end+",time:"+(end-start));
        return null;
    }

    private int page = 1;
    private String generatorURL(String url,int limit){
        int limit1 = (page*limit) - limit;
        page++;
        return url+"&limit="+limit1+","+limit;
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

    @Override
    public void afterPropertiesSet() throws Exception {
        apiConfigPool = new ApiConfigPool();

        apiConfigPool.put(
                new ApiConfig().setUrl("http://srv1.best-fashion.net/export/apiv2/?t=2caac260ced5700794d4d6efb17d4bcb&type=allstock&supplier_sku_split=1&limit=0,10000")
                .setVendor_id(34L)
                .setVendorName("leam")
                .setThreadNum(15).setLimit(10)
                .setEventName(ApiConfigPool.product_delta_update)
                .setExecutor(ApiConfigPool.getExecutor())
                .setOriginName("origin.txt")
                .setOriginPath("/mnt2/leam/compare/")
                .setRevisedName("revised.txt")
                .setRevisedPath("/mnt2/leam/compare/")
                );

        apiConfigPool.put(
                new ApiConfig().setUrl("http://srv1.best-fashion.net/export/apiv2/?t=2caac260ced5700794d4d6efb17d4bcb&type=allstock&supplier_sku_split=1")
                .setVendor_id(34L)
                .setVendorName("leam")
                .setThreadNum(15).setLimit(500)
                .setEventName(ApiConfigPool.product_all_update)
                .setExecutor(ApiConfigPool.getExecutor())
        );
    }
}
