
package pk.shoplus.data;


import com.alibaba.fastjson15.JSONArray;
import com.google.gson.*;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.sql2o.Connection;
import org.sql2o.data.Row;
import pk.shoplus.common.Helper;
import pk.shoplus.model.*;
import pk.shoplus.parameter.EnabledType;
import pk.shoplus.service.*;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.math.BigDecimal.ROUND_HALF_UP;

public class Products {

    private static Logger logger = Logger.getLogger(Products.class);

    public static Products.Insert insert(String part, Product product) {
        return new Products.Insert(part, product);
    }


    public static class Insert {


        private Part part;


        private Product product;


        public Insert(String part, Product product) {
            this.part = new Part(part);
            this.product = product;
        }


        public Product execute() throws Exception {
            Connection conn = DataBase.getConnection();
            pk.shoplus.model.Product pd = createProduct(conn);
            java.util.List<ProductSkuPropertyKey> productSkuPropertyKey =
                    createProductSku(conn, pd);
            createSku(conn, pd, productSkuPropertyKey);
            createCashBack(conn, pd);
            createBrandCategory(conn, pd);
            // 在vendor category 表中加入数据
            // 1.找到数据
            Map<String, Object> brandCategoryMap = new HashMap<String, Object>();
            brandCategoryMap.put("brand_id", pd.brand_id);
            brandCategoryMap.put("category_id", pd.category_id);
            brandCategoryMap.put("enabled", EnabledType.USED);
            BrandCategoryService brandCategoryService = new BrandCategoryService(conn);
            java.util.List<BrandCategory> brandCategoryList =
                    brandCategoryService.getBrandCategoryListByCondition(null, brandCategoryMap);
            // 2.如果无加入
            if (brandCategoryList == null || brandCategoryList.size() <= 0) {
                BrandCategory brandCategory = new BrandCategory();
                brandCategory.brand_id = pd.brand_id;
                brandCategory.category_id = pd.category_id;
                brandCategory.created_at = Helper.getCurrentTimeToUTCWithDate();
                brandCategory.updated_at = Helper.getCurrentTimeToUTCWithDate();
                brandCategory.enabled = EnabledType.USED;
                brandCategoryService.createBrandCategory(brandCategory);
            }

            // 6.在-----表中添加基本属性信息costList
            CategoryProductInfoService categoryProductInfoService = new CategoryProductInfoService(conn);
            ProductInfoService productInfoService = new ProductInfoService(conn);
            java.util.List<Map<String, String>> costList =
                    getCostList(pd.category_id, categoryProductInfoService);
            ProductInfo productInfo = createProductInfo(
                    costList, categoryProductInfoService, productInfoService, pd);

            // 7.创建productproperty信息
            java.util.List<CategoryProductProperty> cppList = getCategoryProductProperty(conn, pd.category_id);
            createProductProperty(conn, cppList, pd);

            return product;
        }


        public void createProductProperty(Connection conn,
                                          java.util.List<CategoryProductProperty> categoryProductPropertyList,
                                          pk.shoplus.model.Product product) throws Exception {
            if (null != categoryProductPropertyList && categoryProductPropertyList.size() > 0) {
                for (CategoryProductProperty categoryProductProperty : categoryProductPropertyList) {
                    if (null != categoryProductProperty) {
                        ProductProperty productProperty = new ProductProperty();
                        productProperty.category_product_property_id =
                                categoryProductProperty.category_product_property_id;
                        productProperty.product_id = product.product_id;
                        productProperty.key_name = categoryProductProperty.key_name;
                        productProperty.type = categoryProductProperty.type;
                        productProperty.created_at = Helper.getCurrentTimeToUTCWithDate();
                        productProperty.updated_at = Helper.getCurrentTimeToUTCWithDate();
                        productProperty.value = getValue(categoryProductProperty.key_name);
                        productProperty.remark = "";
                        productProperty.enabled = EnabledType.USED;

                        // 创建productproperty
                        ProductInfoPropertyService productInfoPropertyService = new ProductInfoPropertyService(conn);
                        productInfoPropertyService.createProductInfoProperty(productProperty);

                    }
                }
            }
        }


