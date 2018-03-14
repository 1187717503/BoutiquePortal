package com.intramirror.web.controller.api.eb;

import com.google.gson.Gson;
import com.intramirror.common.utils.DateUtils;
import com.intramirror.core.common.response.Response;
import com.intramirror.core.common.response.StatusCode;
import com.intramirror.core.net.http.OkHttpUtils;
import com.intramirror.product.api.service.stock.IUpdateStockService;
import com.intramirror.utils.transform.JsonTransformUtil;
import com.intramirror.web.mapping.api.IProductMapping;
import com.intramirror.web.mapping.api.IStockMapping;
import com.intramirror.web.mapping.vo.StockOption;
import com.intramirror.web.thread.UpdateProductThread;
import com.intramirror.web.thread.UpdateStockThread;
import com.intramirror.web.util.ApiDataFileUtils;
import java.util.Date;
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
import pk.shoplus.model.ProductEDSManagement;
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

    @Resource(name = "ebStockMapping")
    private IStockMapping iStockMapping;

    private static Map<String, Object> configMap = new HashMap<>();

    static {
        final ExecutorService allproductRhreadPool = Executors.newFixedThreadPool(10);
        Map<String, Object> ebAllProduct = new HashMap<>();
        ebAllProduct.put("url",
                "https://eleonorabonucci.com/WS/stock.asmx/Get_Article?JSON={\"Codice_Anagrafica\":\"a399c52a-8a43-419f-9d7e-244c32c61e8f\",\"FULL\":true}");
        ebAllProduct.put("vendor_id", "42");
        ebAllProduct.put("executor", allproductRhreadPool);
        ebAllProduct.put("eventName", "product_all_update");
        ebAllProduct.put("fileUtils", new ApiDataFileUtils("eb", "product_all_update"));
        configMap.put("ebAllProduct", ebAllProduct);

        final ExecutorService deltaProductthreadPool = Executors.newFixedThreadPool(10);
        Map<String, Object> ebDeltaProduct = new HashMap<>();
        ebDeltaProduct.put("url",
                "https://eleonorabonucci.com/WS/stock.asmx/Get_Article?JSON={\"Codice_Anagrafica\":\"a399c52a-8a43-419f-9d7e-244c32c61e8f\",\"FULL\":false}");
        ebDeltaProduct.put("vendor_id", "42");
        ebDeltaProduct.put("executor", deltaProductthreadPool);
        ebDeltaProduct.put("eventName", "product_delta_update");
        ebDeltaProduct.put("fileUtils", new ApiDataFileUtils("eb", "product_delta_update"));
        configMap.put("ebDeltaProduct", ebDeltaProduct);

        final ExecutorService allStockthreadPool = Executors.newFixedThreadPool(10);
        Map<String, Object> ebAllStock = new HashMap<>();
        ebAllStock.put("url",
                "https://eleonorabonucci.com/WS/stock.asmx/Get_Article?JSON={\"Codice_Anagrafica\":\"a399c52a-8a43-419f-9d7e-244c32c61e8f\",\"FULL\":true}");
        ebAllStock.put("vendor_id", "42");
        ebAllStock.put("executor", allStockthreadPool);
        ebAllStock.put("eventName", "stock_all_update");
        ebAllStock.put("fileUtils", new ApiDataFileUtils("eb", "stock_all_update"));
        configMap.put("ebAllStock", ebAllStock);

        final ExecutorService deltaStockthreadPool = Executors.newFixedThreadPool(10);
        Map<String, Object> ebDeltaStock = new HashMap<>();
        ebDeltaStock.put("url",
                "https://eleonorabonucci.com/WS/stock.asmx/Get_Article?JSON={\"Codice_Anagrafica\":\"a399c52a-8a43-419f-9d7e-244c32c61e8f\",\"FULL\":false}");
        ebDeltaStock.put("vendor_id", "42");
        ebDeltaStock.put("executor", deltaStockthreadPool);
        ebDeltaStock.put("eventName", "stock_delta_update");
        ebDeltaStock.put("fileUtils", new ApiDataFileUtils("eb", "stock_delta_update"));
        configMap.put("ebDeltaStock", ebDeltaProduct);
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
                logger.error("EleonoraBonucci sync product error " + eventName + ", url : " + url + " , response : " + httpResponse.body().string());
                return Response.status(StatusCode.FAILURE).data(httpResponse);
            }
            String response = httpResponse.body().string();
            if (StringUtils.isEmpty(response)) {
                logger.error("EleonoraBonucci sync product error " + eventName + ", url : " + url + " , response : " + response);
                return Response.status(StatusCode.FAILURE).build();
            }

            Map<String, Object> responseBody = JsonTransformUtil.readValue(response, Map.class);
            if (!Boolean.parseBoolean(responseBody.get("success").toString())) {
                logger.error("EleonoraBonucci sync all product error " + eventName + ", url : " + url + " , response : " + response);
                return Response.status(StatusCode.FAILURE).data(httpResponse);
            }

            List<Map<String, Object>> productList = (List<Map<String, Object>>) responseBody.get("ARTICLE");
            if (productList == null) {
                logger.error("EleonoraBonucci sync product error " + eventName + " due to error json , url : " + url + " , response : " + response);
            }

            logger.info("EleonoraBonucci sync  product " + eventName + ", url : " + url + " , response : " + response);
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
            logger.error("EleonoraBonucci,errorMessage:" + ExceptionUtils.getExceptionDetail(e));
        }

        return Response.success();
    }

    @GetMapping("/stock/sync")
    public Response syncStock(@RequestParam(value = "name") String name) {
        if (StringUtils.isBlank(name)) {
            return Response.status(StatusCode.FAILURE).data("Parameter [name] is missing.");
        }

        Map<String, Object> config = (Map<String, Object>) configMap.get(name);

        String url = config.get("url").toString();
        String vendor_id = config.get("vendor_id").toString();
        String eventName = config.get("eventName").toString();
        ExecutorService executor = (ExecutorService) config.get("executor");
        ApiDataFileUtils fileUtils = (ApiDataFileUtils) config.get("fileUtils");

        try {

            okhttp3.Response httpResponse = OkHttpUtils.get().url(url).build().connTimeOut(60 * 1000).readTimeOut(60 * 1000).execute(); //TODO add url
            if (!httpResponse.isSuccessful()) {
                logger.error("EleonoraBonucci sync stock error " + eventName + " , url : " + url + " , response : " + httpResponse.body().string());
                return Response.status(StatusCode.FAILURE).data(httpResponse);
            }
            String response = httpResponse.body().string();
            if (StringUtils.isEmpty(response)) {
                logger.error("EleonoraBonucci sync stock error " + eventName + ", url : " + url + " , response : " + response);
                return Response.status(StatusCode.FAILURE).build();
            }

            Map<String, Object> responseBody = JsonTransformUtil.readValue(response, Map.class);
            if (!Boolean.parseBoolean(responseBody.get("success").toString())) {
                logger.error("EleonoraBonucci sync stock error " + eventName + ", url : " + url + " , response : " + response);
                return Response.status(StatusCode.FAILURE).data(httpResponse);
            }

            List<Map<String, Object>> productList = (List<Map<String, Object>>) responseBody.get("ARTICLE");
            if (productList == null) {
                logger.error("EB sync stock error " + eventName + "due to error json , url : " + url + " , response : " + response);
            }

            logger.info("EleonoraBonucci sync stock " + eventName + ", url : " + url + " , response : " + response);

            fileUtils.bakPendingFile(name, response);

            for (Map<String, Object> product : productList) {

                if (product.get("Stock_Item") == null) {
                    logger.warn("EB Skip sync stock due to skus missing. " + JsonTransformUtil.toJson(product));
                    continue;
                }
                List<Map<String, Object>> skuList = (List) product.get("Stock_Item");
                if (skuList.size() == 0) {
                    logger.warn("EB Skip sync stock due to skus is empty. " + JsonTransformUtil.toJson(product));
                    continue;
                }

                for (Map<String, Object> sku : skuList) {
                    sku.put("vendor_id", vendor_id);
                    sku.put("SKU", product.get("SKU"));
                    StockOption stockOption = iStockMapping.mapping(sku);
                    logger.info(
                            "EB mapping, stockMap:" + JsonTransformUtil.toJson(sku) + ",stockOption:" + JsonTransformUtil.toJson(stockOption) + ",eventName:"
                                    + eventName);
                    executor.submit(new UpdateStockThread(stockOption, fileUtils, sku));
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("EleonoraBonucci,errorMessage:" + ExceptionUtils.getExceptionDetail(e));
        }

        return Response.success();
    }
}
