package com.intramirror.web.controller.api.service;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson15.JSONArray;
import com.intramirror.utils.transform.JsonTransformUtil;
import static com.intramirror.web.controller.api.service.ApiCommonUtils.escape;
import com.intramirror.web.distributed.utils.KafkaMqUtil;
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
import pk.shoplus.enums.ApiErrorTypeEnum;
import pk.shoplus.model.BoutiqueImage;
import pk.shoplus.model.BrandCategory;
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
import pk.shoplus.service.BoutiqueImageService;
import pk.shoplus.service.BrandCategoryService;
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

/**
 * 创建商品优化
 */
public class ApiCreateProductService extends AbstractService {

    private static final Logger logger = Logger.getLogger(ApiCreateProductService.class);

    private ProductSkuPropertyKey uProductSkuPropertyKey;

    private Boolean no_img;

    public Map<String, Object> createProduct(ProductEDSManagement.ProductOptions productOptions, ProductEDSManagement.VendorOptions vendorOptions) {
        long start = System.currentTimeMillis();
        Map<String, Object> resultMap = null;

        this.productOptions = productOptions;
        this.vendorOptions = vendorOptions;

        Connection conn = null;
        try {
            conn = DBConnector.sql2o.beginTransaction();

            this.checkProductParams(conn);
            this.checkMappingParams(conn);
            this.checkFilter(conn);

            /** 1.create product */
            this.setProduct(conn);

            /** 2.create product_sku_property_key */
            this.setProductSkuPropertyKey(conn);

            /** 3.create product_property */
            this.setProductProperty(conn);

            /** 4.create product_info */
            this.setProductInfo(conn);

            /** 5.create sku */
            this.setSku(conn);

            /** 6.create brand_catgeory */
            this.setBrandCategory(conn);

            resultMap = ApiCommonUtils.successMap();
            this.printChangeLog(conn);
            if (conn != null) {
                conn.commit();
                conn.close();
            }
        } catch (UpdateException e) {
            resultMap = ApiCommonUtils.errorMap(e.getErrorType(), e.getKey(), e.getValue());
            if (conn != null) {
                conn.rollback();
                conn.close();
            }
        } catch (FilterException e) {
            resultMap = ApiCommonUtils.successMap();
            logger.info("ApiUpdateProductService,FilterException,errorMsg:" + e.getMessage() + ",productOptions:" + JSONObject.toJSONString(productOptions)
                    + ",vendorOptions:" + JSONObject.toJSONString(vendorOptions));
            if (conn != null) {
                conn.rollback();
                conn.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("vendor_id")) {
                resultMap = ApiCommonUtils.errorMap(ApiErrorTypeEnum.errorType.error_boutique_id_already_exists, "errorMessage",
                        ExceptionUtils.getExceptionDetail(e));
            } else {
                resultMap = ApiCommonUtils.errorMap(ApiErrorTypeEnum.errorType.error_runtime_exception, "errorMessage", ExceptionUtils.getExceptionDetail(e));
            }
            if (conn != null) {
                conn.rollback();
                conn.close();
            }
        }
        long end = System.currentTimeMillis();
        logger.info("Job_Run_Time,ApiCreateProductService_createProduct,start:" + start + ",end:" + end + ",time:" + (end - start));

        if (resultMap != null && !resultMap.get("info").equals(ApiErrorTypeEnum.errorType.error_boutique_id_already_exists.getDesc())) {
            if (StringUtils.isNotBlank(productOptions.getRequestId())) {
                result.put("id", productOptions.getRequestId());
                result.put("exceptionData", resultMap);
                result.put("time", new Date());
                KafkaMqUtil.sendProductResultMessage(productOptions.getRequestId(), JsonTransformUtil.toJson(result));
            }
        }

        return resultMap;
    }

