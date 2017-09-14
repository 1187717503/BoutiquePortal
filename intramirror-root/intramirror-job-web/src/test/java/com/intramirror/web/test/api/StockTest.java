//package com.intramirror.web.test.api;
//
//import com.intramirror.common.enums.SystemPropertyEnum;
//import com.intramirror.web.test.BaseJunit4Test;
//import org.apache.commons.lang.StringUtils;
//import org.junit.Test;
//import org.sql2o.Connection;
//import pk.shoplus.DBConnector;
//import pk.shoplus.common.Contants;
//import pk.shoplus.model.ProductEDSManagement;
//import pk.shoplus.model.ProductStockEDSManagement;
//import pk.shoplus.model.SkuStore;
//import pk.shoplus.service.MappingCategoryService;
//import pk.shoplus.service.stock.api.IStoreService;
//import pk.shoplus.service.stock.impl.StoreServiceImpl;
//import pk.shoplus.vo.ResultMessage;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by dingyifan on 2017/9/7.
// */
//public class StockTest extends BaseJunit4Test{
//    private static String propertyName = "ART_ID|VAR_ID|ART_FID|STG|BND_ID|BND_NAME|ART|ART_VAR|ART_FAB|ART_COL|SR_ID|SR_DES|GRP_ID|GRP_DES|SUB_GRP_ID|SUB_GRP_DES|ART_DES|COL_ID|COL_DES|REF|EUR|TG_ID|TG|QTY|MADEIN|WV|COMP|IMGTYP|IMG";
//
//    private ProductStockEDSManagement productStockEDSManagement = new ProductStockEDSManagement();
//
//    @Test
//    public void test01() throws Exception {
//        ProductStockEDSManagement.StockOptions stockOptions = productStockEDSManagement.getStockOptions();
//        stockOptions.setProductCode("6000008507");
//        stockOptions.setQuantity("");
//        stockOptions.setSizeValue("M");
//
//        IStoreService storeService = new StoreServiceImpl();
//        ResultMessage resultMessage = storeService.handleApiStockRule(Contants.STOCK_QTY,100,stockOptions.getSizeValue(),stockOptions.getProductCode(),"7");
//        SkuStore skuStore = (SkuStore) resultMessage.getData();
//
//        stockOptions.setQuantity(skuStore.getStore().toString());
//        stockOptions.setReserved(skuStore.getReserved().toString());
//        stockOptions.setVendor_id(resultMessage.getDesc());
//
//        Map<String, Object> serviceResultMap = productStockEDSManagement.updateStock(stockOptions);
//        System.out.println(serviceResultMap);
//    }
//
//
//    @Test
//    public void test02() throws Exception {
//        ProductEDSManagement productEDSManagement = new ProductEDSManagement();
//        ProductEDSManagement.ProductOptions productOptions = productEDSManagement.getProductOptions();
//        Connection conn = null;
//        try {
//            String[] propertyNames = propertyName.split("\\u007C");
//            String[] propertyValues = "37511138|33134439|1295442|\"A17\"|30000027|\"MOSCHINO\"|\"B7611\"|\"8203\"|×|\"2440\"|2|\"D<br>onna\"|30000004|\"ACCESSORI\"|30000045|\"Borse\"|\"\"|14894|\"MILITARY GREEN\"|430|430|1|<br>\"UNI\"|\"800272469556\"|2|\"China\"|\"\"|\"90% polyamide 10% acrylic;\"|5|\"t=UE9RT1JSN2Ny<br>OFV3aExJajJET0lpcFhYb0Fjc1IxVklvTlcrRU9ZRHBuND0&i=a17%2Fmoschino%2Fb761182032440<br>_22_d.jpg\"".split("\\u007C");
//            ProductEDSManagement.SkuOptions skuOptions = productEDSManagement.getSkuOptions();
//            skuOptions.setBarcodes("#");
//            String brandID = "";
//            String firstCategory = "";
//            String secondCategory = "";
//            String threeCategory = "";
//            for(int i = 0,iLen = propertyNames.length;i<iLen;i++) {
//                String pn = propertyNames[i];
//                if(StringUtils.isNotBlank(pn)) {
//                    String pv = propertyValues[i];
//                    pv = pv.replace("\"","");
//                    pv = pv.replace("<br>","");
//                    pv = pv.replace("??","");
//                    pv = pv.replace("×","");
//                    if(pn.equals("VAR_ID")) {productOptions.setCode(pv);}
//                    else if(pn.equals("STG")) {productOptions.setSeasonCode(pv);}
//                    else if(pn.equals("BND_NAME")){productOptions.setBrandName(pv);}
//                    else if(pn.equals("ART")) {brandID = brandID + pv;}
//                    else if(pn.equals("ART_VAR")) {brandID = brandID + pv;}
//                    else if(pn.equals("ART_FAB")) {brandID = brandID + pv;}
//                    else if(pn.equals("ART_COL")) {productOptions.setColorCode(pv);}
//                    else if(pn.equals("SR_DES")) {firstCategory = pv;}
//                    else if(pn.equals("GRP_DES")) {secondCategory = pv;}
//                    else if(pn.equals("SUB_GRP_DES")) {threeCategory = pv;productOptions.setName(pv);}
//                    else if(pn.equals("REF")) {productOptions.setSalePrice(pv);}
//                    else if(pn.equals("TG_ID")) {skuOptions.setSizeid(pv);}
//                    else if(pn.equals("TG")) {skuOptions.setSize(pv);}
//                    else if(pn.equals("QTY")) {skuOptions.setStock(pv);}
//                    else if(pn.equals("MADEIN")) {productOptions.setMadeIn(pv);}
//                    else if(pn.equals("WV")) {}
//                    else if(pn.equals("COMP")) {productOptions.setComposition(pv);}
//                    else if(pn.equals("IMG")) {
//                    }
//                }
//            }
//            if(StringUtils.isNotBlank(brandID)) {productOptions.setBrandCode(brandID);}
//
//            List<ProductEDSManagement.SkuOptions> skuOptionsList = new ArrayList<>();
//            skuOptionsList.add(skuOptions);
//            productOptions.setSkus(skuOptionsList);
//        } catch (Exception e) {
//            if(conn != null) {conn.close();}
//            e.printStackTrace();
//            throw e;
//        } finally {
//            if(conn != null) {conn.close();}
//        }
//    }
//
//    @Test
//    public void test(){
//        Double doubleStock = Double.parseDouble("0.0");
//        int qty = doubleStock.intValue();
//        System.out.println(qty);
//    }
//}
