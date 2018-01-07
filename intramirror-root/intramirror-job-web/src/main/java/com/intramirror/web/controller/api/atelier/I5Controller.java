package com.intramirror.web.controller.api.atelier;

import com.alibaba.fastjson.JSONObject;
import com.intramirror.common.help.ExceptionUtils;
import com.intramirror.web.controller.api.atelier.stock.AtelierStockController;
import com.intramirror.web.mapping.impl.atelier.AtelierUpdateByProductMapping;
import com.intramirror.web.thread.CommonThreadPool;
import com.intramirror.web.thread.UpdateProductThread;
import com.intramirror.web.util.ApiDataFileUtils;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import javax.annotation.Resource;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pk.shoplus.common.utils.FileUtils;
import pk.shoplus.model.ProductEDSManagement;

/**
 * Created by dingyifan on 2018/1/7.
 */

@Controller
@RequestMapping("/atelier/i5")
public class I5Controller {

    private static Logger logger = Logger.getLogger(AtelierStockController.class);
    @Resource(name = "atelierUpdateByProductMapping")
    private AtelierUpdateByProductMapping atelierUpdateByProductMapping;
    private static String path1 = "/mnt2/iCinque/backup/product_all_update/20180107";
//    private static String path1 = "/Users/dingyifan/Documents/fileTest/product/20171220";
    private static String path2 = "/mnt2/iCinque/backup/product_delta_create/20180107";
    private static ThreadPoolExecutor executor_11 =(ThreadPoolExecutor) Executors.newCachedThreadPool();

    @RequestMapping(value = "/update",method = RequestMethod.GET)
    @ResponseBody
    public String update(){

        logger.info("I5Controller,update,start");

        this.handle(path1,"product_all_update");
        this.handle(path2,"product_delta_create");
        logger.info("I5Controller,update,end");
        return null;
    }

    private void handle(String path,String type){
        File root = new File(path);
        File[] files = root.listFiles();
        ApiDataFileUtils fileUtils = new ApiDataFileUtils("iCinque",type);
        int i = 0;
        for(File file:files){

            try {
                String body = FileUtils.file2String(new File(path+"/"+file.getName()),"UTF-8");
                JSONObject jsonObjectBody = JSONObject.parseObject(body);

                Map<String,Object> bodyDataMap = new HashMap<>();
                bodyDataMap.put("Data",jsonObjectBody);
                bodyDataMap.put("vendor_id",11);

                ProductEDSManagement.VendorOptions vendorOptions = new ProductEDSManagement().getVendorOptions();
                vendorOptions.setVendorId(11L);

                ProductEDSManagement.ProductOptions productOptions = atelierUpdateByProductMapping.mapping(bodyDataMap);
                logger.info("I5Controller,productOptions:"+JSONObject.toJSONString(productOptions)+",bodyDataMap:"+JSONObject.toJSONString(bodyDataMap)+",vendorOptions:"+JSONObject.toJSONString(vendorOptions));
                CommonThreadPool.execute("iCinque",executor_11,20,new UpdateProductThread(productOptions,vendorOptions,fileUtils,bodyDataMap));
            } catch (Exception e) {
                e.printStackTrace();

                logger.info("I5Controller,errorMessage:"+ ExceptionUtils.getExceptionDetail(e));
            }
            i++;
        }

        logger.info("I5Controller,handle,end,path:"+path+",type:"+type+",index:"+i);
    }

    public static void main(String[] args) {
        new I5Controller().handle("/Users/dingyifan/Documents/fileTest/product/20171220","product_all_update");
    }
}
