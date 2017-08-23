package pk.shoplus.model;

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

import com.alibaba.fastjson15.JSON;
import com.alibaba.fastjson15.JSONArray;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import pk.shoplus.DBConnector;
import pk.shoplus.common.FileUploadHelper;
import pk.shoplus.common.Helper;
import pk.shoplus.enums.ApiErrorTypeEnum;
import pk.shoplus.parameter.EnabledType;
import pk.shoplus.parameter.ProductFeatureType;
import pk.shoplus.parameter.ProductStatusType;
import pk.shoplus.parameter.StatusType;
import pk.shoplus.parameter.VendorStatus;
import pk.shoplus.service.*;
import pk.shoplus.util.ExceptionUtils;

/**
 * 创建商品
 * @author dingyifan
 *
 */
public class ProductEDSManagement {

    private static Logger logger = Logger.getLogger(ProductEDSManagement.class);
	
	public Map<String,Object> createProduct(ProductOptions productOptions,VendorOptions vendorOptions){
		Map<String,Object> resultMap = new HashMap<String,Object>();

		// ProductOptions转换成List对象
		List<String> productList = this.convertProductList(productOptions);
		this.populateResult(productOptions,productList, vendorOptions.getVendorId(), resultMap, vendorOptions.getStoreCode(), vendorOptions.getApiConfigurationId());
		return resultMap;
	}
	
	/**
     * 根据发来的数据增加商品信息
     *
     *
     * @param columnDataList
     * @param vendorId
     * @return
     */
	protected boolean populateResult(ProductOptions productOptions,List<String> columnDataList, Long vendorId, Map<String, Object> result, String storeCode, Long apiConfigurationId) {
        Connection conn = null ;
        try {
            conn = DBConnector.sql2o.beginTransaction();

            result.put("status",StatusType.FAILURE);

            //check params
            boolean b = validateParam(conn, columnDataList, result);
            if (b) {
                return b;
            }

            Vendor vendor = this.getVendor(conn, vendorId, result);
            if (null == vendor) {
                result.put("info","create product - " + ApiErrorTypeEnum.errorType.vendor_is_null.getDesc() + "vendor_id:"+vendorId );
                result.put("error_enum", ApiErrorTypeEnum.errorType.Data_can_not_be_null);
                result.put("key","vendor_id");
                result.put("value",vendorId);
                return true;
            }

            //get category id
            CategoryService categoryService = new CategoryService(conn);
            String categoryId = columnDataList.get(10).toString();
            Category category = categoryService.getCategoryById(Long.parseLong(categoryId));// this.getCategory(columnDataList, categoryService, mappingCategoryService, result, storeCode);
            if (null == category) {
                result.put("info","create product - " + ApiErrorTypeEnum.errorType.category_not_exist.getDesc() + "category_id:"+categoryId );
                result.put("error_enum", ApiErrorTypeEnum.errorType.Data_can_not_find_mapping);
                result.put("key","category_id");
                result.put("value",categoryId);
                return true;
            }

            // 检查category是否是最后一级
            if (!categoryService.isLastNode(category.getCategory_id())) {
                result.put("info","create product - " + ApiErrorTypeEnum.errorType.category_not_exist.getDesc() + "category_id:"+categoryId );
                result.put("error_enum", ApiErrorTypeEnum.errorType.Data_can_not_find_mapping);
                result.put("key","category_id");
                result.put("value",categoryId);
                return true;
            }

            /** start update by dingyifan 2017-06-15 */
            boolean no_img = false;
            String englishName = columnDataList.get(5);
            ApiBrandMapService apiBrandMapService = new ApiBrandMapService(conn);
            String brandId = apiBrandMapService.getBrandNameByBrand(englishName);
            if(StringUtils.isBlank(brandId)) {
                brandId = apiBrandMapService.getBrandNameByBrandMapping(englishName,apiConfigurationId.toString());
                if(StringUtils.isBlank(brandId)) {
                    result.put("info","create product - " + ApiErrorTypeEnum.errorType.brand_not_exist.getDesc() + "brand_name:"+englishName);
                    result.put("error_enum", ApiErrorTypeEnum.errorType.Data_can_not_find_mapping);
                    result.put("key","brand_name");
                    result.put("value",englishName);
                    return true;
                }

            }

            BrandService brandService = new BrandService(conn);
            Brand brand = brandService.getBrandById(Long.parseLong(brandId));

            if(brand == null) {
                result.put("info","create product - " + ApiErrorTypeEnum.errorType.brand_not_exist.getDesc() + "brand_name:"+englishName);
                result.put("error_enum", ApiErrorTypeEnum.errorType.Data_can_not_find_mapping);
                result.put("key","brand_name");
                result.put("value",englishName);
                return true;
            }

            if(brand != null) {
                Map<String,Object> getApiBrandMaps = new HashMap<>();
                getApiBrandMaps.put("api_configuration_id",apiConfigurationId.toString());
                getApiBrandMaps.put("enabled",EnabledType.USED);
                getApiBrandMaps.put("no_img",EnabledType.USED);
                getApiBrandMaps.put("brand_id",brandId);
                List<ApiBrandMap> apiBrandMapList = apiBrandMapService.getApiBrandMapListByCondition(getApiBrandMaps);
                if(apiBrandMapList != null &&  apiBrandMapList.size() > 0) {
                    no_img = true;
                }
            }

            /** end update by dingyifan 2017-06-15 */

            // Save to product table and get result product_id
            Product product = this.setProduct(category, brand, columnDataList, vendor, result, conn,productOptions,no_img);
            ProductService productService = new ProductService(conn);
            SeasonService seasonService = new SeasonService(conn);
            logger.info(" start productedsmanagement createproduct select season_code by boutiquecode : " + productOptions.getSeasonCode());
            String season_code = seasonService.selSeasonCodeByBoutiqueCode(productOptions.getSeasonCode());
            logger.info(" end productedsmanagement createproduct select season_code by boutiquecode : season_code " + season_code);

            if(org.apache.commons.lang3.StringUtils.isBlank(season_code) || org.apache.commons.lang3.StringUtils.isBlank(productOptions.getSeasonCode())) {
                result.put("info","create product - " + ApiErrorTypeEnum.errorType.season_not_exist.getDesc() + "season_code:"+productOptions.getSeasonCode() );
                result.put("error_enum", ApiErrorTypeEnum.errorType.Data_can_not_find_mapping);
                result.put("key","season_code");
                result.put("value",productOptions.getSeasonCode());
                return true;
            }

            if(StringUtils.isNotBlank(season_code)) {
                product.season_code = season_code;
            } else {
                product.season_code = productOptions.getSeasonCode();
            }
            product = productService.createProduct(product);

            // 在category上增加数据
            List<ProductSkuPropertyKey> propertyKeyIdArr = this.createProductSku(conn, category, product, result, columnDataList);

            // 4. 根据sku_list，在sku表,sku_store表以及sku_property表中插入相关数据
            // 4.1) 把sku_list 转换成 array
            this.createSku(conn, columnDataList, product, propertyKeyIdArr, result, vendor,productOptions);

            // 5. 在vendor_product_cashback表中加入cash_back_list数据
            // 5.1) 把cash_back_list 转换成 jsonarray
            this.createCashBack(product, conn, result);

            // 在vendor category 表中加入数据
            // 1.找到数据
            Map<String, Object> brandCategoryMap = new HashMap<String, Object>();
            brandCategoryMap.put("brand_id", product.brand_id);
            brandCategoryMap.put("category_id", product.category_id);
            brandCategoryMap.put("enabled", EnabledType.USED);
            BrandCategoryService brandCategoryService = new BrandCategoryService(conn);
            List<BrandCategory> brandCategoryList = brandCategoryService.getBrandCategoryListByCondition(null,
                    brandCategoryMap);
            // 2.如果无加入
            if (brandCategoryList == null || brandCategoryList.size() <= 0) {
                BrandCategory brandCategory = new BrandCategory();
                brandCategory.brand_id = product.brand_id;
                brandCategory.category_id = product.category_id;
                brandCategory.created_at = Helper.getCurrentTimeToUTCWithDate();
                brandCategory.updated_at = Helper.getCurrentTimeToUTCWithDate();
                brandCategory.enabled = EnabledType.USED;
                brandCategoryService.createBrandCategory(brandCategory);
            }

            // 6.在-----表中添加基本属性信息costList
            CategoryProductInfoService categoryProductInfoService = new CategoryProductInfoService(conn);
            ProductInfoService productInfoService = new ProductInfoService(conn);
            List<Map<String, String>> costList = getCostList(columnDataList, category, categoryProductInfoService);
            ProductInfo productInfo = null;
            productInfo = createProductInfo(costList, categoryProductInfoService, productInfoService, product, result);

            // 7.创建productproperty信息
            List<CategoryProductProperty> cppList = this.getCategoryProductProperty(conn, category);
            createProductProperty(conn, cppList, columnDataList, product, result);

            // 为了减少并发情况下当前product已经被其他进程插入，在commit前再做一次校验
            // TODO: commit过程中的发生的不一致性也需要处理
            Map<String, Object> condition = new HashMap<String,Object>();
            condition.put("product_code", product.product_code);
            condition.put("enabled", 1);
            long pcount = productService.countProductByCondition(condition);
            logger.info(" create product service by product : " + new Gson().toJson(product));

            if (pcount > 1) {
                result.put("info","create product -- " + ApiErrorTypeEnum.errorType.boutique_create_error.getDesc() + "boutique_id:"+productOptions.getCode() );
                result.put("error_enum", ApiErrorTypeEnum.errorType.Runtime_exception);
                result.put("key","create product error!");
                result.put("value","null");
                conn.rollback();
            } else {
                result.put("product", product);
                result.put("status", StatusType.SUCCESS);
                conn.commit();
            }
            return false;
        } catch (Exception e) {
            if(conn != null) {conn.rollback();conn.close();}
            e.printStackTrace();
            result.put("status",StatusType.FAILURE);
            result.put("key","exception");
            result.put("value",ExceptionUtils.getExceptionDetail(e));
            result.put("info","create product - " + ApiErrorTypeEnum.errorType.Runtime_exception.getDesc() + " error message:" + ExceptionUtils.getExceptionDetail(e));
            result.put("error_enum", ApiErrorTypeEnum.errorType.Runtime_exception);
            return true;
        } finally {
            if(conn != null) {conn.close();}
        }
    }
	/**
     * 获取value值
     *
     * @param keyName
     * @param columnDataList
     * @return
     */
    public String getValue(String keyName, List<String> columnDataList) {
        String value = "";
        switch (keyName) {
            case "Composition":
                value = columnDataList.get(12);
                break;
            case "CarryOver":
                value = columnDataList.get(4);
                break;
            case "ColorDescription":
                value = columnDataList.get(7);
                break;
            case "MadeIn":
                value = columnDataList.get(13);
                break;
            case "SizeFit":
                value = columnDataList.get(14);
                break;
            case "ColorCode":
                value = columnDataList.get(6);
                break;
            case "BrandID":
                value = columnDataList.get(2);
                break;
            case "SeasonCode":
                value = columnDataList.get(3);
                break;
        }
        return value;
    }
	 /**
     * 创建商品属性
     *
     * @param conn
     * @param categoryProductPropertyList
     * @param columnDataList
     * @param product
     */
    public void createProductProperty(Connection conn, List<CategoryProductProperty> categoryProductPropertyList, List<String> columnDataList, Product product, Map<String, Object> result) throws Exception{
        if (null != categoryProductPropertyList && categoryProductPropertyList.size() > 0) {
            for (CategoryProductProperty categoryProductProperty : categoryProductPropertyList) {
                if (null != categoryProductProperty) {
                    ProductProperty productProperty = new ProductProperty();
                    productProperty.category_product_property_id = categoryProductProperty.category_product_property_id;
                    productProperty.product_id = product.product_id;
                    productProperty.key_name = categoryProductProperty.key_name;
                    productProperty.type = categoryProductProperty.type;
                    productProperty.created_at = Helper.getCurrentTimeToUTCWithDate();
                    productProperty.updated_at = Helper.getCurrentTimeToUTCWithDate();
                    productProperty.value = getValue(categoryProductProperty.key_name, columnDataList);
                    productProperty.remark = "eds";
                    productProperty.enabled = EnabledType.USED;

                    if(StringUtils.isNotBlank(productProperty.value)) {
                    	// 创建productproperty
                        ProductInfoPropertyService productInfoPropertyService = new ProductInfoPropertyService(conn);
                        productProperty = productInfoPropertyService.createProductInfoProperty(productProperty);
                        if (productProperty == null) {
                            result.put("info", "failed to create sku property");
                            result.put("status", StatusType.DATABASE_ERROR);
                        }
                    }
                }
            }
        }
    }
	
