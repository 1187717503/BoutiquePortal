package pk.shoplus.service.product.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson15.JSONArray;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.sql2o.Connection;
import pk.shoplus.DBConnector;
import pk.shoplus.common.Contants;
import pk.shoplus.common.Helper;
import pk.shoplus.enums.ApiErrorTypeEnum;
import pk.shoplus.enums.ApiParamterEnum;
import pk.shoplus.enums.ProductPropertyEnumKeyName;
import pk.shoplus.enums.ProductStatusEnum;
import pk.shoplus.model.*;
import pk.shoplus.parameter.EnabledType;
import pk.shoplus.parameter.StatusType;
import pk.shoplus.service.*;
import pk.shoplus.service.price.api.IPriceService;
import pk.shoplus.service.price.impl.PriceServiceImpl;
import pk.shoplus.service.product.api.IProductService;
import pk.shoplus.service.product.api.IProductStatusService;
import pk.shoplus.service.stock.api.IStoreService;
import pk.shoplus.service.stock.impl.StoreServiceImpl;
import pk.shoplus.util.ExceptionUtils;
import pk.shoplus.util.MapUtils;
import pk.shoplus.vo.ResultMessage;
import pk.shoplus.vo.StatusMachineVO;

import java.math.BigDecimal;
import java.util.*;

import static pk.shoplus.enums.ApiErrorTypeEnum.errorType.*;

/**
 * @Author: DingYiFan
 * @Description: 商品接口实现
 * @Date: Create in 13:31 2017/5/25
 * @Modified By:
 */
public class ProductServiceImpl implements IProductService{

    private ProductEDSManagement productEDSManagement = new ProductEDSManagement();

    private ProductStockEDSManagement productStockEDSManagement = new ProductStockEDSManagement();

    private IPriceService priceService = new PriceServiceImpl();

    private static Logger logger = Logger.getLogger(ProductServiceImpl.class);

