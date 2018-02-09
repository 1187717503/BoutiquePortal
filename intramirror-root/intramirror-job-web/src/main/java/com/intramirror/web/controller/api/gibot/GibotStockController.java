package com.intramirror.web.controller.api.gibot;

import com.google.gson.Gson;
import com.intramirror.common.help.ExceptionUtils;
import com.intramirror.common.help.StringUtils;
import com.intramirror.common.utils.DateUtils;
import com.intramirror.core.common.response.Response;
import com.intramirror.core.common.response.StatusCode;
import com.intramirror.product.api.service.stock.IUpdateStockService;
import com.intramirror.utils.transform.JsonTransformUtil;
import com.intramirror.web.mapping.api.IStockMapping;
import com.intramirror.web.mapping.vo.StockOption;
import com.intramirror.web.thread.CommonThreadPool;
import com.intramirror.web.thread.UpdateStockThread;
import com.intramirror.web.util.ApiDataFileUtils;
import com.intramirror.web.util.GetPostRequestUtil;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import javax.annotation.Resource;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created on 2018/2/9.
 *
 * @author YouFeng.Zhu
 */
@RestController
@RequestMapping("/gibot/stock")
public class GibotStockController {
    // logger
    private static final Logger logger = Logger.getLogger(GibotStockController.class);

    // getpost util
    private static GetPostRequestUtil getPostRequestUtil = new GetPostRequestUtil();

    private final static String TOKEN = "Wz9Fye7BfBc6BAhL";

    @Resource(name = "updateStockService")
    private IUpdateStockService iUpdateStockService;

    @Resource(name = "gibotStockMapping")
    private IStockMapping iStockMapping;

    private static Map<String, Object> configMap = new HashMap<>();

    static {
        ThreadPoolExecutor gibotAllProductExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        Map<String, Object> gibotAllProduct = new HashMap<>();
        gibotAllProduct.put("url", "https://www.gibot.it/mcapi/skus/");
        gibotAllProduct.put("vendor_id", "44");
        gibotAllProduct.put("page_size", "100");
        gibotAllProduct.put("threadNum", "5");
        gibotAllProduct.put("executor", gibotAllProductExecutor);
        gibotAllProduct.put("eventName", "stock_all_update");
        gibotAllProduct.put("fileUtils", new ApiDataFileUtils("gibot", "stock_all_update"));
        configMap.put("gibotAllProduct", gibotAllProduct);

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
        int threadNum = Integer.parseInt(config.get("threadNum").toString());
        String eventName = config.get("eventName").toString();
        ThreadPoolExecutor executor = (ThreadPoolExecutor) config.get("executor");
        ApiDataFileUtils fileUtils = (ApiDataFileUtils) config.get("fileUtils");

        boolean isEnd = false;
        long count = 0;

        int page_n = 1;

        try {
            while (!isEnd) {
                String appendUrl = url + "?page_n=" + page_n + "&page_size=" + page_size;
                String response = GetPostRequestUtil.httpsGetRequest(appendUrl, TOKEN);
                if (StringUtils.isBlank(response)) {
                    logger.error("Gibot sync all stock error , url : " + appendUrl + " , response : " + response);
                    break;
                }
                List<Map<String, Object>> skuList = JsonTransformUtil.readValue(response, List.class);
                if (skuList == null) {
                    logger.error("Gibot sync all stock error due to error json , url : " + appendUrl + " , response : " + response);
                    break;
                }
                if (skuList.size() < page_size) {
                    isEnd = true;
                }

                fileUtils.bakPendingFile("page_n" + page_n, response);

                for (Map<String, Object> sku : skuList) {
                    sku.put("vendor_id", vendor_id);

                    logger.info("GibotStock,mapping,start,stockMap:" + new Gson().toJson(sku) + ",eventName:" + eventName);
                    StockOption stockOption = iStockMapping.mapping(sku);
                    logger.info("GibotStock,mapping,end,stockMap:" + new Gson().toJson(sku) + ",stockOption:" + new Gson().toJson(stockOption) + ",eventName:"
                            + eventName);

                    logger.info("GibotStock,execute,startDate:" + DateUtils.getStrDate(new Date()) + ",stockMap:" + new Gson().toJson(sku) + ",stockOption:"
                            + new Gson().toJson(stockOption) + ",eventName:" + eventName);
                    CommonThreadPool.execute(eventName, executor, threadNum, new UpdateStockThread(stockOption, fileUtils, sku));
                    logger.info("GibotStock,execute,endDate:" + DateUtils.getStrDate(new Date()) + ",stockMap:" + new Gson().toJson(sku) + ",stockOption:"
                            + new Gson().toJson(stockOption) + ",eventName:" + eventName);

                }
                page_n++;

            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("GibotStock,errorMessage:" + ExceptionUtils.getExceptionDetail(e));
        }

        return Response.success();
    }
}
