package pk.shoplus.service.stock.impl;

import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.sql2o.Connection;
import pk.shoplus.DBConnector;
import pk.shoplus.common.Contants;
import pk.shoplus.model.Category;
import pk.shoplus.model.SkuStore;
import pk.shoplus.service.CategoryService;
import pk.shoplus.service.SkuPropertyService;
import pk.shoplus.service.SkuStoreService;
import pk.shoplus.service.stock.api.IStoreService;
import pk.shoplus.util.ExceptionUtils;
import pk.shoplus.vo.ResultMessage;

import java.util.List;
import java.util.Map;

/**
 * Created by dingyifan on 2017/6/9.
 */
public class StoreServiceImpl implements IStoreService{

    private static Logger logger = Logger.getLogger(StoreServiceImpl.class);

    @Override
    public ResultMessage handleApiStockRule(int qtyType,int qtyDiff, String size, String productCode,String queueNameEnum) throws Exception {
        logger.info("StoreServiceImplHandleApiStockRule,inputParams,qtyType:"+qtyType+",qtyDiff:"+qtyDiff+",size:"+size+",productCode:"+productCode+",queueNameEnum:"+queueNameEnum);
        ResultMessage resultMessage = new ResultMessage();
        Connection conn = null;
        try {
        	conn = DBConnector.sql2o.open();
            int store = 0,reserved = 0,rs = 0;
            long sku_store_id = 0;

            if(StringUtils.isBlank(queueNameEnum)) {
                return resultMessage.sStatus(false).sMsg("handleApiStockRule 枚举类型为空。size:"+size+",productCode:"+productCode+",queueNameEnum:"+queueNameEnum);
            }

            String vendor_id = this.getVendor_id(queueNameEnum,conn);

            // checked
            if(StringUtils.isBlank(size) || StringUtils.isBlank(productCode)) {
                logger.info("StoreServiceImplHandleApiStockRule,inputParamsIsNull,size:"+size+",productCode:"+productCode);
                if(conn != null) {conn.close();}
                return resultMessage.sStatus(false).sMsg("handleApiStockRule size或者productCode为空。size:"+size+",productCode:"+productCode);
            }

            List<Map<String, Object>> skuStoreIdList = null;
            SkuPropertyService skuPropertyService = new SkuPropertyService(conn);
            if(StringUtils.isNotBlank(productCode) && StringUtils.isNotBlank(size)) {
                logger.info("StoreServiceImplHandleApiStockRule,getSkuPropertyListWithSizeAndProductCode,size:"+size+",productCode:"+productCode);
                skuStoreIdList = skuPropertyService.getSkuPropertyListWithSizeAndProductCode(size, productCode,vendor_id);
                logger.info("StoreServiceImplHandleApiStockRule,getSkuPropertyListWithSizeAndProductCode,skuStoreIdList:"+new Gson().toJson(skuStoreIdList));
            }

            if(skuStoreIdList != null && skuStoreIdList.size() > 0) {
                SkuStoreService skuStoreService = new SkuStoreService(conn);
                logger.info("StoreServiceImplHandleApiStockRule,getSkuStoreByID,skuStoreIdList:"+new Gson().toJson(skuStoreIdList));
                SkuStore skuStore = skuStoreService.getSkuStoreByID(skuStoreIdList.get(0).get("sku_store_id").toString());
                logger.info("StoreServiceImplHandleApiStockRule,getSkuStoreByID,skuStore:"+new Gson().toJson(skuStore));
                store = skuStore.getStore() == null ? 0 : skuStore.getStore().intValue();
                reserved = skuStore.getReserved() == null ? 0 : skuStore.getReserved().intValue();
                sku_store_id = skuStore.getSku_store_id();
            } else {
                logger.info("StoreServiceImplHandleApiStockRule,skuInfoIsNull,size:"+size+",productCode:"+productCode);
                SkuStore skuStore = new SkuStore();
                skuStore.store = Long.valueOf(qtyDiff);
                skuStore.reserved = 0L;
                skuStore.sku_store_id = sku_store_id;
                logger.info("StoreServiceImplHandleApiStockRule,skuInfoIsNull,skuStore:"+new Gson().toJson(skuStore));
                if(conn != null) {conn.close();}
                resultMessage.sStatus(true).sMsg("SUCCESS").sData(skuStore).setDesc(vendor_id);
                logger.info("StoreServiceImplHandleApiStockRule,resultMessage:"+new Gson().toJson(resultMessage));
                return resultMessage;
            }

            // get qty_diff
            if(Contants.STOCK_QTY == qtyType) {
                rs = qtyDiff - (store + reserved);
            } else if(Contants.STOCK_QTY_DIFF == qtyType) {
                rs = qtyDiff;
            } else {
                return  resultMessage.sStatus(false).sMsg("handleApiStockRule 入参qtyType有误。qtyType:"+qtyType);
            }

            // 开始计算
            if(rs >= 0) {
                store = store + rs;
            } else {
                store = store - Math.abs(rs);
            }

            if(store < 0) {
                reserved = reserved - Math.abs(store);

                if(reserved < 0) {
                    reserved = 0;
                }
                store = 0;
            }

            SkuStore skuStore = new SkuStore();
            skuStore.store = Long.valueOf(store);
            skuStore.reserved = Long.valueOf(reserved);
            skuStore.sku_store_id = sku_store_id;
            logger.info("StoreServiceImplHandleApiStockRule,outputParams,skuStore:"+new Gson().toJson(skuStore));
            if(conn != null) {conn.close();}
            resultMessage.sStatus(true).sMsg("SUCCESS").sData(skuStore).setDesc(vendor_id);
            logger.info("StoreServiceImplHandleApiStockRule,resultMessage:"+new Gson().toJson(resultMessage));
            return resultMessage;
        } catch (Exception e) {
            logger.error(" errorMessage  : " + e.getMessage());
            if(conn != null) {conn.close();}
            throw e;
        } finally {
            if(conn != null) {conn.close();}
        }
    }

    public String getVendor_id(String queueNameEnum,Connection conn) throws Exception {
        logger.info("StoreServiceImplGetVendor_id,queueNameEnum:"+queueNameEnum);
        // start 2017-09-07 特殊判断,由于product_code允许重复引起
        String vendor_id = "";
        try {
            Double.parseDouble(queueNameEnum);
            vendor_id = queueNameEnum;
        } catch (Exception e) {
//            e.printStackTrace();
            logger.info("StoreServiceImplHandleApiStockRule,errorMessage:"+ExceptionUtils.getExceptionDetail(e)+",queueNameEnum:"+queueNameEnum);
        }

        if(StringUtils.isBlank(vendor_id)) {
            String sql = "select ac.vendor_id from api_mq am\n" +
                    "                inner join api_configuration ac on(am.api_configuration_id = ac.api_configuration_id)\n" +
                    "                where am.enabled = 1 and am.enabled = 1 and am.`name` = '"+queueNameEnum+"'";
            logger.info("StoreServiceImplHandleApiStockRule,sql:"+sql);
            CategoryService categoryService = new CategoryService(conn);
            List<Map<String,Object>> maps = categoryService.executeSQL(sql);
            if(maps != null && maps.size() > 0) {
                vendor_id = maps.get(0).get("vendor_id").toString();
            }
        }
        // end 2017-09-07 特殊判断,由于product_code允许重复引起
        logger.info("StoreServiceImplGetVendor_id,vendor_id:"+vendor_id);
        return vendor_id;
    }

}