    public void checkFilter(Connection conn) throws Exception {
        ProductService productService = new ProductService(conn);
        String season_code = productOptions.getSeasonCode() == null ? "" : productOptions.getSeasonCode();
        String brand_id = productOptions.getBrandId() == null ? "" : productOptions.getBrandId();

        // 判断season是否需要
        String seasonFilterSQL = " select * from `season_filter` sf " + " where sf.`enabled`  =1  and sf.`vendor_id`  = " + vendorOptions.getVendorId()
                + " and (sf.`season_code` = '-1' or sf.`season_code`  = \"" + season_code + "\")";
        List<Map<String, Object>> seasonFilterMap = productService.executeSQL(seasonFilterSQL);
        if (seasonFilterMap != null && seasonFilterMap.size() > 0) {
            throw new FilterException("season_filter_msg:" + JSONObject.toJSONString(seasonFilterMap));
        }

        // 判断Brand是否需要
        String brandFilterSQL = " select * from `brand_filter`  bf " + " where bf.`enabled`  = 1 and bf.`vendor_id`  =" + vendorOptions.getVendorId()
                + " and (bf.`brand_id` = '-1' or bf.`brand_id`  = '" + brand_id + "')";
        List<Map<String, Object>> brandFilterMap = productService.executeSQL(brandFilterSQL);
        if (brandFilterMap != null && brandFilterMap.size() > 0) {
            throw new FilterException("brand_filter_msg:" + JSONObject.toJSONString(brandFilterMap));
        }

        if (season_code.equalsIgnoreCase("Closeout")) {
            List<ProductEDSManagement.SkuOptions> skuOptions = productOptions.getSkus();
            if (skuOptions != null && skuOptions.size() > 0) {
                boolean flag = true;
                for (int i = 0; i < skuOptions.size(); i++) {
                    int stock = Integer.parseInt(skuOptions.get(i).getStock());
                    if (stock >= 1) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    throw new FilterException("closeout_filter_msg:" + JSONObject.toJSONString(productOptions));
                }
            }
        }
    }

    private void setBrandCategory(Connection conn) throws Exception {
        Map<String, Object> brandCategoryMap = new HashMap<String, Object>();
        brandCategoryMap.put("brand_id", this.uProduct.getBrand_id());
        brandCategoryMap.put("category_id", this.uProduct.getCategory_id());
        brandCategoryMap.put("enabled", EnabledType.USED);
        BrandCategoryService brandCategoryService = new BrandCategoryService(conn);
        List<BrandCategory> brandCategoryList = brandCategoryService.getBrandCategoryListByCondition(null, brandCategoryMap);

        if (brandCategoryList == null || brandCategoryList.size() == 0) {
            BrandCategory brandCategory = new BrandCategory();
            brandCategory.brand_id = this.uProduct.getBrand_id();
            brandCategory.category_id = this.uProduct.getCategory_id();
            brandCategory.created_at = new Date();
            brandCategory.updated_at = new Date();
            brandCategory.enabled = EnabledType.USED;
            brandCategoryService.createBrandCategory(brandCategory);
            logger.info("ApiCreateProductService,setBrandCategory,brandCategory:" + JSONObject.toJSONString(brandCategory));
        }
    }

