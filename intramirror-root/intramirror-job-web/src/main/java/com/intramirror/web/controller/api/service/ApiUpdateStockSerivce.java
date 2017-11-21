package com.intramirror.web.controller.api.service;

import com.alibaba.fastjson.JSONObject;
import com.intramirror.web.mapping.vo.StockOption;
import java.math.BigDecimal;
import java.util.ArrayList;
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
import pk.shoplus.parameter.StatusType;
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
import pk.shoplus.util.DateUtils;
import pk.shoplus.util.ExceptionUtils;

/**
 * 修改库存优化
 */
public class ApiUpdateStockSerivce {

    private static final String update_by_product = "update_by_product";

    private static final String update_by_sku = "update_by_sku";

    private static final Logger logger = Logger.getLogger(ApiUpdateStockSerivce.class);

    private StockOption stockOption;

    private Map<String,Object> skuStoreMap;

    private Product uProduct;

    private List<Map<String,Object>> warningList;

    public Map<String,Object> updateStock(StockOption stockOption) {
        Map<String,Object> resultMap = new HashMap<>();

        this.stockOption = stockOption;
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

            if(warningList == null || warningList.size() == 0) {
                resultMap = ApiCommonUtils.successMap();
            } else {
                resultMap.put("status",StatusType.WARNING);
                resultMap.put("warningMaps",warningList);
            }
            if(conn != null) {conn.commit();conn.close(); }
        } catch (UpdateException e) {
            resultMap = ApiCommonUtils.errorMap(e.getErrorType(),e.getKey(),e.getValue());
            if(conn != null) {conn.commit();conn.close(); }
        } catch (Exception e) {
            e.printStackTrace();
            resultMap = ApiCommonUtils.errorMap(ApiErrorTypeEnum.errorType.error_runtime_exception,"errorMessage",ExceptionUtils.getExceptionDetail(e));
            if(conn != null) {conn.rollback();conn.close(); }
        }
        return resultMap;
    }

    public Map<String,Object> updateStock(StockOption stockOption,Connection conn) {
        Map<String,Object> resultMap = new HashMap<>();
        this.stockOption = stockOption;
        try{

            /** 检查参数 */
            this.checkParams(conn);

            /** 更新stock */
            this.updateStock(conn);

            /** 新增SKU */
            this.createSku(conn);

            /** 更新价格 */
            this.updatePrice(conn);

            if(warningList == null || warningList.size() == 0) {
                resultMap = ApiCommonUtils.successMap();
            } else {
                resultMap.put("status",StatusType.WARNING);
                resultMap.put("warningMaps",warningList);
            }
        } catch (UpdateException e) {
            resultMap = ApiCommonUtils.errorMap(e.getErrorType(),e.getKey(),e.getValue());
        } catch (Exception e) {
            e.printStackTrace();
            resultMap = ApiCommonUtils.errorMap(ApiErrorTypeEnum.errorType.error_runtime_exception,"errorMessage",ExceptionUtils.getExceptionDetail(e));
        }
        return resultMap;
    }

    private void updatePrice(Connection conn) throws Exception {
        String price = stockOption.getPrice();
        if(StringUtils.isBlank(price)) {
            return;
        }

        if(StringUtils.isNotBlank(price)) {
            try {
                BigDecimal bPrice = new BigDecimal(price);
                if(bPrice.intValue() < 10 || bPrice.intValue() > 10000 || bPrice.intValue() == 0) {
                    this.toShopProcessing(conn);
                    this.setWarning(ApiErrorTypeEnum.errorType.warning_price_out_off,"price",price);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.info("ApiUpdateStockSerivce,checkParams,price not parse int,errorMessage:"+ ExceptionUtils.getExceptionDetail(e)+",stockOption:"+JSONObject.toJSONString(stockOption));
                throw new UpdateException("price",price,ApiErrorTypeEnum.errorType.error_data_is_not_number);
            }
        }

        BigDecimal newPrice = new BigDecimal(stockOption.getPrice());
        Product product = this.getProduct(conn);

        BigDecimal oldPrice = product.getMax_retail_price();
        int rs = ApiCommonUtils.ifUpdatePrice(oldPrice,newPrice);

        if(rs == 1 || stockOption.isModify()) {
            IPriceService iPriceService = new PriceServiceImpl();
            logger.info("ApiUpdateStockSerivce,synProductPriceRule,product:"+JSONObject.toJSONString(product) +",newPrice:"+newPrice);
            iPriceService.synProductPriceRule(product,newPrice,conn);
        } else if(rs == 2){
            this.toShopProcessing(conn);
            this.setWarning(ApiErrorTypeEnum.errorType.warning_price_out_off,"price",price);
        }
    }

    public void toShopProcessing(Connection conn) throws Exception{
        ProductService productService = new ProductService(conn);
        ShopProductService shopProductService = new ShopProductService(conn);
        Product product = this.getProduct(conn);

        List<ShopProduct> shopProducts = shopProductService.getShopProductByProductId(product.getProduct_id());
        logger.info("ApiUpdateStockSerivce,toShopProcessing,shopProducts:"+JSONObject.toJSONString(shopProducts));
        if(shopProducts != null && shopProducts.size() > 0 && product.getStatus().intValue() == 3) {
            ShopProduct shopProduct = shopProducts.get(0);

            if(shopProduct.getStatus().intValue() == 0) {
                product.setStatus(4);
                shopProduct.setStatus(2);

                productService.updateProduct(product);
                logger.info("ApiUpdateStockSerivce,toShopProcessing,updateProduct,product:"+JSONObject.toJSONString(product));

                shopProductService.updateShopProduct(shopProduct);
                logger.info("ApiUpdateStockSerivce,toShopProcessing,updateProduct,product:"+JSONObject.toJSONString(shopProduct));
            }
        }
    }

    private void createSku(Connection conn) throws Exception{
        // 必须是update_by_product(product_code,size)，并且不存在skuStoreMap，才可以创建SKU
        if(stockOption.getUpdated_by().equals(ApiUpdateStockSerivce.update_by_product)
                && this.skuStoreMap == null){

            if(StringUtils.isBlank(stockOption.getSku_code())) {
                stockOption.setSku_code("#");
            }

            Product product = this.getProduct(conn);

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
            logger.info("ApiUpdateStockSerivce,createSku,sku:"+JSONObject.toJSONString(sku));

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
            if(skuStore.store.intValue() < 0 || skuStore.store.intValue() > 100) {
                this.setWarning(ApiErrorTypeEnum.errorType.warning_stock_out_off,"store",stockOption.getQuantity());
                skuStore.store = 0L;
            }
            skuStoreService.createSkuStore(skuStore);
            logger.info("ApiUpdateStockSerivce,createSku,sku_store:"+JSONObject.toJSONString(skuStore));

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
            logger.info("ApiUpdateStockSerivce,createSku,product_sku_property_value:"+JSONObject.toJSONString(pspv));

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
            logger.info("ApiUpdateStockSerivce,createSku,sku_property:"+JSONObject.toJSONString(skuProperty));

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
                logger.info("ApiUpdateStockSerivce,createSku,shop_product:"+JSONObject.toJSONString(sps));
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
        // skuStoreMap 在checkParams()就会获取到,如果为空的话，准备创建SKU
        if(this.skuStoreMap == null || this.skuStoreMap.size() == 0) {
            return;
        }

        // 检查last_check ,判断这条消息是不是最新的,如果sku_store的last_check晚于msgDate,就不更新
        Date msgDate = stockOption.getLast_check();
        if(skuStoreMap.get("last_check") != null) {
            Date date = DateUtils.getDateByStr("yyyy-MM-dd HH:mm:ss",skuStoreMap.get("last_check").toString());
            boolean flag = DateUtils.compareDate(date,msgDate);
            logger.info("ApiUpdateStockSerivce,updateStock,flag:"+flag+",msgDate:"+DateUtils.getformatDate(msgDate)+",date:"+DateUtils.getformatDate(date));
            if(flag) {return;}
        }

        logger.info("ApiUpdateStockSerivce,updateStock,inputParams,skuStoreMap:"+JSONObject.toJSONString(skuStoreMap));
        SkuStoreService skuStoreService = new SkuStoreService(conn);
        int originQty = Integer.parseInt(stockOption.getQuantity());
        int qty = Integer.parseInt(stockOption.getQuantity());
        int type = Integer.parseInt(stockOption.getType());
        int rs = 0;
        int store = 0;
        int reserved = 0;
        int confirmed = 0;
        int sku_store_id = 0;
        store = Integer.parseInt(skuStoreMap.get("store").toString());
        reserved = Integer.parseInt(skuStoreMap.get("reserved").toString());
        confirmed = Integer.parseInt(skuStoreMap.get("confirmed").toString());
        sku_store_id = Integer.parseInt(skuStoreMap.get("sku_store_id").toString());

        if(skuStoreService.ifUpdateStock((long)sku_store_id)) {
            logger.info("ApiUpdateStockSerivce,updateStock,store不允许更新,sku_store_id:"+sku_store_id);
            return;
        }

        // -2,1,1的情况
        if(Contants.STOCK_QTY == type && store < 0) {
            store = 0;
        }

        if(Contants.STOCK_QTY == type) {
            if(qty < 0 || qty > 100) {
                qty = 0;
            }
            rs = qty - (store + reserved + confirmed);
        } else {
            if(qty < -100 || qty > 100) {
                qty = 0;
                rs = qty - (store + reserved + confirmed);
            } else {
                rs = qty;
            }
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

        // 当库存小于0，或低于100时，库存在这被清零，然后报警告
        if((originQty < 0 || originQty > 100) && Contants.STOCK_QTY == type) {
            this.setWarning(ApiErrorTypeEnum.errorType.warning_stock_out_off,"store",stockOption.getQuantity());
        } else if((originQty < -100 || originQty > 100) && Contants.STOCK_QTY_DIFF == type) {
            this.setWarning(ApiErrorTypeEnum.errorType.warning_stock_out_off,"store",stockOption.getQuantity());
        }
    }

    private void checkParams(Connection conn) throws Exception {
        // 获取修改库存参数
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

        // 检查quantity是否为NULL
        if(StringUtils.isBlank(quantity)) {
            throw new UpdateException("store",quantity, ApiErrorTypeEnum.errorType.error_data_is_null);
        }

        // 检查quantity是否为数字,如果库存数量 store<0 or store>100,库存置0，报警告 updateStock()执行后
        try {
            Double dStock = Double.parseDouble(quantity);
            quantity = dStock.intValue()+"";
            stockOption.setQuantity(quantity);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("ApiUpdateStockSerivce,checkParams,stock not parse int,errorMessage:"+ ExceptionUtils.getExceptionDetail(e)+",stockOption:"+ JSONObject.toJSONString(stockOption));
            throw new UpdateException("store",quantity, ApiErrorTypeEnum.errorType.error_data_is_not_number);
        }

        // 检查vendorId是否为NULL
        if(StringUtils.isBlank(vendorId)) {
            throw new UpdateException("vendor_id",vendorId,ApiErrorTypeEnum.errorType.error_data_is_null);
        }

        // 检查Type是否为NULL
        if(StringUtils.isBlank(type)) {
            throw new UpdateException("diff_type",type,ApiErrorTypeEnum.errorType.error_data_is_null);
        }

        // 检查是否具备修改库存必要参数,(productCode,sizeValue) or (skuCode)
        if((StringUtils.isNotBlank(productCode) && StringUtils.isNotBlank(sizeValue)) || (StringUtils.isNotBlank(skuCode))) {
            if(StringUtils.isNotBlank(productCode) && StringUtils.isNotBlank(sizeValue)) {
                stockOption.setUpdated_by(ApiUpdateStockSerivce.update_by_product);
            } else {
                stockOption.setUpdated_by(ApiUpdateStockSerivce.update_by_sku);
            }

            // 查询SKU信息
            SkuStoreService skuStoreService = new SkuStoreService(conn);
            List<Map<String,Object>>  skuStoreMapList = null ;
            if(stockOption.getUpdated_by().equals(ApiUpdateStockSerivce.update_by_product)) {
                skuStoreMapList =skuStoreService.api_select_store(productCode,sizeValue,vendorId);
            } else {
                skuStoreMapList =skuStoreService.api_select_store(skuCode,vendorId);
            }

            if(skuStoreMapList == null || skuStoreMapList.size() == 0) {

                // 当修改类型为update_by_sku时,报错:因为没有SIZE
                if(stockOption.getUpdated_by().equals(ApiUpdateStockSerivce.update_by_sku)) {
                    throw new UpdateException("size",sizeValue,ApiErrorTypeEnum.errorType.error_sku_not_exists);
                }
            }else{
                this.skuStoreMap = skuStoreMapList.get(0);
            }

            // 查询商品信息是否存在
            Product product = this.getProduct(conn);
            if(product == null) {
                throw new UpdateException("product_code",stockOption.getProductCode(), ApiErrorTypeEnum.errorType.error_boutique_id_not_exists);
            }
            return;
        }

        // 如果上面这个必备参数条件没过的话，抛出以下任意一个为空的错误
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

    private void setWarning(ApiErrorTypeEnum.errorType errorType,String key,String value){
        Map<String,Object> warningMap = new HashMap<>();
        warningMap.put("status", StatusType.WARNING);
        warningMap.put("error_enum",errorType);
        warningMap.put("key",key);
        warningMap.put("value",value);
        warningMap.put("info",errorType.getDesc());

        if(warningList == null || warningList.size() == 0) {
            warningList = new ArrayList<>();
        }

        warningList.add(warningMap);
    }

}
