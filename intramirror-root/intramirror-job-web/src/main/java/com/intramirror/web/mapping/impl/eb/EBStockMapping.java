package com.intramirror.web.mapping.impl.eb;

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
@Service(value = "ebStockMapping")
public class EBStockMapping implements IStockMapping {

    // logger
    private static final Logger logger = Logger.getLogger(EBStockMapping.class);

    @Override
    public StockOption mapping(Map<String, Object> bodyDataMap) {
        logger.info("ebStockMapping,inputParams,bodyDataMap:" + JSONObject.toJSONString(bodyDataMap));
        StockOption stockOption = new StockOption();
        try {
            // get data
            String vendor_id = bodyDataMap.get("vendor_id").toString();
            String type = StockContants.absolute_qty;
            String productCode = bodyDataMap.get("SKU").toString();
            String size = bodyDataMap.get("Size").toString();
            String stock = bodyDataMap.get("Stock").toString();

            // set data
            stockOption.setBoutique_sku_id(bodyDataMap.get("SKU_item").toString());
            stockOption.setVendor_id(vendor_id);
            stockOption.setQuantity(stock);
            stockOption.setType(type);
            stockOption.setProductCode(productCode);
            stockOption.setSizeValue(size);
            stockOption.setLast_check(new Date());
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("ebStockMapping,errorMessage:" + ExceptionUtils.getExceptionDetail(e));
        }
        logger.info("ebStockMapping,outputParams,bodyDataMap:" + JSONObject.toJSONString(bodyDataMap));
        return stockOption;
    }
}
