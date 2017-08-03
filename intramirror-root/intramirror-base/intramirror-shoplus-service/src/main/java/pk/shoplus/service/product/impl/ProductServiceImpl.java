package pk.shoplus.service.product.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson15.JSONArray;
import com.alibaba.fastjson15.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.sql2o.Connection;
import pk.shoplus.DBConnector;
import pk.shoplus.common.Helper;
import pk.shoplus.enums.ApiParamterEnum;
import pk.shoplus.enums.ProductPropertyEnumKeyName;
import pk.shoplus.enums.ProductStatusEnum;
import pk.shoplus.model.*;
import pk.shoplus.mq.enums.QueueNameEnum;
import pk.shoplus.parameter.EnabledType;
import pk.shoplus.parameter.StatusType;
import pk.shoplus.service.*;
import pk.shoplus.service.price.api.IPriceService;
import pk.shoplus.service.price.impl.PriceServiceImpl;
import pk.shoplus.service.product.api.IProductService;
import pk.shoplus.service.product.api.IProductStatusService;
import pk.shoplus.vo.AddShopVo;
import pk.shoplus.vo.ResultMessage;
import pk.shoplus.vo.StatusMachineVO;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("status", StatusType.FAILURE);
        Connection conn = null ;
        try{
            // start transaction
            conn = DBConnector.sql2o.beginTransaction();

            ProductService productService = new ProductService(conn);
            CategoryService categoryService = new CategoryService(conn);
            CategoryProductInfoService categoryProductInfoService = new CategoryProductInfoService(conn);
            ProductInfoService productInfoService = new ProductInfoService(conn);
            SkuService skuService = new SkuService(conn);
            PriceChangeRuleService priceChangeRuleService = new PriceChangeRuleService(conn);
            ApiParameterService apiParameterService = new ApiParameterService(conn);

            // check product
            Map<String,Object> productConditions = new HashMap<>();
            productConditions.put("product_code",productOptions.getCode());
            productConditions.put("enabled",EnabledType.USED);
            Product product = productService.getProductByCondition(productConditions,null);
            if(product == null) {
                resultMap.put("info","Invalid boutique_id !");
                return resultMap;
            }
            Long oldCategoryId = product.getCategory_id();

            // check category
            if(StringUtils.isBlank(productOptions.getCategoryId())) {
                resultMap.put("info","Invalid category_id!");
                return resultMap;
            }
            Category category = categoryService.getCategoryById(Long.parseLong(productOptions.getCategoryId()));
            if(category == null) {
                resultMap.put("info","Invalid category_id!");
                return resultMap;
            }
            if(!categoryService.isLastNode(category.getCategory_id())) {
                resultMap.put("info", "The category of data is not level three!!!");
                return resultMap;
            }

            /** start update by dingyifan 2017-07-31 */
            if(StringUtils.isBlank(productOptions.getSalePrice()) || "0".equals(StringUtils.trim(productOptions.getSalePrice()))) {
                resultMap.put("info", "The price of the goods is empty or 0!!!");
                return resultMap;
            }
            /** end update by dingyifan 2017-07-31 */

            /*// check brand
            Brand brand = productEDSManagement.getBrand(productOptions.getBrandName(),vendorOptions.getApiConfigurationId(),conn,resultMap);
            if(brand == null) {
                return resultMap;
            }*/

            /** start update by dingyifan 2017-06-15 */
            boolean no_img = false;
            String englishName = productOptions.getBrandName();
            ApiBrandMapService apiBrandMapService = new ApiBrandMapService(conn);
            String brandId = apiBrandMapService.getBrandNameByBrand(englishName);
            if(org.apache.commons.lang.StringUtils.isBlank(brandId)) {
                brandId = apiBrandMapService.getBrandNameByBrandMapping(englishName,vendorOptions.apiConfigurationId.toString());
                if(org.apache.commons.lang.StringUtils.isBlank(brandId)) {
                    resultMap.put("info", " json_data[ 6 ] brand doesn't exist !!!");
                    resultMap.put("status", StatusType.FAILURE);
                    return resultMap;
                }
            }

            BrandService brandService = new BrandService(conn);
            Brand brand = brandService.getBrandById(Long.parseLong(brandId));

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
            }

            /** end update by dingyifan 2017-06-15 */

            // update brand_id,season_code,carry_over,color_code,color_description,composition,made_in,size_fit
            this.updateProductProperty(conn,product.getProduct_id(),ProductPropertyEnumKeyName.BrandID.getCode(),productOptions.getBrandCode());
            this.updateProductProperty(conn,product.getProduct_id(),ProductPropertyEnumKeyName.SeasonCode.getCode(),productOptions.getSeasonCode());
            this.updateProductProperty(conn,product.getProduct_id(),ProductPropertyEnumKeyName.CarryOver.getCode(),productOptions.getCarryOver());
            this.updateProductProperty(conn,product.getProduct_id(),ProductPropertyEnumKeyName.ColorCode.getCode(),productOptions.getColorCode());
            this.updateProductProperty(conn,product.getProduct_id(),ProductPropertyEnumKeyName.ColorDescription.getCode(),productOptions.getColorDesc());
            this.updateProductProperty(conn,product.getProduct_id(),ProductPropertyEnumKeyName.MadeIn.getCode(),productOptions.getMadeIn());
            this.updateProductProperty(conn,product.getProduct_id(),ProductPropertyEnumKeyName.SizeFit.getCode(),productOptions.getSizeFit());
            this.updateProductProperty(conn,product.getProduct_id(),ProductPropertyEnumKeyName.Composition.getCode(),productOptions.getComposition());

            // update category_id,product_description
