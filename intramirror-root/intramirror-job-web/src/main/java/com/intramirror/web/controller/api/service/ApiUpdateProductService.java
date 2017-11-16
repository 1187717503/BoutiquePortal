package com.intramirror.web.controller.api.service;

import com.alibaba.fastjson.JSONObject;
import static com.intramirror.web.controller.api.service.ApiCommonUtils.escape;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.sql2o.Connection;
import pk.shoplus.DBConnector;
import pk.shoplus.enums.ApiErrorTypeEnum;
import pk.shoplus.model.Product;
import pk.shoplus.model.ProductEDSManagement;
import pk.shoplus.service.ProductService;
import pk.shoplus.util.ExceptionUtils;

/**
 * 修改商品优化
 */
public class ApiUpdateProductService {

    private static final Logger logger = Logger.getLogger(ApiCreateProductService.class);

    private ProductEDSManagement.ProductOptions productOptions ;

    private ProductEDSManagement.VendorOptions vendorOptions ;

    private Product uProduct;

    public Map<String,Object> updateProduct(ProductEDSManagement.ProductOptions productOptions,ProductEDSManagement.VendorOptions vendorOptions){
        Map<String,Object> resultMap = null;

        this.productOptions = productOptions ;
        this.vendorOptions = vendorOptions ;

        Connection conn = null;
        try {
            conn = DBConnector.sql2o.beginTransaction();

            this.checkProductParams(conn);
            this.checkMappingParams(conn);

            resultMap = ApiCommonUtils.successMap();
        } catch (UpdateException e) {
            resultMap = ApiCommonUtils.errorMap(e.getErrorType(),e.getKey(),e.getValue());
        } catch (Exception e) {
            e.printStackTrace();
            resultMap = ApiCommonUtils.errorMap(ApiErrorTypeEnum.errorType.error_runtime_exception,"errorMessage", ExceptionUtils.getExceptionDetail(e));
            if(conn != null) {conn.rollback();conn.close();}
        } finally {
            if(conn != null) {conn.commit();conn.close();}
        }
        return resultMap;
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
                productOptions.setBrand_name(brandMap.get("english_name").toString());
            } else {
                brandMap = productService.getBrandMapping(vendorOptions.getVendorId(),brandName);
                if(brandMap != null) {
                    productOptions.setBrandId(brandMap.get("brand_id").toString());
                    productOptions.setBrand_name(brandMap.get("english_name").toString());
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
            throw new UpdateException("category",JSONObject.toJSONString(mappingCategory), ApiErrorTypeEnum.errorType.data_can_not_find_mapping);
        }

        if(StringUtils.isBlank(productOptions.getBrandId())) {
            throw new UpdateException("brand",brandName, ApiErrorTypeEnum.errorType.data_can_not_find_mapping);
        }

        if(StringUtils.isBlank(mappingSeason)) {
            throw new UpdateException("season",seasonCode, ApiErrorTypeEnum.errorType.data_can_not_find_mapping);
        }
    }

    private void checkProductParams(Connection conn) throws Exception{
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

        if(StringUtils.isBlank(weight)) {
            productOptions.setWeight("1");
        } else {
            productOptions.setWeight(weight);
        }
        if(StringUtils.isBlank(length)) {
            productOptions.setLength("0");
        } else {
            productOptions.setLength(length);
        }
        if(StringUtils.isBlank(width)) {
            productOptions.setWidth("0");
        } else {
            productOptions.setWidth(width);
        }
        if(StringUtils.isBlank(height)) {
            productOptions.setHeigit("0");
        } else {
            productOptions.setHeigit(height);
        }

        if(StringUtils.isBlank(product_code)) {
            throw new UpdateException("product_code",product_code, ApiErrorTypeEnum.errorType.error_data_is_null);
        }

        if(vendor_id == null) {
            throw new UpdateException("vendor_id","", ApiErrorTypeEnum.errorType.error_data_is_null);
        }

        Product product = this.getProduct(conn);
        if(product == null) {
            throw new UpdateException("product_code",product_code, ApiErrorTypeEnum.errorType.error_boutique_id_not_exists);
        }
    }

    private Product getProduct(Connection conn) throws Exception{
        if(this.uProduct == null) {
            ProductService productService = new ProductService(conn);
            Map<String, Object> condition = new HashMap<>();
            condition.put("product_code", productOptions.getCode());
            condition.put("enabled", 1);
            condition.put("vendor_id",vendorOptions.getVendorId());
            Product product = productService.getProductByCondition(condition, null);
            this.uProduct = product;
        }
        return this.uProduct;
    }

}