        public String getValue(String keyName) {
            ProductContentDetails contentDetails = product.getContentDetails();
            String value = null;
            switch (keyName) {
                case "Composition":
                    value = contentDetails.getComposition();
                    break;
                case "CarryOver":
                    value = contentDetails.getCarryOver();
                    break;
                case "ColorDescription":
                    value = contentDetails.getColorDescription();
                    break;
                case "MadeIn":
                    value = contentDetails.getMadeIn();
                    break;
                case "SizeFit":
                    value = contentDetails.getSizeFit();
                    break;
                case "ColorCode":
                    value = contentDetails.getColorCode();
                    break;
                case "BrandID":
                    value = contentDetails.getBrandCode();
                    break;
                case "SeasonCode":
                    value = contentDetails.getSeasonCode();
                    break;
            }
            if (value == null) {
                value = "";
            }
            return value;
        }


        private java.util.List<CategoryProductProperty> getCategoryProductProperty(
                Connection conn, Long categoryId) throws Exception {
            java.util.List<CategoryProductProperty> cppList = null;
            CategoryProductPropertyService cppService = new CategoryProductPropertyService(conn);
            Map<String, Object> condition = new HashMap<>();
            condition.put("category_id", categoryId);
            condition.put("enabled", 1);

            cppList = cppService.getCategoryProductPropertyListByCondition(condition);

            return cppList;
        }


