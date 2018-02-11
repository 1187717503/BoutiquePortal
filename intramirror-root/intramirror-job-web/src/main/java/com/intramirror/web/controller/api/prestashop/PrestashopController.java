package com.intramirror.web.controller.api.prestashop;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.intramirror.product.api.service.stock.IUpdateStockService;
import com.intramirror.web.controller.api.utils.ApiHttpTools;
import com.intramirror.web.mapping.impl.prestashop.PrestashopProductMapping;
import com.intramirror.web.mapping.impl.prestashop.PrestashopStockMapping;
import com.intramirror.web.mapping.vo.StockOption;
import com.intramirror.web.thread.CommonThreadPool;
import com.intramirror.web.thread.UpdateProductThread;
import com.intramirror.web.thread.UpdateStockThread;
import com.intramirror.web.util.ApiDataFileUtils;
import java.util.HashMap;
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
import pk.shoplus.util.DateUtils;
import pk.shoplus.util.MapUtils;

@Controller
@RequestMapping("/prestashop")
public class PrestashopController implements InitializingBean {

    private static final Logger logger = Logger.getLogger(PrestashopController.class);

    private Map<String, Map<String, Object>> paramsMap;

    public static ProductEDSManagement productEDSManagement = new ProductEDSManagement();

    @Resource(name = "prestashopProductMapping")
    private PrestashopProductMapping productMapping;

    @Resource(name = "prestashopStockMapping")
    private PrestashopStockMapping stockMapping;

    @Resource(name = "updateStockService")
    private IUpdateStockService stockService;

