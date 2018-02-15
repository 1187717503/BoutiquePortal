package com.intramirror.web.mapping.impl.coltori;

import com.intramirror.common.help.ExceptionUtils;
import com.intramirror.web.mapping.api.IStockMapping;
import com.intramirror.web.mapping.vo.StockContants;
import com.intramirror.web.mapping.vo.StockOption;
import java.util.Date;
import java.util.Map;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 * Created by dingyifan on 2017/11/2.
 */
@Service(value = "coltoriStockMapping")
public class ColtoriStockMapping implements IStockMapping{

    private static final Logger logger = Logger.getLogger(ColtoriStockMapping.class);

    @Override
    public StockOption mapping(Map<String, Object> bodyDataMap) {
        logger.info("ColtoriStockMapping,inputParams,bodyDataMap:"+ JSONObject.fromObject(bodyDataMap));
        StockOption stockOption = new StockOption();
        try {
            stockOption.setProductCode(bodyDataMap.get("product_code").toString());
            stockOption.setQuantity(new Double(bodyDataMap.get("stock").toString()).intValue()+"");
            stockOption.setVendor_id(bodyDataMap.get("vendor_id").toString());
            stockOption.setSizeValue(bodyDataMap.get("size").toString());
            stockOption.setLast_check(new Date());
            stockOption.setSku_code(bodyDataMap.get("barcode").toString());
            stockOption.setType(StockContants.absolute_qty);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("ColtoriStockMapping,errorMessage:"+ ExceptionUtils.getExceptionDetail(e)+",bodyDataMap:"+ JSONObject.fromObject(bodyDataMap));
        }
        return stockOption;
    }
}
