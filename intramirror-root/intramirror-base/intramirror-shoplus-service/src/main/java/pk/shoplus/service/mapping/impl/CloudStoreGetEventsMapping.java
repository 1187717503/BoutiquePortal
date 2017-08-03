package pk.shoplus.service.mapping.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.sql2o.Connection;
import pk.shoplus.DBConnector;
import pk.shoplus.common.Contants;
import pk.shoplus.model.ProductStockEDSManagement;
import pk.shoplus.model.SkuStore;
import pk.shoplus.mq.enums.QueueNameEnum;
import pk.shoplus.mq.enums.QueueTypeEnum;
import pk.shoplus.parameter.StatusType;
import pk.shoplus.service.SkuPropertyService;
import pk.shoplus.service.mapping.api.IMapping;
import pk.shoplus.service.stock.api.IStoreService;
import pk.shoplus.service.stock.impl.StoreServiceImpl;
import pk.shoplus.vo.ResultMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dingyifan on 2017/6/7.
 */
public class CloudStoreGetEventsMapping implements IMapping{

    @Override
    public Map<String, Object> handleMappingAndExecute(String mqData) {
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
                    resultMessage = storeService.handleApiStockRule(Contants.STOCK_QTY,qtyDiff,size,productCode);
                } else {
                    resultMessage  = storeService.handleApiStockRule(Contants.STOCK_QTY_DIFF,qtyDiff,size,productCode);
                }

                if(resultMessage.getStatus()) {
                    skuStore = (SkuStore) resultMessage.getData();
                } else {
                    resultMap.put("info",resultMessage.getMsg());
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
            Map<String, Object> updateMap = productStockEDSManagement.updateStock(stockOptions);
            if(updateMap != null && updateMap.get("status").toString().equals(StatusType.SUCCESS+"")) {
                return updateMap;
            } else {
                resultMap.put("info",new Gson().toJson(updateMap));
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("info","CloudStoreGetEventsMapping.handleMappingAndExecute" + e.getMessage());
        }
        return resultMap;
    }


}