//            product.category_id = category.getCategory_id();
            product.name = productOptions.getName();
            product.description = productOptions.getDesc();
            product.brand_id = brand.brand_id;

            logger.info(" start handle update product image ! " );
            ApiLogBatchService apiLogBatchService = new ApiLogBatchService(conn);
            String images = apiLogBatchService.selApiLogBatch(productOptions.getCode());

            if((StringUtils.isNotBlank(images) && !images.contains(productOptions.getImgByFilippo()) && StringUtils.isNotBlank(productOptions.getImgByFilippo()))
                    || this.imgIsNull(product.cover_img)) {
                if(!no_img) {
                    List<String> coverImg = productEDSManagement.convertImgPathList(productOptions.getCoverImg());
                    List<String> descImg = productEDSManagement.convertImgPathList(productOptions.getDescImg());

                    /** start update by dingyifan 2017-07-31 */
                    // 记录上传图片出错日志
                    if(StringUtils.isNotBlank(productOptions.getCoverImg())) {
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
                resultMap.put("status",StatusType.FAILURE);
                resultMap.put("info", " season_code is null 找不到映射!!!");
                return resultMap;
            }
            if(StringUtils.isNotBlank(season_code)) {
                product.season_code = season_code;
            } else {
                product.season_code = productOptions.getSeasonCode();
            }
            productService.updateProduct(product);

            // query category_product_info by oldCategoryId
            CategoryProductInfo categoryProductInfo = this.getCategoryProductInfo(categoryProductInfoService,oldCategoryId);
            if(categoryProductInfo != null) {
                // query product_info
                Map<String,Object> productInfoConditions = new HashMap<>();
                productInfoConditions.put("category_prodcut_info_id",categoryProductInfo.category_product_info_id);
                productInfoConditions.put("product_id",product.product_id);
                productInfoConditions.put("enabled",EnabledType.USED);
                List<ProductInfo> productInfos = productInfoService.getProductInfoByCondition(productInfoConditions);
                if(productInfos != null && productInfos.size() > 0) {
                    ProductInfo productInfo = productInfos.get(0);

                    // update weight,height,width,length
                    String weight = productOptions.getWeight();
                    String height = productOptions.getHeigit();
                    String width = productOptions.getWidth();
                    String length = productOptions.getLength();

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
                }
            }

            // query sku
            List<ProductEDSManagement.SkuOptions> skus = productOptions.getSkus();
            Map<String,Object> skuConditions = new HashMap<>();
            skuConditions.put("product_id",product.product_id);
            skuConditions.put("enabled",EnabledType.USED);
            List<Sku> skuList = skuService.getSkuListByCondition(skuConditions);

            List<Map<String,Object>> sizeValues = categoryService.executeSQL("select distinct pspv.`value` from sku\n" +
                    "left join sku_property sp on(sku.sku_id = sp.sku_id and sp.enabled = 1)\n" +
                    "left join product_sku_property_value pspv on(sp.product_sku_property_value_id = pspv.product_sku_property_value_id and pspv.enabled = 1)\n" +
                    "where sku.enabled = 1 and pspv.value is not null and product_id = "+product.product_id);

            // 如果sku不存在创建sku信息
            for(ProductEDSManagement.SkuOptions skuOption : skus) {
                boolean ifFlag = false;
                if(sizeValues != null && sizeValues.size() > 0) {
                    for(Map<String,Object> sizeValue : sizeValues) {
                        if(sizeValue.get("value").toString().equals(skuOption.getSize())) {
                            ifFlag = true;
                            break;
                        }
                    }
                }

                if(!ifFlag) {

                    // 添加sku信息
                    this.updateProductByAddSku(conn,product,category,skuOption,productOptions.getSalePrice(),vendorOptions.getVendorId());
                } else {
                    ProductStockEDSManagement.StockOptions stockOptions = productStockEDSManagement.getStockOptions();
                    stockOptions.setProductCode(product.getProduct_code());
                    stockOptions.setQuantity(skuOption.getStock());
                    stockOptions.setSizeValue(skuOption.getSize());
                    productStockEDSManagement.updateStock(stockOptions);
                }
            }

            // update sku price
            Map<String,Object> paramMap = apiParameterService.getParamByKeyName(ApiParamterEnum.ALLOW_PRICE_CHANGE.getCode());
            if(paramMap != null && paramMap.get("param_value")!=null && paramMap.get("param_value").toString().equals("1")){
                String strPrice = productOptions.getSalePrice();
                BigDecimal price =new BigDecimal(strPrice.trim());
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
            }

            // update product status
            ResultMessage resultMessage = this.changeStatus(conn,product,vendorOptions.getVendorId());

            if(!resultMessage.getStatus()){
                resultMap.put("info",resultMessage.getMsg());
            } else {
                resultMap.put("status", StatusType.SUCCESS);
                conn.commit();
            }

        } catch (Exception e) {
            if(conn != null) {
                conn.rollback();conn.close();
            }
            e.printStackTrace();
            resultMap.put("info","udpateProduct ErrorMessage:"+e.getMessage());
        } finally {
            if(conn != null) {conn.close();}
        }
        return resultMap;
    }

    @Override
    public void updateProductByAddSku(Connection conn, Product product, Category category, ProductEDSManagement.SkuOptions skuOption,String salePrice,Long vendorId) throws Exception {
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

    public void getProductStatus(int currentStatus,int currentShopStatus,ShopProduct shopProduct){

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
    
    public boolean imgIsNull(String imgUrl){
    	if(StringUtils.isBlank(imgUrl) || imgUrl.equalsIgnoreCase("null") || imgUrl.equals("[]")) {
    		return true;
    	}
    	return false;
    }

}
