package com.intramirror.web.controller.api.service;

import com.alibaba.fastjson.JSONObject;
import static com.intramirror.web.controller.api.service.ApiCommonUtils.errorMap;
import static com.intramirror.web.controller.api.service.ApiCommonUtils.escape;
import static com.intramirror.web.controller.api.service.ApiCommonUtils.ifUpdatePrice;
import static com.intramirror.web.controller.api.service.ApiCommonUtils.isErrorMap;
import static com.intramirror.web.controller.api.service.ApiCommonUtils.successMap;
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
import pk.shoplus.common.Helper;
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
 * 修改库存Service
 */
public class ApiUpdateStockSerivce {

    private static final Logger logger = Logger.getLogger(ApiUpdateStockSerivce.class);

    public Map<String,Object> execute(){
        Map<String,Object> resultMap = new HashMap<>();
        Connection conn = null;
        try {

            Map<String,Object> checkParamMap = this.checkParams();
            if(isErrorMap(checkParamMap)) {return checkParamMap;}

            conn = DBConnector.sql2o.beginTransaction();
            resultMap = this.executeStockRule(conn);
            if(conn!=null){conn.commit();conn.close();}

            if(stockOption.getStore() < 0) {
                return errorMap(ApiErrorTypeEnum.errorType.error_stock_is_negative,"store",stockOption.getStore()+"");
            }

            if(stockOption.getStore() > 100) {
                return errorMap(ApiErrorTypeEnum.errorType.error_stock_out_off,"store",stockOption.getStore()+"");
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.info("ApiUpdateStockSerivce,executeUpdateStock,errorMessage:"+ExceptionUtils.getExceptionDetail(e)+",stockOption:"+JSONObject.toJSONString(stockOption));
            if(conn!=null){conn.rollback();conn.close();}
            return errorMap(ApiErrorTypeEnum.errorType.error_runtime_exception,"errorMessage",ExceptionUtils.getExceptionDetail(e));
        }
        return resultMap;
    }

    public Map<String,Object> executeStockRule(Connection conn) throws Exception{
        SkuStoreService skuStoreService = new SkuStoreService(conn);
        List<Map<String,Object>> skuStoreMap = null;
        if(StringUtils.isNotBlank(stockOption.getProductCode())) {
            skuStoreMap =skuStoreService.api_select_store(stockOption.getProductCode(),stockOption.getSizeValue(),stockOption.getVendor_id());
        } else {
            skuStoreMap =skuStoreService.api_select_store(stockOption.getSku_code(),stockOption.getVendor_id());
        }
        logger.info("ApiUpdateStockSerivce,selectSkuStore,api_select_store,skuStoreMap:"+JSONObject.toJSONString(skuStoreMap));

        if(skuStoreMap == null || skuStoreMap.size() == 0) {
            if(StringUtils.isBlank(stockOption.getProductCode()) || StringUtils.isBlank(stockOption.getSizeValue())) {
                return errorMap(ApiErrorTypeEnum.errorType.error_size_is_null,"size",stockOption.getSizeValue());
            } else {
                Map<String,Object> createSkuResult = this.executeCreateSku(conn);
                if(isErrorMap(createSkuResult)) {
                    return createSkuResult;
                }
            }
        } else { // 处理库存规则
            int qty = Integer.parseInt(stockOption.getQuantity());
            int type = Integer.parseInt(stockOption.getType());
            this.productId = skuStoreMap.get(0).get("product_id").toString();
            int rs;
            int store = 0;
            int reserved = 0;
            int confirmed = 0;
            int sku_store_id = 0;
            store = Integer.parseInt(skuStoreMap.get(0).get("store").toString());
            reserved = Integer.parseInt(skuStoreMap.get(0).get("reserved").toString());
            confirmed = Integer.parseInt(skuStoreMap.get(0).get("confirmed").toString());
            sku_store_id = Integer.parseInt(skuStoreMap.get(0).get("sku_store_id").toString());

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
            logger.info("ApiUpdateStockSerivce,executeUpdateStock,updateStockEnd,skuStoreMap:"+JSONObject.toJSONString(skuStoreMap));
        }

        // update price
        if(StringUtils.isNotBlank(stockOption.getPrice())) {
            IPriceService iPriceService = new PriceServiceImpl();
            Product getProduct = this.getProduct(conn);
            BigDecimal newPrice = new BigDecimal(stockOption.getPrice());
            BigDecimal oldPrice = getProduct.getMax_retail_price();

            int rs = ifUpdatePrice(oldPrice,newPrice);
            if(rs == 1) {
                iPriceService.synProductPriceRule(getProduct,newPrice,conn);
            } else if(rs == 2){
                return errorMap(ApiErrorTypeEnum.errorType.error_price_out_of_range,"price",stockOption.getPrice());
            }
        }
        return successMap();
    }

    private Map<String,Object> executeCreateSku(Connection conn) throws Exception {
        return this.createSku(conn,stockOption.getProductCode(),stockOption.getSku_code(),stockOption.getSizeValue(),stockOption.getQuantity());
    }

    private Map<String,Object> createSku(Connection conn,String productCode,String skuCode,String size,String quantity) throws Exception {
        if(StringUtils.isBlank(skuCode)) {
            skuCode = "#";
        }

        // 查询product
        Product product = this.getProduct(conn);
        if(product == null) {
            return errorMap(ApiErrorTypeEnum.errorType.error_boutique_is_not_exists,"product_code",productCode);
        }

        // 1.create sku
        SkuService skuService = new SkuService(conn);
        Sku sku = new Sku();
        sku.product_id = product.getProduct_id();
        sku.size = size;
        sku.sku_code = skuCode;
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
        skuStore.store  = Long.valueOf(quantity);
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
        productSkuPropertyValue.value = size;
        productSkuPropertyValue.remark = "job";
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
        skuProperty.remark = "job";
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
            sps.created_at = Helper.getCurrentTimeToUTCWithDate();
            sps.updated_at = Helper.getCurrentTimeToUTCWithDate();
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
        return successMap();
    }

    private Map<String,Object> checkParams() throws Exception{
        logger.info("ApiUpdateStockSerivce,checkParams,inputParams,stockOption:"+JSONObject.toJSONString(stockOption));
        String quantity =  StringUtils.trim(stockOption.getQuantity());
        String vendorId = StringUtils.trim(stockOption.getVendor_id());
        String type =StringUtils.trim( stockOption.getType());
        String price = StringUtils.trim(stockOption.getPrice());
        String productCode = StringUtils.trim(stockOption.getProductCode());
        String skuCode = StringUtils.trim(stockOption.getSku_code());
        String sizeValue = StringUtils.trim(stockOption.getSizeValue());

        stockOption.setQuantity(escape(quantity));
        stockOption.setVendor_id(escape(vendorId));
        stockOption.setType(escape(type));
        stockOption.setPrice(escape(price));
        stockOption.setProductCode(escape(productCode));
        stockOption.setSku_code(escape(skuCode));
        stockOption.setSizeValue(escape(sizeValue));

        // check quantity
        if(StringUtils.isBlank(quantity)) {
            return errorMap(ApiErrorTypeEnum.errorType.error_stock_is_null,"store",quantity);
        }

        try {
            int stock = Integer.parseInt(quantity);
            if(stock < 0 || stock > 100) {
                stockOption.setStore(stock);
                stockOption.setQuantity("0");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("ApiUpdateStockSerivce,checkParams,stock not parse int,errorMessage:"+ ExceptionUtils.getExceptionDetail(e)+",stockOption:"+JSONObject.toJSONString(stockOption));
            return errorMap(ApiErrorTypeEnum.errorType.error_stock_is_not_number,"store",quantity);
        }

        // check vendorId
        if(StringUtils.isBlank(vendorId)) {
            return errorMap(ApiErrorTypeEnum.errorType.error_vendor_is_null,"vendor_id",vendorId);
        }

        // check type
        if(StringUtils.isBlank(type)) {
            return errorMap(ApiErrorTypeEnum.errorType.error_stock_type_is_null,"stock_type",type);
        }

        // check price
        if(StringUtils.isNotBlank(price)) {
            try {
                BigDecimal bPrice = new BigDecimal(price);
                if(bPrice.intValue() < 10 || bPrice.intValue() > 10000 || bPrice.intValue() == 0) {
                    return errorMap(ApiErrorTypeEnum.errorType.error_price,"price",price);
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.info("ApiUpdateStockSerivce,checkParams,price not parse int,errorMessage:"+ ExceptionUtils.getExceptionDetail(e)+",stockOption:"+JSONObject.toJSONString(stockOption));
                return errorMap(ApiErrorTypeEnum.errorType.error_price_is_not_number,"price",price);
            }
        }

        if((StringUtils.isNotBlank(productCode) && StringUtils.isNotBlank(sizeValue)) || (StringUtils.isNotBlank(skuCode))) {
            return successMap();
        } else {
            return errorMap(ApiErrorTypeEnum.errorType.error_update_stock_param,"stockOption",JSONObject.toJSONString(stockOption));
        }
    }


    private Product getProduct(Connection conn) throws Exception {
        if(this.productObj == null) {
            ProductService productService = new ProductService(conn);
            Map<String,Object> productConditionMap = new HashMap<>();
            productConditionMap.put("enabled", EnabledType.USED);
            productConditionMap.put("vendor_id",stockOption.getVendor_id());

            if(StringUtils.isNotBlank(stockOption.getProductCode())) {
                productConditionMap.put("product_code",stockOption.getProductCode());
                Product product = productService.getProductByCondition(productConditionMap,"*");
                this.productObj = product;
            } else if(StringUtils.isNotBlank(stockOption.getSku_code())) {
                productConditionMap.put("product_id",this.productId);
                Product product = productService.getProductByCondition(productConditionMap,"*");
                this.productObj = product;
            }
        }
        return this.productObj;
    }

    private StockOption stockOption;
    private Product productObj;
    private String productId;

    public StockOption getStockOption() {
        return stockOption;
    }

    public void setStockOption(StockOption stockOption) {
        this.stockOption = stockOption;
    }

    public ApiUpdateStockSerivce(StockOption stockOption) {
        this.stockOption = stockOption;
    }

    public Product getProductObj() {
        return productObj;
    }

    public void setProductObj(Product productObj) {
        this.productObj = productObj;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