    @GetMapping("/{name}")
    public Map<String, Object> update(@PathVariable(value = "name") String name) {
        Map<String, Object> paramMap = paramsMap.get(name);
        String url = paramMap.get("url").toString();
        int page = Integer.parseInt(paramMap.get("page").toString());
        int perpage = Integer.parseInt(paramMap.get("perpage").toString());
        long vendor_id = Long.parseLong(paramMap.get("vendor_id").toString());
        String seret = paramMap.get("secret").toString();
        String date_upd = paramMap.get("date_upd").toString();
        ThreadPoolExecutor executor = (ThreadPoolExecutor) paramMap.get("executor");
        ApiDataFileUtils fileUtils = (ApiDataFileUtils) paramMap.get("fileUtils");
        if (!name.contains("all")) {
            date_upd = DateUtils.getTimeByMINUTE(5);
        }

        try {
            int index = 0;
            while (true) {
                String callUrl = url + "&page=" + page + "&perpage=" + perpage + "&secret=" + seret + "&date_upd=" + date_upd;
                logger.info("PrestashopController,callUrl:" + callUrl);
                String response = ApiHttpTools.httpGet(callUrl);
                JSONObject responseObj = JSONObject.parseObject(response);
                JSONObject data = responseObj.getJSONObject("data");
                if (data.toJSONString().length() < 10) {
                    logger.info("PrestashopController,break:name->" + name + ",response:" + response);
                    break;
                }
                JSONArray products = data.getJSONArray("products");
                if (products.size() == 0) {
                    logger.info("PrestashopController,break:name->" + name + ",response:" + response);
                    break;
                }

                for (int i = 0, len = products.size(); i < len; i++) {
                    JSONObject product = products.getJSONObject(i);

                    if (name.contains("product")) {
                        Map<String, Object> mqDataMap = new HashMap<String, Object>();
                        mqDataMap.put("Data", product);
                        ProductEDSManagement.ProductOptions productOptions = productMapping.mapping(mqDataMap);
                        ProductEDSManagement.VendorOptions vendorOptions = productEDSManagement.getVendorOptions();
                        vendorOptions.setVendorId(vendor_id);

                        logger.info("PrestashopController,updateProduct:mqDataMap->" + JSONObject.toJSONString(mqDataMap) + ",productOptions:" + JSONObject
                                .toJSONString(productOptions) + ",vendorOptions->" + JSONObject.toJSONString(vendorOptions) + ",name->" + name + ",index->"
                                + index);
                        CommonThreadPool.execute(name, executor, 10, new UpdateProductThread(productOptions, vendorOptions, fileUtils, mqDataMap));
                    } else if (name.contains("stock")) {
                        JSONArray sizes = product.getJSONArray("sizes");
                        for (int j = 0, jLen = sizes.size(); j < jLen; j++) {
                            JSONObject size = sizes.getJSONObject(j);
                            Map<String, Object> mqDataMap = new HashMap<String, Object>();
                            mqDataMap.put("id_product", product.getString("id_product"));
                            mqDataMap.put("size", size.getString("size"));
                            mqDataMap.put("quantity", size.getString("quantity"));
                            mqDataMap.put("vendor_id", vendor_id);
                            StockOption stockOption = stockMapping.mapping(mqDataMap);

                            logger.info("PrestashopController,updateStock:mqDataMap->" + JSONObject.toJSONString(mqDataMap) + ",stockOption:" + JSONObject
                                    .toJSONString(stockOption) + ",name->" + name + ",index->" + index);
                            CommonThreadPool.execute(name, executor, 10, new UpdateStockThread(stockOption, fileUtils, mqDataMap));
                        }
                    }
                    index++;
                }
                page++;
            }

            if (name.contains("all") && index > 100) {
                stockService.zeroClearing(vendor_id);
                logger.info("PrestashopController,zeroClearing:vendor_id->" + vendor_id + ",index->" + index);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("PrestashopController,ErrorMessage:e->" + e + ",name->" + name);
        }
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // @formatter:off
        paramsMap = new HashMap<>();
        //https://www.linoricci.it/DropShipping/?method=getProductsList&page=1&perpage=1&secret=N5RNF8DHUW4VRQWALMBIIT18EYY4EY4D
        paramsMap.put("linoricci_product_all_update", new MapUtils(new HashMap<>())
                .putData("url", "https://www.linoricci.it/DropShipping/?method=getProductsList")
                .putData("page", "1")
                .putData("perpage", "10")
                .putData("secret","N5RNF8DHUW4VRQWALMBIIT18EYY4EY4D")
                .putData("date_upd", "")
                .putData("vendor_id", "45")
                .putData("executor", (ThreadPoolExecutor) Executors.newCachedThreadPool())
                .putData("fileUtils", new ApiDataFileUtils("linoricci", "product_all_update")).getMap());

        paramsMap.put("linoricci_product_delta_update", new MapUtils(new HashMap<>())
                .putData("url", "https://www.linoricci.it/DropShipping/?method=getProductsList")
                .putData("page", "1")
                .putData("perpage", "10")
                .putData("secret","N5RNF8DHUW4VRQWALMBIIT18EYY4EY4D")
                .putData("date_upd", "")
                .putData("vendor_id", "45")
                .putData("executor", (ThreadPoolExecutor) Executors.newCachedThreadPool())
                .putData("fileUtils", new ApiDataFileUtils("linoricci", "product_delta_update")).getMap());

        //https://www.linoricci.it/DropShipping/?method=getStock&page=2&perpage=10&secret=N5RNF8DHUW4VRQWALMBIIT18EYY4EY4D
        paramsMap.put("linoricci_stock_all_update",new MapUtils(new HashMap<>())
                .putData("url","https://www.linoricci.it/DropShipping/?method=getStock")
                .putData("page","1")
                .putData("perpage","10")
                .putData("secret","N5RNF8DHUW4VRQWALMBIIT18EYY4EY4D")
                .putData("date_upd", "")
                .putData("vendor_id", "45")
                .putData("executor", (ThreadPoolExecutor) Executors.newCachedThreadPool())
                .putData("fileUtils", new ApiDataFileUtils("linoricci", "stock_all_update")).getMap());

        paramsMap.put("linoricci_stock_delta_update",new MapUtils(new HashMap<>())
                .putData("url","https://www.linoricci.it/DropShipping/?method=getStock")
                .putData("page","1")
                .putData("perpage","10")
                .putData("secret","N5RNF8DHUW4VRQWALMBIIT18EYY4EY4D")
                .putData("date_upd", "")
                .putData("vendor_id", "45")
                .putData("executor", (ThreadPoolExecutor) Executors.newCachedThreadPool())
                .putData("fileUtils", new ApiDataFileUtils("linoricci", "stock_delta_update")).getMap());
        // @formatter:on
    }
}
