package com.intramirror.web.controller.api.service;

import com.alibaba.fastjson.JSONObject;
import com.intramirror.web.mapping.vo.StockOption;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.sql2o.Connection;
import pk.shoplus.DBConnector;
import pk.shoplus.common.Contants;
import pk.shoplus.enums.ApiErrorTypeEnum;
import pk.shoplus.model.Product;
import pk.shoplus.model.ProductSkuPropertyKey;
import pk.shoplus.model.ProductSkuPropertyValue;
import pk.shoplus.model.ShopProduct;
import pk.shoplus.model.ShopProductSku;
import pk.shoplus.model.Sku;
import pk.shoplus.model.SkuProperty;
import pk.shoplus.model.SkuStore;
import pk.shoplus.parameter.EnabledType;
import pk.shoplus.service.ProductPropertyKeyService;
import pk.shoplus.service.ProductPropertyValueService;
import pk.shoplus.service.ProductService;
import pk.shoplus.service.ShopProductService;
import pk.shoplus.service.ShopProductSkuService;
import pk.shoplus.service.SkuPropertyService;
import pk.shoplus.service.SkuService;
import pk.shoplus.service.SkuStoreService;
import pk.shoplus.service.price.api.IPriceService;
import pk.shoplus.service.price.impl.PriceServiceImpl;
import pk.shoplus.util.ExceptionUtils;

/**
 * 修改库存优化
 */
public class ApiUpdateStockSerivce {

    private static final String update_by_product = "update_by_product";

    private static final String update_by_sku = "update_by_sku";

    private static final Logger logger = Logger.getLogger(ApiUpdateStockSerivce.class);

    public Map<String,Object> updateStock(StockOption stockOption) {
        this.stockOption = stockOption;

        Map<String,Object> resultMap = new HashMap<>();
        Connection conn = null ;
        try{
            conn = DBConnector.sql2o.beginTransaction();

            /** 检查参数 */
            this.checkParams(conn);

            /** 更新stock */
            this.updateStock(conn);

            /** 新增SKU */
            this.createSku(conn);

            /** 更新价格 */
            this.updatePrice(conn);

            resultMap = ApiCommonUtils.successMap();
        } catch (UpdateException e) {
            e.printStackTrace();
            resultMap = ApiCommonUtils.errorMap(e.getErrorType(),e.getKey(),e.getValue());
        } catch (Exception e) {
            e.printStackTrace();
            resultMap = ApiCommonUtils.errorMap(ApiErrorTypeEnum.errorType.error_runtime_exception,"errorMessage",ExceptionUtils.getExceptionDetail(e));
        } finally {
            if(conn != null) {conn.commit();conn.close();}
        }
        return resultMap;
    }

    private void updatePrice(Connection conn) throws Exception {
        if(StringUtils.isBlank(stockOption.getPrice())) {
            return;
        }
        BigDecimal newPrice = new BigDecimal(stockOption.getPrice());

        Product product = this.getProduct(conn);
        if(product == null) {
            throw new UpdateException("product_code",stockOption.getProductCode(), ApiErrorTypeEnum.errorType.error_boutique_id_not_exists);
        }
        BigDecimal oldPrice = product.getMax_retail_price();
        int rs = ApiCommonUtils.ifUpdatePrice(oldPrice,newPrice);

        if(rs == 1) {
            IPriceService iPriceService = new PriceServiceImpl();
            iPriceService.synProductPriceRule(product,newPrice,conn);
        } else if(rs == 2){
            throw new UpdateException("price",stockOption.getPrice(),ApiErrorTypeEnum.errorType.error_price_out_off);
        }
    }

