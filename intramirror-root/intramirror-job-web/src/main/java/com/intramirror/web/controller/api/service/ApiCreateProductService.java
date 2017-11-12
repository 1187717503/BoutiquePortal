package com.intramirror.web.controller.api.service;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson15.JSON;
import com.alibaba.fastjson15.JSONArray;
import static com.intramirror.web.controller.api.service.ApiCommonUtils.errorMap;
import static com.intramirror.web.controller.api.service.ApiCommonUtils.escape;
import static com.intramirror.web.controller.api.service.ApiCommonUtils.successMap;
import com.intramirror.web.mapping.vo.StockOption;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.sql2o.Connection;
import pk.shoplus.DBConnector;
import pk.shoplus.common.FileUploadHelper;
import pk.shoplus.common.Helper;
import pk.shoplus.enums.ApiErrorTypeEnum;
import pk.shoplus.model.CategoryProductInfo;
import pk.shoplus.model.CategoryProductProperty;
import pk.shoplus.model.Product;
import pk.shoplus.model.ProductEDSManagement;
import pk.shoplus.model.ProductInfo;
import pk.shoplus.model.ProductProperty;
import pk.shoplus.model.ProductSkuPropertyKey;
import pk.shoplus.model.ProductSkuPropertyValue;
import pk.shoplus.model.Sku;
import pk.shoplus.model.SkuProperty;
import pk.shoplus.model.SkuStore;
import pk.shoplus.parameter.EnabledType;
import pk.shoplus.parameter.ProductFeatureType;
import pk.shoplus.parameter.ProductStatusType;
import pk.shoplus.service.CategoryProductInfoService;
import pk.shoplus.service.CategoryProductPropertyService;
import pk.shoplus.service.ProductInfoService;
import pk.shoplus.service.ProductPropertyKeyService;
import pk.shoplus.service.ProductPropertyService;
import pk.shoplus.service.ProductPropertyValueService;
import pk.shoplus.service.ProductService;
import pk.shoplus.service.SkuPropertyService;
import pk.shoplus.service.SkuService;
import pk.shoplus.service.SkuStoreService;
import pk.shoplus.service.price.api.IPriceService;
import pk.shoplus.service.price.impl.PriceServiceImpl;
import pk.shoplus.util.ExceptionUtils;

public class ApiCreateProductService {

    private static final Logger logger = Logger.getLogger(ApiCreateProductService.class);

