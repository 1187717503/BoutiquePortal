package com.intramirror.web.mapping.impl.filippo;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.intramirror.web.mapping.api.IStockMapping;
import com.intramirror.web.mapping.vo.StockContants;
import com.intramirror.web.mapping.vo.StockOption;

import difflib.DiffRow;
import pk.shoplus.model.ProductStockEDSManagement;
import pk.shoplus.util.ExceptionUtils;

@Service(value = "filippoSynStockMapping")
public class FilippoSynStockMapping implements IStockMapping{

    // logger
    private final static Logger logger = Logger.getLogger(FilippoSynStockMapping.class);

    private static String propertyName = "ART_ID|VAR_ID|STG|BND_ID|ART|ART_VAR|ART_FAB|ART_COL|SUB_GRP_ID|REF|EUR|TG_ID|TG|QTY";

    private ProductStockEDSManagement productStockEDSManagement = new ProductStockEDSManagement();
    
    @Override
    public StockOption mapping(Map<String, Object> bodyDataMap) {
    	logger.info("FilippoSynStockMappingHandleMappingAndExecute,inputParams,mqData"+bodyDataMap);
    	StockOption stockOption = new StockOption();
        try {
            String propertyValue = bodyDataMap.get("product_data").toString();
            ProductStockEDSManagement.StockOptions stockOptions = this.handleMappingData(propertyValue);
            String vendor_id = bodyDataMap.get("vendor_id").toString();
            stockOption.setSizeValue(stockOptions.getSizeValue()==null?"":stockOptions.getSizeValue());
            stockOption.setProductCode(stockOptions.getProductCode()==null?"":stockOptions.getProductCode());
            stockOption.setQuantity(stockOptions.getQuantity()==null?"":stockOptions.getQuantity());
            stockOption.setVendor_id(vendor_id);
            stockOption.setType(StockContants.absolute_qty); // 库存绝对值
            logger.info("FilippoSynStockMappingHandleMappingAndExecute,result:"+JSONObject.toJSONString(stockOption));
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("CloudStoreGetEventsMappingHandleMappingAndExecute,errorMessage:"+ExceptionUtils.getExceptionDetail(e)+",mqData:"+bodyDataMap);
        }
        return stockOption;
    }
    
    public ProductStockEDSManagement.StockOptions handleMappingData(String propertyValue) throws Exception{
        if(StringUtils.isNotBlank(propertyValue) && propertyValue.contains("newLine")) {
            DiffRow diffRow = new Gson().fromJson(propertyValue, DiffRow.class);
            propertyValue = diffRow.getNewLine();
        }
        ProductStockEDSManagement.StockOptions stockOptions = productStockEDSManagement.getStockOptions();
        try {
            String[] propertyNames = propertyName.split("\\u007C");
            String[] propertyValues = propertyValue.split("\\u007C");

            for(int i = 0,iLen = propertyNames.length;i<iLen;i++) {
                String pn = propertyNames[i];
                if(StringUtils.isNotBlank(pn)) {
                    String pv = propertyValues[i];
                    pv = pv.replace("\"","");
                    pv = pv.replace("<br>","");
                    if(pn.equals("VAR_ID")) {stockOptions.setProductCode(pv);}
                    if(pn.equals("TG")) {stockOptions.setSizeValue(pv);}
                    if(pn.equals("QTY")) {stockOptions.setQuantity(pv);}
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(propertyValue);
            logger.info("FilippoSynStockMappingHandleMappingData,errorMessage:"+ExceptionUtils.getExceptionDetail(e)+",propertyValue:"+propertyValue);
            throw e;
        }
        return stockOptions;
    }
}
