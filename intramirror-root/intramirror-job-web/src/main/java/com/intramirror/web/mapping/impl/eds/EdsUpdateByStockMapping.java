package com.intramirror.web.mapping.impl.eds;

import com.google.gson.Gson;
import com.intramirror.web.mapping.api.IStockMapping;
import com.intramirror.web.mapping.vo.StockContants;
import com.intramirror.web.mapping.vo.StockOption;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import pk.shoplus.util.ExceptionUtils;

import java.util.Date;
import java.util.Map;

@Service(value = "edsUpdateByStockMapping")
public class EdsUpdateByStockMapping implements IStockMapping{

    // logger
    private final static Logger logger = Logger.getLogger(EdsUpdateByProductMapping.class);

    @Override
    public StockOption mapping(Map<String, Object> bodyDataMap) {
        logger.info("EdsUpdateByStockMapping,inputParams,bodyDataMap:"+new Gson().toJson(bodyDataMap));
        StockOption stockOption = new StockOption();
        try {
            stockOption.setProductCode(bodyDataMap.get("product_id")==null?"":bodyDataMap.get("product_id").toString());
            stockOption.setSizeValue(bodyDataMap.get("size")==null?"":bodyDataMap.get("size").toString());
            stockOption.setQuantity(bodyDataMap.get("quantity")==null?"":bodyDataMap.get("quantity").toString());
            stockOption.setVendor_id(bodyDataMap.get("vendor_id")==null?"":bodyDataMap.get("vendor_id").toString());
            stockOption.setType(StockContants.absolute_qty); // 库存绝对值
            stockOption.setLast_check(new Date());
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("EdsUpdateByStockMapping,errorMessage:"+ ExceptionUtils.getExceptionDetail(e));
        }
        logger.info("EdsUpdateByStockMapping,outputParams,stockOption:"+new Gson().toJson(stockOption));
        return stockOption;
    }
}
