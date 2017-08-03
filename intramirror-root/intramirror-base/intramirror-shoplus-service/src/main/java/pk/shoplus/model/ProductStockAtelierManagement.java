package pk.shoplus.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.sql2o.Connection;

import com.alibaba.fastjson15.JSON;
import com.alibaba.fastjson15.JSONArray;
import com.alibaba.fastjson15.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import pk.shoplus.DBConnector;
import pk.shoplus.common.Helper;
import pk.shoplus.parameter.EnabledType;
import pk.shoplus.service.ApiConfigurationService;
import pk.shoplus.service.CategoryProductInfoService;
import pk.shoplus.service.CategorySkuPropertyKeyService;
import pk.shoplus.service.PriceChangeRuleService;
import pk.shoplus.service.ProductPropertyKeyService;
import pk.shoplus.service.ProductPropertyValueService;
import pk.shoplus.service.ProductService;
import pk.shoplus.service.SkuPropertyService;
import pk.shoplus.service.SkuService;
import pk.shoplus.service.SkuStoreService;

/**
 * 提供给Atelier用的Service,通过这个Service更新商品库存
 * @author dingyifan
 * 参考{@link shoplus-api UpdateSKUStockController}
 */
public class ProductStockAtelierManagement {
	
	private static Logger logger = Logger.getLogger(ProductStockEDSManagement.class);
	
	public Map<String, Object> updateStock(StockAtelierOptions stockOptions){
		Map<String, Object> result = new HashMap<String, Object>();
		
		Connection conn = null;
        try {
        	String body = new Gson().toJson(stockOptions.getSkus());
            String version = stockOptions.getVersion();
            String storeID = stockOptions.getStoreId();

            if (storeID == null) {
                return ResultHelper.createErrorResult(
                        "1", "E001: Parameter StoreID    is mandatory");
            }

            if (version == null || !version.equals("1.0"))  {
                return ResultHelper.createErrorResult(
                        "1", "E001: Parameter Version is mandatory");
            }

            JSONObject requestData = getRequestData(body);
            if (requestData == null) {
                return ResultHelper.createErrorResult(
                        "30", "E030: Empty Request");
            }

            JSONArray sku = requestData.getJSONArray("sku");
            if (sku == null) {
                return ResultHelper.createErrorResult("5", "E005: Invalid Request Format");
            }

            if (sku.size() > 200) {
                return ResultHelper.createErrorResult("204", "E204: Too many SKU in one request");
            }
            
            conn = DBConnector.sql2o.beginTransaction();
            ApiConfiguration apiConfiguration = getApiConfiguration(conn, storeID);
            if (apiConfiguration == null) {
                return ResultHelper.createErrorResult(
                        "1", "E001: Parameter StoreID is mandatory");
            }
            ProductService productService = new ProductService(conn);
            //
            for (Object item: sku) {
                JSONObject data = (JSONObject) item;
                // find product
                String boutiqueId = data.getString("boutique_id");
                if (boutiqueId == null) {
                    return ResultHelper.createErrorResult("5", "E005: Invalid Request Format");
                }
                List<Map<String, Object>> products = productService.getProductIdAndNameByCode(boutiqueId);
                if (products == null || products.size() <= 0) {
                    return ResultHelper.createErrorResult(
                            "204", "E204: Could not find Product");
                }
                Long productId = (Long) products.get(0).get("product_id");
                // find sku of product
                SkuService skuService = new SkuService(conn);
                Map<String, Object> condition = new HashMap<>();
                condition.put("product_id", productId);
                List<Sku> skuList = skuService.getSkuListByCondition(condition);
                SkuPropertyService skuPropertyService = new SkuPropertyService(conn);
                SkuStoreService skuStoreService = new SkuStoreService(conn);
                String size = data.getString("size");
                Long stock = data.getLong("stock");
                String barcode = data.getString("barcode");
                if (size == null || stock == null || barcode == null) {
                    return ResultHelper.createErrorResult("5", "E005: Invalid Request Format");
                }
                Boolean hasSize = false;
                BigDecimal price = null;
                // map product sku for update or create
                for (Sku skuItem: skuList) {
                    List<Map<String, Object>> skuPropertyList =
                            skuPropertyService.getSkuPropertyListBySkuId(skuItem.sku_id);
                    price = skuItem.price;
                    for (Object propertyItem: skuPropertyList) {
                        HashMap<String, HashMap> properties = (HashMap) propertyItem;
                        Object key = properties.get("key_name");
                        if (key.toString().equals("Size")) {
                            Object value = properties.get("value_name");
                            if (value.toString().equals(size)) {
                                // update stock
                                hasSize = true;
                                SkuStore skuStore = skuStoreService.getSkuStoreBySkuId(skuItem.sku_id);
                                if (skuStore != null && skuStore.store != stock) {
                                    skuStore.store = stock;
                                        skuStore.updated_at = Helper.getCurrentTimeToUTCWithDate();
                                    skuStoreService.updateSkuStore(skuStore);
                                }
                                result.put("ResponseStatus", "1000");
                                break;
                            }
                        }
                    }
                }
                if (!hasSize && price != null) {
                    // create sku;
                    Product product = productService.getProductById(productId);
                    createSku(conn, product, size,
                            stock.toString(), barcode, price.toString());
                    result.put("ResponseStatus", "1000");
                }
            }
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if(conn != null) {conn.rollback();conn.close();}
            return ResultHelper.createErrorResult(
                    "6", "E006: Unexpected internal error");
        } finally {
            if(conn != null) {conn.close();}
        }

        return result;
	}

