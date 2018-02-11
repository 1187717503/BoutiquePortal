package com.intramirror.web.mapping.impl.prestashop;

import com.alibaba.fastjson.JSONObject;
import com.intramirror.web.mapping.api.IStockMapping;
import com.intramirror.web.mapping.vo.StockContants;
import com.intramirror.web.mapping.vo.StockOption;
import java.util.Date;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class PrestashopStockMapping implements IStockMapping {

    private static final Logger logger = Logger.getLogger(PrestashopProductMapping.class);

    @Override
    public StockOption mapping(Map<String, Object> bodyDataMap) {
        StockOption stockOption = new StockOption();
        try {
            stockOption.setProductCode(bodyDataMap.get("id_product").toString());
            stockOption.setSizeValue(bodyDataMap.get("size").toString());
            stockOption.setQuantity(bodyDataMap.get("quantity").toString());
            stockOption.setVendor_id(bodyDataMap.get("vendor_id").toString());
            stockOption.setType(StockContants.absolute_qty); // 库存绝对值
            stockOption.setLast_check(new Date());
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("PrestashopStockMapping,ErrorMessage:e->" + e + ",bodyDataMap:" + JSONObject.toJSONString(bodyDataMap));
        }
        return stockOption;
    }
}
