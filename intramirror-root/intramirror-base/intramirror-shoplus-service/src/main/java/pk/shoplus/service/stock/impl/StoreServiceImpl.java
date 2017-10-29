package pk.shoplus.service.stock.impl;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.sql2o.Connection;
import pk.shoplus.DBConnector;
import pk.shoplus.common.Contants;
import pk.shoplus.model.SkuStore;
import pk.shoplus.service.CategoryService;
import pk.shoplus.service.SkuPropertyService;
import pk.shoplus.service.SkuStoreService;
import pk.shoplus.service.stock.api.IStoreService;
import pk.shoplus.util.ExceptionUtils;
import pk.shoplus.vo.ResultMessage;

/**
 * Created by dingyifan on 2017/6/9.
 */
public class StoreServiceImpl implements IStoreService{

    private static Logger logger = Logger.getLogger(StoreServiceImpl.class);

    @Override
    public ResultMessage handleApiStockRule(int qtyType,int qtyDiff, String size, String productCode,String queueNameEnum) throws Exception {
        logger.info("StoreServiceImplHandleApiStockRule,inputParams,qtyType :"+qtyType+",qtyDiff :"+qtyDiff+",size :"+size+",productCode :"+productCode+",queueNameEnum :"+queueNameEnum);
        Connection conn = null;
        try {
            conn = DBConnector.sql2o.beginTransaction();
            ResultMessage resultMessage = this.handle(conn,qtyType,qtyDiff,size,productCode,queueNameEnum);
            if(conn != null) {conn.close();}
            return resultMessage;
        } catch (Exception e) {
            if(conn != null) {conn.close();}
            throw e;
        }
    }

    @Override
    public ResultMessage handleApiStockRuleService(Connection conn, int qtyType, int qtyDiff, String size, String productCode, String queueNameEnum) throws Exception {
        return this.handle(conn,qtyType,qtyDiff,size,productCode,queueNameEnum);
    }

    private ResultMessage handle(Connection conn,int qtyType,int qtyDiff, String size, String productCode,String queueNameEnum) throws Exception{
        ResultMessage resultMessage = new ResultMessage();
        try {
            int store = 0,reserved = 0, rs = 0;
            Long confirmed = 0l;
            long sku_store_id = 0;
            Long skuId = 0l;
            Date last_check = null;

            if(StringUtils.isBlank(queueNameEnum)) {
                return resultMessage.sStatus(false).sMsg("handleApiStockRule 枚举类型为空。size : "+size+",productCode :"+productCode+",queueNameEnum :"+queueNameEnum);
            }

            String vendor_id = this.getVendor_id(queueNameEnum,conn);

            // checked
            if(StringUtils.isBlank(size) || StringUtils.isBlank(productCode)) {
                logger.info("StoreServiceImplHandleApiStockRule,inputParamsIsNull,size:"+size+",productCode:"+productCode);
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
                logger.info("sku: "+skuStore.getSku_id()+" 用户 System 操作系统同步,买手店 "+vendor_id+" 扣减库存前库存情况 : "+ JSON.toJSONString(skuStore));

                store = skuStore.getStore() == null ? 0 : skuStore.getStore().intValue();
                reserved = skuStore.getReserved() == null ? 0 : skuStore.getReserved().intValue();
                confirmed = skuStore.getConfirmed() == null ? 0l : skuStore.getConfirmed();
                sku_store_id = skuStore.getSku_store_id();
                skuId = skuStore.getSku_id();
                last_check = skuStore.getLast_check();
            } else {
                logger.info("StoreServiceImplHandleApiStockRule,skuInfoIsNull,size:"+size+",productCode:"+productCode);
                SkuStore skuStore = new SkuStore();
                skuStore.store = Long.valueOf(qtyDiff);
                skuStore.reserved = 0L;
                skuStore.confirmed = 0L;
                skuStore.sku_store_id = sku_store_id;
                logger.info("StoreServiceImplHandleApiStockRule,skuInfoIsNull,skuStore:"+new Gson().toJson(skuStore));
                resultMessage.sStatus(true).sMsg("SUCCESS").sData(skuStore).setDesc(vendor_id);
                logger.info("StoreServiceImplHandleApiStockRule,resultMessage:"+new Gson().toJson(resultMessage));
                return resultMessage;
            }


            // get qty_diff
            if(Contants.STOCK_QTY == qtyType) {
                logger.info("sku: "+JSON.toJSONString(skuId)+" 用户 System 操作系统同步,买手店 "+vendor_id+" 系统库存值 : "+ JSON.toJSONString(qtyDiff));
                rs = qtyDiff - (store + reserved + confirmed.intValue());
            } else if(Contants.STOCK_QTY_DIFF == qtyType) {
                rs = qtyDiff;
            } else {
                return  resultMessage.sStatus(false).sMsg("handleApiStockRule 入参qtyType有误。qtyType:"+qtyType);
            }
            logger.info("sku: "+JSON.toJSONString(skuId)+" 用户 System 操作系统同步,买手店 "+vendor_id+" 扣减库存的差异值 : "+ JSON.toJSONString(rs));

            // 开始计算
            if(rs >= 0) {
                logger.info("sku: "+JSON.toJSONString(skuId)+" 用户 System 操作系统同步,买手店 "+vendor_id+" 扣减库存的rs(差异值)>=0时执行 store = store + rs");
                store = store + rs;
            } else {
                if (confirmed > 0) {
                    confirmed = confirmed + rs;
                    logger.info("sku: "+JSON.toJSONString(skuId)+" 用户 System 操作系统同步,买手店 "+vendor_id+" 扣减库存的rs(差异值)<0 && confirmed > 0时执行 confirmed = confirmed + rs");
                    if (confirmed < 0) {
                        store = store + confirmed.intValue();
                        confirmed = 0l;
                        logger.info("sku: "+JSON.toJSONString(skuId)+" 用户 System 操作系统同步,买手店 "+vendor_id+" 扣减库存的rs(差异值)<0 && confirmed > 0 && (confirmed + rs) <0时执行 store = store + confirmed;confirmed = 0");
                    }
                } else {
                    store = store + rs;
                    logger.info("sku: "+JSON.toJSONString(skuId)+" 用户 System 操作系统同步,买手店 "+vendor_id+" 扣减库存的rs(差异值)<0 && confirmed <0 时执行 store = store + rs");
                }
            }

            SkuStore skuStore = new SkuStore();
            skuStore.store = Long.valueOf(store);
            skuStore.reserved = Long.valueOf(reserved);
            skuStore.confirmed = confirmed;
            skuStore.sku_store_id = sku_store_id;
            skuStore.setLast_check(last_check);
            logger.info("StoreServiceImplHandleApiStockRule,outputParams,skuStore:"+new Gson().toJson(skuStore));
            resultMessage.sStatus(true).sMsg("SUCCESS").sData(skuStore).setDesc(vendor_id);
            logger.info("StoreServiceImplHandleApiStockRule,resultMessage:"+new Gson().toJson(resultMessage));
            return resultMessage;
        } catch (Exception e) {
            logger.error(" errorMessage  : " + e.getMessage());
            throw e;
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
