package com.intramirror.web.mapping.impl.gibot;

import com.alibaba.fastjson15.JSONObject;
import com.intramirror.web.mapping.api.IStockMapping;
import com.intramirror.web.mapping.vo.StockContants;
import com.intramirror.web.mapping.vo.StockOption;
import java.util.Date;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import pk.shoplus.util.ExceptionUtils;

/**
 * Created by dingyifan on 2017/9/19.
 */
@Service(value = "gibotStockMapping")
public class GibotStockMapping implements IStockMapping {

    // logger
    private static final Logger logger = Logger.getLogger(GibotStockMapping.class);

    @Override
    public StockOption mapping(Map<String, Object> bodyDataMap) {
        logger.info("GibotStockMapping,inputParams,bodyDataMap:" + JSONObject.toJSONString(bodyDataMap));
        StockOption stockOption = new StockOption();
        try {
            // get data
            String vendor_id = bodyDataMap.get("vendor_id").toString();
            String type = StockContants.absolute_qty;
            String boutique_id = bodyDataMap.get("product_id").toString();
            String size = bodyDataMap.get("variants").toString();
            String stock = bodyDataMap.get("stock").toString();

            // set data
            stockOption.setBoutique_sku_id(bodyDataMap.get("sku_id").toString());
            stockOption.setVendor_id(vendor_id);
            stockOption.setQuantity(stock);
            stockOption.setType(type);
            stockOption.setProductCode(boutique_id);
            stockOption.setSizeValue(size);
            stockOption.setLast_check(new Date());
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("GibotStockMapping,errorMessage:" + ExceptionUtils.getExceptionDetail(e));
        }
        logger.info("GibotStockMapping,outputParams,bodyDataMap:" + JSONObject.toJSONString(bodyDataMap));
        return stockOption;
    }
}
