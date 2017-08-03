package pk.shoplus.service.mapping.impl;

import com.alibaba.fastjson15.JSONObject;
import com.google.gson.Gson;
import difflib.DiffRow;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.sql2o.Connection;
import pk.shoplus.DBConnector;
import pk.shoplus.common.Contants;
import pk.shoplus.model.ProductEDSManagement;
import pk.shoplus.model.ProductStockEDSManagement;
import pk.shoplus.model.SkuStore;
import pk.shoplus.parameter.StatusType;
import pk.shoplus.service.MappingCategoryService;
import pk.shoplus.service.mapping.api.IMapping;
import pk.shoplus.service.product.api.IProductService;
import pk.shoplus.service.product.impl.ProductServiceImpl;
import pk.shoplus.service.stock.api.IStoreService;
import pk.shoplus.service.stock.impl.StoreServiceImpl;
import pk.shoplus.thirdpart.easypay.Status;
import pk.shoplus.util.MapUtils;
import pk.shoplus.vo.ResultMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dingyifan on 2017/7/5.
 */
public class FilippoSynStockMapping implements IMapping{

    private static Logger logger = Logger.getLogger(FilippoSynProductMapping.class);

    private static String propertyName = "ART_ID|VAR_ID|STG|BND_ID|ART|ART_VAR|ART_FAB|ART_COL|SUB_GRP_ID|REF|EUR|TG_ID|TG|QTY";

    private ProductStockEDSManagement productStockEDSManagement = new ProductStockEDSManagement();

    @Override
    public Map<String, Object> handleMappingAndExecute(String mqData) {
        logger.info(" start FilippoSynStockMapping.handleMappingAndExecute();");
        MapUtils mapUtils = new MapUtils(new HashMap<>());
        try {
            Map<String,Object> mqDataMap = JSONObject.parseObject(mqData);
            String propertyValue = mqDataMap.get("product_data").toString();
            ProductStockEDSManagement.StockOptions stockOptions = this.handleMappingData(propertyValue);

            if(StringUtils.isBlank(stockOptions.getQuantity())) {
                return mapUtils.putData("status", StatusType.FAILURE).putData("info","FilippoSynStockMapping.handleMappingAndExecute() quantity is null !!!").getMap();
            } else {
                IStoreService storeService = new StoreServiceImpl();
                int qty = Integer.parseInt(stockOptions.getQuantity());
                ResultMessage resultMessage = storeService.handleApiStockRule(Contants.STOCK_QTY,qty,stockOptions.getSizeValue(),stockOptions.getProductCode());
                if(resultMessage.getStatus()) {
                    SkuStore skuStore = (SkuStore) resultMessage.getData();
                    stockOptions.setQuantity(skuStore.getStore().toString());
                    stockOptions.setReserved(skuStore.getReserved().toString());
                } else {
                    return mapUtils.putData("status",StatusType.FAILURE).putData("info",resultMessage.getMsg()).getMap();
                }
                logger.info("开始调用filippo库存更新Service,stockOptions:"+new Gson().toJson(stockOptions));
                Map<String, Object> serviceResultMap = productStockEDSManagement.updateStock(stockOptions);
                logger.info("结束调用filippo库存更新Service,serviceResultMap:"+new Gson().toJson(serviceResultMap));

                return serviceResultMap;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(" error message : " + e.getMessage());
            mapUtils.putData("status", StatusType.FAILURE).putData("info","FilippoSynStockMapping error message : " + e.getMessage());
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
            throw e;
        }
        return stockOptions;
    }
}
