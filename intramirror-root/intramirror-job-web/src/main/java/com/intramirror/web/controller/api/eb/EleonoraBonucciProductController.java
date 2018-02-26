package com.intramirror.web.controller.api.eb;

import com.intramirror.core.common.response.Response;
import com.intramirror.core.common.response.StatusCode;
import com.intramirror.product.api.service.stock.IUpdateStockService;
import com.intramirror.utils.transform.JsonTransformUtil;
import com.intramirror.web.mapping.api.IProductMapping;
import com.intramirror.web.util.ApiDataFileUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.Resource;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pk.shoplus.util.ExceptionUtils;

/**
 * Created on 2018/2/22.
 *
 * @author YouFeng.Zhu
 */
@RestController
@RequestMapping("/eb")
public class EleonoraBonucciProductController {
    // logger
    private static final Logger logger = Logger.getLogger(EleonoraBonucciProductController.class);

    @Resource(name = "ebProductMapping")
    private IProductMapping iProductMapping;

    @Resource(name = "updateStockService")
    private IUpdateStockService iUpdateStockService;

    private static Map<String, Object> configMap = new HashMap<>();

    static {
        final ExecutorService allproductRhreadPool = Executors.newFixedThreadPool(10);
        Map<String, Object> ebAllProduct = new HashMap<>();
        ebAllProduct.put("url",
                "https://eleonorabonucci.com/WS/stock.asmx/Get_Article?JSON={\"Codice_Anagrafica\":\"a6c9eb33-0465-4674-aedc-0615cdf6282e\",\"FULL\":true}");
        ebAllProduct.put("vendor_id", "46");
        ebAllProduct.put("executor", allproductRhreadPool);
        ebAllProduct.put("eventName", "product_all_update");
        ebAllProduct.put("fileUtils", new ApiDataFileUtils("eb", "product_all_update"));
        configMap.put("ebAllProduct", ebAllProduct);

        final ExecutorService deltaProductthreadPool = Executors.newFixedThreadPool(10);
        Map<String, Object> ebDeltaProduct = new HashMap<>();
        ebDeltaProduct.put("url",
                "https://eleonorabonucci.com/WS/stock.asmx/Get_Article?JSON={\"Codice_Anagrafica\":\"a6c9eb33-0465-4674-aedc-0615cdf6282e\",\"FULL\":false}");
        ebDeltaProduct.put("vendor_id", "46");
        ebDeltaProduct.put("executor", deltaProductthreadPool);
        ebDeltaProduct.put("eventName", "product_delta_update");
        ebDeltaProduct.put("fileUtils", new ApiDataFileUtils("eb", "product_delta_update"));
        configMap.put("ebDeltaProduct", ebDeltaProduct);
    }

    @GetMapping("/product/sync")
    public Response syncProduct(@RequestParam(value = "name") String name) {
        if (StringUtils.isBlank(name)) {
            return Response.status(StatusCode.FAILURE).data("Parameter [name] is missing.");
        }

        Map<String, Object> config = (Map<String, Object>) configMap.get(name);

        String url = config.get("url").toString();
        String vendor_id = config.get("vendor_id").toString();
        String eventName = config.get("eventName").toString();
        ExecutorService executor = (ExecutorService) config.get("executor");
        ApiDataFileUtils fileUtils = (ApiDataFileUtils) config.get("fileUtils");

        long count = 0;

        try {

            okhttp3.Response httpResponse = OkHttpUtils.get().url(url).build().connTimeOut(60 * 1000).readTimeOut(60 * 1000).execute(); //TODO add url
            if (!httpResponse.isSuccessful()) {
                logger.error("EleonoraBonucci sync all product error , url : " + url + " , response : " + httpResponse.body().string());
                return Response.status(StatusCode.FAILURE).data(httpResponse);
            }
            String response = httpResponse.body().string();
            if (StringUtils.isEmpty(response)) {
                logger.error("EleonoraBonucci sync all product error , url : " + url + " , response : " + response);
                return Response.status(StatusCode.FAILURE).build();
            }

            Map<String, Object> responseBody = JsonTransformUtil.readValue(response, Map.class);
            if (!Boolean.parseBoolean(responseBody.get("success").toString())) {
                logger.error("EleonoraBonucci sync all product error , url : " + url + " , response : " + response);
                return Response.status(StatusCode.FAILURE).data(httpResponse);
            }

            List<Map<String, Object>> productList = (List<Map<String, Object>>) responseBody.get("ARTICLE");
            if (productList == null) {
                logger.error("EleonoraBonucci sync all product error due to error json , url : " + url + " , response : " + response);
            }

            fileUtils.bakPendingFile("all", response);

            for (Map<String, Object> product : productList) {
                Map<String, Object> mqDataMap = new HashMap<>();
                mqDataMap.put("product", product);
                mqDataMap.put("vendor_id", vendor_id);
                logger.info("EleonoraBonucci mqDataMap:" + new Gson().toJson(mqDataMap) + ",eventName:" + eventName);
                ProductEDSManagement.ProductOptions productOptions = iProductMapping.mapping(mqDataMap);

                ProductEDSManagement.VendorOptions vendorOptions = new ProductEDSManagement.VendorOptions();
                vendorOptions.setVendorId(Long.parseLong(vendor_id));

                logger.info("EleonoraBonucci,execute,startDate:" + DateUtils.getStrDate(new Date()) + ",productOptions:" + new Gson().toJson(productOptions)
                        + ",vendorOptions:" + new Gson().toJson(vendorOptions) + ",eventName:" + eventName);
                executor.submit(new UpdateProductThread(productOptions, vendorOptions, fileUtils, mqDataMap));

                count++;
            }

            if (eventName.equals("product_all_update") && count > 100) {
                logger.info("EleonoraBonucci,zeroClearing,start,vendor_id:" + vendor_id);
                iUpdateStockService.zeroClearing(Long.parseLong(vendor_id));
                logger.info("EleonoraBonucci,zeroClearing,end,vendor_id:" + vendor_id);
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("GibotProduct,errorMessage:" + ExceptionUtils.getExceptionDetail(e));
        }

        return Response.success();
    }
}
