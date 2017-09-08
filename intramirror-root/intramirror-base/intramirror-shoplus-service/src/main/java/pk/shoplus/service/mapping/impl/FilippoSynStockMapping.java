package pk.shoplus.service.mapping.impl;

import com.alibaba.fastjson15.JSONObject;
import com.google.gson.Gson;
import difflib.DiffRow;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import pk.shoplus.common.Contants;
import pk.shoplus.enums.ApiErrorTypeEnum;
import pk.shoplus.model.ProductStockEDSManagement;
import pk.shoplus.model.SkuStore;
import pk.shoplus.parameter.StatusType;
import pk.shoplus.service.mapping.api.IMapping;
import pk.shoplus.service.stock.api.IStoreService;
import pk.shoplus.service.stock.impl.StoreServiceImpl;
import pk.shoplus.util.ExceptionUtils;
import pk.shoplus.util.MapUtils;
import pk.shoplus.vo.ResultMessage;

import java.util.HashMap;
import java.util.Map;

import static pk.shoplus.enums.ApiErrorTypeEnum.errorType.handle_stock_rule_error;
import static pk.shoplus.enums.ApiErrorTypeEnum.errorType.Runtime_exception;

/**
 * Created by dingyifan on 2017/7/5.
 */
public class FilippoSynStockMapping implements IMapping{

    private static Logger logger = Logger.getLogger(FilippoSynProductMapping.class);

    private static String propertyName = "ART_ID|VAR_ID|STG|BND_ID|ART|ART_VAR|ART_FAB|ART_COL|SUB_GRP_ID|REF|EUR|TG_ID|TG|QTY";

    private ProductStockEDSManagement productStockEDSManagement = new ProductStockEDSManagement();

    @Override
    public Map<String, Object> handleMappingAndExecute(String mqData,String queueNameEnum) {
        logger.info(" start FilippoSynStockMapping.handleMappingAndExecute();");
        MapUtils mapUtils = new MapUtils(new HashMap<>());
        try {
            Map<String,Object> mqDataMap = JSONObject.parseObject(mqData);
            String propertyValue = mqDataMap.get("product_data").toString();
            ProductStockEDSManagement.StockOptions stockOptions = this.handleMappingData(propertyValue);

            if(StringUtils.isBlank(stockOptions.getQuantity())) {
                return mapUtils.putData("status", StatusType.FAILURE).putData("key","stock").putData("value","null").putData("error_enum", ApiErrorTypeEnum.errorType.Data_can_not_be_null).putData("info","FilippoSynStockMapping.handleMappingAndExecute() quantity is null !!!").getMap();
            } else {
                IStoreService storeService = new StoreServiceImpl();
                logger.info("FilippoSynStockMappingHandleMappingAndExecute,covertStock,stockOptions:"+new Gson().toJson(stockOptions));
                Double doubleStock = Double.parseDouble(stockOptions.getQuantity());
                int qty = doubleStock.intValue();
                logger.info("FilippoSynStockMappingHandleMappingAndExecute,covertStock,qty:"+qty);
                ResultMessage resultMessage = storeService.handleApiStockRule(Contants.STOCK_QTY,qty,stockOptions.getSizeValue(),stockOptions.getProductCode(),queueNameEnum);
                if(resultMessage.getStatus()) {
                    SkuStore skuStore = (SkuStore) resultMessage.getData();
                    stockOptions.setQuantity(skuStore.getStore().toString());
                    stockOptions.setVendor_id(resultMessage.getDesc());
                    stockOptions.setReserved(skuStore.getReserved().toString());
                } else {
                    return mapUtils.putData("status",StatusType.FAILURE)
                            .putData("error_enum",handle_stock_rule_error)
                            .putData("product_code",stockOptions.getProductCode())
                            .putData("key","size")
                            .putData("value",stockOptions.getSizeValue())
                            .putData("size",stockOptions.getSizeValue())
                            .putData("info",resultMessage.getMsg()).getMap();
                }

                logger.info("开始调用filippo库存更新Service,stockOptions:"+new Gson().toJson(stockOptions));
                Map<String, Object> serviceResultMap = productStockEDSManagement.updateStock(stockOptions);
                logger.info("结束调用filippo库存更新Service,serviceResultMap:"+new Gson().toJson(serviceResultMap)+",stockOptions:"+new Gson().toJson(stockOptions));
                serviceResultMap.put("product_code",stockOptions.getProductCode());
                serviceResultMap.put("size",stockOptions.getSizeValue());
                return serviceResultMap;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(" error message : " + e.getMessage());
            mapUtils.putData("key","exception");
            mapUtils.putData("value",ExceptionUtils.getExceptionDetail(e));
            mapUtils.putData("error_enum", Runtime_exception);
            mapUtils.putData("status", StatusType.FAILURE).putData("info","FilippoSynStockMapping error message : " + ExceptionUtils.getExceptionDetail(e));
            logger.info("FilippoSynStockMappingHandleMappingAndExecute,errorMessage:"+ExceptionUtils.getExceptionDetail(e)+",mqData:"+mqData);
        }
        logger.info(" end FilippoSynStockMapping.handleMappingAndExecute();");
        return mapUtils.getMap();
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