        private ProductInfo createProductInfo(
                java.util.List<Map<String, String>> costList, CategoryProductInfoService categoryProductInfoService,
                ProductInfoService productInfoService, pk.shoplus.model.Product product) {

            JsonArray costListArray = new JsonParser().parse(JSONArray.toJSONString(costList)).getAsJsonArray();
            JsonObject costObj = costListArray.get(0).getAsJsonObject();


            // 查询category product info信息
            Map<String, Object> categoryProductInfoCondition = new HashMap<String, Object>();
            categoryProductInfoCondition.put("enabled", EnabledType.USED);
            categoryProductInfoCondition.put("category_product_info_id", costObj.get("id").getAsLong());


            java.util.List<CategoryProductInfo> categoryProductInfoList = null;
            try {
                categoryProductInfoList = categoryProductInfoService
                        .getCategoryProductInfoListByCondition(categoryProductInfoCondition);
            } catch (Exception e) {
                e.printStackTrace();
            }

            CategoryProductInfo categoryProductInfo = null;
            categoryProductInfo = categoryProductInfoList.get(0);
            // 生成Product Info信息
            ProductInfo productInfo = new ProductInfo();
            productInfo.category_prodcut_info_id = costObj.get("id").getAsLong();
            productInfo.product_id = product.product_id;
            productInfo.created_at = Helper.getCurrentTimeToUTCWithDate();
            productInfo.updated_at = Helper.getCurrentTimeToUTCWithDate();
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
            try {
                productInfo = productInfoService.createProductInfo(productInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return productInfo;
        }


        private java.util.List<Map<String, String>> getCostList(
                Long categoryId, CategoryProductInfoService categoryProductInfoService) throws Exception {
            java.util.List<Map<String, String>> costList = null;
            Map<String, String> map = null;
            Map<String, Object> categoryProductInfoCondition = new HashMap<String, Object>();
            categoryProductInfoCondition.put("enabled", EnabledType.USED);
            categoryProductInfoCondition.put("category_id", categoryId);

            java.util.List<CategoryProductInfo> categoryProductInfoList = categoryProductInfoService
                    .getCategoryProductInfoListByCondition(categoryProductInfoCondition);
            if (null != categoryProductInfoList && categoryProductInfoList.size() > 0) {
                costList = new ArrayList<>();
                CategoryProductInfo categoryProductInfo = categoryProductInfoList.get(0);
                map = new HashMap<>();
                Long categoryInfoId = categoryProductInfo.getCategory_product_info_id();
                map.put("id", categoryInfoId != null ? categoryInfoId.toString() : "");
                ProductContentDetails contentDetails = product.getContentDetails();
                if (contentDetails != null) {
                    BigDecimal t;
                    t = contentDetails.getWeight();
                    if (t != null) {
                        map.put("weight", t.toString());
                    }
                    //
                    t = contentDetails.getLength();
                    if (t != null) {
                        map.put("len", t.toString());
                    }
                    //
                    t = contentDetails.getWeight();
                    if (t != null) {
                        map.put("width", t.toString());
                    }
                    //
                    t = contentDetails.getHeight();
                    if (t != null) {
                        map.put("height", t.toString());
                    }
                    map.put("size", "");
                }
                costList.add(map);
            }

            return costList;
        }


        private void createBrandCategory(Connection conn, pk.shoplus.model.Product product) throws Exception {
            // 在vendor category 表中加入数据
            // 1.找到数据
            Map<String, Object> brandCategoryMap = new HashMap<String, Object>();
            brandCategoryMap.put("brand_id", product.brand_id);
            brandCategoryMap.put("category_id", product.category_id);
            brandCategoryMap.put("enabled", EnabledType.USED);
            BrandCategoryService brandCategoryService = new BrandCategoryService(conn);
            java.util.List<BrandCategory> brandCategoryList =
                    brandCategoryService.getBrandCategoryListByCondition(null, brandCategoryMap);
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
        }


        private java.util.List<Map<String, String>> getCashBackList() {
            java.util.List<Map<String, String>> cashBackList = new ArrayList<>();
            return cashBackList;
        }


        private void createCashBack(Connection conn, pk.shoplus.model.Product product) throws Exception {
            java.util.List<Map<String, String>> cashBackList = getCashBackList();
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
                vendorProductCashbackLevel.remark = "";
                vendorProductCashbackLevel.status = 0;
                vendorProductCashbackLevel.created_at = Helper.getCurrentTimeToUTCWithDate();
                vendorProductCashbackLevel.updated_at = Helper.getCurrentTimeToUTCWithDate();
                vendorProductCashbackLevel.enabled = EnabledType.USED;
                VendorProductCashbackLevelService vendorProductCashbackLevelService =
                        new VendorProductCashbackLevelService(conn);

                vendorProductCashbackLevel = vendorProductCashbackLevelService
                        .createVendorProductCashbackLevel(vendorProductCashbackLevel);
            }
        }
        
        private void createSku(Connection conn, pk.shoplus.model.Product pd,
                               java.util.List<ProductSkuPropertyKey> propertyKeyIdArr) throws Exception {

            if (!part.contains("skuDetails")) return;

            ProductSkuDetails skuDetails = product.getSkuDetails();
            if (skuDetails == null) return;
            if (skuDetails.size() <= 0) return;

            CategoryProductInfoService categoryProductInfoService = new CategoryProductInfoService(conn);
            SkuPropertyService skuPropertyService = new SkuPropertyService(conn);
            PriceChangeRuleService priceChangeRuleService = new PriceChangeRuleService(conn);

            //获取商品的折扣率
            Integer vendorDiscount = 100;
            Integer adminDiscount = 100;
            Map<String,Object> priceRuleList = priceChangeRuleService.getCurrentChangeRuleByProduct(pd);
            if (null != priceRuleList) {
                vendorDiscount = Integer.parseInt(priceRuleList.get("price1").toString());
                adminDiscount = Integer.parseInt(priceRuleList.get("price3").toString());
            }
            // 4.2) 每个sku 实体的数据加入sku表
            for (ProductSku item : skuDetails) {
                Sku sku = new Sku();
                sku.product_id = pd.product_id;
                String barcode = item.getBarcode();
                if (barcode == null) {
                    barcode = "";
                }
                sku.sku_code = barcode;
                sku.name = pd.name;
                sku.coverpic = pd.cover_img;
                sku.introduction = pd.description;
                //
                BigDecimal price = item.getPrice();
                if (price == null) {
                    price = new BigDecimal(0);
                }
                sku.price = price;
                //
                BigDecimal salePrice = item.getSalePrice();
                if (salePrice == null) {
                    salePrice = new BigDecimal(price.doubleValue() * vendorDiscount/((1+0.22)*100));
                }
                sku.in_price = salePrice;
                sku.im_price = new BigDecimal(price.doubleValue() * adminDiscount/((1)*100));
                sku.created_at = Helper.getCurrentTimeToUTCWithDate();
                sku.updated_at = Helper.getCurrentTimeToUTCWithDate();
                sku.enabled = EnabledType.USED;

                SkuService skuService = new SkuService(conn);
                sku = skuService.createSku(sku);

                // 查询category product info信息
                Map<String, Object> categoryProductInfoCondition = new HashMap<>();
                CategoryProductInfo categoryProductInfo = null;

                categoryProductInfoCondition.put("enabled", EnabledType.USED);
                categoryProductInfoCondition.put("category_id", pd.category_id);

                java.util.List<CategoryProductInfo> categoryProductInfoList = categoryProductInfoService
                        .getCategoryProductInfoListByCondition(categoryProductInfoCondition);

                categoryProductInfo = categoryProductInfoList.get(0);

                // 4.3) 每个sku 实体的数据加入sku_store表
                SkuStore skuStore = new SkuStore();
                skuStore.sku_id = sku.sku_id;
                skuStore.product_id = pd.product_id;
                BigDecimal stock = item.getStock();
                if (stock == null) {
                    stock = new BigDecimal(0);
                }
                skuStore.store = stock.longValue();
                skuStore.remind = 0;
                skuStore.ordered = 0;
                skuStore.confirm = 0;
                skuStore.ship = 0;
                skuStore.finished = 0;
                skuStore.clear = 0;
                skuStore.changed = 0;
                skuStore.returned = 0;
                skuStore.agree_return_rate = categoryProductInfo.agree_return_rate;
                skuStore.created_at = Helper.getCurrentTimeToUTCWithDate();
                skuStore.updated_at = Helper.getCurrentTimeToUTCWithDate();
                skuStore.enabled = EnabledType.USED;

                SkuStoreService skuStoreService = new SkuStoreService(conn);
                skuStore = skuStoreService.createSkuStore(skuStore);

                // 5.每个sku数据的属性加入sku_property表
                // 5.1) 通过property 找到

                for (int j = 0; j < propertyKeyIdArr.size(); j++) {
                    String propertyValue = item.getSize();
                    if (propertyValue == null) {
                        propertyValue = "";
                    }
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
                            skuProperty.remark = "";
                            skuProperty.created_at = Helper.getCurrentTimeToUTCWithDate();
                            skuProperty.updated_at = Helper.getCurrentTimeToUTCWithDate();
                            skuProperty.enabled = EnabledType.USED;
                            skuPropertyService.createSkuProperty(skuProperty);
                        }
                    }
                }
                logger.info("update sku by Products sku : "+new Gson().toJson(sku));
            }
        }


