package com.intramirror.web.test.api;

import com.intramirror.web.test.BaseJunit4Test;
import org.junit.Test;
import pk.shoplus.common.Contants;
import pk.shoplus.model.ProductEDSManagement;
import pk.shoplus.model.ProductStockEDSManagement;
import pk.shoplus.model.SkuStore;
import pk.shoplus.service.stock.api.IStoreService;
import pk.shoplus.service.stock.impl.StoreServiceImpl;
import pk.shoplus.vo.ResultMessage;

import java.util.Map;

/**
 * Created by dingyifan on 2017/9/7.
 */
public class StockTest extends BaseJunit4Test{

    private ProductStockEDSManagement productStockEDSManagement = new ProductStockEDSManagement();

    @Test
    public void test01() throws Exception {
        ProductStockEDSManagement.StockOptions stockOptions = productStockEDSManagement.getStockOptions();
        stockOptions.setProductCode("6000008507");
        stockOptions.setQuantity("");
        stockOptions.setSizeValue("M");

        IStoreService storeService = new StoreServiceImpl();
        ResultMessage resultMessage = storeService.handleApiStockRule(Contants.STOCK_QTY,100,stockOptions.getSizeValue(),stockOptions.getProductCode(),"7");
        SkuStore skuStore = (SkuStore) resultMessage.getData();

        stockOptions.setQuantity(skuStore.getStore().toString());
        stockOptions.setReserved(skuStore.getReserved().toString());
        stockOptions.setVendor_id(resultMessage.getDesc());

        Map<String, Object> serviceResultMap = productStockEDSManagement.updateStock(stockOptions);
        System.out.println(serviceResultMap);
    }

    @Test
    public void test(){
        Double doubleStock = Double.parseDouble("0.0");
        int qty = doubleStock.intValue();
        System.out.println(qty);
    }
}
