package com.intramirror.web.controller.api.atelier;

import com.alibaba.fastjson15.JSONObject;
import com.intramirror.web.mapping.impl.atelier.AtelierUpdateByProductMapping;
import com.intramirror.web.thread.CommonThreadPool;
import com.intramirror.web.thread.UpdateProductThread;
import com.intramirror.web.util.ApiDataFileUtils;
import com.intramirror.web.util.AtelierFileUtils;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import javax.annotation.Resource;
import org.apache.ibatis.annotations.Param;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import pk.shoplus.model.ProductEDSManagement;

/**
 * Created by dingyifan on 2017/12/19.
 */
@Controller
@RequestMapping("atelier/consume/product")
public class ProductConsume implements InitializingBean {

    private static final Logger logger = Logger.getLogger(ProductConsume.class);

    @Resource(name = "atelierUpdateByProductMapping")
    private AtelierUpdateByProductMapping atelierUpdateByProductMapping;

    // 初始化线程池
    private ThreadPoolExecutor executor1;
    private ThreadPoolExecutor executor2;
    private ThreadPoolExecutor executor3;
    private ThreadPoolExecutor executor4;
    private ThreadPoolExecutor executor5;

    @Autowired
    private AtelierFileUtils fileUtils;

    // 线程数量
    private int threadNum = 100;

    @RequestMapping("/execute")
    public void execute(@Param(value = "num")Integer num){
        try {
            logger.info("ProductConsume,inputParams,num:"+num);
            List<String> messages = fileUtils.read(num);
            logger.info("ProductConsume,messages,length:"+messages.size());

            ThreadPoolExecutor executor = null;
            int tIndex = 1;

            for(int i = 0,len=messages.size();i<len;i++) {
                String message = messages.get(i);
                logger.info("ProductConsume,message,content:"+message);

                // 读取message
                Map msgMap = JSONObject.parseObject(message);
                String vendor_id = msgMap.get("vendor_id").toString();
                String eventName = msgMap.get("eventName").toString();
                String type = msgMap.get("type").toString();
                ApiDataFileUtils fileUtils = new ApiDataFileUtils(eventName,type);
                ProductEDSManagement.VendorOptions vendorOptions = new ProductEDSManagement().getVendorOptions();
                vendorOptions.setVendorId(Long.parseLong(vendor_id));

                // mapping message
                logger.info("ProductConsume,mapping,start,msgMap:"+JSONObject.toJSONString(msgMap));
                ProductEDSManagement.ProductOptions productOptions = atelierUpdateByProductMapping.mapping(msgMap);
                logger.info("ProductConsume,mapping,end,msgMap:"+JSONObject.toJSONString(msgMap)+",productOptions:"+JSONObject.toJSONString(productOptions));

                switch (tIndex) {
                    case 1:
                        executor = executor1;
                        break;
                    case 2:
                        executor = executor2;
                        break;
                    case 3:
                        executor = executor3;
                        break;
                    case 4:
                        executor = executor4;
                        break;
                    case 5:
                        executor = executor5;
                        break;
                    default:
                        tIndex=1;
                        break;
                }

                logger.info("ProductConsume,execute,start,msgMap:"+JSONObject.toJSONString(msgMap)+",productOptions:"+JSONObject.toJSONString(productOptions) +",i:"+i);
                CommonThreadPool.execute(eventName,executor,threadNum,new UpdateProductThread(productOptions,vendorOptions,fileUtils,msgMap));
                logger.info("ProductConsume,execute,end,msgMap:"+JSONObject.toJSONString(msgMap)+",productOptions:"+JSONObject.toJSONString(productOptions)+",i:"+i);

                tIndex ++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        executor1 =(ThreadPoolExecutor) Executors.newCachedThreadPool();
        executor2 =(ThreadPoolExecutor) Executors.newCachedThreadPool();
        executor3 =(ThreadPoolExecutor) Executors.newCachedThreadPool();
        executor4 =(ThreadPoolExecutor) Executors.newCachedThreadPool();
        executor5 =(ThreadPoolExecutor) Executors.newCachedThreadPool();
    }
}