    private void createSku(Connection conn) throws Exception{
        if(stockOption.getUpdated_by().equals(ApiUpdateStockSerivce.update_by_product)
                && this.skuStoreMap == null){

            if(StringUtils.isBlank(stockOption.getSku_code())) {
                stockOption.setSku_code("#");
            }

            Product product = this.getProduct(conn);

            if(product == null) {
                throw new UpdateException("product_code",stockOption.getProductCode(), ApiErrorTypeEnum.errorType.error_boutique_id_not_exists);
            }

            // 1.create sku
            SkuService skuService = new SkuService(conn);
            Sku sku = new Sku();
            sku.product_id = product.getProduct_id();
            sku.size = stockOption.getSizeValue();
            sku.sku_code = stockOption.getSku_code();
            sku.name = product.getName();
            sku.coverpic = product.getCover_img();
            sku.introduction = product.getDescription();
            Map condition = new HashMap<>();
            condition.put("product_id", product.getProduct_id());
            condition.put("enabled", 1);
            List<Sku> list = skuService.getSkuListByCondition(condition);
            if (null != list && list.size() > 0) {
                Sku skuTemp = list.get(0);
                sku.im_price = skuTemp.getIm_price();
                sku.in_price = skuTemp.getIn_price();
                sku.price = skuTemp.getPrice();
            } else {
                IPriceService iPriceService = new PriceServiceImpl();
                Sku sku1 = iPriceService.getPriceByRule(product,product.getVendor_id(),product.getMax_retail_price(),conn);
                sku.im_price = sku1.getIm_price();
                sku.in_price = sku1.getIn_price();
                sku.price = product.getMax_retail_price();
            }
            sku.created_at = new Date();
            sku.updated_at = new Date();
            sku.enabled = EnabledType.USED;
            sku.last_check = new Date();
            skuService.createSku(sku);

            // 2.create sku_store
            SkuStoreService skuStoreService = new SkuStoreService(conn);
            SkuStore skuStore = new SkuStore();
            skuStore.product_id = product.getProduct_id();
            skuStore.sku_id = sku.sku_id;
            skuStore.store  = Long.valueOf(stockOption.getQuantity());
            skuStore.reserved = 0L;
            skuStore.confirmed = 0L;
            skuStore.remind = 0;
            skuStore.ordered = 0;
            skuStore.confirm = 0;
            skuStore.ship = 0;
            skuStore.finished = 0;
            skuStore.clear = 0;
            skuStore.changed = 0;
            skuStore.returned = 0;
            skuStore.created_at = new Date();
            skuStore.updated_at = new Date();
            skuStore.enabled = EnabledType.USED;
            skuStore.setLast_check(new Date());
            skuStoreService.createSkuStore(skuStore);

            // 3.select product_sku_property_key
            ProductPropertyKeyService productPropertyKeyService = new ProductPropertyKeyService(conn);
            ProductSkuPropertyKey pspk = null;
            Map<String, Object> param = new HashMap<>();
            param.put("product_id", product.getProduct_id());
            param.put("name", "Size");
            param.put("enabled", true);
            List<ProductSkuPropertyKey> keyList = productPropertyKeyService.getProductPropertyKeyListByCondition(param);
            if (null != keyList && keyList.size() > 0) {
                pspk = keyList.get(0);
            }

            // 4.create product_sku_property_value
            ProductPropertyValueService productPropertyValueService = new ProductPropertyValueService(conn);
            ProductSkuPropertyValue productSkuPropertyValue = new ProductSkuPropertyValue();
            productSkuPropertyValue.product_sku_property_key_id = pspk.getProduct_sku_property_key_id();
            productSkuPropertyValue.value = stockOption.getSizeValue();
            productSkuPropertyValue.remark = "";
            productSkuPropertyValue.created_at = new Date();
            productSkuPropertyValue.updated_at = new Date();
            productSkuPropertyValue.enabled = EnabledType.USED;
            ProductSkuPropertyValue pspv = productPropertyValueService.createProductPropertyValue(productSkuPropertyValue);

            // 5.create sku_property
            SkuPropertyService skuPropertyService = new SkuPropertyService(conn);
            SkuProperty skuProperty = new SkuProperty();
            skuProperty.sku_id = sku.getSku_id();
            skuProperty.product_sku_property_key_id = pspk.getProduct_sku_property_key_id();
            skuProperty.product_sku_property_value_id = pspv.getProduct_sku_property_value_id();
            skuProperty.name = product.getName();
            skuProperty.remark = "";
            skuProperty.created_at = new Date();
            skuProperty.updated_at = new Date();
            skuProperty.enabled = EnabledType.USED;
            skuPropertyService.createSkuProperty(skuProperty);

            // 6.如果商品在shop_product中有,添加这个SKU到SHOP
            ShopProductService shopProductService = new ShopProductService(conn);
            ShopProductSkuService shopProductSkuService = new ShopProductSkuService(conn);
            Map<String,Object> shopConditions = new HashMap<>();
            shopConditions.put("enabled",EnabledType.USED);
            shopConditions.put("product_id",product.getProduct_id());
            List<ShopProduct> shopList = shopProductService.getShopProductListByCondition(shopConditions);
            if(shopList!=null && shopList.size()>0) {
                ShopProduct shopProduct = shopList.get(0);
                ShopProductSku sps = new ShopProductSku();
                sps.created_at = new Date();
                sps.updated_at = new Date();
                sps.enabled = EnabledType.USED;
                sps.shop_product_id = shopProduct.getShop_product_id();
                sps.sku_id = sku.getSku_id();
                sps.name = sku.getName();
                sps.coverpic = sku.getCoverpic();
                sps.introduction = sku.getIntroduction();
                sps.sale_price = sku.getIm_price();
                sps.shop_id = shopProduct.getShop_id();
                shopProductSkuService.create(sps);
            }
        }

    }