    private void setSku(Connection conn) throws Exception {
        ProductService productService = new ProductService(conn);
        IPriceService iPriceService = new PriceServiceImpl();
        Sku sku1 = iPriceService.getPriceByRule(this.uProduct, this.uProduct.getVendor_id(), this.uProduct.getMax_retail_price(), conn);
        this.uProduct.setMax_boutique_price(sku1.getIn_price());
        this.uProduct.setMin_boutique_price(sku1.getIn_price());
        this.uProduct.setMax_im_price(sku1.getIm_price());
        this.uProduct.setMin_im_price(sku1.getIm_price());
        productService.updateProduct(uProduct);

        SkuService skuService = new SkuService(conn);
        List<ProductEDSManagement.SkuOptions> skuOptions = productOptions.getSkus();
        if (skuOptions == null || skuOptions.size() == 0) {
            return;
        }

        for (ProductEDSManagement.SkuOptions skuOption : skuOptions) {
            Sku sku = new Sku();
            sku.product_id = this.uProduct.getProduct_id();
            sku.size = skuOption.getSize();
            sku.sku_code = skuOption.getBarcodes();
            sku.name = this.uProduct.getName();
            sku.coverpic = this.uProduct.getCover_img();
            sku.introduction = this.uProduct.getDescription();
            sku.im_price = sku1.getIm_price();
            sku.in_price = sku1.getIn_price();
            sku.price = this.uProduct.getMax_retail_price();
            sku.created_at = new Date();
            sku.updated_at = new Date();
            sku.enabled = EnabledType.USED;
            sku.last_check = new Date();
            sku.boutique_sku_id = skuOption.getBoutique_sku_id();
            skuService.createSku(sku);
            logger.info("ApiCreateProductService,setSku,sku:" + JSONObject.toJSONString(sku));

            SkuStoreService skuStoreService = new SkuStoreService(conn);
            SkuStore skuStore = new SkuStore();
            skuStore.product_id = this.uProduct.getProduct_id();
            skuStore.sku_id = sku.sku_id;
            skuStore.store = Long.valueOf(skuOption.getStock());
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
            logger.info("ApiCreateProductService,setSku,skuStore:" + JSONObject.toJSONString(skuStore));

            ProductPropertyValueService productPropertyValueService = new ProductPropertyValueService(conn);
            ProductSkuPropertyValue productSkuPropertyValue = new ProductSkuPropertyValue();
            productSkuPropertyValue.product_sku_property_key_id = this.uProductSkuPropertyKey.getProduct_sku_property_key_id();
            productSkuPropertyValue.value = skuOption.getSize();
            productSkuPropertyValue.remark = skuOption.getSizeid() == null ? "" : skuOption.getSizeid();
            productSkuPropertyValue.created_at = new Date();
            productSkuPropertyValue.updated_at = new Date();
            productSkuPropertyValue.enabled = EnabledType.USED;
            ProductSkuPropertyValue pspv = productPropertyValueService.createProductPropertyValue(productSkuPropertyValue);
            logger.info("ApiCreateProductService,setSku,pspv:" + JSONObject.toJSONString(pspv));

            SkuPropertyService skuPropertyService = new SkuPropertyService(conn);
            SkuProperty skuProperty = new SkuProperty();
            skuProperty.sku_id = sku.getSku_id();
            skuProperty.product_sku_property_key_id = this.uProductSkuPropertyKey.getProduct_sku_property_key_id();
            skuProperty.product_sku_property_value_id = pspv.getProduct_sku_property_value_id();
            skuProperty.name = this.uProduct.getName();
            skuProperty.remark = "";
            skuProperty.created_at = new Date();
            skuProperty.updated_at = new Date();
            skuProperty.enabled = EnabledType.USED;
            skuPropertyService.createSkuProperty(skuProperty);
            logger.info("ApiCreateProductService,setSku,skuProperty:" + JSONObject.toJSONString(skuProperty));
        }
    }

    public void setProductInfo(Connection conn) throws Exception {
        CategoryProductInfoService categoryProductInfoService = new CategoryProductInfoService(conn);
        Map<String, Object> categoryProductInfoMap = new HashMap<>();
        categoryProductInfoMap.put("enabled", EnabledType.USED);
        categoryProductInfoMap.put("category_id", this.uProduct.getCategory_id());
        List<CategoryProductInfo> categoryProductInfoList = categoryProductInfoService.getCategoryProductInfoListByCondition(categoryProductInfoMap);
        if (categoryProductInfoList != null && categoryProductInfoList.size() > 0) {
            CategoryProductInfo categoryProductInfo = categoryProductInfoList.get(0);
            ProductInfoService productInfoService = new ProductInfoService(conn);
            ProductInfo productInfo = new ProductInfo();
            productInfo.category_prodcut_info_id = categoryProductInfo.getCategory_product_info_id();
            productInfo.product_id = this.uProduct.getProduct_id();
            productInfo.created_at = new Date();
            productInfo.updated_at = new Date();
            productInfo.remark = "";
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
            productInfo.weight_amount = new BigDecimal(productOptions.getWeight());
            productInfo.length_amount = new BigDecimal(productOptions.getLength());
            productInfo.width_amount = new BigDecimal(productOptions.getWeight());
            productInfo.height_amount = new BigDecimal(productOptions.getHeigit());
            productInfo.size_amount = new BigDecimal(0);
            productInfo.other_property = "";
            productInfoService.createProductInfo(productInfo);
            logger.info("ApiCreateProductService,setProductInfo,productInfo:" + JSONObject.toJSONString(productInfo));
        }
    }

