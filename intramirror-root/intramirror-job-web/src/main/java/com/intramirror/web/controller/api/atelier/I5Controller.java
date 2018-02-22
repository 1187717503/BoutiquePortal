package com.intramirror.web.controller.api.atelier;

import com.alibaba.fastjson.JSONObject;
import com.intramirror.common.help.ExceptionUtils;
import com.intramirror.web.controller.api.atelier.stock.AtelierStockController;
import com.intramirror.web.mapping.impl.atelier.AtelierUpdateByProductMapping;
import com.intramirror.web.mapping.vo.StockContants;
import com.intramirror.web.mapping.vo.StockOption;
import com.intramirror.web.thread.CommonThreadPool;
import com.intramirror.web.thread.UpdateStockThread;
import com.intramirror.web.util.ApiDataFileUtils;
import java.io.File;
import java.util.HashMap;
import java.util.List;
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
import pk.shoplus.util.DateUtils;

/**
 * Created by dingyifan on 2018/1/7.
 */

@Controller
@RequestMapping("/atelier/julian")
public class I5Controller {

    private static Logger logger = Logger.getLogger(AtelierStockController.class);

    @Resource(name = "atelierUpdateByProductMapping")
    private AtelierUpdateByProductMapping atelierUpdateByProductMapping;

    private static String path1 = "/mnt2/julian/backup/product_all_update/20180210";
    //    private static String path1 = "D:\\mnt2\\julian\\backup\\20180210";
    private static String path2 = "/mnt2/julian/backup/product_all_update/20180209";
    private static String path3 = "/mnt2/julian/backup/product_all_update/20180208";

    private static ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    @RequestMapping(value = "/update", method = RequestMethod.GET)
    @ResponseBody
    public String update() {
        //        this.handle(path1, "product_all_update", "20180210");
        this.handle(path2, "product_all_update", "20180209");
        this.handle(path3, "product_all_update", "20180208");
        return null;
    }

    private void handle(String path, String type, String date) {
        File root = new File(path);
        File[] files = root.listFiles();
        ApiDataFileUtils fileUtils = new ApiDataFileUtils("julian_bak", type);
        int i = 0;

        for (File file : files) {

            try {
                String body = FileUtils.file2String(new File(path + "/" + file.getName()), "UTF-8");
                JSONObject jsonObjectBody = JSONObject.parseObject(body);

                Map<String, Object> bodyDataMap = new HashMap<>();
                bodyDataMap.put("Data", jsonObjectBody);
                bodyDataMap.put("vendor_id", 26);

                ProductEDSManagement.VendorOptions vendorOptions = new ProductEDSManagement().getVendorOptions();
                vendorOptions.setVendorId(26L);

                ProductEDSManagement.ProductOptions productOptions = atelierUpdateByProductMapping.mapping(bodyDataMap);
                List<ProductEDSManagement.SkuOptions> skus = productOptions.getSkus();

                for (ProductEDSManagement.SkuOptions skuOptions : skus) {
                    StockOption option = new StockOption();
                    option.setProductCode(productOptions.getCode());
                    option.setSizeValue(skuOptions.getSize());
                    option.setQuantity(skuOptions.getStock());
                    option.setVendor_id("26");
                    option.setType(StockContants.absolute_qty); // 库存绝对值
                    option.setLast_check(DateUtils.getDateByStr("yyyy-MM-dd HH:mm:ss", "2018-02-11 00:00:00"));
                    logger.info(
                            "I5Controller,option:" + JSONObject.toJSONString(option) + ",bodyDataMap:" + JSONObject.toJSONString(bodyDataMap) + ",date:" + date
                                    + ",i:" + i);
                    CommonThreadPool.execute("julian_product_all_update", executor, 50, new UpdateStockThread(option, fileUtils, bodyDataMap));
                }

            } catch (Exception e) {
                e.printStackTrace();
                logger.info("I5Controller,errorMessage:" + ExceptionUtils.getExceptionDetail(e));
            }
            i++;
        }
        logger.info("I5Controller,handle,end,path:" + path + ",type:" + type + ",index:" + i);
    }

}