	/**
     * 获取类目信息
     *
     * @param conn
     * @param category
     * @return
     */
    public List<CategoryProductProperty> getCategoryProductProperty(Connection conn, Category category) throws Exception{
        List<CategoryProductProperty> cppList = null;
        CategoryProductPropertyService cppService = new CategoryProductPropertyService(conn);

        Map<String, Object> condition = new HashMap<>();
        condition.put("category_id", category.getCategory_id());
        condition.put("enabled", 1);

        cppList = cppService.getCategoryProductPropertyListByCondition(condition);

        return cppList;
    }
	
	/**
     * 创建商品基本信息
     *
     * @param costList
     * @param categoryProductInfoService
     * @param productInfoService
     * @param product
     * @param result
     * @return
     */
    public ProductInfo createProductInfo(List<Map<String, String>> costList, CategoryProductInfoService categoryProductInfoService,
                                         ProductInfoService productInfoService, Product product, Map<String, Object> result) throws Exception {

        JsonArray costListArray = new JsonParser().parse(JSONArray.toJSONString(costList)).getAsJsonArray();
        JsonObject costObj = costListArray.get(0).getAsJsonObject();

        if ("".equals(costObj.get("weight").getAsString()) && "".equals(costObj.get("len").getAsString())
                && "".equals(costObj.get("width").getAsString()) && "".equals(costObj.get("height").getAsString())
                && "".equals(costObj.get("size").getAsString())) {
            result.put("product", null);
            result.put("status", StatusType.PARAMETEREXIST);
            //return result;
        }

        // 查询category product info信息
        Map<String, Object> categoryProductInfoCondition = new HashMap<String, Object>();
        categoryProductInfoCondition.put("enabled", EnabledType.USED);
        categoryProductInfoCondition.put("category_product_info_id", costObj.get("id").getAsLong());


        List<CategoryProductInfo> categoryProductInfoList = null;
        categoryProductInfoList = categoryProductInfoService
                    .getCategoryProductInfoListByCondition(categoryProductInfoCondition);

        CategoryProductInfo categoryProductInfo = null;
        if (categoryProductInfoList.size() > 0) {
            categoryProductInfo = categoryProductInfoList.get(0);
        } else {
            result.put("product", null);
            result.put("status", StatusType.DATABASE_ERROR);
            //return result;
        }
        // 生成Product Info信息
        ProductInfo productInfo = new ProductInfo();
        productInfo.category_prodcut_info_id = costObj.get("id").getAsLong();
        productInfo.product_id = product.product_id;
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

        if (productInfo.weight) {
            JsonElement weight = costObj.get("weight");
            if (null != weight && !"".equals(weight.getAsString())) {
                productInfo.weight_amount = weight.getAsBigDecimal();
            } else {
                productInfo.weight_amount = new BigDecimal(1);
            }
        }
        if (productInfo.length) {
            JsonElement len = costObj.get("len");
            if (null != len && !"".equals(len.getAsString())) {
                productInfo.length_amount = len.getAsBigDecimal();
            } else {
                productInfo.length_amount = new BigDecimal(0);
            }
        }
        if (productInfo.width) {
            JsonElement width = costObj.get("width");
            if (null != width && !"".equals(width.getAsString())) {
                productInfo.width_amount = width.getAsBigDecimal();
            } else {
                productInfo.width_amount = new BigDecimal(0);
            }
        }
        if (productInfo.height) {
            JsonElement height = costObj.get("height");
            if (null != height && !"".equals(height.getAsString())) {
                productInfo.height_amount = height.getAsBigDecimal();
            } else {
                productInfo.height_amount = new BigDecimal(0);
            }
        }
        if (productInfo.size) {
            JsonElement size = costObj.get("size");
            if (null != size && !"".equals(size.getAsString())) {
                productInfo.size_amount = size.getAsBigDecimal();
            } else {
                productInfo.size_amount = new BigDecimal(0);
            }
        }

        productInfo.other_property = "";

        // 创建productInfo
        productInfo = productInfoService.createProductInfo(productInfo);

        return productInfo;
    }
	