    private Product getProduct(Connection conn) throws Exception {
        if(this.getuProduct() != null) {
            return this.getuProduct();
        }

        ProductService productService = new ProductService(conn);
        Map<String,Object> productConditionMap = new HashMap<>();
        productConditionMap.put("enabled", EnabledType.USED);
        productConditionMap.put("vendor_id",stockOption.getVendor_id());

        if(skuStoreMap != null) {
            productConditionMap.put("product_id",skuStoreMap.get("product_id").toString());
        } else {
            productConditionMap.put("product_code",stockOption.getProductCode());
        }

        Product product = productService.getProductByCondition(productConditionMap,"*");
        this.uProduct = product;
        return this.getuProduct();
    }

    private void updateStock(Connection conn) throws Exception{
        if(this.skuStoreMap == null || this.skuStoreMap.size() == 0) {
            return;
        }

        logger.info("ApiUpdateStockSerivce,updateStock,inputParams,skuStoreMap:"+JSONObject.toJSONString(skuStoreMap));
        SkuStoreService skuStoreService = new SkuStoreService(conn);
        int qty = Integer.parseInt(stockOption.getQuantity());
        int type = Integer.parseInt(stockOption.getType());
        int rs;
        int store = 0;
        int reserved = 0;
        int confirmed = 0;
        int sku_store_id = 0;
        store = Integer.parseInt(skuStoreMap.get("store").toString());
        reserved = Integer.parseInt(skuStoreMap.get("reserved").toString());
        confirmed = Integer.parseInt(skuStoreMap.get("confirmed").toString());
        sku_store_id = Integer.parseInt(skuStoreMap.get("sku_store_id").toString());

        if(Contants.STOCK_QTY == type) {
            rs = qty - (store + reserved + confirmed);
        } else {
            rs = qty;
        }

        if(confirmed == 0 && Contants.STOCK_QTY == type) {
            store = qty - reserved;
        } else {
            if(rs >= 0) {
                store = store + rs;
            } else {
                if(confirmed > 0) {
                    confirmed = confirmed + rs;
                    if(confirmed < 0) {
                        store = store + confirmed;
                        confirmed = 0;
                    }
                } else {
                    store = store + rs;
                }
            }
        }

        SkuStore skuStore = new SkuStore();
        skuStore.setUpdated_at(new Date());
        skuStore.setLast_check(new Date());
        skuStore.setSku_store_id((long)sku_store_id);
        skuStore.setStore((long)store);
        skuStore.setReserved((long)reserved);
        skuStore.setConfirmed((long)confirmed);
        skuStoreService.updateSkuStore(skuStore);
        logger.info("ApiUpdateStockSerivce,updateStock,outputParams,skuStore:"+JSONObject.toJSONString(skuStore));
    }