        private java.util.List<String> getPropertyKeyArray(Connection conn) throws Exception {
            CategorySkuPropertyKeyService categorySkuPropertyKeyService = new CategorySkuPropertyKeyService(conn);
            java.util.List<String> propertyKeyList = null;
            if (part.contains("snippet")) {
                ProductSnippet snippet = product.getSnippet();
                if (snippet != null) {
                    String id = snippet.getCategoryId();
                    if (id != null) {
                        propertyKeyList = categorySkuPropertyKeyService
                                .getCategoryPropertyKeyNameListByCategory(Long.parseLong(id));
                    }
                }
            }
            return propertyKeyList;
        }


        private java.util.List<ProductSkuPropertyKey> createProductSku(
                Connection conn, pk.shoplus.model.Product pd) throws Exception {
            // 2. 获取product_id，根据property_key_list 在 product_sku_property_key
            // 插入数据
            // 2.1) 把property_key_list转换成数组

            java.util.List<String> keyList = getPropertyKeyArray(conn);
            JsonArray propertyKeyArray = new JsonParser().parse(JSONArray.toJSONString(keyList)).getAsJsonArray();

            // 2.2) 把property_value_list转换成数组
            java.util.List<Map<String, java.util.List<String>>> valueList = null;
            java.util.List<String> list = new ArrayList<>();
            Map<String, java.util.List<String>> map = new HashMap<>();

            if (part.contains("skuDetails")) {
                ProductSkuDetails skuDetails = product.getSkuDetails();
                for (ProductSku sku : skuDetails) {
                    String size = sku.getSize();
                    if (size != null) {
                        list.add(sku.getSize());
                    }
                }
                map.put("Size", list);
            }

            valueList = new ArrayList<Map<String, java.util.List<String>>>();
            valueList.add(map);

            JsonArray propertyValueArray = new JsonParser().parse(JSONArray.toJSONString(valueList)).getAsJsonArray();

            // 2.3) 把每个key 插入到 product_sku_property_key表中
            java.util.List<ProductSkuPropertyKey> propertyKeyIdArr = new ArrayList<ProductSkuPropertyKey>();
            ProductPropertyKeyService productPropertyKeyService = new ProductPropertyKeyService(conn);
            ProductPropertyValueService productPropertyValueService = new ProductPropertyValueService(conn);
            for (int i = 0; i < propertyKeyArray.size(); i++) {
                // 插入数据库
                ProductSkuPropertyKey productSkuPropertyKey = new ProductSkuPropertyKey();
                productSkuPropertyKey.product_id = pd.product_id;
                productSkuPropertyKey.name = propertyKeyArray.get(i).getAsString();
                productSkuPropertyKey.remark = "";
                productSkuPropertyKey.created_at = Helper.getCurrentTimeToUTCWithDate();
                productSkuPropertyKey.updated_at = Helper.getCurrentTimeToUTCWithDate();
                productSkuPropertyKey.enabled = EnabledType.USED;

                productSkuPropertyKey = productPropertyKeyService.createProductPropertyKey(productSkuPropertyKey);

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
                            productPropertyValue.product_sku_property_key_id =
                                    productSkuPropertyKey.product_sku_property_key_id;
                            productPropertyValue.value = valueArray.get(k).getAsString();
                            productPropertyValue.remark = "";
                            productPropertyValue.created_at = Helper.getCurrentTimeToUTCWithDate();
                            productPropertyValue.updated_at = Helper.getCurrentTimeToUTCWithDate();
                            productPropertyValue.enabled = EnabledType.USED;
                            productPropertyValue = productPropertyValueService
                                    .createProductPropertyValue(productPropertyValue);
                            productSkuPropertyKey.propertyValueList.add(productPropertyValue);
                        }
                    }
                }

                // 记录插入productPropertyKey数据
                propertyKeyIdArr.add(productSkuPropertyKey);
            }

            return propertyKeyIdArr;
        }


        private pk.shoplus.model.Product createProduct(
                Connection conn) throws Exception {
            ProductService productService = new ProductService(conn);

            pk.shoplus.model.Product model = new pk.shoplus.model.Product();
            String t = null;

            model.product_code = product.getCode();
            model.remark = "";
            model.name = "";
            model.description = "";
            model.cover_img = "";
            model.description_img = "";
            model.remark = "";

            if (part.contains("snippet")) {
                ProductSnippet snippet = product.getSnippet();
                // set brand id
                t = snippet.getBrandId();
                if (t != null) {
                    model.brand_id = Long.parseLong(t);
                }
                // set vendor id
                t = snippet.getVendorId();
                if (t != null) {
                    model.vendor_id = Long.parseLong(t);
                }
                // set name
                t = snippet.getTitle();
                if (t != null) {
                    model.name = t;
                }
                //
                t = snippet.getCategoryId();
                if (t != null) {
                    model.category_id = Long.parseLong(t);
                }
                //
                t = snippet.getDescription();
                if (t != null) {
                    model.description = t;
                }
                //
                ThumbnailDetails cover = snippet.getThumbnails();
                if (cover != null && cover.size() > 0) {
                    JSONArray arr = new JSONArray();
                    for (Thumbnail item : cover) {
                        arr.add(item.getUrl());
                    }
                    model.cover_img = arr.toJSONString();
                }
                //
                int feature = snippet.getFeature();
                if (feature == 0) {
                    model.feature = ProductFeature.NORMAL;
                } else {
                    model.feature = feature;
                }
                //
                t = snippet.getRemark();
                if (t != null) {
                    model.remark = t;
                }
            }

            model.status = ProductApprovalStatus.PENDING;
            if (part.contains("status")) {
                ProductStatus status = product.getStatus();
                if (status != null) {
                    int approvalStatus = status.getApprovalStatus();
                    if (approvalStatus != 0) {
                        model.status = approvalStatus;
                    }
                }
            }

            if (part.contains("contentDetails")) {
                ProductContentDetails contentDetails = product.getContentDetails();
                ThumbnailDetails thumbnails = contentDetails.getThumbnails();
                if (thumbnails != null && thumbnails.size() > 0) {
                    JSONArray arr = new JSONArray();
                    for (Thumbnail item : thumbnails) {
                        arr.add(item.getUrl());
                    }
                    model.description_img = arr.toJSONString();
                }
            }

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            model.publish_at = (format.parse(DateFormatUtils.format(new Date(), "yyyy-MM-dd")));

            Calendar curr = Calendar.getInstance();
            curr.set(Calendar.YEAR, curr.get(Calendar.YEAR) + 100);
            Date date = curr.getTime();
            model.valid_at = (format.parse(DateFormatUtils.format(date, "yyyy-MM-dd")));

            model.created_at = Helper.getCurrentTimeToUTCWithDate();
            model.updated_at = Helper.getCurrentTimeToUTCWithDate();
            model.enabled = EnabledType.USED;

            productService.createProduct(model);
            return model;
        }


    }


    public static Products.List list(String part) {
        return new Products.List(part);
    }


    /**
     *
     */
    public static class List {


        private Part part;


        private Map<String, Object> params;


        public List(String part) {
            this.part = new Part(part);
            this.params = new HashMap<>();
        }


        public Products.List setCategory(String category) {
            params.put("category", category);
            return this;
        }


        public Products.List setId(String id) {
            params.put("id", id);
            return this;
        }


        public Products.List setCode(String code) {
            params.put("code", code);
            return this;
        }


        public Products.List setChart(String type) {
            params.put("chart", type);
            return this;
        }


        public Products.ListResponse execute() throws Exception {
            Connection conn = DataBase.getConnection();
            ProductService service = new ProductService(conn);
            java.util.List<pk.shoplus.model.Product> list =
                    new java.util.ArrayList<pk.shoplus.model.Product>();
            if (params.containsKey("chart")) {
                String chart = (String) params.get("chart");
                return queryChart(chart);
            } else if (params.containsKey("id")) {
                Long id = Long.valueOf((String) params.get("id"));
                pk.shoplus.model.Product product = service.getProductById(id);
                if (product != null) {
                    list.add(product);
                }
            } else if (params.containsKey("code")) {
                // TODO: by code only get the id,code fields.
                String code = (String) params.get("code");
                java.util.List<Map<String, Object>> res = service.getProductIdAndNameByCode(code);
                for (Map item : res) {
                    pk.shoplus.model.Product p = new pk.shoplus.model.Product();
                    p.product_id = (Long) item.get("product_id");
                    p.product_code = (String) item.get("product_code");
                    list.add(p);
                }
            }
            java.util.List<Product> items = new java.util.ArrayList<Product>();
            for (pk.shoplus.model.Product item : list) {
                Product p = new Product();
                if (part.contains("id")) {
                    p.setId(item.product_id.toString());
                }
                if (part.contains("code")) {
                    p.setCode(item.product_code);
                }
                if (part.contains("snippet")) {
                    ProductSnippet snippet = new ProductSnippet();
                    snippet.setThumbnails(ThumbnailDetails.fromString(item.cover_img))
                            .setTitle(item.name)
                            .setCategoryId(item.category_id.toString())
                            .setVendorId(item.vendor_id.toString())
                            .setBrandId(item.brand_id.toString())
                            .setPublishedAt(item.publish_at)
                            .setDescription(item.description);
                    p.setSnippet(snippet);
                }
                if (part.contains("contentDetails")) {
                    ProductContentDetails contentDetails = new ProductContentDetails();
                    p.setContentDetails(contentDetails);
                }
                if (part.contains("skuDetails")) {
                    ProductSkuDetails skuDetails = new ProductSkuDetails();
                    p.setSkuDetails(skuDetails);
                }
                if (part.contains("status")) {
                    ProductStatus status = new ProductStatus();
                    p.setStatus(status);
                }
                items.add(p);
            }
            Products.ListResponse res = new Products.ListResponse();
            res.setItems(items);
            return res;
        }


        private Products.ListResponse queryChart(String chart) {
            java.util.List<Product> items = new java.util.ArrayList<Product>();
            Products.ListResponse res = new Products.ListResponse();
            res.setItems(items);

            String category = null;
            if (params.containsKey("category")) {
                category = (String) params.get("category");
            }
            if (chart.equals("bestSeller")) {
                chart = "BestSeller";
            } else if (chart.equals("newThisWeek")) {
                chart = "NewThisWeek";
            } else if (chart.equals("recommend")) {
                chart = "Recommend";
            }

            String sql = "SELECT *, " +
                    "br.english_name brandTitle," +
                    "pp1.value brandCode, " +
                    "pp3.value sellerNumber, " +
                    "pp4.value newThisWeek, " +
                    "pp5.value recommend, " +
                    "pp2.value colorCode, " +
                    "sk.price price, " +
                    "sk.in_price supplyPrice, " +
                    "sps.sale_price salePrice, " +
                    "ca.name categoryTitle, " +
                    "ca1.name parentCategoryTitle, " +
                    "ca2.name ancestorCategoryTitle, " +
                    "SUM(st.store) stock , p.cover_img as coverImg " +
                    "FROM " +
                    "product p " +
                    "LEFT JOIN " +
                    "brand br " +
                    "ON " +
                    "p.brand_id = br.brand_id " +
                    "LEFT JOIN " +
                    "product_property pp " +
                    "ON " +
                    "p.product_id = pp.product_id AND " +
                    "pp.key_name = '" + chart + "' " +
                    "LEFT JOIN shop_product sp " +
                    "ON p.product_id = sp.product_id " +
                    "LEFT JOIN product_property pp1 " +
                    "ON " +
                    "pp.product_id = pp1.product_id AND " +
                    "pp1.key_name = 'BrandID' " +
                    "LEFT JOIN product_property pp2 " +
                    "ON " +
                    "p.product_id = pp2.product_id AND " +
                    "pp2.key_name = 'ColorCode' " +
                    "LEFT JOIN product_property pp3 " +
                    "ON " +
                    "p.product_id = pp3.product_id AND " +
                    "pp3.key_name = 'BestSeller' " +
                    "LEFT JOIN product_property pp4 " +
                    "ON " +
                    "p.product_id = pp4.product_id AND " +
                    "pp4.key_name = 'NewThisWeek' " +
                    "LEFT JOIN product_property pp5 " +
                    "ON " +
                    "p.product_id = pp5.product_id AND " +
                    "pp5.key_name = 'Recommend' " +
                    "LEFT JOIN (SELECT * FROM sku GROUP BY product_id) sk " +
                    "ON " +
                    "p.product_id = sk.product_id " +
                    "LEFT JOIN shop_product_sku sps " +
                    "ON sk.sku_id = sps.sku_id " +
                    "LEFT JOIN sku_store st " +
                    "ON p.product_id = st.product_id " +
                    "LEFT JOIN category ca " +
                    "ON p.category_id = ca.category_id " +
                    "LEFT JOIN category ca1 " +
                    "ON ca.parent_id = ca1.category_id " +
                    "LEFT JOIN category ca2 " +
                    "ON ca1.parent_id = ca2.category_id " +
                    "WHERE " +
                    "sp.enabled = 1 AND p.enabled = 1 AND sp.shop_id = 65 AND " +
                    "p.status = 3 AND sp.status = 0 AND pp.value > 0 " +
                    (category != null ? "AND p.category_id IN " +
                            "(" +
                            "SELECT category_id FROM category " +
                            "WHERE level = 3 AND " +
                            "(category_id IN (" + category + ") OR " +
                            "parent_id IN (" + category + "))" +
                            ") " : "") +
                    "GROUP BY p.product_id " +
                    "ORDER BY CAST(pp.value AS DECIMAL) " +
                    "LIMIT 0, 100";

            java.util.List<Row> rows = DataBase.query(sql);

            for (Row row : rows) {
                Product p = new Product();
                if (part.contains("id")) {
                    p.setId(row.getString("product_id"));
                }
                if (part.contains("code")) {
                    p.setCode(row.getString("product_code"));
                }
                if (part.contains("snippet")) {
                    ProductSnippet snippet = new ProductSnippet();
                    //
                    BigDecimal salePrice = row.getBigDecimal("salePrice");
                    if (salePrice != null) {
                        snippet.setSalePrice(salePrice);
                    }
                    //
                    BigDecimal price = row.getBigDecimal("price");
                    if (price != null) {
                        snippet.setPrice(price);
                    }
                    //
                    if (price != null && salePrice != null && !price.equals(BigDecimal.ZERO)) {
                        BigDecimal discount = salePrice.divide(price, 10, ROUND_HALF_UP);
                        snippet.setDiscount(discount);
                    }
                    //
                    String coverImg = row.getString("coverImg");
                    if (coverImg != null) {
                        snippet.setThumbnails(ThumbnailDetails.fromString(coverImg));
                    } else {
                        snippet.setThumbnails(ThumbnailDetails.fromString("[-1]"));
                    }
                    //
                    String title = row.getString("name");
                    if (title != null) {
                        snippet.setTitle(title);
                    }
                    //
                    String brandTitle = row.getString("brandTitle");
                    if (brandTitle != null) {
                        snippet.setBrandTitle(brandTitle);
                    }
                    //
                    String categoryId = row.getString("category_id");
                    if (categoryId != null) {
                        snippet.setCategoryId(categoryId);
                    }
                    //
                    String vendorId = row.getString("vendor_id");
                    if (vendorId != null) {
                        snippet.setVendorId(vendorId);
                    }
                    //
                    String brandId = row.getString("brand_id");
                    if (brandId != null) {
                        snippet.setBrandId(brandId);
                    }
                    //
                    String categoryTitle = row.getString("categoryTitle");
                    if (categoryTitle != null) {
                        snippet.setCategoryTitle(categoryTitle);
                    }
                    //
                    String parentCategoryTitle = row.getString("parentCategoryTitle");
                    if (parentCategoryTitle != null) {
                        snippet.setParentCategoryTitle(parentCategoryTitle);
                    }
                    //
                    String ancestorCategoryTitle = row.getString("ancestorCategoryTitle");
                    if (ancestorCategoryTitle != null) {
                        snippet.setAncestorCategoryTitle(ancestorCategoryTitle);
                    }
                    //
                    Date publishedAt = row.getDate("publish_at");
                    if (publishedAt != null) {
                        snippet.setPublishedAt(publishedAt);
                    }
                    //
                    BigDecimal stock = row.getBigDecimal("stock");
                    if (stock != null) {
                        snippet.setStock(stock);
                    }
                    //
                    BigDecimal supplyPrice = row.getBigDecimal("supplyPrice");
                    if (supplyPrice != null) {
                        snippet.setSupplyPrice(supplyPrice);
                    }
                    //
                    String description = row.getString("description");
                    if (description != null) {
                        snippet.setDescription(description);
                    }
                    p.setSnippet(snippet);
                }
                if (part.contains("contentDetails")) {
                    ProductContentDetails contentDetails = new ProductContentDetails();
                    Integer sellerNumber = row.getInteger("sellerNumber");
                    if (sellerNumber != null) {
                        contentDetails.setBestSellerNumber(sellerNumber);
                    }
                    Integer newThisWeekNumber = row.getInteger("newThisWeek");
                    if (newThisWeekNumber != null) {
                        contentDetails.setNewThisWeekNumber(newThisWeekNumber);
                    }
                    Integer recommendNumber = row.getInteger("recommend");
                    if (recommendNumber != null) {
                        contentDetails.setRecommendNumber(recommendNumber);
                    }
                    contentDetails
                            .setColorCode(row.getString("colorCode"))
                            .setBrandCode((row.getString("brandCode")));
                    p.setContentDetails(contentDetails);
                }
                if (part.contains("skuDetails")) {
                    ProductSkuDetails skuDetails = new ProductSkuDetails();
                    p.setSkuDetails(skuDetails);
                }
                if (part.contains("status")) {
                    ProductStatus status = new ProductStatus();
                    p.setStatus(status);
                }
                items.add(p);
            }

            return res;
        }


    }


    /**
     *
     */
    public static class ListResponse {


        private java.util.List<Product> items;


        private Products.ListResponse setItems(
                java.util.List<Product> items) {
            this.items = items;
            return this;
        }


        public java.util.List<Product> getItems() {
            return items;
        }


        public boolean isEmpty() {
            return items.isEmpty();
        }

    }


    public static Products.Update update(String part, Product product) {
        return new Products.Update(part, product);
    }


    public static class Update {


        private Part part;


        private Product product;


        public Update(String part, Product product) {
            this.part = new Part(part);
            this.product = product;
        }


        public Product execute() throws Exception {
            ProductContentDetails contentDetails = product.getContentDetails();
            if (part.contains("contentDetails.bestSellerNumber")) {
                int number = contentDetails.getBestSellerNumber();
                String sql = "UPDATE product_property " +
                        "SET value = " + number + " " +
                        "WHERE key_name = 'BestSeller' AND " +
                        "product_id = " + product.getId();
                DataBase.update(sql);
            }
            if (part.contains("contentDetails.newThisWeekNumber")) {
                int number = contentDetails.getNewThisWeekNumber();
                String sql = "UPDATE product_property " +
                        "SET value = " + number + " " +
                        "WHERE key_name = 'NewThisWeek' AND " +
                        "product_id = " + product.getId();
                DataBase.update(sql);
            }
            if (part.contains("contentDetails.recommendNumber")) {
                int number = contentDetails.getRecommendNumber();
                String sql = "UPDATE product_property " +
                        "SET value = " + number + " " +
                        "WHERE key_name = 'Recommend' AND " +
                        "product_id = " + product.getId();
                DataBase.update(sql);
            }
            return product;
        }


    }

}
