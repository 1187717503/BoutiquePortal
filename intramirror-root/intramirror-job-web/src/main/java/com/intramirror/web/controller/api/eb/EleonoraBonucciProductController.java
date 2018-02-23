package com.intramirror.web.controller.api.eb;

import com.intramirror.core.common.response.Response;
import com.intramirror.core.common.response.StatusCode;
import com.intramirror.utils.transform.JsonTransformUtil;
import com.intramirror.web.util.ApiDataFileUtils;
import com.intramirror.web.util.GetPostRequestUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

    // getpost util
    private static GetPostRequestUtil getPostRequestUtil = new GetPostRequestUtil();

    private static Map<String, Object> configMap = new HashMap<>();

    static {
        final ExecutorService threadPool = Executors.newFixedThreadPool(10);
        Map<String, Object> ebAllProduct = new HashMap<>();
        ebAllProduct.put("url",
                "https://eleonorabonucci.com/WS/stock.asmx/Get_Article?JSON={\"Codice_Anagrafica\":\"a6c9eb33-0465-4674-aedc-0615cdf6282e\",\"FULL\":true}");
        ebAllProduct.put("vendor_id", "44");
        ebAllProduct.put("executor", threadPool);
        ebAllProduct.put("eventName", "product_all_update");
        ebAllProduct.put("fileUtils", new ApiDataFileUtils("eb", "product_all_update"));
        configMap.put("ebAllProduct", ebAllProduct);
    }

    @GetMapping("/sync")
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

        boolean isEnd = false;
        long count = 0;

        try {
            String appendUrl = url;

            String response = GetPostRequestUtil.httpsGetRequest(appendUrl, "");
            if (StringUtils.isEmpty(response)) {
                logger.error("Gibot sync all product error , url : " + appendUrl + " , response : " + response);
                return Response.status(StatusCode.FAILURE).build();
            }
            List<Map<String, Object>> productList = JsonTransformUtil.readValue(response, List.class);
            if (productList == null) {
                logger.error("Gibot sync all product error due to error json , url : " + appendUrl + " , response : " + response);

            }

            fileUtils.bakPendingFile("all", response);

            //                for (int i = 0; i < productList.size(); i++) {
            //                    if (!(productList.get(i) instanceof Map)) {
            //                        logger.warn("GibotProduct error data . " + productList.get(i).toString());
            //                    }
            //                    Map<String, Object> product = productList.get(i);
            //                    Map<String, Object> mqDataMap = new HashMap<>();
            //                    mqDataMap.put("product", product);
            //                    mqDataMap.put("vendor_id", vendor_id);
            //                    logger.info("GibotProduct mqDataMap:" + new Gson().toJson(mqDataMap) + ",eventName:" + eventName);
            //                    ProductEDSManagement.ProductOptions productOptions = iProductMapping.mapping(mqDataMap);
            //
            //                    ProductEDSManagement.VendorOptions vendorOptions = new ProductEDSManagement.VendorOptions();
            //                    vendorOptions.setVendorId(Long.parseLong(vendor_id));
            //                    logger.info("GibotProduct,productOptions:" + new Gson().toJson(productOptions) + ",vendorOptions:" + new Gson().toJson(vendorOptions)
            //                            + ",eventName:" + eventName);
            //
            //                    logger.info("GibotProduct,execute,startDate:" + DateUtils.getStrDate(new Date()) + ",productOptions:" + new Gson().toJson(productOptions)
            //                            + ",vendorOptions:" + new Gson().toJson(vendorOptions) + ",eventName:" + eventName);
            //                    CommonThreadPool.execute(eventName, executor, threadNum, new UpdateProductThread(productOptions, vendorOptions, fileUtils, mqDataMap));
            //                    logger.info("GibotProduct,execute,endDate:" + DateUtils.getStrDate(new Date()) + ",productOptions:" + new Gson().toJson(productOptions)
            //                            + ",vendorOptions:" + new Gson().toJson(vendorOptions) + ",eventName:" + eventName);
            //                    count++;
            //                }
            //
            //
            //            if (eventName.equals("product_all_update") && count > 100) {
            //                logger.info("GibotProduct,zeroClearing,start,vendor_id:" + vendor_id);
            //                iUpdateStockService.zeroClearing(Long.parseLong(vendor_id));
            //                logger.info("GibotProduct,zeroClearing,end,vendor_id:" + vendor_id);
            //            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("GibotProduct,errorMessage:" + ExceptionUtils.getExceptionDetail(e));
        }

        return Response.success();
    }
}