    private JSONObject getRequestData(String body) {
        try {
            JSONObject data = JSON.parseObject(body);
            return data;
        } catch (Exception e) {
            return new JSONObject();
        }
    }


    private ApiConfiguration getApiConfiguration(Connection conn, String storeID) {
        ApiConfigurationService apiConfigurationService =
                new ApiConfigurationService(conn);
        Map<String, Object> condition = new HashMap<>();
        condition.put("store_code", storeID);
        condition.put("system", "intramirror");
        try {
            ApiConfiguration apiConfiguration =
                    apiConfigurationService.getMappingByCondition(condition, null);
            return apiConfiguration;
        } catch(Exception e) {
            return null;
        }
    }


    private void createSku(Connection conn, Product product, 
            String size, String stock, 
            String barcode, String price) throws Exception {
        List<ProductSkuPropertyKey> propertyKeyIdArr = createProductSku(conn,  product, size);
        List<Map<String, Object>> skuList = getSkuList(product, barcode, stock, price, size);
        JsonArray skuArray = new JsonParser().parse(JSONArray.toJSONString(skuList)).getAsJsonArray();
        CategoryProductInfoService categoryProductInfoService = new CategoryProductInfoService(conn);
        SkuPropertyService skuPropertyService = new SkuPropertyService(conn);
        PriceChangeRuleService priceChangeRuleService = new PriceChangeRuleService(conn);

        //获取商品的折扣率
//        Integer discountPercentage = getDiscountPercentage(product, product.vendor_id, priceChangeRuleService);
        Integer vendorDiscount = 100;
        Integer adminDiscount = 100;
        Map<String,Object> priceRuleList = priceChangeRuleService.getCurrentChangeRuleByProduct(product);
        if (null != priceRuleList) {
            vendorDiscount = Integer.parseInt(priceRuleList.get("price1").toString());
            adminDiscount = Integer.parseInt(priceRuleList.get("price3").toString());
        }

        // 4.2) 每个sku 实体的数据加入sku表
        for (int i = 0; i < skuArray.size(); i++) {
            JsonObject skuObj = skuArray.get(i).getAsJsonObject();
            Sku sku = new Sku();
            sku.product_id = product.product_id;
            sku.sku_code = barcode;
            sku.name = product.name;
            sku.coverpic = product.cover_img;
            sku.introduction = product.description;
            sku.price = new BigDecimal(skuObj.get("price").getAsString().trim());
            sku.in_price = new BigDecimal(sku.price.doubleValue() * vendorDiscount/((1+0.22)*100));
            sku.im_price = new BigDecimal(sku.price.doubleValue() * adminDiscount/((1)*100));
            sku.created_at = Helper.getCurrentTimeToUTCWithDate();
            sku.updated_at = Helper.getCurrentTimeToUTCWithDate();
            sku.enabled = EnabledType.USED;

            SkuService skuService = new SkuService(conn);
            sku = skuService.createSku(sku);

            // 查询category product info信息
            Map<String, Object> categoryProductInfoCondition = new HashMap<String, Object>();
            CategoryProductInfo categoryProductInfo = null;

            categoryProductInfoCondition.put("enabled", EnabledType.USED);
            categoryProductInfoCondition.put("category_id", product.category_id);

            List<CategoryProductInfo> categoryProductInfoList = categoryProductInfoService
                    .getCategoryProductInfoListByCondition(categoryProductInfoCondition);

            categoryProductInfo = categoryProductInfoList.get(0);

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
            skuStore.agree_return_rate = categoryProductInfo.agree_return_rate;
            skuStore.created_at = Helper.getCurrentTimeToUTCWithDate();
            skuStore.updated_at = Helper.getCurrentTimeToUTCWithDate();
            skuStore.enabled = EnabledType.USED;

            SkuStoreService skuStoreService = new SkuStoreService(conn);
            skuStore = skuStoreService.createSkuStore(skuStore);

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
                        skuProperty.remark = "";
                        skuProperty.created_at = Helper.getCurrentTimeToUTCWithDate();
                        skuProperty.updated_at = Helper.getCurrentTimeToUTCWithDate();
                        skuProperty.enabled = EnabledType.USED;
                        skuPropertyService.createSkuProperty(skuProperty);
                    }
                }
            }

        }
    }


    private List<Map<String, Object>> getSkuList (
            Product product, String barcode, String stock, String price, String size) {
        List<Map<String, Object>> skuList = null;
        Map<String, Object> map = null;
        List<String> sizeList = null;

        skuList = new ArrayList<>();
        sizeList = new ArrayList<>();
        sizeList.add(size);
        map = new HashMap<>();
        map.put("property", sizeList);
        map.put("skuCode", barcode);
        map.put("store", stock);
        map.put("price", price);
        map.put("storeMini", 0);
        map.put("inPrice", 0);
        skuList.add(map);

        return skuList;
    }