    public void setProductProperty(Connection conn) throws Exception {
        ProductPropertyService productPropertyService = new ProductPropertyService(conn);
        CategoryProductPropertyService categoryProductPropertyService = new CategoryProductPropertyService(conn);
        Map<String, Object> categoryProductPropertMap = new HashMap<>();
        categoryProductPropertMap.put("category_id", productOptions.getCategoryId());
        categoryProductPropertMap.put("enabled", EnabledType.USED);
        List<CategoryProductProperty> categoryProductProperties = categoryProductPropertyService.getCategoryProductPropertyListByCondition(
                categoryProductPropertMap);
        for (CategoryProductProperty cpp : categoryProductProperties) {
            ProductProperty productProperty = new ProductProperty();
            productProperty.category_product_property_id = cpp.getCategory_product_property_id();
            productProperty.product_id = this.uProduct.getProduct_id();
            productProperty.key_name = cpp.getKey_name();
            productProperty.type = cpp.getType();
            productProperty.created_at = new Date();
            productProperty.updated_at = new Date();
            productProperty.value = this.getProductValue(cpp.getKey_name());
            productProperty.remark = "";
            productProperty.enabled = EnabledType.USED;
            if (StringUtils.isNotBlank(productProperty.getValue())) {
                productPropertyService.createProductProperty(productProperty);
                logger.info("ApiCreateProductService,setProductProperty,productProperty:" + JSONObject.toJSONString(productProperty));
            }
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

    public void setProductSkuPropertyKey(Connection conn) throws Exception {
        ProductPropertyKeyService productPropertyKeyService = new ProductPropertyKeyService(conn);
        ProductSkuPropertyKey productSkuPropertyKey = new ProductSkuPropertyKey();
        productSkuPropertyKey.product_id = this.uProduct.getProduct_id();
        productSkuPropertyKey.name = "Size";
        productSkuPropertyKey.remark = "";
        productSkuPropertyKey.created_at = new Date();
        productSkuPropertyKey.updated_at = new Date();
        productSkuPropertyKey.enabled = EnabledType.USED;
        uProductSkuPropertyKey = productPropertyKeyService.createProductPropertyKey(productSkuPropertyKey);
        logger.info("ApiCreateProductService,setProductSkuPropertyKey,productSkuPropertyKey:" + JSONObject.toJSONString(productSkuPropertyKey));
    }

    private void setProduct(Connection conn) throws Exception {
        ProductService productService = new ProductService(conn);
        String name = productOptions.getName();
        if (StringUtils.isBlank(name)) {
            name = productOptions.getBrand_name();
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Product product = new Product();
        product.category_id = Long.parseLong(productOptions.getCategoryId());
        product.vendor_id = vendorOptions.getVendorId();
        product.brand_id = Long.parseLong(productOptions.getBrandId());
        product.product_code = productOptions.getCode();
        product.name = ApiCommonUtils.handleName(name);
        product.description = ApiCommonUtils.handleName(productOptions.getDesc());
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

        productOptions.setVendor_id(product.getVendor_id());
        //        ApiCommonUtils.sortProductOptionsImage(productOptions);

        /* 由于Julian的服装的第一张图不是正面照，需要将其放到所有图片的最后一张,只处理成人的男士、女士的服装，不更改包、鞋和配饰*//*
        if (product.vendor_id == 26L) {
            if (productService.isClothing(product.category_id)) {
                try {
                    List<String> originList = JSONArray.parseArray(productOptions.getCoverImg(), String.class);
                    String one = originList.get(0);
                    originList.remove(0);
                    originList.add(one);
                    productOptions.setCoverImg(new Gson().toJson(originList));
                    productOptions.setDescImg(new Gson().toJson(originList));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        *//* end*/

        /*boolean noImg = this.getNoImg(conn);
        if (noImg) {
            product.cover_img = "[]";
            product.description_img = "[]";
        } else {
            String coverImg = ApiCommonUtils.downloadImgs(productOptions.getCoverImg());
            product.cover_img = coverImg;
            product.description_img = coverImg;
        }*/
        product.cover_img = "[]";
        product.description_img = "[]";
        product.season_code = productOptions.getSeasonCode();
        product.max_retail_price = new BigDecimal(productOptions.getSalePrice());
        product.min_retail_price = new BigDecimal(productOptions.getSalePrice());
        product.last_check = new Date();
        product.designer_id = productOptions.getBrandCode();
        product.color_code = productOptions.getColorCode();

        product = productService.createProduct(product);
        this.uProduct = product;
        logger.info("ApiCreateProductService,setProduct,product:" + JSONObject.toJSONString(product));
        this.saveBoutiqueImage(product, conn);
    }

    private void checkMappingParams(Connection conn) throws Exception {
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
        if (StringUtils.isNotBlank(boutique_category_id)) {
            Map<String, Object> categoryMap = productService.getBoutiqueCategory(vendorOptions.getVendorId().toString(), boutique_category_id);
            if (categoryMap != null) {
                productOptions.setCategoryId(categoryMap.get("category_id").toString());
            }
        }
        if (StringUtils.isNotBlank(productOptions.getCategory1()) && StringUtils.isNotBlank(productOptions.getCategory2()) && StringUtils.isNotBlank(
                productOptions.getCategory3())) {
            Map<String, Object> categoryMap = productService.getCategory(category1, category2, category3);
            if (categoryMap != null) {
                productOptions.setCategoryId(categoryMap.get("category_id").toString());
            } else {
                categoryMap = productService.getThreeCategory(vendorOptions.getVendorId().toString(), category1, category2, category3);
                if (categoryMap != null) {
                    productOptions.setCategoryId(categoryMap.get("category_id").toString());
                }
            }
        }
        if (StringUtils.isBlank(productOptions.getCategoryId()) && StringUtils.isBlank(category3) && StringUtils.isNotBlank(productOptions.getCategory1())
                && StringUtils.isNotBlank(productOptions.getCategory2())) {
            String categoryId = productService.getDeafultCategory(vendorOptions.getVendorId(), category1, category2);
            productOptions.setCategoryId(categoryId);
        }

        if (vendorOptions.getVendorId() == 42 && StringUtils.isBlank(productOptions.getCategoryId()) && StringUtils.isNotBlank(category3) && StringUtils
                .isNotBlank(category1) && StringUtils.isBlank(category2)) {
            Map<String, Object> categoryMap = productService.getCategoryWithoutC2(vendorOptions.getVendorId().toString(), category1, category3);
            if (categoryMap != null) {
                productOptions.setCategoryId(categoryMap.get("category_id").toString());
            }
        }

        // get brand
        if (StringUtils.isNotBlank(brandName)) {
            Map<String, Object> brandMap = productService.getBrand(brandName);
            if (brandMap != null) {
                productOptions.setBrandId(brandMap.get("brand_id").toString());
                productOptions.setBrand_name(brandMap.get("english_name").toString());
            } else {
                brandMap = productService.getBrandMapping(brandName);
                if (brandMap != null) {
                    productOptions.setBrandId(brandMap.get("brand_id").toString());
                    productOptions.setBrand_name(brandMap.get("english_name").toString());
                }
            }
        }

        // get season
        if (StringUtils.isNotBlank(seasonCode)) {
            Map<String, Object> seasonMap = productService.getSeason(seasonCode);
            if (seasonMap != null) {
                mappingSeason = seasonMap.get("season_code").toString();
                productOptions.setSeasonCode(mappingSeason);
            }
        }

        Map<String, Object> mappingCategory = new HashMap<>();
        mappingCategory.put("boutique_category_id", boutique_category_id);
        mappingCategory.put("category1", category1);
        mappingCategory.put("categroy2", category2);
        mappingCategory.put("category3", category3);

        if (StringUtils.isBlank(productOptions.getCategoryId())) {
            throw new UpdateException("category", JSONObject.toJSONString(mappingCategory), ApiErrorTypeEnum.errorType.data_can_not_find_mapping);
        }

        if (StringUtils.isBlank(productOptions.getBrandId())) {
            throw new UpdateException("brand", brandName, ApiErrorTypeEnum.errorType.data_can_not_find_mapping);
        }

        if (StringUtils.isBlank(mappingSeason)) {
            throw new UpdateException("season", seasonCode, ApiErrorTypeEnum.errorType.data_can_not_find_mapping);
        }

        if (!productService.ifBrand(productOptions.getBrandId())) {
            throw new UpdateException("brand", brandName, ApiErrorTypeEnum.errorType.data_can_not_find_mapping);
        }

        if (!productService.ifCategory(productOptions.getCategoryId())) {
            throw new UpdateException("category", JSONObject.toJSONString(mappingCategory), ApiErrorTypeEnum.errorType.data_can_not_find_mapping);
        }

        // AD 特殊判断
        if (vendorOptions.getVendorId().intValue() == 22) {
            boolean flag = productService.duplicateColorBrandByADCreate(productOptions.getBrandCode(), productOptions.getColorCode(),
                    productOptions.getSeasonCode());
            if (flag) {
                throw new UpdateException("color_code,BrandID",
                        productOptions.getBrandCode() + "," + productOptions.getColorCode() + "," + productOptions.getSeasonCode(),
                        ApiErrorTypeEnum.errorType.error_duplicate_product);
            }
        }
    }

    private void saveBoutiqueImage(Product product, Connection conn) {
        try {
            if (product == null) {
                return;
            }

            BoutiqueImageService boutiqueImageService = new BoutiqueImageService(conn);
            Map<String, Object> condition = new HashMap<>();
            condition.put("product_id", product.getProduct_id());

            BoutiqueImage boutiqueImage = boutiqueImageService.getBoutiqueImageByCondition(condition);
            List<String> coverImgs = JSONArray.parseArray(this.productOptions.getCoverImg(), String.class);

            if (boutiqueImage == null) {
                if (coverImgs != null && coverImgs.size() > 0) {
                    boutiqueImage = new BoutiqueImage();
                    boutiqueImage.setCreated_at(new Date());
                    boutiqueImage.setUpdated_at(new Date());
                    boutiqueImage.setImage(JsonTransformUtil.toJson(coverImgs));
                    boutiqueImage.setProduct_id(product.getProduct_id());
                    boutiqueImageService.createBoutiqueImage(boutiqueImage);
                }
            } else {
                List<String> oldImages = JSONArray.parseArray(boutiqueImage.getImage(), String.class);
                List<String> images = new ArrayList<>();
                for (String newImage : coverImgs) {
                    boolean newFlag = true;
                    for (String oldImage : oldImages) {
                        if (newImage.equalsIgnoreCase(oldImage)) {
                            newFlag = false;
                            break;
                        }
                    }
                    if (newFlag) {
                        images.add(newImage);
                    }
                }
                oldImages.addAll(images);
                boutiqueImage.setImage(JsonTransformUtil.toJson(oldImages));
                boutiqueImage.setUpdated_at(new Date());
                boutiqueImageService.updateBoutiqueImage(boutiqueImage);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("saveBoutiqueImage,ErrorMessage:" + e);
        }
    }

    private void checkProductParams(Connection conn) throws Exception {
        String product_code = escape(StringUtils.trim(productOptions.getCode()));
        String designer_id = escape(StringUtils.trim(productOptions.getBrandCode()));
        String brand_name = escape(StringUtils.trim(productOptions.getBrandName()));
        String color_code = escape(StringUtils.trim(productOptions.getColorCode()));
        String cover_img = escape(StringUtils.trim(productOptions.getCoverImg()));
        String price = escape(StringUtils.trim(productOptions.getSalePrice()));
        String product_name = escape(StringUtils.trim(productOptions.getName()));
        String product_desc = escape(StringUtils.trim(productOptions.getDesc()));
        String carryOver = escape(StringUtils.trim(productOptions.getCarryOver()));
        String colorDesc = escape(StringUtils.trim(productOptions.getColorDesc()));
        String desc = escape(StringUtils.trim(productOptions.getDesc()));
        String composition = escape(StringUtils.trim(productOptions.getComposition()));
        String madeIn = escape(StringUtils.trim(productOptions.getMadeIn()));
        String sizeFit = escape(StringUtils.trim(productOptions.getSizeFit()));
        String weight = escape(StringUtils.trim(productOptions.getWeight()));
        String length = escape(StringUtils.trim(productOptions.getLength()));
        String width = escape(StringUtils.trim(productOptions.getWidth()));
        String height = escape(StringUtils.trim(productOptions.getHeigit()));

        Long vendor_id = vendorOptions.getVendorId();
        productOptions.setCode(product_code);
        productOptions.setBrandCode(designer_id);
        productOptions.setBrandName(brand_name);
        productOptions.setColorCode(color_code);
        productOptions.setCoverImg(cover_img);
        productOptions.setSalePrice(price);
        productOptions.setName(product_name);
        productOptions.setDesc(product_desc);
        productOptions.setCarryOver(carryOver);
        productOptions.setColorDesc(colorDesc);
        productOptions.setDesc(desc);
        productOptions.setComposition(composition);
        productOptions.setMadeIn(madeIn);
        productOptions.setSizeFit(sizeFit);

        if (StringUtils.isBlank(weight)) {
            productOptions.setWeight("1");
        } else {
            productOptions.setWeight(weight);
        }
        if (StringUtils.isBlank(length)) {
            productOptions.setLength("0");
        } else {
            productOptions.setLength(length);
        }
        if (StringUtils.isBlank(width)) {
            productOptions.setWidth("0");
        } else {
            productOptions.setWidth(width);
        }
        if (StringUtils.isBlank(height)) {
            productOptions.setHeigit("0");
        } else {
            productOptions.setHeigit(height);
        }

        if (StringUtils.isBlank(product_code)) {
            throw new UpdateException("product_code", product_code, ApiErrorTypeEnum.errorType.error_data_is_null);
        }

        if (vendor_id == null) {
            throw new UpdateException("vendor_id", "", ApiErrorTypeEnum.errorType.error_data_is_null);
        }

        Product product = this.getProduct(conn);
        if (product != null) {
            throw new UpdateException("product_code", product_code, ApiErrorTypeEnum.errorType.error_boutique_id_already_exists);
        }

        if (StringUtils.isBlank(designer_id)) {
            throw new UpdateException("BrandID", designer_id, ApiErrorTypeEnum.errorType.error_data_is_null);
        }

        if (StringUtils.isBlank(brand_name)) {
            throw new UpdateException("brand_name", brand_name, ApiErrorTypeEnum.errorType.error_data_is_null);
        }

        if (StringUtils.isBlank(color_code)) {
            throw new UpdateException("color_code", color_code, ApiErrorTypeEnum.errorType.error_data_is_null);
        }

        if (StringUtils.isBlank(price)) {
            throw new UpdateException("price", price, ApiErrorTypeEnum.errorType.error_data_is_null);
        }

        if (StringUtils.isNotBlank(price)) {
            try {
                BigDecimal bPrice = new BigDecimal(price);
                if (bPrice.intValue() < 10 || bPrice.intValue() > 10000 || bPrice.intValue() == 0) {
                    throw new UpdateException("price", price, ApiErrorTypeEnum.errorType.error_price_out_off);
                }
            } catch (UpdateException e) {
                e.printStackTrace();
                throw new UpdateException(e.getKey(), e.getValue(), e.getErrorType());
            } catch (Exception e) {
                e.printStackTrace();
                logger.info("ApiUpdateStockSerivce,checkParams,price not parse int,errorMessage:" + ExceptionUtils.getExceptionDetail(e) + ",productOptions:"
                        + JSONObject.toJSONString(productOptions));
                throw new UpdateException("price", price, ApiErrorTypeEnum.errorType.error_data_is_not_number);
            }
        }

        List<ProductEDSManagement.SkuOptions> skuOptions = productOptions.getSkus();
        if (skuOptions == null || skuOptions.size() == 0) {
            return;
        }

        for (ProductEDSManagement.SkuOptions skuOption : skuOptions) {
            String sku_code = escape(StringUtils.trim(skuOption.getBarcodes()));
            if (StringUtils.isBlank(sku_code)) {
                sku_code = "#";
            }

            String size = escape(StringUtils.trim(skuOption.getSize()));
            if (StringUtils.isBlank(size)) {
                throw new UpdateException("size", JSONObject.toJSONString(skuOption), ApiErrorTypeEnum.errorType.error_sku_not_exists);
            }

            String stock = skuOption.getStock();
            try {
                Double dStock = Double.parseDouble(stock);
                int bStock = dStock.intValue();
                stock = dStock.intValue() + "";
                if (bStock < 0 || bStock > 100) {
                    throw new UpdateException("store", stock, ApiErrorTypeEnum.errorType.error_stock_out_off);
                }
            } catch (UpdateException e) {
                e.printStackTrace();
                throw new UpdateException(e.getKey(), e.getValue(), e.getErrorType());
            } catch (Exception e) {
                e.printStackTrace();
                logger.info("ApiUpdateStockSerivce,checkParams,stock not parse int,errorMessage:" + ExceptionUtils.getExceptionDetail(e) + ",productOptions:"
                        + JSONObject.toJSONString(productOptions));
                throw new UpdateException("store", stock, ApiErrorTypeEnum.errorType.error_data_is_not_number);
            }

            skuOption.setBarcodes(sku_code);
            skuOption.setStock(stock);
            skuOption.setSize(size);
        }

    }

    private boolean getNoImg(Connection conn) throws Exception {
        if (no_img != null) {
            return no_img;
        }

        ProductService productService = new ProductService(conn);
        no_img = productService.getNoImg(productOptions.getBrandName(), vendorOptions.getVendorId());
        return no_img;
    }

}