	/**
     * 获取cost list
     *
     * @param columnDataList
     * @param category
     * @param categoryProductInfoService
     * @return
     */
    public List<Map<String, String>> getCostList(List<String> columnDataList, Category category, CategoryProductInfoService categoryProductInfoService) {
        List<Map<String, String>> costList = null;
        Map<String, String> map = null;
        Map<String, Object> categoryProductInfoCondition = new HashMap<String, Object>();
        categoryProductInfoCondition.put("enabled", EnabledType.USED);
        categoryProductInfoCondition.put("category_id", category.getCategory_id());
        try {
            List<CategoryProductInfo> categoryProductInfoList = categoryProductInfoService
                    .getCategoryProductInfoListByCondition(categoryProductInfoCondition);
            if (null != categoryProductInfoList && categoryProductInfoList.size() > 0) {
                costList = new ArrayList<>();
                CategoryProductInfo categoryProductInfo = categoryProductInfoList.get(0);
                map = new HashMap<>();
                Long categoryInfoId = categoryProductInfo.getCategory_product_info_id();
                map.put("id", categoryInfoId != null ? categoryInfoId.toString() : "");
                map.put("weight", columnDataList.get(17));
                map.put("len", columnDataList.get(18));
                map.put("width", columnDataList.get(19));
                map.put("height", columnDataList.get(20));
                map.put("size", "");
                costList.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return costList;
    }
    
    /**
     * 获取返点信息
     *
     * @return
     */
    public List<Map<String, String>> getCashBackList() {
        List<Map<String, String>> cashBackList = new ArrayList<>();

		/*Map<String, String> map = new HashMap<>();
        map.put("type", "");
		map.put("full", "");
		map.put("back", "");

		cashBackList.add(map);*/

        return cashBackList;
    }
	
	 public void createCashBack(Product product, Connection conn, Map<String, Object> result) {
	        List<Map<String, String>> cashBackList = getCashBackList();
	        JsonArray cashBackArray = new JsonParser().parse(JSONArray.toJSONString(cashBackList)).getAsJsonArray();

	        // 5.2) 添加记录到表 vendor_product_cashback_level中
	        for (int i = 0; i < cashBackArray.size(); i++) {
	            VendorProductCashbackLevel vendorProductCashbackLevel = new VendorProductCashbackLevel();
	            vendorProductCashbackLevel.vendor_id = product.vendor_id;
	            vendorProductCashbackLevel.product_id = product.product_id;
	            vendorProductCashbackLevel.shop_grade = 0l;
	            vendorProductCashbackLevel.type = cashBackArray.get(i).getAsJsonObject().get("type").getAsInt();
	            vendorProductCashbackLevel.name = "";
	            vendorProductCashbackLevel.full = cashBackArray.get(i).getAsJsonObject().get("full").getAsInt();
	            if (vendorProductCashbackLevel.type == 1001) {
	                vendorProductCashbackLevel.back = cashBackArray.get(i).getAsJsonObject().get("back")
	                        .getAsBigDecimal();
	            }
	            if (vendorProductCashbackLevel.type == 1000) {
	                vendorProductCashbackLevel.back = cashBackArray.get(i).getAsJsonObject().get("back")
	                        .getAsBigDecimal().divide(new BigDecimal(100));
	            }
	            vendorProductCashbackLevel.begin_at = product.publish_at;
	            vendorProductCashbackLevel.valid_at = product.valid_at;
	            vendorProductCashbackLevel.last_cash_back_at = product.publish_at;
	            vendorProductCashbackLevel.period = 0;
	            vendorProductCashbackLevel.remark = "eds";
	            vendorProductCashbackLevel.status = 0;
	            vendorProductCashbackLevel.created_at = Helper.getCurrentTimeToUTCWithDate();
	            vendorProductCashbackLevel.updated_at = Helper.getCurrentTimeToUTCWithDate();
	            vendorProductCashbackLevel.enabled = EnabledType.USED;
	            VendorProductCashbackLevelService vendorProductCashbackLevelService = new VendorProductCashbackLevelService(
	                    conn);
	            try {
	                vendorProductCashbackLevel = vendorProductCashbackLevelService
	                        .createVendorProductCashbackLevel(vendorProductCashbackLevel);
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	            if (vendorProductCashbackLevel == null) {
	                // 返回状态， product实体
	                result.put("info", "failed to create cash back");
	                result.put("status", StatusType.DATABASE_ERROR);
	            }
	        }
	    }
	 public List<Map<String, Object>> getSkuList(List<String> rowDataList) {
	        List<Map<String, Object>> skuList = null;
	        String price = rowDataList.get(21);
	        String store = "";
	        String skuCode = "";
	        String size = "";
	        Map<String, Object> map = null;
	        List<String> sizeList = null;

	        if (null != rowDataList && rowDataList.size() > 22) {
	            skuList = new ArrayList<>();
	            for (int i = 22; i < rowDataList.size(); i = i + 3) {


	                size = rowDataList.get(i);

	                if ((i + 1) < rowDataList.size()) {
	                    skuCode = rowDataList.get(i + 1);
	                } else {
	                    skuCode = "";
	                    break;
	                }
	                if ((i + 2) < rowDataList.size()) {
	                    store = rowDataList.get(i + 2);
	                } else {
	                    store = "";
	                    break;
	                }

	                if (StringUtils.isNotBlank(size)
	                        && StringUtils.isNotBlank(skuCode)
	                        && StringUtils.isNotBlank(store)
	                        && StringUtils.isNotBlank(price)) {
	                    int storeNum = (new Double(store)).intValue();
	                    sizeList = new ArrayList<>();
	                    sizeList.add(size);

	                    map = new HashMap<>();
	                    map.put("property", sizeList);
	                    map.put("skuCode", skuCode);
	                    map.put("store", storeNum < 0 ? 0 : storeNum);
	                    map.put("price", price);
	                    map.put("storeMini", 0);
	                    map.put("inPrice", 0);

	                    skuList.add(map);
	                } else {
	                    //TODO 异常日志
	                }
	            }
	        }

	        return skuList;
	    }
	public void createSku(Connection conn, List<String> columnDataList, Product product, List<ProductSkuPropertyKey> propertyKeyIdArr, Map<String, Object> result, Vendor vendor,ProductOptions productOptions) throws Exception {
        List<Map<String, Object>> skuList = this.getSkuList(columnDataList);
        JsonArray skuArray = new JsonParser().parse(JSONArray.toJSONString(skuList)).getAsJsonArray();
        CategoryProductInfoService categoryProductInfoService = new CategoryProductInfoService(conn);
        SkuPropertyService skuPropertyService = new SkuPropertyService(conn);
        PriceChangeRuleService priceChangeRuleService = new PriceChangeRuleService(conn);
        Map<String, Object> priceRuleList = null;
        Integer vendorDiscount = 100;
        Integer adminDiscount = 100;
        //获取商品的折扣率
        if (null != product.getCategory_id()
                && null != product.getBrand_id()
                && null != product.getProduct_code()
                && !"".equals(product.getProduct_code())) {
            priceRuleList = priceChangeRuleService.getCurrentChangeRuleByProduct(product);
        }

        if (null != priceRuleList) {
            vendorDiscount = Integer.parseInt(priceRuleList.get("price1").toString());
            adminDiscount = Integer.parseInt(priceRuleList.get("price3").toString());
        }

        // 4.2) 每个sku 实体的数据加入sku表
        for (int i = 0; i < skuArray.size(); i++) {
            JsonObject skuObj = skuArray.get(i).getAsJsonObject();
            Sku sku = new Sku();
            sku.product_id = product.product_id;
            sku.sku_code = skuObj.get("skuCode").getAsString();
            sku.name = product.name;
            sku.coverpic = product.cover_img;
            sku.introduction = product.description;
            sku.price = new BigDecimal(skuObj.get("price").getAsString().trim());
//            sku.price = new BigDecimal(skuObj.get("price").getAsString().trim());
            sku.in_price = new BigDecimal(sku.price.doubleValue() * vendorDiscount / ((1 + 0.22) * 100));
            sku.im_price = new BigDecimal(sku.price.doubleValue() * adminDiscount / ((1) * 100));
            sku.created_at = Helper.getCurrentTimeToUTCWithDate();
            sku.updated_at = Helper.getCurrentTimeToUTCWithDate();
            sku.enabled = EnabledType.USED;

            if(StringUtils.isNotBlank(productOptions.getFullUpdateProductFlag())){
                if(productOptions.getFullUpdateProductFlag().equals("1")) {
                    sku.full_modify_date = new Date();
                    logger.info("createProduct set full modify date sku : " + new Gson().toJson(sku));
                }
            }
            SkuService skuService = new SkuService(conn);

            sku = skuService.createSku(sku);
            if (sku == null) {
                result.put("info", "line failed to create sku");
                result.put("status", StatusType.DATABASE_ERROR);
                //return result;
            }

            // 查询category product info信息
            Map<String, Object> categoryProductInfoCondition = new HashMap<String, Object>();
            CategoryProductInfo categoryProductInfo = null;

            categoryProductInfoCondition.put("enabled", EnabledType.USED);
            categoryProductInfoCondition.put("category_id", product.category_id);


            List<CategoryProductInfo> categoryProductInfoList = null;
            categoryProductInfoList = categoryProductInfoService
                        .getCategoryProductInfoListByCondition(categoryProductInfoCondition);


            if (categoryProductInfoList.size() > 0) {
                categoryProductInfo = categoryProductInfoList.get(0);
            } else {
                result.put("info", "failed to get the category of product information");
                result.put("status", StatusType.DATABASE_ERROR);
//                return result;
            }

            // 4.3) 每个sku 实体的数据加入sku_store表
            SkuStore skuStore = new SkuStore();
            skuStore.sku_id = sku.sku_id;
            skuStore.product_id = product.product_id;
            skuStore.store = new BigDecimal(skuObj.get("store").getAsString().trim()).longValue();
            skuStore.remind = new BigDecimal(skuObj.get("storeMini").getAsString().trim()).intValue();
            skuStore.ordered = 0;
            skuStore.confirm = 0;
            skuStore.ship = 0;
            skuStore.finished = 0;
            skuStore.clear = 0;
            skuStore.changed = 0;
            skuStore.returned = 0;
            // skuStore.agree_return_rate =
            // skuObj.get("agreeReturnRate").getAsBigDecimal();
            skuStore.agree_return_rate = categoryProductInfo.agree_return_rate == null ? new BigDecimal(0) : categoryProductInfo.agree_return_rate;
            skuStore.created_at = Helper.getCurrentTimeToUTCWithDate();
            skuStore.updated_at = Helper.getCurrentTimeToUTCWithDate();
            skuStore.enabled = EnabledType.USED;

            SkuStoreService skuStoreService = new SkuStoreService(conn);
            skuStore = skuStoreService.createSkuStore(skuStore);

            if (skuStore == null) {
                // 返回状态， product实体
                result.put("info", "failed to create sku store");
                result.put("status", StatusType.DATABASE_ERROR);
                //return result;
            }

            // 5.每个sku数据的属性加入sku_property表
            // 5.1) 通过property 找到

            for (int j = 0; j < propertyKeyIdArr.size(); j++) {

                String propertyValue = skuObj.get("property").getAsJsonArray().get(j).getAsString();

                for (int k = 0; k < propertyKeyIdArr.get(j).getPropertyValueList().size(); k++) {
                    // propertyKeyIdArr.get(j).getPropertyValueList().get(k).value);
                    // 找到相同的value，找到value id，填入 sku_property 表
                    if (propertyValue.equals(propertyKeyIdArr.get(j).getPropertyValueList().get(k).value)) {
                        // 5.3) 填入 sku_property 表
                        SkuProperty skuProperty = new SkuProperty();
                        skuProperty.sku_id = sku.sku_id;
                        skuProperty.product_sku_property_key_id = propertyKeyIdArr
                                .get(j).product_sku_property_key_id;
                        skuProperty.product_sku_property_value_id = propertyKeyIdArr.get(j).getPropertyValueList()
                                .get(k).product_sku_property_value_id;
                        skuProperty.name = sku.name;
                        skuProperty.remark = "eds";
                        skuProperty.created_at = Helper.getCurrentTimeToUTCWithDate();
                        skuProperty.updated_at = Helper.getCurrentTimeToUTCWithDate();
                        skuProperty.enabled = EnabledType.USED;

                        skuProperty = skuPropertyService.createSkuProperty(skuProperty);


                        if (skuProperty == null) {
                            result.put("info", "failed to create sku property");
                            result.put("status", StatusType.DATABASE_ERROR);
                        }
                    }
                }
            }

        }
    }
	/**
     * 获取分类类目下的属性key列表
    *
    * @param category
    * @param categorySkuPropertyKeyService
    * @return
    */
   public List<String> getPropertyKeyArray(Category category, CategorySkuPropertyKeyService categorySkuPropertyKeyService) {
       List<String> propertyKeyList = null;
       try {
           propertyKeyList = categorySkuPropertyKeyService.getCategoryPropertyKeyNameListByCategory(category.getCategory_id());
       } catch (Exception e) {
           e.printStackTrace();
       }
       return propertyKeyList;
   }
	
	 public List<ProductSkuPropertyKey> createProductSku(Connection conn, Category category, Product product, Map<String, Object> result, List<String> cloumnDataList) {
	        // 2. 获取product_id，根据property_key_list 在 product_sku_property_key
	        // 插入数据
	        // 2.1) 把property_key_list转换成数组
	        CategorySkuPropertyKeyService categorySkuPropertyKeyService = new CategorySkuPropertyKeyService(conn);
	        List<String> keyList = getPropertyKeyArray(category, categorySkuPropertyKeyService);
	        JsonArray propertyKeyArray = new JsonParser().parse(JSONArray.toJSONString(keyList)).getAsJsonArray();

	        // 2.2) 把property_value_list转换成数组
	        //CategorySkuPropertyValueService categorySkuPropertyValueService = new CategorySkuPropertyValueService(conn);
	        //List<Map<String, List<String>>> valueList = getPropertyValueArray(category, categorySkuPropertyValueService);
	        List<Map<String, List<String>>> valueList = null;
	        List<String> list = new ArrayList<>();
	        Map<String, List<String>> map = new HashMap<>();
	        String size = "";
	        for (int i = 22; i < cloumnDataList.size(); i = i + 3) {
	            size = cloumnDataList.get(i);
	            list.add(size);
	        }
	        map.put("Size", list);

	        valueList = new ArrayList<Map<String, List<String>>>();
	        valueList.add(map);

	        JsonArray propertyValueArray = new JsonParser().parse(JSONArray.toJSONString(valueList)).getAsJsonArray();

	        // 2.3) 把每个key 插入到 product_sku_property_key表中
	        List<ProductSkuPropertyKey> propertyKeyIdArr = new ArrayList<ProductSkuPropertyKey>();
	        ProductPropertyKeyService productPropertyKeyService = new ProductPropertyKeyService(conn);
	        ProductPropertyValueService productPropertyValueService = new ProductPropertyValueService(conn);
	        for (int i = 0; i < propertyKeyArray.size(); i++) {
	            // 插入数据库
	            ProductSkuPropertyKey productSkuPropertyKey = new ProductSkuPropertyKey();
	            productSkuPropertyKey.product_id = product.product_id;
	            productSkuPropertyKey.name = propertyKeyArray.get(i).getAsString();
	            productSkuPropertyKey.remark = "eds";
	            productSkuPropertyKey.created_at = Helper.getCurrentTimeToUTCWithDate();
	            productSkuPropertyKey.updated_at = Helper.getCurrentTimeToUTCWithDate();
	            productSkuPropertyKey.enabled = EnabledType.USED;

	            try {
	                productSkuPropertyKey = productPropertyKeyService.createProductPropertyKey(productSkuPropertyKey);
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	            if (productSkuPropertyKey == null) {
	                result.put("info", "Failed to create commodity SKU property key");
	                result.put("status", StatusType.DATABASE_ERROR);
	            }

	            productSkuPropertyKey.propertyValueList = new ArrayList<>();
	            // 2.4) 根据不同的key 把每个value 插入到 product_sku_property_value表中
	            // 3.
	            // 获取每条propery_key的纪录，获取product_sku_property_key_id，根据property_value_list，
	            // 在 product_propery_value表插入数据
	            for (int j = 0; j < propertyValueArray.size(); j++) {
	                // 如果有
	                if (propertyValueArray.get(j).getAsJsonObject().has(productSkuPropertyKey.name)) {
	                    JsonArray valueArray = propertyValueArray.get(j).getAsJsonObject()
	                            .get(productSkuPropertyKey.name).getAsJsonArray();
	                    for (int k = 0; k < valueArray.size(); k++) {
	                        ProductSkuPropertyValue productPropertyValue = new ProductSkuPropertyValue();
	                        productPropertyValue.product_sku_property_key_id = productSkuPropertyKey.product_sku_property_key_id;
	                        productPropertyValue.value = valueArray.get(k).getAsString();
	                        productPropertyValue.remark = cloumnDataList.get(8) == null ? "eds" : cloumnDataList.get(8);
	                        productPropertyValue.created_at = Helper.getCurrentTimeToUTCWithDate();
	                        productPropertyValue.updated_at = Helper.getCurrentTimeToUTCWithDate();
	                        productPropertyValue.enabled = EnabledType.USED;

	                        try {
	                            productPropertyValue = productPropertyValueService
	                                    .createProductPropertyValue(productPropertyValue);
	                        } catch (Exception e) {
	                            e.printStackTrace();
	                        }
	                        if (productPropertyValue.product_sku_property_value_id == null) {
	                            result.put("info", "Failed to create commodity SKU property value");
	                            result.put("status", StatusType.DATABASE_ERROR);
	                        }

	                        productSkuPropertyKey.propertyValueList.add(productPropertyValue);
	                    }
	                }
	            }

	            // 记录插入productPropertyKey数据
	            propertyKeyIdArr.add(productSkuPropertyKey);
	        }

	        return propertyKeyIdArr;
	    }
	
	public Product setProduct(Category category, Brand brand, List<String> cloumnDataList, Vendor vendor, Map<String, Object> result,Connection conn,ProductOptions productOptions,boolean no_img) throws Exception{
        Product product = null;
        try {
            product = new Product();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            product.category_id = category.getCategory_id();
            product.vendor_id = vendor.vendor_id;
            product.brand_id = brand.getBrand_id();
            product.product_code = cloumnDataList.get(1);
            String coverImgs = convertImgPath(cloumnDataList.get(15));

            /** start update by dingyifan 2017-07-31 */
            if(StringUtils.isBlank(coverImgs) || "[]".equals(coverImgs)) {
                // 记录上传图片出错日志
                if(org.apache.commons.lang3.StringUtils.isNotBlank(productOptions.getCoverImg())) {
                    ApiLogImageErrorService apiLogImageErrorService = new ApiLogImageErrorService(conn);
                    ApiLogImageError apiLogImageError = new ApiLogImageError();
                    apiLogImageError.setBoutique_id(productOptions.getCode());
                    apiLogImageError.setImages(productOptions.getCoverImg());
                    apiLogImageError.setError_type(1);
                    apiLogImageError.setError_info(new Gson().toJson(productOptions));
                    apiLogImageErrorService.create(apiLogImageError);
                }
            }
            /** start update by dingyifan 2017-07-31 */

            if(!no_img) {
                product.cover_img = coverImgs;
            }

            if(StringUtils.isNotBlank(product.cover_img)) {
                logger.info(" start record create picture !!!");
                ApiLogBatchService apiLogBatchService = new ApiLogBatchService(conn);
                apiLogBatchService.createApiLogBatch(product.product_code,productOptions.getImgByFilippo());
                logger.info(" end record create picture !!!");
            }

            product.name = cloumnDataList.get(0);

            if(StringUtils.isBlank(product.name)) {
                product.name = category.getName();
            }

            product.description = cloumnDataList.get(11);
            if(!no_img) {
//                product.description_img = convertImgPath(cloumnDataList.get(16));
                product.description_img = coverImgs;
            }
            product.remark = "eds";
            product.status = ProductStatusType.NEW_PENDING;
            product.feature = ProductFeatureType.NORMAL;
            product.publish_at = (format.parse(DateFormatUtils.format(new Date(), "yyyy-MM-dd")));

            Calendar curr = Calendar.getInstance();
            curr.set(Calendar.YEAR, curr.get(Calendar.YEAR) + 100);
            Date date = curr.getTime();
            product.valid_at = (format.parse(DateFormatUtils.format(date, "yyyy-MM-dd")));

            product.created_at = Helper.getCurrentTimeToUTCWithDate();
            product.updated_at = Helper.getCurrentTimeToUTCWithDate();
            product.enabled = EnabledType.USED;
        } catch (Exception e) {
            e.printStackTrace();
            product = null;
            throw e;
        }

        return product;
    }
	
	/**
     * 转换图片路径,读取图片流写入到OSS
     *
     * @param imgPaths
     * @return
     */
    public String convertImgPath (String imgPaths) {
        List<String> converImgPathList = null;
        if (StringUtils.isNotBlank(imgPaths)) {
            List<String> imgList = JSONArray.parseArray(imgPaths, String.class);
            try {
                if (null != imgList && imgList.size() > 0) {
                    converImgPathList = new ArrayList<>(imgList.size());
                    for (String imgPath: imgList) {
                        List<String> list = FileUploadHelper.uploadFileByImgUrl2(imgPath);
                        imgPath = list.get(0);
                        if(StringUtils.isNotBlank(imgPath)) {
                            converImgPathList.add(imgPath);
                        }
                    }
                } else {
                    converImgPathList = new ArrayList<>();
                }

            } catch (Exception e) {
                e.printStackTrace();
                converImgPathList = new ArrayList<>();
            }
        }

        return JSON.toJSONString(converImgPathList);
    }

    public List<String> convertImgPathList (String imgPaths) {
        List<String> converImgPathList = null;
        if (StringUtils.isNotBlank(imgPaths)) {
            List<String> imgList = JSONArray.parseArray(imgPaths, String.class);
            try {
                if (null != imgList && imgList.size() > 0) {
                    converImgPathList = new ArrayList<>(imgList.size());
                    for (String imgPath: imgList) {
                        List<String> list = FileUploadHelper.uploadFileByImgUrl2(imgPath);
                        imgPath = list.get(0);
                        if(StringUtils.isNotBlank(imgPath)) {
                            converImgPathList.add(imgPath);
                        }
                    }
                } else {
                    converImgPathList = new ArrayList<>();
                }

            } catch (Exception e) {
                e.printStackTrace();
                converImgPathList = new ArrayList<>();
            }
        }

        return converImgPathList;
    }
	
	/**
     * 查询类目
     *
     * @param columnDataList
     * @param categoryService
     * @return
     */
    public Category getCategory(List<String> columnDataList, CategoryService categoryService, MappingCategoryService mappingCategoryService, Map<String, Object> result, String storeCode) throws Exception {
        String firstCategoryName = columnDataList.get(8).toString();
        String secondCategoryName = columnDataList.get(9).toString();
        String thirdCategoryName = columnDataList.get(10).toString();
        Category category = null;

        Object [] object = new Object[]{storeCode, firstCategoryName, secondCategoryName, thirdCategoryName};

        List<Map<String, Object>> apiCategoryMap = null;
        apiCategoryMap = mappingCategoryService.getMappingCategoryInfoByCondition(object);
        if (null != apiCategoryMap && apiCategoryMap.size() > 0) {
            if (apiCategoryMap.size() > 1) {
                result.put("info", "  json_data[ 11 ] find multiple same category!!!");
                result.put("status", StatusType.FAILURE);
            } else {
                category = categoryService.convertMapToCategory(apiCategoryMap.get(0));
            }
        } else {
            result.put("info", " json_data[ 8,9,10 ] can not find category to map!!!");
            result.put("status", StatusType.FAILURE);
        }
        return category;
    }
	
	 public boolean checkVendorAcitived(Vendor vendor, Map<String, Object> result) {
	        boolean b = false;
	        if (vendor.status != VendorStatus.ACTIVED) {
	            result.put("info", "Current supplier non effective supplier!");
	            result.put("status", StatusType.VENDOR_NOT_ACTIVED);
	            b = true;
	        }
	        return b;
	    }
	
	public Vendor getVendor(Connection conn, Long vendorId, Map<String, Object> result) {
        // Get vendor
        Vendor vendor = null;
        try {
            if (null != vendorId) {
                VendorService vendorService = new VendorService(conn);
                vendor = vendorService.getVendorByVendorId(vendorId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (vendor == null) {
            result.put("info",  "Cannot find supplier!");
            result.put("status", StatusType.VENDOR_NULL_BY_USERID);
        }

        return vendor;
    }
	
	 /**
     * 查询品牌信息
     *
     * @param englishName
     * @param conn
     * @return
     */
    public Brand getBrand(String englishName, Long apiConfigurationId, Connection conn, Map<String, Object> result) throws Exception {
        ApiBrandMapService apiBrandMapService = new ApiBrandMapService(conn);
        englishName = apiBrandMapService.getApiBrandMapListByCondition(englishName);
        Brand brand = null;
        Map<String, Object> condition = new HashMap<>();
        condition.put("english_name", englishName);
        condition.put("status", 2);
        try {
            BrandService brandService = new BrandService(conn);
            List<Brand> brands = brandService.getBrandListByCondition(condition);
            if (null != brands && brands.size() > 0) {
                brand = brands.get(0);
            } else {
                condition = new HashMap<>();
                condition.put("boutique_brand_name",englishName);
                condition.put("api_configuration_id", apiConfigurationId);
                List<ApiBrandMap> apiBrandMapList = apiBrandMapService.getApiBrandMapListByCondition(condition);
                if (null != apiBrandMapList && apiBrandMapList.size() > 0) {
                    brand = new Brand();
                    brand.brand_id = apiBrandMapList.get(0).getBrand_id();
                    brand.english_name = apiBrandMapList.get(0).getBrand_name();
                } else {
                    result.put("info", " json_data[ 6 ] brand doesn't exist !!!");
                    result.put("status", StatusType.FAILURE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return brand;
    }
	
	/**
     * 检查校验参数合法性
     *
     * @param columnDataList
     * @param result
     * @return
     */
    public boolean validateParam(Connection conn, List<String> columnDataList, Map<String, Object> result) {
        /*String productName = columnDataList.get(0);
        if (StringUtils.isBlank(productName)) {
            result.put("info","create product - "+ ApiErrorTypeEnum.errorType.boutique_name_is_null.getDesc()+"boutique_name:null");
            result.put("error_enum", ApiErrorTypeEnum.errorType.boutique_name_is_null);
            result.put("key","boutique_name");
            result.put("value","null");
            return true;
        }*/
        result.put("status", StatusType.FAILURE);
        String boutiqueId = columnDataList.get(1);
        if (StringUtils.isBlank(boutiqueId)) {
            result.put("info","create product - "+ ApiErrorTypeEnum.errorType.boutique_id_is_null.getDesc()+"boutique_id:null");
            result.put("error_enum", ApiErrorTypeEnum.errorType.Data_can_not_be_null);
            result.put("key","boutique_id");
            result.put("value","null");
            return true;
        } else {
            ProductService productService = new ProductService(conn);
            Map<String, Object> condition = new HashMap<>();
            condition.put("product_code", boutiqueId);
            condition.put("enabled", 1);
            try {
                Product product = productService.getProductByCondition(condition, null);
                if (null != product) {
                    result.put("status",StatusType.PRODUCT_ALREADY_EXISTS);
                    result.put("info","create product - "+ ApiErrorTypeEnum.errorType.boutique_id_already_exist.getDesc()+"boutique_id:"+boutiqueId);
                    result.put("error_enum", ApiErrorTypeEnum.errorType.Data_is_duplicated);
                    result.put("key","boutique_id");
                    result.put("value",boutiqueId);
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String brandId = columnDataList.get(2);
        if (StringUtils.isBlank(brandId)) {
            result.put("info","create product - "+ ApiErrorTypeEnum.errorType.brandID_is_null.getDesc()+"BrandID:null");
            result.put("error_enum", ApiErrorTypeEnum.errorType.Data_can_not_be_null);
            result.put("key","BrandID");
            result.put("value","null");
            return true;
        }

        String brand = columnDataList.get(5);
        if (StringUtils.isBlank(brand)) {
            result.put("info","create product - "+ ApiErrorTypeEnum.errorType.brand_name_is_null.getDesc()+"brand_name:null");
            result.put("error_enum", ApiErrorTypeEnum.errorType.Data_can_not_be_null);
            result.put("key","brand");
            result.put("value",null);
            return true;
        }

        String colorCode = columnDataList.get(6);
        if (StringUtils.isBlank(colorCode)) {
            result.put("info","create product - "+ ApiErrorTypeEnum.errorType.colorCode_is_null.getDesc()+"colorCode:null");
            result.put("error_enum", ApiErrorTypeEnum.errorType.Data_can_not_be_null);
            result.put("key","ColorCode");
            result.put("value","null");
            return true;
        }

        String threeCategoryId = columnDataList.get(10);
        if (StringUtils.isBlank(threeCategoryId)) {
            result.put("info","create product - "+ ApiErrorTypeEnum.errorType.category_is_null.getDesc()+"category_id:null");
            result.put("error_enum", ApiErrorTypeEnum.errorType.Data_can_not_be_null);
            result.put("key","category_id");
            result.put("value","null");
            return true;
        }

        String coverImage = columnDataList.get(15);
        if (StringUtils.isBlank(coverImage)) {
            result.put("info","create product - "+ ApiErrorTypeEnum.errorType.coverImage_is_null.getDesc()+"coverImage:null");
            result.put("error_enum", ApiErrorTypeEnum.errorType.Data_can_not_be_null);
            result.put("key","coverImage");
            result.put("value","null");
            return true;
        }

        String descriptionImage = columnDataList.get(16);
        if (StringUtils.isBlank(descriptionImage)) {
            result.put("info","create product - "+ ApiErrorTypeEnum.errorType.descImage_is_null.getDesc()+"descImage:null");
            result.put("error_enum", ApiErrorTypeEnum.errorType.Data_can_not_be_null);
            result.put("key","descImage");
            result.put("value","null");
            return true;
        }

        String weight = columnDataList.get(17);
        if (StringUtils.isBlank(weight)) {
            result.put("info","create product - "+ ApiErrorTypeEnum.errorType.weight_is_null.getDesc()+"descImage:null");
            result.put("error_enum", ApiErrorTypeEnum.errorType.Data_can_not_be_null);
            result.put("key","weight");
            result.put("value","null");
            return true;
        }

        String salePrice = columnDataList.get(21);
        if (StringUtils.isBlank(salePrice) || "0".equals(StringUtils.trim(salePrice))) {
            result.put("info","create product - "+ ApiErrorTypeEnum.errorType.retail_price_is_zero.getDesc()+"salePrice:"+salePrice);
            result.put("error_enum", ApiErrorTypeEnum.errorType.retail_price_is_zero);
            result.put("key","retail_price");
            result.put("value",salePrice);
            return true;
        }

        String size = "";
        String barcode = "";
        String stock = "";
        for (int i = 22; i < columnDataList.size(); i = i + 3) {
            size = columnDataList.get(i);
            if (StringUtils.isBlank(size)) {
                result.put("info","create product - "+ ApiErrorTypeEnum.errorType.skuSize_is_null.getDesc()+"size:null");
                result.put("error_enum", ApiErrorTypeEnum.errorType.Data_can_not_be_null);
                result.put("key","size");
                result.put("value","null");
                return true;
            }
            barcode = columnDataList.get(i + 1);
            if (StringUtils.isBlank(barcode)) {
                result.put("info","create product - "+ ApiErrorTypeEnum.errorType.skuCode_is_null.getDesc()+"barcode:null");
                result.put("error_enum", ApiErrorTypeEnum.errorType.Data_can_not_be_null);
                result.put("key","barcode");
                result.put("value","null");
                return true;
            }
            stock = columnDataList.get(i + 2);
            if (StringUtils.isBlank(stock)) {
                result.put("info","create product - "+ ApiErrorTypeEnum.errorType.skuStock_is_null.getDesc()+"stock:null");
                result.put("error_enum", ApiErrorTypeEnum.errorType.Data_can_not_be_null);
                result.put("key","stock");
                result.put("value","null");
                return true;
            }
        }
        return false;
    }
	
	/**
	 * ProductOptions转换成List对象
	 * @param productOptions
	 * @return List
	 */
	public List<String> convertProductList(ProductOptions productOptions){
		// 初始化属性
		List<String> productList = new ArrayList<String>();
		
		// 转换ProductOptions
		productList.add(0,productOptions.getName()); // item_name
		
		productList.add(1,productOptions.getCode()); // product_id
		
		productList.add(2,productOptions.getBrandCode()); // product_reference
		
		productList.add(3,productOptions.getSeasonCode()); // season_year + season_reference
		
		productList.add(4,productOptions.getCarryOver()); // atelier 
		
		productList.add(5,productOptions.getBrandName()); // brand
		
		productList.add(6,productOptions.getColorCode()); // color_reference
		
		productList.add(7,productOptions.getColorDesc()); // color
		
		productList.add(8,productOptions.getSkus().get(0).getSizeid()); // 注意 - sizeid for filippo
		
		productList.add(9,""); // 注意 - first_category 
		
		productList.add(10,productOptions.getCategoryId()); // 注意 - second_category
		
		productList.add(11,productOptions.getDesc()); // item_description
		
		productList.add(12, productOptions.getComposition()); // 注意 technical_info
		
		productList.add(13, productOptions.getMadeIn()); // made_In
		
		productList.add(14,productOptions.getSizeFit()); // 注意 suitable
		
		if(StringUtils.isNotBlank(productOptions.getCoverImg())) { // item_images
			productList.add(15,productOptions.getCoverImg()); 
		} else {
			productList.add(15,"[]"); 
		}

        if(StringUtils.isNotBlank(productOptions.getDescImg())) { // item_images
            productList.add(16,productOptions.getDescImg());
        } else {
            productList.add(16,"[]");
        }
		
		if(StringUtils.isNotBlank(productOptions.getWeight())) { // weight
			productList.add(17,productOptions.getWeight());
		} else {
			productList.add(17,"1");
		}
		
		if(StringUtils.isNotBlank(productOptions.getLength())) { // length
			productList.add(18,productOptions.getLength());
		} else {
			productList.add(18,"1");
		}
		
		if(StringUtils.isNotBlank(productOptions.getWidth())) { // width
			productList.add(19,productOptions.getWidth());
		} else {
			productList.add(19,"1");
		}
		
		if(StringUtils.isNotBlank(productOptions.getHeigit())) { // height
			productList.add(20,productOptions.getHeigit());
		} else {
			productList.add(20,"1");
		}
		
		productList.add(21, productOptions.getSalePrice()); // retail_price
		
		List<SkuOptions> skus = productOptions.getSkus(); // variants
		if(skus != null && skus.size() != 0) {
			for(SkuOptions sku : skus) {
				productList.add(sku.getSize());
				if(StringUtils.isBlank(sku.getBarcodes())) {
				    sku.setBarcodes("#");
                }
				productList.add(sku.getBarcodes());
				productList.add(sku.getStock());
			}
		}
		
		return productList;
	}

	public ProductOptions getProductOptions(){
		return new ProductOptions();
	}
	
	public SkuOptions getSkuOptions(){
		return new SkuOptions();
	}
	
	public VendorOptions getVendorOptions(){
		return new VendorOptions();
	}
	
	public class ProductOptions{
		public String name = ""; 
		
		public String code = "";
		
		public String brandCode = "";
		
		public String seasonCode = "";
		
		public String carryOver = "";
		
		public String brandName = "";
		
		public String colorCode = "";
		
		public String colorDesc = "";
		
		public String categoryId = "";
		
		public String desc = "";
		
		public String composition = "";
		
		public String madeIn = "";
		
		public String sizeFit = "";
		
		public String coverImg = "";
		
		public String descImg = "";
		
		public String weight = "";
		
		public String length = "";
		
		public String width = "";

		public String heigit = "";
		
		public String retailPrice = "";
		
		public String salePrice = "";

		public String imgByFilippo;

		public String fullUpdateProductFlag = "";

        public String getFullUpdateProductFlag() {
            return fullUpdateProductFlag;
        }

        public void setFullUpdateProductFlag(String fullUpdateProductFlag) {
            this.fullUpdateProductFlag = fullUpdateProductFlag;
        }

        public List<SkuOptions> skus = new ArrayList<>();
		
		public String getName() {
			return name;
		}

		public ProductOptions setName(String name) {
			this.name = name;
			return this;
		}

		public String getCode() {
			return StringUtils.trim(code);
		}

		public ProductOptions setCode(String code) {
			this.code = code;
			return this;
		}

		public String getBrandCode() {
			return StringUtils.trim(brandCode);
		}

		public ProductOptions setBrandCode(String brandCode) {
			this.brandCode = brandCode;
			return this;
		}

		public String getSeasonCode() {
			return seasonCode;
		}

		public ProductOptions setSeasonCode(String seasonCode) {
			this.seasonCode = seasonCode;
			return this;
		}

		public String getCarryOver() {
			return carryOver;
		}

		public ProductOptions setCarryOver(String carryOver) {
			this.carryOver = carryOver;
			return this;
		}

		public String getBrandName() {
			return brandName;
		}

		public ProductOptions setBrandName(String brandName) {
			this.brandName = brandName;
			return this;
		}

		public String getColorCode() {
			return colorCode;
		}

		public ProductOptions setColorCode(String colorCode) {
			this.colorCode = colorCode;
			return this;
		}

		public String getColorDesc() {
			return colorDesc;
		}

		public ProductOptions setColorDesc(String colorDesc) {
			this.colorDesc = colorDesc;
			return this;
		}

		public String getCategoryId() {
			return StringUtils.trim(categoryId);
		}

		public ProductOptions setCategoryId(String categoryId) {
			this.categoryId = categoryId;
			return this;
		}

		public String getDesc() {
			return desc;
		}

		public ProductOptions setDesc(String desc) {
			this.desc = desc;
			return this;
		}

		public String getComposition() {
			return composition;
		}

		public ProductOptions setComposition(String composition) {
			this.composition = composition;
			return this;
		}

		public String getMadeIn() {
			return madeIn;
		}

		public ProductOptions setMadeIn(String madeIn) {
			this.madeIn = madeIn;
			return this;
		}

		public String getSizeFit() {
			return sizeFit;
		}

		public ProductOptions setSizeFit(String sizeFit) {
			this.sizeFit = sizeFit;
			return this;
		}

		public String getCoverImg() {
			return coverImg;
		}

		public ProductOptions setCoverImg(String coverImg) {
			this.coverImg = coverImg;
			return this;
		}

		public String getDescImg() {
			return descImg;
		}

		public ProductOptions setDescImg(String descImg) {
			this.descImg = descImg;
			return this;
		}

		public String getWeight() {
			return weight;
		}

		public ProductOptions setWeight(String weight) {
			this.weight = weight;
			return this;
		}

		public String getLength() {
			return length;
		}

		public ProductOptions setLength(String length) {
			this.length = length;
			return this;
		}

		public String getWidth() {
			return width;
		}

		public ProductOptions setWidth(String width) {
			this.width = width;
			return this;
		}

		public String getHeigit() {
			return heigit;
		}

		public ProductOptions setHeigit(String heigit) {
			this.heigit = heigit;
			return this;
		}

		public String getRetailPrice() {
			return retailPrice;
		}

		public ProductOptions setRetailPrice(String retailPrice) {
			this.retailPrice = retailPrice;
			return this;
		}

		public String getSalePrice() {
			return StringUtils.trim(salePrice.replaceAll(",","."));
		}

		public ProductOptions setSalePrice(String salePrice) {
			this.salePrice = salePrice;
			return this;
		}

		public List<SkuOptions> getSkus() {
			return skus;
		}

		public ProductOptions setSkus(List<SkuOptions> skus) {
			this.skus = skus;
			return this;
		}

        public String getImgByFilippo() {
            return imgByFilippo;
        }

        public void setImgByFilippo(String imgByFilippo) {
            this.imgByFilippo = imgByFilippo;
        }
    }
	
	public class SkuOptions{
		
		public String size;
		
		public String stock;
		
		public String barcodes;

		public String sizeid; // filippo

		public String getSize() {
			return size;
		}

		public void setSize(String size) {
			this.size = size;
		}

		public String getStock() {
			return stock;
		}

		public void setStock(String stock) {
			this.stock = stock;
		}

		public String getBarcodes() {
			return barcodes;
		}

		public void setBarcodes(String barcodes) {
			this.barcodes = barcodes;
		}

        public String getSizeid() {
            return sizeid;
        }

        public void setSizeid(String sizeid) {
            this.sizeid = sizeid;
        }
    }
	
	public class VendorOptions{
		public String storeCode;
		
		public Long vendorId;
		
		public Long apiConfigurationId;

		public Long getVendorId() {
			return vendorId;
		}

		public void setVendorId(Long vendorId) {
			this.vendorId = vendorId;
		}

		public Long getApiConfigurationId() {
			return apiConfigurationId;
		}

		public void setApiConfigurationId(Long apiConfigurationId) {
			this.apiConfigurationId = apiConfigurationId;
		}

		public String getStoreCode() {
			return storeCode;
		}

		public void setStoreCode(String storeCode) {
			this.storeCode = storeCode;
		}
		
	}
}
