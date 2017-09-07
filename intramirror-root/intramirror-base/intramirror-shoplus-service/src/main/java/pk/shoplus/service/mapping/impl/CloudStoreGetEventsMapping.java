package pk.shoplus.service.mapping.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.sql2o.Connection;
import pk.shoplus.DBConnector;
import pk.shoplus.common.Contants;
import pk.shoplus.model.ProductStockEDSManagement;
import pk.shoplus.model.SkuStore;
import pk.shoplus.parameter.StatusType;
import pk.shoplus.service.mapping.api.IMapping;
import pk.shoplus.service.stock.api.IStoreService;
import pk.shoplus.service.stock.impl.StoreServiceImpl;
import pk.shoplus.util.ExceptionUtils;
import pk.shoplus.vo.ResultMessage;

import java.util.HashMap;
import java.util.Map;

import static pk.shoplus.enums.ApiErrorTypeEnum.errorType.handle_stock_rule_error;
import static pk.shoplus.enums.ApiErrorTypeEnum.errorType.Runtime_exception;

/**
 * Created by dingyifan on 2017/6/7.
 */
public class CloudStoreGetEventsMapping implements IMapping{

    private static Logger logger = Logger.getLogger(CloudStoreGetEventsMapping.class);

    @Override
    public Map<String, Object> handleMappingAndExecute(String mqData,String queueNameEnum) {
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("status", StatusType.FAILURE);

        ProductStockEDSManagement productStockEDSManagement = new ProductStockEDSManagement();
        ProductStockEDSManagement.StockOptions stockOptions = productStockEDSManagement.getStockOptions();

        try {

            int qtyDiff = 0;

            // get params
            JSONObject jsonObject = JSONObject.parseObject(mqData);
            int type = jsonObject.getInteger("type");

            JSONObject additionalInfo = jsonObject.getJSONObject("additional_info");
            String sku = additionalInfo.getString("sku");

            String productCode = sku == null ? "" : sku.substring(0,sku.indexOf("-"));
            String size = sku == null ?  "" : sku.substring(sku.indexOf("-")+1,sku.length());

            if(type == Contants.EVENTS_TYPE_4) {
                qtyDiff = additionalInfo.getInteger("qty");
            } else if(type == Contants.EVENTS_TYPE_0){
                qtyDiff = additionalInfo.getInteger("qty_diff");
            }

            // 库存计算
            IStoreService storeService = new StoreServiceImpl();
            SkuStore skuStore = new SkuStore();
            if(type == Contants.EVENTS_TYPE_0 || type == Contants.EVENTS_TYPE_4) {

                // get rs
                ResultMessage resultMessage = new ResultMessage();
                if(type == Contants.EVENTS_TYPE_4) {
                    resultMessage = storeService.handleApiStockRule(Contants.STOCK_QTY,qtyDiff,size,productCode,queueNameEnum);
                } else {
                    resultMessage  = storeService.handleApiStockRule(Contants.STOCK_QTY_DIFF,qtyDiff,size,productCode,queueNameEnum);
                }

                if(resultMessage.getStatus()) {
                    skuStore = (SkuStore) resultMessage.getData();
                } else {
                    resultMap.put("info",resultMessage.getMsg());
                    resultMap.put("error_enum",handle_stock_rule_error);
                    resultMap.put("product_code",productCode);
                    return resultMap;
                }

            } else if(type == Contants.EVENTS_TYPE_1){
                skuStore.store = 0L;
                stockOptions.setType(Contants.EVENTS_TYPE_1+"");
            } else {
                resultMap.put("info"," Type类型有误!!!");
                return resultMap;
            }

            // update stock
            stockOptions.setProductCode(productCode);
            stockOptions.setSizeValue(size);
            stockOptions.setQuantity(skuStore.getStore().toString());
            stockOptions.setReserved(skuStore.getReserved() == null ? "" : skuStore.getReserved().toString());

            Connection conn = null;
            try {
                conn = DBConnector.sql2o.open();
                StoreServiceImpl iStoreService = new StoreServiceImpl();
                String vendor_id = iStoreService.getVendor_id(queueNameEnum,conn);
                stockOptions.setVendor_id(vendor_id);
                if(conn != null) {conn.close();}
            } catch (Exception e) {
                if(conn != null) {conn.close();}
            } finally {
                if(conn != null) {conn.close();}
            }

            logger.info("开始cloudstore调用修改库存 stockOptions : " + new Gson().toJson(stockOptions));
            Map<String, Object> updateMap = productStockEDSManagement.updateStock(stockOptions);
            logger.info("结束cloudstore调用修改库存 updateMap : " + new Gson().toJson(updateMap)+", stockOptions : " + new Gson().toJson(stockOptions));
            updateMap.put("product_code",stockOptions.getProductCode());
            updateMap.put("sku_size",size);
            return updateMap;
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("key","exception");
            resultMap.put("value", ExceptionUtils.getExceptionDetail(e));
            resultMap.put("info","CloudStoreGetEventsMapping.handleMappingAndExecute" + ExceptionUtils.getExceptionDetail(e));
            resultMap.put("error_enum", Runtime_exception);
            logger.info("CloudStoreGetEventsMappingHandleMappingAndExecute,errorMessage:"+ExceptionUtils.getExceptionDetail(e));
        }
        return resultMap;
    }


}