    @Override
    public Map<String, Object> updateProduct(ProductEDSManagement.ProductOptions productOptions, ProductEDSManagement.VendorOptions vendorOptions) {
        logger.info("ProductServiceImplUpdateProduct,inputParams,productOptions:"+new Gson().toJson(productOptions)+",vendorOptions:"+new Gson().toJson(vendorOptions));
        List<Map<String,Object>> warningMaps = new ArrayList<>();
        MapUtils mapUtils = new MapUtils(new HashMap<>());
        Connection conn = null ;
        try{
            conn = DBConnector.sql2o.beginTransaction();

            CategoryProductInfoService categoryProductInfoService = new CategoryProductInfoService(conn);
            ApiParameterService apiParameterService = new ApiParameterService(conn);
            ProductInfoService productInfoService = new ProductInfoService(conn);
            CategoryService categoryService = new CategoryService(conn);
            ProductService productService = new ProductService(conn);
            SkuService skuService = new SkuService(conn);

            Map<String,Object> productConditions = new HashMap<>();
            productConditions.put("product_code",productOptions.getCode());
            productConditions.put("vendor_id",vendorOptions.getVendorId());
            productConditions.put("enabled",EnabledType.USED);
            logger.info("ProductServiceImplUpdateProduct,getProductByCondition,productConditions:"+new Gson().toJson(productConditions));
            Product product = productService.getProductByCondition(productConditions,null);
            logger.info("ProductServiceImplUpdateProduct,getProductByCondition,product:"+new Gson().toJson(product));
            if(product == null) {
                return mapUtils.putData("status",StatusType.FAILURE)
                               .putData("info","update product - "+ ApiErrorTypeEnum.errorType.boutique_id_not_exist.getDesc()+" boutique_id:"+productOptions.getCode())
                               .putData("error_enum",ApiErrorTypeEnum.errorType.Data_not_exist)
                               .putData("key","boutique_id")
                               .putData("value",productOptions.getCode())
                               .getMap();
            }

            Category category = null ;
            if(StringUtils.isBlank(productOptions.getCategoryId())) {
                mapUtils = new MapUtils(new HashMap<>());
                mapUtils.putData("status",StatusType.WARNING)
                        .putData("info","update product - " + ApiErrorTypeEnum.errorType.category_is_null.getDesc()+" category_id:null")
                        .putData("error_enum",warning_data_can_not_find_mapping)
                        .putData("key","category_id")
                        .putData("value","null");
                warningMaps.add(mapUtils.getMap());
            } else {
                category = categoryService.getCategoryById(Long.parseLong(productOptions.getCategoryId()));
                if(category == null) {
                    mapUtils = new MapUtils(new HashMap<>());
                    mapUtils.putData("status",StatusType.WARNING)
                            .putData("info","update product - "+ ApiErrorTypeEnum.errorType.category_not_exist.getDesc()+" category_id:"+productOptions.getCategoryId())
                            .putData("error_enum",warning_data_can_not_find_mapping)
                            .putData("key","category_id")
                            .putData("value",productOptions.getCategoryId());
                    warningMaps.add(mapUtils.getMap());
                }
            }

            if(category != null && !categoryService.isLastNode(category.getCategory_id())) {
                mapUtils = new MapUtils(new HashMap<>());
                mapUtils.putData("status",StatusType.WARNING)
                        .putData("info","update product - "+ ApiErrorTypeEnum.errorType.category_is_not_three.getDesc()+" category_id:"+productOptions.getCategoryId())
                        .putData("error_enum",warning_data_can_not_find_mapping)
                        .putData("key","category_id")
                        .putData("value",category.getCategory_id());
                warningMaps.add(mapUtils.getMap());
            } else if(category != null) {
                product.category_id = category.getCategory_id();
            }

            if(StringUtils.isBlank(productOptions.getSalePrice()) || "0".equals(StringUtils.trim(productOptions.getSalePrice()))) {
                mapUtils = new MapUtils(new HashMap<>());
                mapUtils.putData("status",StatusType.WARNING)
                        .putData("info","update product - "+ ApiErrorTypeEnum.errorType.retail_price_is_zero.getDesc()+" retail_price:"+productOptions.getSalePrice())
                        .putData("error_enum",warning_retail_price_is_zero)
                        .putData("key","retail_price")
                        .putData("value",productOptions.getSalePrice());
                warningMaps.add(mapUtils.getMap());
            }

            boolean no_img = false;
            String englishName = productOptions.getBrandName();
            BrandService brandService = new BrandService(conn);
            ApiBrandMapService apiBrandMapService = new ApiBrandMapService(conn);
            String brandId = apiBrandMapService.getBrandNameByBrand(englishName);
            if(StringUtils.isBlank(brandId)) {
                brandId = apiBrandMapService.getBrandNameByBrandMapping(englishName,vendorOptions.getVendorId().toString());
                if(StringUtils.isBlank(brandId)) {
                    mapUtils = new MapUtils(new HashMap<>());
                    mapUtils.putData("status",StatusType.WARNING)
                            .putData("info","update product - "+ ApiErrorTypeEnum.errorType.brand_not_exist.getCode()+" brand_name:"+productOptions.getBrandName())
                            .putData("error_enum",warning_data_can_not_find_mapping)
                            .putData("key","brand_name")
                            .putData("value",productOptions.getBrandName());
                    warningMaps.add(mapUtils.getMap());
                }
            }

            Brand brand = null;
            if(StringUtils.isNotBlank(brandId)) {
                brand = brandService.getBrandById(Long.parseLong(brandId));
            }

            if(brand != null) {
                Map<String,Object> getApiBrandMaps = new HashMap<>();
                getApiBrandMaps.put("api_configuration_id",vendorOptions.apiConfigurationId.toString());
                getApiBrandMaps.put("enabled",EnabledType.USED);
                getApiBrandMaps.put("no_img",EnabledType.USED);
                getApiBrandMaps.put("brand_id",brandId);
                List<ApiBrandMap> apiBrandMapList = apiBrandMapService.getApiBrandMapListByCondition(getApiBrandMaps);
                if(apiBrandMapList != null && apiBrandMapList.size() > 0) {
                    no_img = true;
                }
                product.brand_id = brand.brand_id;
            }

            // update brand_id,season_code,carry_over,color_code,color_description,composition,made_in,size_fit
            this.updateProductPropertyByBrandIDColorCode(conn,product.getProduct_id(),ProductPropertyEnumKeyName.BrandID.getCode(),productOptions.getBrandCode(),warningMaps,mapUtils);
            this.updateProductPropertyByBrandIDColorCode(conn,product.getProduct_id(),ProductPropertyEnumKeyName.ColorCode.getCode(),productOptions.getColorCode(),warningMaps,mapUtils);

            /** BrandID,ColorCode值与原来不一致 下架商品 warning */
            this.updateProductProperty(conn,product.getProduct_id(),ProductPropertyEnumKeyName.CarryOver.getCode(),productOptions.getCarryOver());
            this.updateProductProperty(conn,product.getProduct_id(),ProductPropertyEnumKeyName.ColorCode.getCode(),productOptions.getColorCode());
            this.updateProductProperty(conn,product.getProduct_id(),ProductPropertyEnumKeyName.ColorDescription.getCode(),productOptions.getColorDesc());
            this.updateProductProperty(conn,product.getProduct_id(),ProductPropertyEnumKeyName.MadeIn.getCode(),productOptions.getMadeIn());
            this.updateProductProperty(conn,product.getProduct_id(),ProductPropertyEnumKeyName.SizeFit.getCode(),productOptions.getSizeFit());
            this.updateProductProperty(conn,product.getProduct_id(),ProductPropertyEnumKeyName.Composition.getCode(),productOptions.getComposition());

            if(StringUtils.isNotBlank(productOptions.getName())) {
                product.name = productOptions.getName();
            }

            if(StringUtils.isNotBlank(productOptions.getDesc())) {
                product.description = productOptions.getDesc();
            }

            ApiLogBatchService apiLogBatchService = new ApiLogBatchService(conn);
            String images = apiLogBatchService.selApiLogBatch(productOptions.getCode());

            if((StringUtils.isNotBlank(images) && !images.contains(productOptions.getImgByFilippo()) && StringUtils.isNotBlank(productOptions.getImgByFilippo()))
                    || this.imgIsNull(product.cover_img)) {
                if(!no_img) {
                    List<String> coverImg = productEDSManagement.convertImgPathList(productOptions.getCoverImg());
                    List<String> descImg = productEDSManagement.convertImgPathList(productOptions.getDescImg());

                    /** start update by dingyifan 2017-07-31 */
                    // 记录上传图片出错日志
                    if(StringUtils.isNotBlank(productOptions.getCoverImg()) && StringUtils.isNotBlank(productOptions.getImgByFilippo())) {
                        boolean flag = false;
                        if(coverImg == null || coverImg.size() == 0) {
                            flag = true;
                        }
                        if(flag) {
                            ApiLogImageErrorService apiLogImageErrorService = new ApiLogImageErrorService(conn);
                            ApiLogImageError apiLogImageError = new ApiLogImageError();
                            apiLogImageError.setImages(productOptions.getCoverImg());
                            apiLogImageError.setBoutique_id(productOptions.getCode());
                            apiLogImageError.setError_type(1);
                            apiLogImageError.setError_info(new Gson().toJson(productOptions));
                            apiLogImageErrorService.create(apiLogImageError);
                        }
                    }
                    /** start update by dingyifan 2017-07-31 */

                    if(coverImg != null && coverImg.size() > 0) {
                        if(StringUtils.isNotBlank(product.cover_img) && product.cover_img.length() > 5) {
                            List<String> coverImgStringList = JSONArray.parseArray(product.cover_img,String.class);
                            coverImgStringList.addAll(coverImg);
                            product.cover_img  = JSON.toJSONString(coverImgStringList);

                            List<String> descImgStringList = JSONArray.parseArray(product.description_img,String.class);
                            descImgStringList.addAll(descImg);
                            product.description_img  = JSON.toJSONString(descImgStringList);

                        } else {
                            product.cover_img  = JSON.toJSONString(coverImg);
                            product.description_img  = JSON.toJSONString(descImg);
                        }

                        apiLogBatchService.createApiLogBatch(productOptions.getCode(),productOptions.getImgByFilippo());
                    }

                }
            }
            logger.info(" end handle update product image ! " );

            SeasonService seasonService = new SeasonService(conn);
            logger.info(" start productedsmanagement createproduct select season_code by boutiquecode : " + productOptions.getSeasonCode());
            String season_code = seasonService.selSeasonCodeByBoutiqueCode(productOptions.getSeasonCode());
            logger.info(" end productedsmanagement createproduct select season_code by boutiquecode : season_code " + season_code);

            if(StringUtils.isBlank(season_code) || StringUtils.isBlank(productOptions.getSeasonCode())) {
                mapUtils = new MapUtils(new HashMap<>());
                mapUtils.putData("status",StatusType.WARNING)
                        .putData("info","update product - "+ ApiErrorTypeEnum.errorType.season_not_exist.getDesc()+" season_code:"+productOptions.getSeasonCode() )
                        .putData("error_enum",warning_data_can_not_find_mapping)
                        .putData("key","season_code")
                        .putData("value","null");
                warningMaps.add(mapUtils.getMap());
            }

            if(StringUtils.isNotBlank(season_code)) {
                product.season_code = season_code;
            } else if(StringUtils.isNotBlank(productOptions.getSeasonCode())) {
                product.season_code = productOptions.getSeasonCode();
            }

            product.updated_at = new Date();
            productService.updateProduct(product);

            CategoryProductInfo categoryProductInfo = this.getCategoryProductInfo(categoryProductInfoService,product.category_id);
            if(categoryProductInfo != null) {
                // query product_info
                Map<String,Object> productInfoConditions = new HashMap<>();
                productInfoConditions.put("category_prodcut_info_id",categoryProductInfo.category_product_info_id);
                productInfoConditions.put("product_id",product.product_id);
                productInfoConditions.put("enabled",EnabledType.USED);
                List<ProductInfo> productInfos = productInfoService.getProductInfoByCondition(productInfoConditions);
                String weight = productOptions.getWeight();
                String height = productOptions.getHeigit();
                String width = productOptions.getWidth();
                String length = productOptions.getLength();
                if(productInfos != null && productInfos.size() > 0) {
                    ProductInfo productInfo = productInfos.get(0);

                    // update weight,height,width,length

                    if(StringUtils.isNotBlank(weight)) {
                        productInfo.weight_amount = BigDecimal.valueOf(Long.parseLong(weight));
                    }
                    if(StringUtils.isNotBlank(height)) {
                        productInfo.height_amount = BigDecimal.valueOf(Long.parseLong(height));
                    }
                    if(StringUtils.isNotBlank(width)) {
                        productInfo.width_amount = BigDecimal.valueOf(Long.parseLong(width));
                    }
                    if(StringUtils.isNotBlank(length)) {
                        productInfo.length_amount = BigDecimal.valueOf(Long.parseLong(length));
                    }
//                    CategoryProductInfo cpi = this.getCategoryProductInfo(categoryProductInfoService,product.category_id);
//                    productInfo.category_prodcut_info_id = cpi.category_product_info_id;
                    productInfoService.updateProductInfoById(productInfo);
                } else {
                    ProductInfo productInfo = new ProductInfo();
                    productInfo.category_prodcut_info_id = categoryProductInfo.getCategory_product_info_id();
                    productInfo.product_id = product.product_id;
                    productInfo.created_at = new Date();
                    productInfo.updated_at = new Date();
                    productInfo.remark = " update product ";
                    productInfo.enabled = EnabledType.USED;
                    productInfo.weight = categoryProductInfo.weight;
                    productInfo.length = categoryProductInfo.length;
                    productInfo.width = categoryProductInfo.width;
                    productInfo.height = categoryProductInfo.height;
                    productInfo.size = categoryProductInfo.size;
                    if(StringUtils.isNotBlank(weight)) {
                        productInfo.weight_amount = BigDecimal.valueOf(Long.parseLong(weight));
                    }
                    if(StringUtils.isNotBlank(height)) {
                        productInfo.height_amount = BigDecimal.valueOf(Long.parseLong(height));
                    }
                    if(StringUtils.isNotBlank(width)) {
                        productInfo.width_amount = BigDecimal.valueOf(Long.parseLong(width));
                    }
                    if(StringUtils.isNotBlank(length)) {
                        productInfo.length_amount = BigDecimal.valueOf(Long.parseLong(length));
                    }
                    productInfo.size = categoryProductInfo.size;
                    productInfo.other_property = "";
                    productInfoService.createProductInfo(productInfo);
                }
            }

            // query sku
            List<ProductEDSManagement.SkuOptions> skus = productOptions.getSkus();

            List<Map<String,Object>> sizeValues = categoryService.executeSQL("select distinct sku.sku_id,pspv.`value` from sku\n" +
                    "left join sku_property sp on(sku.sku_id = sp.sku_id and sp.enabled = 1)\n" +
                    "left join product_sku_property_value pspv on(sp.product_sku_property_value_id = pspv.product_sku_property_value_id and pspv.enabled = 1)\n" +
                    "where sku.enabled = 1 and pspv.value is not null and product_id = "+product.product_id);

            // 如果sku不存在创建sku信息
            for(ProductEDSManagement.SkuOptions skuOption : skus) {
                boolean ifFlag = false;
                String sku_id = "";
                if(sizeValues != null && sizeValues.size() > 0) {
                    for(Map<String,Object> sizeValue : sizeValues) {
                        if(sizeValue.get("value").toString().equals(skuOption.getSize())) {
                            ifFlag = true;
                            sku_id = sizeValue.get("sku_id").toString();
                            break;
                        }
                    }
                }

                if(!ifFlag) {
                    if(category != null) {
                        // 添加sku信息
                        this.updateProductByAddSku(conn,product,category,skuOption,productOptions.getSalePrice(),vendorOptions.getVendorId(),productOptions);

                    } else {
                        logger.info("productServiceImplCatgeoryIsNull."+new Gson().toJson(product));
                    }
                } else {
                    ProductStockEDSManagement.StockOptions stockOptions = productStockEDSManagement.getStockOptions();
                    IStoreService storeService = new StoreServiceImpl();
                    BigDecimal bigDecimal = new BigDecimal(skuOption.getStock());
                    ResultMessage resultMessage = storeService.handleApiStockRule(Contants.STOCK_QTY,bigDecimal.intValue(),skuOption.getSize(),product.getProduct_code());

                    SkuStore skuStore = null;
                    if(resultMessage.getStatus()) {
                        skuStore = (SkuStore) resultMessage.getData();
                    } else {
                        return mapUtils.putData("status",StatusType.FAILURE)
                                .putData("info",resultMessage.getMsg())
                                .putData("error_enum", Data_create_error)
                                .putData("key","info")
                                .putData("value",resultMessage.getMsg()).getMap();
                    }

                    /*stockOptions.setProductCode(product.getProduct_code());
                    stockOptions.setSizeValue(skuOption.getSize());
                    stockOptions.setQuantity(skuStore.getStore().toString());
                    stockOptions.setReserved(skuStore.getReserved() == null ? "" : skuStore.getReserved().toString());
                    productStockEDSManagement.updateStock(stockOptions);*/

                    SkuStoreService skuStoreService = new SkuStoreService(conn);
                    if(bigDecimal.intValue() < 0) {
                        skuStore.store = 0L;
                        mapUtils = new MapUtils(new HashMap<>());
                        mapUtils.putData("status",StatusType.WARNING)
                                .putData("info","update product - "+ ApiErrorTypeEnum.errorType.warning_data_is_negative.getDesc()+" 库存为负数stock:"+bigDecimal)
                                .putData("error_enum",warning_data_is_negative)
                                .putData("key","stock")
                                .putData("value",bigDecimal);
                        warningMaps.add(mapUtils.getMap());
                    }
                    skuStoreService.updateSkuStore(skuStore);

                    if(org.apache.commons.lang.StringUtils.isNotBlank(productOptions.getFullUpdateProductFlag()) &&
                            productOptions.getFullUpdateProductFlag().equals("1")){
                        if(StringUtils.isNotBlank(sku_id)) {
                            Sku sku = new Sku();
                            sku.sku_id = Long.parseLong(sku_id);
                            sku.full_modify_date = new Date();
                            skuService.updateSku(sku);
                            logger.info("updateProduct set full modify date sku : " + new Gson().toJson(sku));
                        }
                    }

                }
            }

            // update sku price
            Map<String,Object> paramMap = apiParameterService.getParamByKeyName(ApiParamterEnum.ALLOW_PRICE_CHANGE.getCode());
            if(StringUtils.isNotBlank(productOptions.getSalePrice()) && paramMap != null && paramMap.get("param_value")!=null && paramMap.get("param_value").toString().equals("1")){
                String strPrice = productOptions.getSalePrice();

                /** update by dingyifan 2017-08-16 */
                BigDecimal price =new BigDecimal(0);
                try {
                    price =new BigDecimal(strPrice.trim());

                    if(price.intValue() >= 0) {

                        Map<String,Object> skuConditions = new HashMap<>();
                        skuConditions.put("product_id",product.product_id);
                        skuConditions.put("enabled",EnabledType.USED);
                        List<Sku> skuList = skuService.getSkuListByCondition(skuConditions);

                        BigDecimal oldPrice = skuList.get(0).getPrice();

                        boolean flag = ProductServiceImpl.isPrice(oldPrice,price);
                        if(flag) {
                            Sku skuPrice = priceService.getPriceByRule(product,vendorOptions.getVendorId(),price,conn);
                            BigDecimal inPrice = skuPrice.getIn_price();
                            for(Sku sku : skuList){
                                sku.price = price;
                                sku.in_price = inPrice;
                                sku.im_price = skuPrice.getIm_price();
                                sku.updated_at = new Date();
                                skuService.updateSku(sku);
                                logger.info("update sku by productServiceImpl sku : "+new Gson().toJson(sku));
                            }
                        } else {
                            mapUtils = new MapUtils(new HashMap<>());
                            mapUtils.putData("status",StatusType.WARNING)
                                    .putData("info","update product - " + warning_price_out_of_range.getDesc() + " retail_price:" + productOptions.getSalePrice())
                                    .putData("error_enum",warning_price_out_of_range)
                                    .putData("key","retail_price")
                                    .putData("value",productOptions.getSalePrice()).getMap();
                            warningMaps.add(mapUtils.getMap());
                        }
                    } else {
                        mapUtils = new MapUtils(new HashMap<>());
                        mapUtils.putData("status",StatusType.WARNING)
                                .putData("info","update product - " + warning_data_is_negative.getDesc() + " retail_price:" + productOptions.getSalePrice())
                                .putData("error_enum",warning_data_is_negative)
                                .putData("key","retail_price")
                                .putData("value",productOptions.getSalePrice()).getMap();
                        warningMaps.add(mapUtils.getMap());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                /** update by dingyifan 2017-08-16 */
            }

            mapUtils.putData("handle_type","updateProduct");
            if(mapUtils.getMap().get("status") == null) {
                mapUtils.putData("status",StatusType.SUCCESS)
                        .putData("info","success");
            }

            if(warningMaps != null && warningMaps.size() > 0) {
                mapUtils =  new MapUtils(new HashMap<>());
                mapUtils.putData("warningMaps",warningMaps);
                mapUtils.putData("status",StatusType.WARNING);
            }

            logger.info("ProductServiceImplUpdateProduct,outputParams,product:"+new Gson().toJson(product));
            conn.commit();
        } catch (Exception e) {
            if(conn != null) {conn.rollback();conn.close();}
            e.printStackTrace();
            mapUtils.putData("status",StatusType.FAILURE)
                    .putData("info","update product - "+ ApiErrorTypeEnum.errorType.Runtime_exception.getDesc()+" exception:"+ExceptionUtils.getExceptionDetail(e))
                    .putData("error_enum", Runtime_exception)
                    .putData("key","exception")
                    .putData("value",ExceptionUtils.getExceptionDetail(e));
        } finally {
            if(conn != null) {conn.close();}
        }
        return mapUtils.getMap();
    }


    @Override
    public void updateProductByAddSku(Connection conn, Product product, Category category, ProductEDSManagement.SkuOptions skuOption, String salePrice, Long vendorId, ProductEDSManagement.ProductOptions productOptions) throws Exception {
        CategorySkuPropertyKeyService categorySkuPropertyKeyService = new CategorySkuPropertyKeyService(conn);
        ProductPropertyValueService productPropertyValueService = new ProductPropertyValueService(conn);
        ProductPropertyKeyService productPropertyKeyService = new ProductPropertyKeyService(conn);
        SkuStoreService skuStoreService = new SkuStoreService(conn);
        SkuPropertyService skuPropertyService = new SkuPropertyService(conn);
        CategoryProductInfoService categoryProductInfoService = new CategoryProductInfoService(conn);
        SkuService skuService = new SkuService(conn);

        // query category property
        List<String> keyList = this.getPropertyKeyArray(category, categorySkuPropertyKeyService);
        JsonArray propertyKeyArray = new JsonParser().parse(JSONArray.toJSONString(keyList)).getAsJsonArray();

        for (int i = 0; i < propertyKeyArray.size(); i++) {

            // query product_sku_property
            Map<String,Object> productPropertyMapCondition = new HashMap<>();
            productPropertyMapCondition.put("enabled",EnabledType.USED);
            productPropertyMapCondition.put("product_id",product.product_id);
            productPropertyMapCondition.put("name",propertyKeyArray.get(i).getAsString());
            List<ProductSkuPropertyKey> productSkuPropertyKeyList = productPropertyKeyService.getProductPropertyKeyListByCondition(productPropertyMapCondition);

            // 判断product_sku_property 是否存在该key,没有则创建
            ProductSkuPropertyKey productSkuPropertyKey = new ProductSkuPropertyKey();
            if(productSkuPropertyKeyList != null && productSkuPropertyKeyList.size() > 0) {
                productSkuPropertyKey = productSkuPropertyKeyList.get(0);
            } else {
                productSkuPropertyKey.product_id = product.product_id;
                productSkuPropertyKey.name = propertyKeyArray.get(i).getAsString();
                productSkuPropertyKey.remark = "eds";
                productSkuPropertyKey.created_at = Helper.getCurrentTimeToUTCWithDate();
                productSkuPropertyKey.updated_at = Helper.getCurrentTimeToUTCWithDate();
                productSkuPropertyKey.enabled = EnabledType.USED;
                productSkuPropertyKey = productPropertyKeyService.createProductPropertyKey(productSkuPropertyKey);
            }

            // 判断product_sku_property_value 是否存在该value,没有则创建
            ProductSkuPropertyValue productPropertyValue = new ProductSkuPropertyValue();
            Map<String,Object> productPropertyValueMapCondition = new HashMap<>();
            productPropertyValueMapCondition.put("enabled",EnabledType.USED);
            productPropertyValueMapCondition.put("product_sku_property_key_id",productSkuPropertyKey.getProduct_sku_property_key_id());
            productPropertyValueMapCondition.put("value",skuOption.getSize());
            List<ProductSkuPropertyValue> productSkuPropertyValues = productPropertyValueService.getProductPropertyValueListByCondition(productPropertyValueMapCondition);
            if(productSkuPropertyValues != null && productSkuPropertyValues.size() > 0) {
                productPropertyValue = productSkuPropertyValues.get(0);
            } else {
                productPropertyValue.product_sku_property_key_id = productSkuPropertyKey.product_sku_property_key_id;
                productPropertyValue.value = skuOption.getSize();
                productPropertyValue.remark = skuOption.getSizeid() == null ? "update product" : skuOption.getSizeid();
                productPropertyValue.created_at = Helper.getCurrentTimeToUTCWithDate();
                productPropertyValue.updated_at = Helper.getCurrentTimeToUTCWithDate();
                productPropertyValue.enabled = EnabledType.USED;
                productPropertyValue = productPropertyValueService.createProductPropertyValue(productPropertyValue);
            }

            // get skuPrice
            String strPrice = salePrice;
            BigDecimal price =new BigDecimal(strPrice.trim());
            Sku skuPrice = priceService.getPriceByRule(product,vendorId,price,conn);

            // create sku
            Sku sku = new Sku();
            sku.product_id = product.product_id;
            sku.sku_code = skuOption.getBarcodes();
            sku.name = product.name;
            sku.coverpic = product.cover_img;
            sku.introduction = product.description;
            sku.price = price;
            sku.im_price = skuPrice.getIm_price();
            sku.in_price = skuPrice.getIn_price();
            sku.created_at = Helper.getCurrentTimeToUTCWithDate();
            sku.updated_at = Helper.getCurrentTimeToUTCWithDate();
            sku.enabled = EnabledType.USED;
            if(org.apache.commons.lang.StringUtils.isNotBlank(productOptions.getFullUpdateProductFlag())){
                if(productOptions.getFullUpdateProductFlag().equals("1")) {
                    sku.full_modify_date = new Date();
                    logger.info("updateProduct set full modify date newSku : " + new Gson().toJson(sku));
                }
            }
            sku = skuService.createSku(sku);

            // get category_product_info
            Map<String, Object> categoryProductInfoCondition = new HashMap<String, Object>();
            categoryProductInfoCondition.put("enabled", EnabledType.USED);
            categoryProductInfoCondition.put("category_id", product.category_id);
            List<CategoryProductInfo> categoryProductInfoList = categoryProductInfoService
                    .getCategoryProductInfoListByCondition(categoryProductInfoCondition);
            CategoryProductInfo categoryProductInfo = new CategoryProductInfo();
            if(categoryProductInfoList != null && categoryProductInfoList.size() > 0) {
                categoryProductInfo = categoryProductInfoList.get(0);
            }

            // create sku_store
            SkuStore skuStore = new SkuStore();
            skuStore.sku_id = sku.sku_id;
            skuStore.product_id = product.product_id;
            skuStore.store = new BigDecimal(skuOption.getStock().trim()).longValue();
            skuStore.remind = 0;
            skuStore.ordered = 0;
            skuStore.confirm = 0;
            skuStore.ship = 0;
            skuStore.finished = 0;
            skuStore.clear = 0;
            skuStore.changed = 0;
            skuStore.returned = 0;
            skuStore.agree_return_rate = categoryProductInfo.agree_return_rate == null ? new BigDecimal(0) : categoryProductInfo.agree_return_rate;
            skuStore.created_at = Helper.getCurrentTimeToUTCWithDate();
            skuStore.updated_at = Helper.getCurrentTimeToUTCWithDate();
            skuStore.enabled = EnabledType.USED;
            skuStoreService.createSkuStore(skuStore);

            SkuProperty skuProperty = new SkuProperty();
            skuProperty.sku_id = sku.sku_id;
            skuProperty.product_sku_property_key_id = productSkuPropertyKey.product_sku_property_key_id;
            skuProperty.product_sku_property_value_id = productPropertyValue.product_sku_property_value_id;
            skuProperty.name = sku.name;
            skuProperty.remark = "eds";
            skuProperty.created_at = Helper.getCurrentTimeToUTCWithDate();
            skuProperty.updated_at = Helper.getCurrentTimeToUTCWithDate();
            skuProperty.enabled = EnabledType.USED;
            skuProperty = skuPropertyService.createSkuProperty(skuProperty);

            break;
        }
    }

    public List<String> getPropertyKeyArray(Category category, CategorySkuPropertyKeyService categorySkuPropertyKeyService) {
        List<String> propertyKeyList = null;
        try {
            propertyKeyList = categorySkuPropertyKeyService.getCategoryPropertyKeyNameListByCategory(category.getCategory_id());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return propertyKeyList;
    }

    public ResultMessage changeStatus(Connection conn,Product product,Long vendorId) throws Exception {
        ResultMessage resultMessage = new ResultMessage();

        // get vendor
        VendorService vendorService = new VendorService(conn);
        Vendor vendor = vendorService.getVendorByVendorId(vendorId);

        // get shop
        ShopService shopService = new ShopService(conn);
        Shop shop = shopService.getShop(vendor.user_id);
        ShopProduct shopProduct = null;

        if( shop != null) {
            // get shop_product
            ShopProductService shopProductService = new ShopProductService(conn);
            shopProduct = shopProductService.getShopProductByShopAndProductId(shop.shop_id,
                    product.product_id);
        }

        // init statusMachineVO
        IProductStatusService productStatusService = new ProductStatusServiceImpl();
        StatusMachineVO statusMachineVO = new StatusMachineVO();
        statusMachineVO.setProductId(product.getProduct_id());
        statusMachineVO.setUserId(vendor.user_id);

        int currentStatus = product.status;
        int currentShopStatus = shopProduct == null ? -1 : shopProduct.getStatus();

        if(currentStatus == ProductStatusEnum.SHOP_OFF_SALE.getProductStatus()
                && currentShopStatus == ProductStatusEnum.SHOP_OFF_SALE.getShopProductStatus()){ // shop off sale

            // shop off sale -> modify pending
            productStatusService.statusMachineChange(conn,statusMachineVO,ProductStatusEnum.MODIFY_PENDING);

        } else if(currentStatus == ProductStatusEnum.SHOP_ON_SALE.getProductStatus()
                && currentShopStatus == ProductStatusEnum.SHOP_ON_SALE.getShopProductStatus()){ // shop on sale

            // shop off sale -> admin off sale
            productStatusService.statusMachineChange(conn,statusMachineVO,ProductStatusEnum.ADMIN_OFF_SALE);

            // admin off sale -> modify pending
            productStatusService.statusMachineChange(conn,statusMachineVO,ProductStatusEnum.MODIFY_PENDING);

        } else if(currentStatus == ProductStatusEnum.ADMIN_SHOP_OFF_SALE.getProductStatus()
                && currentShopStatus == ProductStatusEnum.ADMIN_SHOP_OFF_SALE.getShopProductStatus()) { // admin shop off sale

            // admin off sale -> modify pending
            productStatusService.statusMachineChange(conn,statusMachineVO,ProductStatusEnum.MODIFY_PENDING);

        } else if(currentStatus == ProductStatusEnum.SHOP_SOLD_OUT.getProductStatus()
                && currentShopStatus == ProductStatusEnum.SHOP_SOLD_OUT.getShopProductStatus()){

            // shop sold out -> admin shop off sale
            productStatusService.statusMachineChange(conn,statusMachineVO,ProductStatusEnum.ADMIN_SHOP_OFF_SALE);

            // admin off sale -> modify pending
            productStatusService.statusMachineChange(conn,statusMachineVO,ProductStatusEnum.MODIFY_PENDING);

        } else if(currentStatus == ProductStatusEnum.ADMIN_APPROVED.getProductStatus() && shopProduct == null) { // admin approved

            // admin approved -> admin off sale
            productStatusService.statusMachineChange(conn,statusMachineVO,ProductStatusEnum.ADMIN_OFF_SALE);

            // admin off sale -> modify pending
            productStatusService.statusMachineChange(conn,statusMachineVO,ProductStatusEnum.MODIFY_PENDING);

        } else if(currentStatus == ProductStatusEnum.ADMIN_OFF_SALE.getProductStatus() && shopProduct == null) { // admin off sale

            // admin off sale -> modify pending
            productStatusService.statusMachineChange(conn,statusMachineVO,ProductStatusEnum.MODIFY_PENDING);

        } else if(currentStatus == ProductStatusEnum.MODIFY_PENDING.getProductStatus()) { // modify pending

            // 不用修改

        } else if(currentStatus == ProductStatusEnum.MODIFY_REJECT.getProductStatus()){ // modify reject

            // modify reject -> modify pending
            productStatusService.statusMachineChange(conn,statusMachineVO,ProductStatusEnum.MODIFY_PENDING);

        } else if(currentStatus == ProductStatusEnum.NEW_REJECTED.getProductStatus()){ // new rejected

            // new rejected -> modify pending
            productStatusService.statusMachineChange(conn,statusMachineVO,ProductStatusEnum.MODIFY_PENDING);

        }else if(currentStatus == ProductStatusEnum.NEW_PENDING.getProductStatus()) { // new pending

            // new pending -> modify pending
            productStatusService.statusMachineChange(conn,statusMachineVO,ProductStatusEnum.MODIFY_PENDING);

        }

        return resultMessage;
    }

    public CategoryProductInfo getCategoryProductInfo(CategoryProductInfoService categoryProductInfoService,Long oldCategoryId) throws Exception{
        Map<String, Object> categoryProductInfoCondition = new HashMap<String, Object>();
        categoryProductInfoCondition.put("enabled", EnabledType.USED);
        categoryProductInfoCondition.put("category_id", oldCategoryId);
        List<CategoryProductInfo> categoryProductInfoList = categoryProductInfoService.getCategoryProductInfoListByCondition(categoryProductInfoCondition);
        if(categoryProductInfoList != null && categoryProductInfoList.size() > 0) {
            return categoryProductInfoList.get(0);
        }
        return null;
    }

    public void updateProductProperty(Connection conn,Long productId,String keyName,String value) throws  Exception{
        ProductPropertyService productPropertyService = new ProductPropertyService(conn);

        if(StringUtils.isNotBlank(value)){
            ProductProperty productProperty = new ProductProperty();
            productProperty.value = value;

            Map<String,Object> productPropertyConditions = new HashMap<>();
            productPropertyConditions.put("product_id",productId);
            productPropertyConditions.put("key_name",keyName);
            productPropertyConditions.put("enabled",EnabledType.USED);

            productPropertyService.updateProductPropertyByCondition(productProperty,productPropertyConditions);
        }
    }

    public void updateProductPropertyByBrandIDColorCode(Connection conn,Long productId,String keyName,String value,List<Map<String,Object>> maps,MapUtils mapUtils) throws  Exception{
        ProductPropertyService productPropertyService = new ProductPropertyService(conn);

        if(StringUtils.isNotBlank(value)){
            ProductProperty productProperty = new ProductProperty();
            productProperty.value = value;

            Map<String,Object> productPropertyConditions = new HashMap<>();
            productPropertyConditions.put("product_id",productId);
            productPropertyConditions.put("key_name",keyName);
            productPropertyConditions.put("enabled",EnabledType.USED);

            List<ProductProperty> list = productPropertyService.getProductPropertyByCondition(productPropertyConditions);
            if(list != null && list.size() > 0) {
                ShopProductService shopProductService = new ShopProductService(conn);

                if(list.get(0).getValue().equals(value)) {
                   return;
                }

                if(keyName.equals(ProductPropertyEnumKeyName.ColorCode.getCode())) {
                    mapUtils = new MapUtils(new HashMap<>());
                    mapUtils.putData("status",StatusType.WARNING)
                            .putData("info","update product - "+ ApiErrorTypeEnum.errorType.warning_ColorCode_change.getDesc()+" ColorCode:"+value)
                            .putData("error_enum",ApiErrorTypeEnum.errorType.warning_ColorCode_change)
                            .putData("key","ColorCode")
                            .putData("value",value);
                    maps.add(mapUtils.getMap());
                    logger.info("warning_ColorCode_changeProductProperty,value:"+value+",list:"+new Gson().toJson(list));
                    shopProductService.changeShopProductStopByProduct(productId.toString());
                } else if(keyName.equals(ProductPropertyEnumKeyName.BrandID.getCode())) {
                    mapUtils = new MapUtils(new HashMap<>());
                    mapUtils.putData("status",StatusType.WARNING)
                            .putData("info","update product - "+ ApiErrorTypeEnum.errorType.warning_BrandID_change.getDesc()+" BrandID:"+value)
                            .putData("error_enum",ApiErrorTypeEnum.errorType.warning_BrandID_change)
                            .putData("key","BrandID")
                            .putData("value",value);
                    maps.add(mapUtils.getMap());
                    logger.info("warning_BrandID_changeProductProperty,value:"+value+",list:"+new Gson().toJson(list));
                    shopProductService.changeShopProductStopByProduct(productId.toString());
                }
            }
        }
    }
    
    public boolean imgIsNull(String imgUrl){
    	if(StringUtils.isBlank(imgUrl) || imgUrl.equalsIgnoreCase("null") || imgUrl.equals("[]")) {
    		return true;
    	}
    	return false;
    }
/*

    public static void main(String[] args) {
        BigDecimal oldPrice = new BigDecimal(100);
        BigDecimal newPrice = new BigDecimal(200);
        System.out.println(ProductServiceImpl.isPrice(oldPrice,newPrice));
    }
*/


    public static boolean isPrice(BigDecimal oldPrice,BigDecimal newPrice){
        BigDecimal rangePrice = oldPrice.multiply(new BigDecimal(0.05));

        BigDecimal minPrice = oldPrice.subtract(rangePrice);
        BigDecimal maxPrice = oldPrice.add(rangePrice);

        int dy = newPrice.compareTo(minPrice);
        int xy = newPrice.compareTo(maxPrice);
        System.out.println(dy+"----"+xy);
        System.out.println(minPrice+"----"+maxPrice);

        if(dy == 1 && xy == -1) {
            return true;
        }
        return false;
    }

}
