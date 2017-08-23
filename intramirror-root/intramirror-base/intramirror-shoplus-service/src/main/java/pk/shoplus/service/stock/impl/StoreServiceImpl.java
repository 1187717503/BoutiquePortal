package pk.shoplus.service.stock.impl;

import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.sql2o.Connection;
import pk.shoplus.DBConnector;
import pk.shoplus.common.Contants;
import pk.shoplus.model.SkuStore;
import pk.shoplus.service.SkuPropertyService;
import pk.shoplus.service.SkuStoreService;
import pk.shoplus.service.stock.api.IStoreService;
import pk.shoplus.vo.ResultMessage;

import java.util.List;
import java.util.Map;

/**
 * Created by dingyifan on 2017/6/9.
 */
public class StoreServiceImpl implements IStoreService{

    private static Logger logger = Logger.getLogger(StoreServiceImpl.class);

    @Override
    public ResultMessage handleApiStockRule(int qtyType,int qtyDiff, String size, String productCode) throws Exception {
        ResultMessage resultMessage = new ResultMessage();
        Connection conn = null;
        try {
        	conn = DBConnector.sql2o.open();
            int store = 0;
            int reserved = 0;
            int rs = 0;
            long sku_store_id = 0;

            // checked
            if(StringUtils.isBlank(size) || StringUtils.isBlank(productCode)) {
                return resultMessage.sStatus(false).sMsg("handleApiStockRule size或者productCode为空。size:"+size+",productCode:"+productCode);
            }

            /* get sku store,reserved */
            List<Map<String, Object>> skuStoreIdList = null;
            SkuPropertyService skuPropertyService = new SkuPropertyService(conn);
            if(StringUtils.isNotBlank(productCode) && StringUtils.isNotBlank(size)) {
                skuStoreIdList = skuPropertyService.getSkuPropertyListWithSizeAndProductCode(size, productCode);
            }

            if(skuStoreIdList != null && skuStoreIdList.size() > 0) {
                SkuStoreService skuStoreService = new SkuStoreService(conn);
                SkuStore skuStore = skuStoreService.getSkuStoreByID(skuStoreIdList.get(0).get("sku_store_id").toString());
                logger.info(" updateStock before,skuStore:"+new Gson().toJson(skuStore)+",size:"+size+","+productCode);
                store = skuStore.getStore() == null ? 0 : skuStore.getStore().intValue();
                reserved = skuStore.getReserved() == null ? 0 : skuStore.getReserved().intValue();
                sku_store_id = skuStore.getSku_store_id();
            } else {
                return resultMessage.sStatus(false).sMsg("handleApiStockRule 没有查询到SKU信息。size:"+size+",productCode:"+productCode);
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
            if(conn != null) {conn.close();}
            return resultMessage.sStatus(true).sMsg("SUCCESS").sData(skuStore);
        } catch (Exception e) {
            logger.error(" errorMessage  : " + e.getMessage());
            if(conn != null) {conn.close();}
            throw e;
        } finally {
            if(conn != null) {conn.close();}
        }
    }

}
