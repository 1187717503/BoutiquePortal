package com.intramirror.web.controller.api.prestashop;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.intramirror.product.api.service.stock.IUpdateStockService;
import com.intramirror.web.controller.api.utils.ApiHttpTools;
import com.intramirror.web.mapping.impl.prestashop.PrestashopProductMapping;
import com.intramirror.web.thread.CommonThreadPool;
import com.intramirror.web.thread.UpdateProductThread;
import com.intramirror.web.util.ApiDataFileUtils;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import javax.annotation.Resource;
import org.apache.ibatis.annotations.Param;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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

    @Resource(name = "updateStockService")
    private IUpdateStockService stockService;

    @GetMapping("/{name}")
    public Map<String, Object> execute(@Param(value = "name") String name) {
        Map<String, Object> paramMap = paramsMap.get(name);
        String url = paramMap.get("url").toString();
        int page = Integer.parseInt(paramMap.get("page").toString());
        int perpage = Integer.parseInt(paramMap.get("perpage").toString());
        long vendor_id = Long.parseLong(paramMap.get("vendor_id").toString());
        String seret = paramMap.get("seret").toString();
        String date_upd = paramMap.get("date_upd").toString();
        ThreadPoolExecutor executor = (ThreadPoolExecutor) paramMap.get("executor");
        ApiDataFileUtils fileUtils = (ApiDataFileUtils) paramMap.get("fileUtils");
        if (!name.contains("all")) {
            date_upd = DateUtils.getStrDate(new Date(), "yyyy-MM-dd HH:mm:ss");
        }

        try {
            int index = 0;
            while (true) {
                String callUrl = url + "&page=" + page + "&perpage=" + perpage + "&seret=" + seret + "&date_upd=" + date_upd;
                String response = ApiHttpTools.httpGet(callUrl);
                JSONObject responseObj = JSONObject.parseObject(response);
                JSONObject data = responseObj.getJSONObject("data");
                if (data.toJSONString().length() < 10) {
                    break;
                }
                JSONArray products = data.getJSONArray("products");
                if (products.size() == 0) {
                    break;
                }

                for (int i = 0, len = products.size(); i < len; i++) {
                    JSONObject product = products.getJSONObject(i);
                    Map<String, Object> mqDataMap = new HashMap<String, Object>();
                    mqDataMap.put("Data", product);

                    ProductEDSManagement.ProductOptions productOptions = productMapping.mapping(mqDataMap);

                    ProductEDSManagement.VendorOptions vendorOptions = productEDSManagement.getVendorOptions();
                    vendorOptions.setVendorId(vendor_id);

                    CommonThreadPool.execute(name, executor, 10, new UpdateProductThread(productOptions, vendorOptions, fileUtils, mqDataMap));
                    index++;
                }
            }

            if (name.contains("all") && index > 100) {
                stockService.zeroClearing(vendor_id);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                .putData("perpage", "100")
                .putData("secret","N5RNF8DHUW4VRQWALMBIIT18EYY4EY4D")
                .putData("date_upd", "")
                .putData("executor", (ThreadPoolExecutor) Executors.newCachedThreadPool())
                .putData("fileUtils", new ApiDataFileUtils("linoricci", "product_all_update")).getMap());
        // @formatter:on
    }
}