    public Map<String,Object> executeCreateProduct(){
        Connection conn = null;
        try {

            conn = DBConnector.sql2o.beginTransaction();

            Map<String,Object> checkBaseParamsMap = this.checkBaseParams(conn);
            Map<String,Object> checkMappingParamsMap = this.checkMappingParams(conn);

            if(conn != null) {conn.commit();conn.close();}
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setProduct(Connection conn) throws Exception{
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Product product = new Product();
        product.category_id = Long.parseLong(productOptions.getCategoryId());
        product.vendor_id = vendorOptions.getVendorId();
        product.brand_id = Long.parseLong(productOptions.getBrandId());
        product.product_code = productOptions.getCode();
        product.name = productOptions.getName();
        product.description = productOptions.getDesc();
        product.remark = "";
        product.status = ProductStatusType.NEW_PENDING;
        product.feature = ProductFeatureType.NORMAL;
        product.publish_at = (format.parse(DateFormatUtils.format(new Date(), "yyyy-MM-dd")));
        Calendar curr = Calendar.getInstance();
        curr.set(Calendar.YEAR, curr.get(Calendar.YEAR) + 100);
        Date date = curr.getTime();
        product.valid_at = (format.parse(DateFormatUtils.format(date, "yyyy-MM-dd")));
        product.created_at = new Date();
        product.updated_at = new Date();
        product.enabled = EnabledType.USED;

        String coverImg = this.downloadImgs(productOptions.getCoverImg());
        product.cover_img = coverImg;
        product.description_img = coverImg;

        product.season_code = productOptions.getSeasonCode();
        product.max_retail_price = new BigDecimal(productOptions.getSalePrice());
        product.min_retail_price = new BigDecimal(productOptions.getSalePrice());
        product.last_check = new Date();
        product.designer_id = productOptions.getBrandCode();
        product.color_code = productOptions.getColorCode();

        ProductService productService = new ProductService(conn);
        product = productService.createProduct(product);
        this.setProductObj(product);
    }

    public void setProductSkuPropertyKey(Connection conn) throws Exception{
        ProductPropertyKeyService productPropertyKeyService = new ProductPropertyKeyService(conn);
        ProductSkuPropertyKey productSkuPropertyKey = new ProductSkuPropertyKey();
        productSkuPropertyKey.product_id = this.productObj.getProduct_id();
        productSkuPropertyKey.name = "Size";
        productSkuPropertyKey.remark = "";
        productSkuPropertyKey.created_at = Helper.getCurrentTimeToUTCWithDate();
        productSkuPropertyKey.updated_at = Helper.getCurrentTimeToUTCWithDate();
        productSkuPropertyKey.enabled = EnabledType.USED;
        productSkuPropertyKey = productPropertyKeyService.createProductPropertyKey(productSkuPropertyKey);
        this.setProductSkuPropertyKeyObj(productSkuPropertyKey);
    }

    public void setProductProperty(Connection conn) throws Exception{
        ProductPropertyService productPropertyService = new ProductPropertyService(conn);
        CategoryProductPropertyService categoryProductPropertyService = new CategoryProductPropertyService(conn);
        Map<String,Object> categoryProductPropertMap = new HashMap<>();
        categoryProductPropertMap.put("category_id",productOptions.getCategoryId());
        categoryProductPropertMap.put("enabled",EnabledType.USED);
        List<CategoryProductProperty> categoryProductProperties = categoryProductPropertyService.getCategoryProductPropertyListByCondition(categoryProductPropertMap);
        for(CategoryProductProperty cpp : categoryProductProperties) {
            ProductProperty productProperty = new ProductProperty();
            productProperty.category_product_property_id = cpp.getCategory_product_property_id();
            productProperty.product_id = productObj.getProduct_id();
            productProperty.key_name = cpp.getKey_name();
            productProperty.type = cpp.getType();
            productProperty.created_at = Helper.getCurrentTimeToUTCWithDate();
            productProperty.updated_at = Helper.getCurrentTimeToUTCWithDate();
            productProperty.value = this.getProductValue(cpp.getKey_name());
            productProperty.remark = "";
            productProperty.enabled = EnabledType.USED;
            productPropertyService.createProductProperty(productProperty);
        }
    }

    public void setProductInfo(Connection conn) throws Exception{
        CategoryProductInfoService categoryProductInfoService = new CategoryProductInfoService(conn);
        Map<String,Object> categoryProductInfoMap = new HashMap<>();
        categoryProductInfoMap.put("enabled",EnabledType.USED);
        categoryProductInfoMap.put("category_id",productObj.getCategory_id());
        List<CategoryProductInfo> categoryProductInfoList = categoryProductInfoService.getCategoryProductInfoListByCondition(categoryProductInfoMap);
        if(categoryProductInfoList != null && categoryProductInfoList.size() >0) {
            CategoryProductInfo categoryProductInfo = categoryProductInfoList.get(0);
            ProductInfoService productInfoService = new ProductInfoService(conn);
            ProductInfo productInfo = new ProductInfo();
            productInfo.category_prodcut_info_id = categoryProductInfo.getCategory_product_info_id();
            productInfo.product_id = productInfo.getProduct_id();
            productInfo.created_at = Helper.getCurrentTimeToUTCWithDate();
            productInfo.updated_at = Helper.getCurrentTimeToUTCWithDate();
            productInfo.remark = "eds";
            productInfo.enabled = EnabledType.USED;
            productInfo.weight = categoryProductInfo.weight;
            productInfo.length = categoryProductInfo.length;
            productInfo.width = categoryProductInfo.width;
            productInfo.height = categoryProductInfo.height;
            productInfo.size = categoryProductInfo.size;
            productInfo.weight_unit = categoryProductInfo.weight_unit;
            productInfo.length_unit = categoryProductInfo.length_unit;
            productInfo.width_unit = categoryProductInfo.width_unit;
            productInfo.height_unit = categoryProductInfo.height_unit;
            productInfo.size_unit = categoryProductInfo.size_unit;
            productInfo.weight_amount = new BigDecimal(1);
            productInfo.length_amount = new BigDecimal(0);
            productInfo.width_amount = new BigDecimal(0);
            productInfo.height_amount = new BigDecimal(0);
            productInfo.size_amount = new BigDecimal(0);
            productInfoService.createProductInfo(productInfo);
            productInfo.other_property = "";
        }
    }

    private void setSku(Connection conn) throws Exception {
        SkuService skuService = new SkuService(conn);
        List<ProductEDSManagement.SkuOptions> skuOptions = productOptions.getSkus();
        if(skuOptions == null || skuOptions.size() == 0) {
            return;
        }

        for(ProductEDSManagement.SkuOptions skuOption : skuOptions) {
            Sku sku = new Sku();
            sku.product_id = productObj.getProduct_id();
            sku.size = skuOption.getSize();
            sku.sku_code = skuOption.getBarcodes();
            sku.name = productObj.getName();
            sku.coverpic = productObj.getCover_img();
            sku.introduction = productObj.getDescription();
            IPriceService iPriceService = new PriceServiceImpl();
            Sku sku1 = iPriceService.getPriceByRule(productObj,productObj.getVendor_id(),productObj.getMax_retail_price(),conn);
            sku.im_price = sku1.getIm_price();
            sku.in_price = sku1.getIn_price();
            sku.price = productObj.getMax_retail_price();
            sku.created_at = new Date();
            sku.updated_at = new Date();
            sku.enabled = EnabledType.USED;
            sku.last_check = new Date();
            skuService.createSku(sku);

            SkuStoreService skuStoreService = new SkuStoreService(conn);
            SkuStore skuStore = new SkuStore();
            skuStore.product_id = productObj.getProduct_id();
            skuStore.sku_id = sku.sku_id;
            skuStore.store  = Long.valueOf(skuOption.getStock());
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

            ProductPropertyValueService productPropertyValueService = new ProductPropertyValueService(conn);
            ProductSkuPropertyValue productSkuPropertyValue = new ProductSkuPropertyValue();
            productSkuPropertyValue.product_sku_property_key_id = productSkuPropertyKeyObj.getProduct_sku_property_key_id();
            productSkuPropertyValue.value = skuOption.getSize();
            productSkuPropertyValue.remark = "";
            productSkuPropertyValue.created_at = new Date();
            productSkuPropertyValue.updated_at = new Date();
            productSkuPropertyValue.enabled = EnabledType.USED;
            ProductSkuPropertyValue pspv = productPropertyValueService.createProductPropertyValue(productSkuPropertyValue);

            SkuPropertyService skuPropertyService = new SkuPropertyService(conn);
            SkuProperty skuProperty = new SkuProperty();
            skuProperty.sku_id = sku.getSku_id();
            skuProperty.product_sku_property_key_id = productSkuPropertyKeyObj.getProduct_sku_property_key_id();
            skuProperty.product_sku_property_value_id = pspv.getProduct_sku_property_value_id();
            skuProperty.name = productObj.getName();
            skuProperty.remark = "job";
            skuProperty.created_at = new Date();
            skuProperty.updated_at = new Date();
            skuProperty.enabled = EnabledType.USED;
            skuPropertyService.createSkuProperty(skuProperty);
        }
    }

    public String getProductValue(String keyName) {
        String value = "";
        switch (keyName) {
        case "Composition":
            value = productOptions.getComposition();
            break;
        case "CarryOver":
            value = productOptions.getCarryOver();
            break;
        case "ColorDescription":
            value = productOptions.getColorDesc();
            break;
        case "MadeIn":
            value = productOptions.getMadeIn();
            break;
        case "SizeFit":
            value = productOptions.getSizeFit();
            break;
        case "ColorCode":
            value = productOptions.getColorCode();
            break;
        case "BrandID":
            value = productOptions.getBrandCode();
            break;
        case "SeasonCode":
            value = productOptions.getSeasonCode();
            break;
        }
        return value;
    }

    private String downloadImgs (String originImg) {
        List<String> newList = new ArrayList<>();
        try {
            List<String> originList = JSONArray.parseArray(originImg, String.class);
            for (String origin : originList) {
                List<String> downList = FileUploadHelper.uploadFileByImgUrl2(origin);
                if(downList != null && downList.size() > 0) {
                    newList.add(downList.get(0));
                }
            }
        } catch (Exception e) {
            logger.info("ApiCreateProductService,downloadImgs,errorMessage:"+ExceptionUtils.getExceptionDetail(e)+",originImg:"+originImg);
            newList = new ArrayList<>();
        }
        return JSON.toJSONString(newList);
    }

    private Map<String,Object> checkMappingParams(Connection conn) throws Exception {
        ProductService productService = new ProductService(conn);
        String boutique_category_id = escape(StringUtils.trim(productOptions.getCategory_boutique_id()));
        String category1 = escape(StringUtils.trim(productOptions.getCategory1()));
        String category2 = escape(StringUtils.trim(productOptions.getCategory2()));
        String category3 = escape(StringUtils.trim(productOptions.getCategory3()));
        String brandName = escape(StringUtils.trim(productOptions.getBrandName()));
        String seasonCode = escape(StringUtils.trim(productOptions.getSeasonCode()));
        String mappingSeason = "";

        productOptions.setCategory_boutique_id(boutique_category_id);
        productOptions.setCategory1(category1);
        productOptions.setCategory2(category2);
        productOptions.setCategory3(category3);
        productOptions.setBrandName(brandName);
        productOptions.setSeasonCode(seasonCode);

        // get category
        if(StringUtils.isNotBlank(boutique_category_id)) {
            Map<String,Object> categoryMap = productService.getBoutiqueCategory(vendorOptions.getVendorId().toString(),boutique_category_id);
            if(categoryMap != null) {
                productOptions.setCategoryId(categoryMap.get("category_id").toString());
            }
        }

        if(StringUtils.isNotBlank(productOptions.getCategory1())
                &&StringUtils.isNotBlank(productOptions.getCategory2())
                &&StringUtils.isNotBlank(productOptions.getCategory3())) {
            Map<String,Object> categoryMap =productService.getCategory(category1,category2,category3);
            if(categoryMap != null) {
                productOptions.setCategoryId(categoryMap.get("category_id").toString());
            } else {
                categoryMap = productService.getThreeCategory(vendorOptions.getVendorId().toString(),category1,category2,category3);
                if(categoryMap != null) {
                    productOptions.setCategoryId(categoryMap.get("category_id").toString());
                }
            }
        }

        if(StringUtils.isBlank(productOptions.getCategoryId())
                && StringUtils.isBlank(category3)
                && StringUtils.isNotBlank(productOptions.getCategory1())
                && StringUtils.isNotBlank(productOptions.getCategory2())
                ) {
            String categoryId = productService.getDeafultCategory(vendorOptions.getVendorId(),category1,category2);
            productOptions.setCategoryId(categoryId);
        }

        // get brand
        if(StringUtils.isNotBlank(brandName)) {
            Map<String,Object> brandMap = productService.getBrand(brandName);
            if(brandMap != null) {
                productOptions.setBrandId(brandMap.get("brand_id").toString());
            } else {
                brandMap = productService.getBrandMapping(brandName);
                if(brandMap != null) {
                    productOptions.setBrandId(brandMap.get("brand_id").toString());
                }
            }
        }

        // get season
        if(StringUtils.isNotBlank(seasonCode)) {
            Map<String,Object> seasonMap = productService.getSeason(seasonCode);
            if(seasonMap != null) {
                mappingSeason = seasonMap.get("season_code").toString();
                productOptions.setSeasonCode(mappingSeason);
            }
        }


        Map<String,Object> mappingCategory = new HashMap<>();
        mappingCategory.put("boutique_category_id",boutique_category_id);
        mappingCategory.put("category1",category1);
        mappingCategory.put("categroy2",category2);
        mappingCategory.put("category3",category3);

        if(StringUtils.isBlank(productOptions.getCategoryId())) {
            return errorMap(ApiErrorTypeEnum.errorType.error_boutique_id_is_null,"category",JSONObject.toJSONString(mappingCategory));
        }

        if(StringUtils.isBlank(productOptions.getBrandId())) {
            return errorMap(ApiErrorTypeEnum.errorType.error_boutique_id_is_null,"brand",brandName);
        }

        if(StringUtils.isBlank(mappingSeason)) {
            return errorMap(ApiErrorTypeEnum.errorType.error_boutique_id_is_null,"season",seasonCode);
        }

        return successMap();
    }

    private Map<String,Object> checkBaseParams(Connection conn) throws Exception{
        String product_code = escape(StringUtils.trim(productOptions.getCode()));
        String designer_id = escape(StringUtils.trim(productOptions.getBrandCode()));
        String brand_name = escape(StringUtils.trim(productOptions.getBrandName()));
        String color_code = escape(StringUtils.trim(productOptions.getColorCode()));
        String cover_img = escape(StringUtils.trim(productOptions.getCoverImg()));
        String price = escape(StringUtils.trim(productOptions.getSalePrice()));
        String product_name = escape(StringUtils.trim(productOptions.getName()));
        String product_desc = escape(StringUtils.trim(productOptions.getDesc()));

        Long vendor_id = vendorOptions.getVendorId();

        productOptions.setCode(product_code);
        productOptions.setBrandCode(designer_id);
        productOptions.setBrandName(brand_name);
        productOptions.setColorCode(color_code);
        productOptions.setCoverImg(cover_img);
        productOptions.setSalePrice(price);
        productOptions.setName(product_name);
        productOptions.setDesc(product_desc);

        if(StringUtils.isBlank(product_code)) {
            return errorMap(ApiErrorTypeEnum.errorType.error_boutique_id_is_null,"product_code",product_code);
        }

        if(StringUtils.isBlank(product_name)) {
            return errorMap(ApiErrorTypeEnum.errorType.error_boutique_id_is_null,"product_name",product_name);
        }

        if(vendor_id == null) {
            return errorMap(ApiErrorTypeEnum.errorType.error_vendor_is_null,"vendor_id","null");
        }

        Product product = this.getProduct(conn);
        if(product == null) {
            return errorMap(ApiErrorTypeEnum.errorType.error_boutique_id_already_exist,"product_code",product_code);
        }

        if(StringUtils.isBlank(designer_id)) {
            return errorMap(ApiErrorTypeEnum.errorType.error_boutique_id_already_exist,"designer_id",designer_id);
        }

        if(StringUtils.isBlank(brand_name)) {
            return errorMap(ApiErrorTypeEnum.errorType.error_boutique_id_already_exist,"brand_name",brand_name);
        }

        if(StringUtils.isBlank(color_code)) {
            return errorMap(ApiErrorTypeEnum.errorType.error_boutique_id_already_exist,"color_code",color_code);
        }

        try {
            BigDecimal bPrice = new BigDecimal(price);
            if(bPrice.intValue() < 10 || bPrice.intValue() > 10000 || bPrice.intValue() == 0) {
                return errorMap(ApiErrorTypeEnum.errorType.error_price,"price",price);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("ApiCreateProductService,executeCheckParams,price not parse int,errorMessage:"+ ExceptionUtils
                    .getExceptionDetail(e)+",productOptions:"+ JSONObject.toJSONString(productOptions));
            return errorMap(ApiErrorTypeEnum.errorType.error_price_is_not_number,"price",price);
        }

        List<ProductEDSManagement.SkuOptions> skus = productOptions.getSkus(); // variants
        for(ProductEDSManagement.SkuOptions skuOptions : skus) {
            /*StockOption stockOption = new StockOption();
            stockOption.setLast_check(new Date());
            stockOption.setQuantity(skuOptions.getStock());
            stockOption.setProductCode(product_code);
            stockOption.setVendor_id(vendor_id.toString());
            stockOption.setType(Contants.STOCK_QTY+"");
            stockOption.setSizeValue(skuOptions.getSize());
            this.addStockOptions(stockOption);*/
            if(StringUtils.isBlank(skuOptions.getSize())) {
                return errorMap(ApiErrorTypeEnum.errorType.error_price_is_not_number,"sku",JSONObject.toJSONString(skus));
            }

        }
        return successMap();
    }

    private Product getProduct(Connection conn) throws Exception{
        if(this.productObj == null) {
            ProductService productService = new ProductService(conn);
            Map<String, Object> condition = new HashMap<>();
            condition.put("product_code", productOptions.getCode());
            condition.put("enabled", 1);
            condition.put("vendor_id",vendorOptions.getVendorId());
            Product product = productService.getProductByCondition(condition, null);
            this.productObj = product;
        }
        return this.productObj;
    }

    private Product productObj;
    private ProductSkuPropertyKey productSkuPropertyKeyObj;
    private ProductEDSManagement.ProductOptions productOptions;
    private ProductEDSManagement.VendorOptions vendorOptions;
    private List<StockOption> stockOptions;

    public ProductEDSManagement.ProductOptions getProductOptions() {
        return productOptions;
    }

    public void setProductOptions(ProductEDSManagement.ProductOptions productOptions) {
        this.productOptions = productOptions;
    }

    public ProductEDSManagement.VendorOptions getVendorOptions() {
        return vendorOptions;
    }

    public void setVendorOptions(ProductEDSManagement.VendorOptions vendorOptions) {
        this.vendorOptions = vendorOptions;
    }

    public Product getProductObj() {
        return productObj;
    }

    public void setProductObj(Product productObj) {
        this.productObj = productObj;
    }

    public List<StockOption> getStockOptions() {
        return stockOptions;
    }

    public void setStockOptions(List<StockOption> stockOptions) {
        this.stockOptions = stockOptions;
    }

    public void addStockOptions(StockOption stockOption) {
        if(this.stockOptions == null) {
            this.stockOptions = new ArrayList<>();
        }
        this.stockOptions.add(stockOption);
    }

    public ProductSkuPropertyKey getProductSkuPropertyKeyObj() {
        return productSkuPropertyKeyObj;
    }

    public void setProductSkuPropertyKeyObj(ProductSkuPropertyKey productSkuPropertyKeyObj) {
        this.productSkuPropertyKeyObj = productSkuPropertyKeyObj;
    }
}