/*
    private Integer getDiscountPercentage (Product product, Long vendorId, PriceChangeRuleService priceChangeRuleService) {
        Integer discountPercentage = 0;
        List<Map<String, Object>> priceRuleList = null;
        //获取商品的折扣率
        try {
            if (null != product.getCategory_id()
                    && null != product.getBrand_id()
                    && null != product.getProduct_code()
                    && !"".equals(product.getProduct_code())) {
                priceRuleList = priceChangeRuleService.getCurrentChangeRuleByProduct(product.getCategory_id(),
                        product.getBrand_id(), product.getProduct_code(), vendorId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (null != priceRuleList && priceRuleList.size() > 0) {
            Map<String, Object> map = priceRuleList.get(0);
            if (null != map && null != map.get("discount_percentage")) {
                discountPercentage = Integer.valueOf(map.get("discount_percentage").toString());
            }
        }

        return  discountPercentage;
    }*/


    private List<ProductSkuPropertyKey> createProductSku(Connection conn,
                                                         Product product, String size) throws Exception {
        // 2. 获取product_id，根据property_key_list 在 product_sku_property_key
        // 插入数据
        // 2.1) 把property_key_list转换成数组
        CategorySkuPropertyKeyService categorySkuPropertyKeyService = new CategorySkuPropertyKeyService(conn);
        List<String> keyList = getPropertyKeyArray(product.category_id, categorySkuPropertyKeyService);
        JsonArray propertyKeyArray = new JsonParser().parse(JSONArray.toJSONString(keyList)).getAsJsonArray();

        // 2.2) 把property_value_list转换成数组
        //CategorySkuPropertyValueService categorySkuPropertyValueService = new CategorySkuPropertyValueService(conn);
        //List<Map<String, List<String>>> valueList = getPropertyValueArray(category, categorySkuPropertyValueService);
        List<Map<String, List<String>>> valueList = null;
        List<String> list = new ArrayList<>();
        Map<String, List<String>> map = new HashMap<>();

        list.add(size);

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
            /*
            ProductSkuPropertyKey productSkuPropertyKey = new ProductSkuPropertyKey();
            productSkuPropertyKey.product_id = product.product_id;
            productSkuPropertyKey.name = propertyKeyArray.get(i).getAsString();
            productSkuPropertyKey.remark = "";
            productSkuPropertyKey.created_at = Helper.getCurrentTimeToUTCWithDate();
            productSkuPropertyKey.updated_at = Helper.getCurrentTimeToUTCWithDate();
            productSkuPropertyKey.enabled = EnabledType.USED;

            productSkuPropertyKey = productPropertyKeyService.createProductPropertyKey(productSkuPropertyKey);
            */
            HashMap<String, Object> condition = 
                new HashMap<String, Object>();
            condition.put("product_id", product.product_id);
            condition.put("name", "Size");
            ProductSkuPropertyKey productSkuPropertyKey = 
                productPropertyKeyService
                    .getProductPropertyKeyListByCondition(condition)
                    .get(0);
                

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

    private List<String> getPropertyKeyArray (Long categoryId, CategorySkuPropertyKeyService categorySkuPropertyKeyService) {
        List<String> propertyKeyList = null;
        try {
            propertyKeyList = categorySkuPropertyKeyService.getCategoryPropertyKeyNameListByCategory(categoryId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return propertyKeyList;
    }
    
    public StockAtelierSkuOptions getStockAtelierSkuOptions(){
    	return new StockAtelierSkuOptions();
    }
    
    public StockAtelierOptions getStockAtelierOptions(){
    	return new StockAtelierOptions();
    }
    
    public class StockAtelierOptions{
    	private String version;
    	private String storeId;
    	private List<StockAtelierSkuOptions> skus;

    	public String getVersion() {
			return version;
		}
		public void setVersion(String version) {
			this.version = version;
		}
		public String getStoreId() {
			return storeId;
		}
		public void setStoreId(String storeId) {
			this.storeId = storeId;
		}
		public List<StockAtelierSkuOptions> getSkus() {
			return skus;
		}
		public void setSkus(List<StockAtelierSkuOptions> skus) {
			this.skus = skus;
		}
    }
    
    public class StockAtelierSkuOptions{
    	private String boutiqueId;
    	private String barcode;
    	private String size;
    	private String stock;
    	
		public String getBoutiqueId() {
			return boutiqueId;
		}
		public void setBoutiqueId(String boutiqueId) {
			this.boutiqueId = boutiqueId;
		}
		public String getBarcode() {
			return barcode;
		}
		public void setBarcode(String barcode) {
			this.barcode = barcode;
		}
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
    }
}
