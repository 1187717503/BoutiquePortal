package com.intramirror.web.mapping.impl.atelier;

import com.intramirror.web.mapping.api.IStockMapping;
import com.intramirror.web.mapping.vo.StockContants;
import com.intramirror.web.mapping.vo.StockOption;
import java.util.Date;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by dingyifan on 2017/9/19.
 */
@Service(value = "atelierStockMapping")
public class AtelierStockMapping implements IStockMapping {

    private static final Logger LOGGER = LoggerFactory.getLogger(AtelierStockMapping.class);

    @Override
    public StockOption mapping(Map<String, Object> bodyDataMap) {
        StockOption stockOption = new StockOption();
        try {
            // get data

            String vendor_id = getString(bodyDataMap, "vendor_id");
            String boutique_id = getString(bodyDataMap, "boutique_id");
            String size = getString(bodyDataMap, "size");
            String stock = getString(bodyDataMap, "stock");

            // set data
            stockOption.setVendor_id(vendor_id);
            stockOption.setQuantity(stock);
            stockOption.setType(StockContants.absolute_qty);
            stockOption.setProductCode(boutique_id);
            stockOption.setSizeValue(size);
            stockOption.setLast_check(new Date());
        } catch (Exception e) {
            LOGGER.error("AtelierStockMapping error", e);
        }
        return stockOption;
    }

    private String getString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value == null ? null : value.toString();
    }
}
