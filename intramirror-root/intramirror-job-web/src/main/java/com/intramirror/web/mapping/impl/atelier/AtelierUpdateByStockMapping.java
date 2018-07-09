package com.intramirror.web.mapping.impl.atelier;

import com.alibaba.fastjson15.JSONObject;
import com.google.gson.Gson;
import com.intramirror.web.mapping.api.IStockMapping;
import com.intramirror.web.mapping.vo.StockOption;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import pk.shoplus.util.ExceptionUtils;

import java.util.Date;
import java.util.Map;

/**
 * Created by dingyifan on 2017/9/19.
 */
@Service(value = "atelierUpdateByStockMapping")
public class AtelierUpdateByStockMapping implements IStockMapping{

    // logger
    private static final Logger logger = Logger.getLogger(AtelierUpdateByStockMapping.class);

    @Override
    public StockOption mapping(Map<String, Object> bodyDataMap) {
        logger.info("AtelierUpdateByStockMapping,inputParams,bodyDataMap:"+JSONObject.toJSONString(bodyDataMap));
        StockOption stockOption = new StockOption();
        try {
            // get data
            JSONObject sku = (JSONObject) bodyDataMap.get("sku");
            String vendor_id = bodyDataMap.get("vendor_id").toString();
            String type = bodyDataMap.get("type").toString();
            String boutique_id = sku.getString("boutique_id");
            String size = sku.getString("size");
            String stock = sku.getString("stock");

            // set data
            stockOption.setVendor_id(vendor_id);
            stockOption.setQuantity(stock);
            stockOption.setType(type);
            stockOption.setProductCode(boutique_id);
            stockOption.setSizeValue(size);
            stockOption.setLast_check(new Date());
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("AtelierUpdateByStockMapping,errorMessage:"+ ExceptionUtils.getExceptionDetail(e));
        }
        logger.info("AtelierUpdateByStockMapping,outputParams,bodyDataMap:"+JSONObject.toJSONString(bodyDataMap));
        return stockOption;
    }
}