    private void checkParams(Connection conn) throws Exception {
        String quantity =  ApiCommonUtils.escape(StringUtils.trim(stockOption.getQuantity()));
        String vendorId = ApiCommonUtils.escape(StringUtils.trim(stockOption.getVendor_id()));
        String type =ApiCommonUtils.escape(StringUtils.trim( stockOption.getType()));
        String price = ApiCommonUtils.escape(StringUtils.trim(stockOption.getPrice()));
        String productCode = ApiCommonUtils.escape(StringUtils.trim(stockOption.getProductCode()));
        String skuCode = ApiCommonUtils.escape(StringUtils.trim(stockOption.getSku_code()));
        String sizeValue = ApiCommonUtils.escape(StringUtils.trim(stockOption.getSizeValue()));

        stockOption.setQuantity(quantity);
        stockOption.setVendor_id(vendorId);
        stockOption.setType(type);
        stockOption.setPrice(price);
        stockOption.setProductCode(productCode);
        stockOption.setSku_code(skuCode);
        stockOption.setSizeValue(sizeValue);

        if(StringUtils.isBlank(quantity)) {
            throw new UpdateException("store",quantity, ApiErrorTypeEnum.errorType.error_data_is_null);
        }
        try {
            int stock = Integer.parseInt(quantity);
            if(stock < 0 || stock > 100) {
                stockOption.setStore(stock);
                stockOption.setQuantity("0");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("ApiUpdateStockSerivce,checkParams,stock not parse int,errorMessage:"+ ExceptionUtils.getExceptionDetail(e)+",stockOption:"+ JSONObject.toJSONString(stockOption));
            throw new UpdateException("store",quantity,ApiErrorTypeEnum.errorType.error_data_is_not_number);
        }

        if(StringUtils.isBlank(vendorId)) {
            throw new UpdateException("vendor_id",vendorId,ApiErrorTypeEnum.errorType.error_data_is_null);
        }

        if(StringUtils.isBlank(type)) {
            throw new UpdateException("diff_type",type,ApiErrorTypeEnum.errorType.error_data_is_null);
        }

        /*if(StringUtils.isBlank(price)) {
            throw new UpdateException("price",price,ApiErrorTypeEnum.errorType.error_data_is_null);
        }*/

        if(StringUtils.isNotBlank(price)) {
            try {
                BigDecimal bPrice = new BigDecimal(price);
                if(bPrice.intValue() < 10 || bPrice.intValue() > 10000 || bPrice.intValue() == 0) {
                    throw new UpdateException("price",price,ApiErrorTypeEnum.errorType.error_price_out_off);
                }
            } catch (UpdateException e) {
                e.printStackTrace();
                throw new UpdateException("price",price,ApiErrorTypeEnum.errorType.error_price_out_off);
            } catch (Exception e) {
                e.printStackTrace();
                logger.info("ApiUpdateStockSerivce,checkParams,price not parse int,errorMessage:"+ ExceptionUtils.getExceptionDetail(e)+",stockOption:"+JSONObject.toJSONString(stockOption));
                throw new UpdateException("price",price,ApiErrorTypeEnum.errorType.error_data_is_not_number);
            }
        }

        if((StringUtils.isNotBlank(productCode) && StringUtils.isNotBlank(sizeValue)) || (StringUtils.isNotBlank(skuCode))) {
            if(StringUtils.isNotBlank(productCode) && StringUtils.isNotBlank(sizeValue)) {
                stockOption.setUpdated_by(ApiUpdateStockSerivce.update_by_product);
            } else {
                stockOption.setUpdated_by(ApiUpdateStockSerivce.update_by_sku);
            }

            /** 查询SKU信息 */
            SkuStoreService skuStoreService = new SkuStoreService(conn);
            List<Map<String,Object>>  skuStoreMapList = null ;
            if(stockOption.getUpdated_by().equals(ApiUpdateStockSerivce.update_by_product)) {
                skuStoreMapList =skuStoreService.api_select_store(productCode,sizeValue,vendorId);
            } else {
                skuStoreMapList =skuStoreService.api_select_store(skuCode,vendorId);
            }

            if(skuStoreMapList == null || skuStoreMapList.size() == 0) {
                if(stockOption.getUpdated_by().equals(ApiUpdateStockSerivce.update_by_sku)) {
                    throw new UpdateException("size",sizeValue,ApiErrorTypeEnum.errorType.error_size_not_exists);
                }
            }else{
                this.skuStoreMap = skuStoreMapList.get(0);
            }

            return;
        }

        if(StringUtils.isBlank(productCode)) {
            throw new UpdateException("product_code",productCode,ApiErrorTypeEnum.errorType.error_data_is_null);
        }

        if(StringUtils.isBlank(sizeValue)) {
            throw new UpdateException("size",sizeValue,ApiErrorTypeEnum.errorType.error_data_is_null);
        }

        if(StringUtils.isBlank(skuCode)) {
            throw new UpdateException("sku_code",skuCode,ApiErrorTypeEnum.errorType.error_data_is_null);
        }


    }

    private StockOption stockOption;

    private Map<String,Object> skuStoreMap;

    private Product uProduct;

    public StockOption getStockOption() {
        return stockOption;
    }

    public void setStockOption(StockOption stockOption) {
        this.stockOption = stockOption;
    }

    public Map<String, Object> getSkuStoreMap() {
        return skuStoreMap;
    }

    public void setSkuStoreMap(Map<String, Object> skuStoreMap) {
        this.skuStoreMap = skuStoreMap;
    }

    public Product getuProduct() {
        return uProduct;
    }

}
