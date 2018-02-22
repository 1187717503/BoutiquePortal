package com.intramirror.web.controller.api.gibot;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.intramirror.common.utils.DateUtils;
import com.intramirror.core.common.response.Response;
import com.intramirror.core.common.response.StatusCode;
import com.intramirror.product.api.service.stock.IUpdateStockService;
import com.intramirror.utils.transform.JsonTransformUtil;
import com.intramirror.web.controller.api.eds.EdsUpdateByProductController;
import com.intramirror.web.mapping.api.IProductMapping;
import com.intramirror.web.thread.CommonThreadPool;
import com.intramirror.web.thread.UpdateProductThread;
import com.intramirror.web.util.ApiDataFileUtils;
import com.intramirror.web.util.GetPostRequestUtil;
import difflib.DiffRow;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import javax.annotation.Resource;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pk.shoplus.model.ProductEDSManagement;
import pk.shoplus.service.RedisService;
import pk.shoplus.util.ExceptionUtils;
import pk.shoplus.util.FileUtil;

/**
 * Created on 2018/2/9.
 *
 * @author YouFeng.Zhu
 */
@RestController
@RequestMapping("/gibot/product")
public class GibotProductController {

    // logger
    private static final Logger logger = Logger.getLogger(EdsUpdateByProductController.class);

    // getpost util
    private static GetPostRequestUtil getPostRequestUtil = new GetPostRequestUtil();

    private final static String TOKEN = "Wz9Fye7BfBc6BAhL";

    @Resource(name = "gibotProductMapping")
    private IProductMapping iProductMapping;

    @Resource(name = "updateStockService")
    private IUpdateStockService iUpdateStockService;

    // redis
    private static RedisService redisService = RedisService.getInstance();

    private static Map<String, Object> configMap = new HashMap<>();

    static {
        ThreadPoolExecutor gibotAllProductExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        Map<String, Object> gibotAllProduct = new HashMap<>();
        gibotAllProduct.put("url", "https://www.gibot.it/mcapi/products/");
        gibotAllProduct.put("vendor_id", "44");
        gibotAllProduct.put("page_size", "100");
        gibotAllProduct.put("threadNum", "5");
        gibotAllProduct.put("executor", gibotAllProductExecutor);
        gibotAllProduct.put("eventName", "product_all_update");
        gibotAllProduct.put("fileUtils", new ApiDataFileUtils("gibot", "product_all_update"));
        configMap.put("gibotAllProduct", gibotAllProduct);

        ThreadPoolExecutor gibotDeltaProductExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        Map<String, Object> gibotDeltaProduct = new HashMap<>();
        gibotDeltaProduct.put("url", "https://www.gibot.it/mcapi/products/");
        gibotDeltaProduct.put("vendor_id", "44");
        gibotDeltaProduct.put("page_size", "100");
        gibotDeltaProduct.put("threadNum", "5");
        gibotDeltaProduct.put("executor", gibotDeltaProductExecutor);
        gibotDeltaProduct.put("eventName", "product_delta_update");
        gibotDeltaProduct.put("path", "/mnt2/gibot/compare/");
        gibotDeltaProduct.put("origin", "origin.txt");
        gibotDeltaProduct.put("latest", "latest.txt");
        gibotDeltaProduct.put("fileUtils", new ApiDataFileUtils("gibot", "product_delta_update"));
        configMap.put("gibotDeltaProduct", gibotDeltaProduct);

    }

