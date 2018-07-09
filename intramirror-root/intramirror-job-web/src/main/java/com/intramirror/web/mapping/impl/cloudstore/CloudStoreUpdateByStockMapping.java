package com.intramirror.web.mapping.impl.cloudstore;

import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.intramirror.web.mapping.api.IStockMapping;
import com.intramirror.web.mapping.vo.StockContants;
import com.intramirror.web.mapping.vo.StockOption;

import pk.shoplus.common.Contants;
import pk.shoplus.util.ExceptionUtils;

@Service(value = "cloudStoreUpdateByStockMapping")
public class CloudStoreUpdateByStockMapping implements IStockMapping{

    // logger
    private final static Logger logger = Logger.getLogger(CloudStoreUpdateByStockMapping.class);

    @Override
    public StockOption mapping(Map<String, Object> bodyDataMap) {
    	logger.info("CloudStoreGetEventsMappingHandleMappingAndExecute,mqData:"+bodyDataMap);
    	StockOption stockOption = new StockOption();
        try {
            int qtyDiff = 0;
            // get params
            JSONObject jsonObject = JSONObject.parseObject(bodyDataMap.toString());
            int type = jsonObject.getInteger("type");

            JSONObject additionalInfo = jsonObject.getJSONObject("additional_info");
            String sku = additionalInfo.getString("sku");

            String productCode = sku == null ? "" : sku.substring(0,sku.indexOf("-"));
            String size = sku == null ?  "" : sku.substring(sku.indexOf("-")+1,sku.length());

            if(type == Contants.EVENTS_TYPE_4) {
                qtyDiff = additionalInfo.getInteger("qty");
                String stock_price = additionalInfo.getString("stock_price");
                stockOption.setPrice(stock_price);
            } else if(type == Contants.EVENTS_TYPE_0){
                qtyDiff = additionalInfo.getInteger("qty_diff");
            }
            // 库存计算
            if(type == Contants.EVENTS_TYPE_0 || type == Contants.EVENTS_TYPE_4) {
                if(type == Contants.EVENTS_TYPE_4) {
                	stockOption.setType(StockContants.absolute_qty); // 库存绝对值
                } else {
                	stockOption.setType(StockContants.diff_qty); // 库存绝对值
                }

            } else if(type == Contants.EVENTS_TYPE_1){
            	stockOption.setType(StockContants.absolute_qty); // 库存绝对值
            } else {
                logger.info(" Type类型有误!!!");
                return stockOption;
            }
            stockOption.setProductCode(productCode);
            stockOption.setSizeValue(size);
            stockOption.setQuantity(qtyDiff+"");
            stockOption.setLast_check(new Date());
            logger.info("CloudStoreGetEventsMappingHandleMapping stockOptions : " + new Gson().toJson(stockOption));
            return stockOption;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("CloudStoreGetEventsMappingHandleMappingAndExecute,errorMessage:"+ExceptionUtils.getExceptionDetail(e)+",mqData:"+bodyDataMap);
        }
        return stockOption;
    }
}