    @GetMapping("/sync")
    public Response syncProduct(@RequestParam(value = "name") String name) {
        if (StringUtils.isBlank(name)) {
            return Response.status(StatusCode.FAILURE).data("Parameter [name] is missing.");
        }

        Map<String, Object> config = (Map<String, Object>) configMap.get(name);

        String url = config.get("url").toString();
        String vendor_id = config.get("vendor_id").toString();
        int page_size = Integer.parseInt(config.get("page_size").toString());
        int page_n = 1;
        int threadNum = Integer.parseInt(config.get("threadNum").toString());
        String eventName = config.get("eventName").toString();
        ThreadPoolExecutor executor = (ThreadPoolExecutor) config.get("executor");
        ApiDataFileUtils fileUtils = (ApiDataFileUtils) config.get("fileUtils");

        boolean isEnd = false;
        long count = 0;

        try {
            while (!isEnd) {
                String appendUrl = url + "/?page_n=" + page_n + "&page_size=" + page_size;

                String response = GetPostRequestUtil.httpsGetRequest(appendUrl, TOKEN);
                if (StringUtils.isEmpty(response)) {
                    logger.error("Gibot sync all product error , url : " + appendUrl + " , response : " + response);
                    break;
                }
                List<Map<String, Object>> productList = JsonTransformUtil.readValue(response, List.class);
                if (productList == null) {
                    logger.error("Gibot sync all product error due to error json , url : " + appendUrl + " , response : " + response);
                    break;
                }
                if (productList.size() < page_size) {
                    isEnd = true;
                }

                fileUtils.bakPendingFile("page_n" + page_n, response);

                for (int i = 0; i < productList.size(); i++) {
                    if (!(productList.get(i) instanceof Map)) {
                        logger.warn("GibotProduct error data . " + productList.get(i).toString());
                    }
                    Map<String, Object> product = productList.get(i);
                    Map<String, Object> mqDataMap = new HashMap<>();
                    mqDataMap.put("product", product);
                    mqDataMap.put("vendor_id", vendor_id);
                    logger.info("GibotProduct mqDataMap:" + new Gson().toJson(mqDataMap) + ",eventName:" + eventName);
                    ProductEDSManagement.ProductOptions productOptions = iProductMapping.mapping(mqDataMap);

                    ProductEDSManagement.VendorOptions vendorOptions = new ProductEDSManagement.VendorOptions();
                    vendorOptions.setVendorId(Long.parseLong(vendor_id));
                    logger.info("GibotProduct,productOptions:" + new Gson().toJson(productOptions) + ",vendorOptions:" + new Gson().toJson(vendorOptions)
                            + ",eventName:" + eventName);

                    logger.info("GibotProduct,execute,startDate:" + DateUtils.getStrDate(new Date()) + ",productOptions:" + new Gson().toJson(productOptions)
                            + ",vendorOptions:" + new Gson().toJson(vendorOptions) + ",eventName:" + eventName);
                    CommonThreadPool.execute(eventName, executor, threadNum, new UpdateProductThread(productOptions, vendorOptions, fileUtils, mqDataMap));
                    logger.info("GibotProduct,execute,endDate:" + DateUtils.getStrDate(new Date()) + ",productOptions:" + new Gson().toJson(productOptions)
                            + ",vendorOptions:" + new Gson().toJson(vendorOptions) + ",eventName:" + eventName);
                    count++;
                }
                page_n++;
            }

            if (eventName.equals("product_all_update") && count > 100) {
                logger.info("GibotProduct,zeroClearing,start,vendor_id:" + vendor_id);
                iUpdateStockService.zeroClearing(Long.parseLong(vendor_id));
                logger.info("GibotProduct,zeroClearing,end,vendor_id:" + vendor_id);
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("GibotProduct,errorMessage:" + ExceptionUtils.getExceptionDetail(e));
        }

        return Response.success();
    }

    @GetMapping("/sync/delta")
    public Response syncProductDelta(@RequestParam(value = "name") String name) {
        if (StringUtils.isBlank(name)) {
            return Response.status(StatusCode.FAILURE).data("Parameter [name] is missing.");
        }

        Map<String, Object> config = (Map<String, Object>) configMap.get(name);

        String url = config.get("url").toString();
        String vendor_id = config.get("vendor_id").toString();
        int page_size = Integer.parseInt(config.get("page_size").toString());
        int page_n = 1;
        int threadNum = Integer.parseInt(config.get("threadNum").toString());
        String eventName = config.get("eventName").toString();
        ThreadPoolExecutor executor = (ThreadPoolExecutor) config.get("executor");
        ApiDataFileUtils fileUtils = (ApiDataFileUtils) config.get("fileUtils");

        boolean isEnd = false;

        List<Map<String, Object>> productList = new LinkedList<>();

        while (!isEnd) {
            String appendUrl = url + "/?page_n=" + page_n + "&page_size=" + page_size;

            String response = GetPostRequestUtil.httpsGetRequest(appendUrl, TOKEN);
            if (StringUtils.isEmpty(response)) {
                logger.error("Gibot sync all product error , url : " + appendUrl + " , response : " + response);
                break;
            }
            List<Map<String, Object>> tempProductList = JsonTransformUtil.readValue(response, List.class);
            if (tempProductList == null) {
                logger.error("Gibot sync all product error due to error json , url : " + appendUrl + " , response : " + response);
                break;
            }
            logger.info("Gibot sync all product success , url : " + appendUrl + " , response : " + response);

            if (tempProductList.size() < page_size) {
                isEnd = true;
            }

            //            isEnd = true;
            productList.addAll(tempProductList);

            page_n++;
        }

        // 获取全路径
        String origin = config.get("origin").toString();
        String latest = config.get("latest").toString();
        String path = config.get("path").toString();
        String allProductString = JsonTransformUtil.toJson(productList);

        fileUtils.bakPendingFile("all_product", allProductString);

        String diffContent = converString(productList);

        FileUtil.createFileByType(path, latest, diffContent);

        // 如果第一次进来存一份原始文件
        if (!FileUtil.fileExists(path + origin)) {
            FileUtil.createFileByType(path, origin, diffContent);
        }

        try {
            List<DiffRow> diffRows = FileUtil.CompareTxtByType(path + origin, path + latest);
            for (DiffRow diffRow : diffRows) {
                DiffRow.Tag tag = diffRow.getTag();
                String product = "";
                if (tag == DiffRow.Tag.INSERT || tag == DiffRow.Tag.CHANGE) {
                    logger.info("MatgentoController,insertandchange,diffRow:" + JSONObject.toJSONString(diffRow));
                    product = diffRow.getNewLine().replace("<br>", "");
                } else if (tag == DiffRow.Tag.DELETE) {
                    logger.info("MatgentoController,delete,diffRow:" + JSONObject.toJSONString(diffRow));
                    product = diffRow.getOldLine().replace("<br>", "");
                }

                if (StringUtils.isNotBlank(product)) {
                    Map<String, Object> mqDataMap = new HashMap<>();

                    if (tag == DiffRow.Tag.DELETE) {
                        mqDataMap.put("stock", "0");
                    }
                    mqDataMap.put("product", JsonTransformUtil.readValue(product, Map.class));
                    mqDataMap.put("vendor_id", vendor_id);

                    ProductEDSManagement.ProductOptions productOptions = iProductMapping.mapping(mqDataMap);
                    ProductEDSManagement.VendorOptions vendorOptions = new ProductEDSManagement.VendorOptions();
                    vendorOptions.setVendorId(Long.parseLong(vendor_id));

                    logger.info(
                            "GibotProduct delta,execute,startDate:" + DateUtils.getStrDate(new Date()) + ",productOptions:" + new Gson().toJson(productOptions)
                                    + ",vendorOptions:" + new Gson().toJson(vendorOptions));
                    CommonThreadPool.execute(eventName, executor, threadNum, new UpdateProductThread(productOptions, vendorOptions, fileUtils, mqDataMap));
                    logger.info(
                            "GibotProduct delta,execute,endDate:" + DateUtils.getStrDate(new Date()) + ",productOptions:" + new Gson().toJson(productOptions)
                                    + ",vendorOptions:" + new Gson().toJson(vendorOptions));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 结束后，用最新文件覆盖原始文件，准备下次更新
        FileUtil.createFileByType(path, origin, diffContent);

        return Response.success();
    }

    private static String converString(List<Map<String, Object>> list) {
        StringBuilder sb = new StringBuilder();

        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (!(list.get(i) instanceof Map)) {
                    continue;
                }
                sb.append(JsonTransformUtil.toJson(list.get(i))).append("\n");
            }

        }
        return sb.toString();
    }

    //    public static void main(String argv[]) throws IOException {
    //        File file = new File("C:\\mnt2\\gibot\\backup\\product_delta_update\\20180212\\all_product20180212161632_1518423392396189527.txt");
    //        InputStream fileInputStream = new FileInputStream(file);
    //        String content = IOUtil.streamToString(fileInputStream);
    //        List<Map<String, Object>> tempProductList = JsonTransformUtil.readValue(content, List.class);
    //        converString(tempProductList);
    //
    //    }

}
