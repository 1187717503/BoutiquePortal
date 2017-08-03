package pk.shoplus.common;

import java.math.BigDecimal;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pk.shoplus.model.Product;
import pk.shoplus.model.ProductSkuPropertyKey;
import pk.shoplus.model.Sku;
import pk.shoplus.model.SkuStore;

public class ProductHelper {

	/**
	 * 从map 转换成 product
	 * 
	 * @param map
	 * @return
	 */
	static public Product convertToProductFromMap(Map<String, Object> map) {
		Product product = new Product();
		product.product_id = (Long.parseLong(map.get("product_id").toString()));
		product.category_id = (Long) map.get("category_id");
		product.vendor_id = (Long) map.get("vendor_id");
		product.brand_id = (Long) map.get("brand_id");
		product.product_code = (String) map.get("product_code");
		product.cover_img = (String) map.get("cover_img");
		product.name = (String) map.get("name");
		product.description = (String) map.get("description");
		product.description_img = (String) map.get("description_img");
		product.remark = (String) map.get("remark");
		product.status = (Integer) map.get("status");
		product.publish_at = (Date) map.get("publish_at");
		product.valid_at = (Date) map.get("valid_at");
		product.feature = (Integer) map.get("feature");
		product.rejected_reason = (String) map.get("rejected_reason");
		product.rejected_by = (String) map.get("rejected_by");
		product.rejected_at = (Date) map.get("rejected_at");
		product.created_at = (Date) map.get("created_at");
		product.updated_at = (Date) map.get("updated_at");
		product.enabled = (Boolean) map.get("enabled");
		product.customer_rating = (BigDecimal) map.get("customer_rating");
		product.score = (BigDecimal) map.get("score");
		product.shop_rating = (BigDecimal) map.get("shop_rating");
		if (null != map.get("season_code")) {
			product.season_code = map.get("season_code").toString();
		}

		/*
		 * product.cover_img_list = new ArrayList<String>(); JsonArray
		 * coverImgJsonArray = new
		 * JsonParser().parse(product.cover_img).getAsJsonArray(); for (int i =
		 * 0; i < coverImgJsonArray.size(); i++) {
		 * product.cover_img_list.add(coverImgJsonArray.get(i).getAsString()); }
		 * 
		 * product.description_img_list = new ArrayList<String>(); JsonArray
		 * descriptionImgJsonArray = new
		 * JsonParser().parse(product.description_img).getAsJsonArray(); for
		 * (int i = 0; i < descriptionImgJsonArray.size(); i++) {
		 * product.description_img_list.add(descriptionImgJsonArray.get(i).
		 * getAsString()); }
		 */
		return product;
	}

	/**
	 * 从map 转换成 sku
	 * 
	 * @param map
	 * @return
	 */
	static public Sku convertToSkuFromMap(Map<String, Object> map) {
		Sku sku = new Sku();
		sku.sku_id = (Long.parseLong(map.get("sku_id").toString()));
		sku.product_id = (Long) map.get("product_id");
		sku.sku_code = (String) map.get("sku_code");
		sku.name = (String) map.get("name");
		sku.coverpic = (String) map.get("coverpic");
		sku.introduction = (String) map.get("introduction");
		sku.in_price = (BigDecimal) map.get("in_price");
		sku.price = (BigDecimal) map.get("price");
		sku.created_at = (Date) map.get("created_at");
		sku.updated_at = (Date) map.get("updated_at");
		sku.enabled = (Boolean) map.get("enabled");
		return sku;
	}

	/**
	 * 从map 转换成库存的 map
	 * 
	 * @param map
	 * @return
	 */
	static public Map<String, Object> convertToSkuStoreMapFromMap(Map<String, Object> map) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("store", map.get("store"));
		resultMap.put("remind", map.get("remind"));
		resultMap.put("ordered", map.get("ordered"));
		resultMap.put("confirm", map.get("confirm"));
		resultMap.put("ship", map.get("ship"));
		resultMap.put("finished", map.get("finished"));
		resultMap.put("returned", map.get("returned"));
		resultMap.put("changed", map.get("changed"));
		resultMap.put("clear", map.get("clear"));
		resultMap.put("agree_return_rate", map.get("agree_return_rate"));
		return resultMap;
	}

	/**
	 * 把map转换成 product property key
	 * 
	 * @param map
	 * @return
	 */
	static public ProductSkuPropertyKey convertToProductPropertyKeyFromMap(Map<String, Object> map) {
		ProductSkuPropertyKey productSkuPropertyKey = new ProductSkuPropertyKey();
		productSkuPropertyKey.product_sku_property_key_id = Long
				.parseLong(map.get("product_sku_property_key_id").toString());
		productSkuPropertyKey.product_id = (Long) map.get("product_id");
		productSkuPropertyKey.name = (String) map.get("name");
		productSkuPropertyKey.remark = (String) map.get("remark");
		productSkuPropertyKey.created_at = (Date) map.get("created_at");
		productSkuPropertyKey.updated_at = (Date) map.get("updated_at");
		productSkuPropertyKey.enabled = (Boolean) map.get("enabled");
		return productSkuPropertyKey;
	}

	// 根据list 获取product的 where string
	static public String getProductWhereString(List<Map<String, Object>> productList, String tablename) {
		String whereStr = "";
		for (int i = 0; i < productList.size(); i++) {

			if (productList.get(i).get("product_id") != null) {
				if (whereStr.length() > 0) {
					whereStr = whereStr + " or " + tablename + ".product_id = " + productList.get(i).get("product_id");
				} else {
					whereStr = tablename + ".product_id = " + productList.get(i).get("product_id");
				}
			}
		}

		return whereStr;
	}

	/**
	 * 根据SkuStore转换成map
	 * 
	 * @param skuStore
	 * @return
	 */
	static public Map<String, Object> convertToMapForSkuStore(SkuStore skuStore) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("agree_return_rate", skuStore.agree_return_rate);
		map.put("changed", skuStore.changed);
		map.put("clear", skuStore.clear);
		map.put("confirm", skuStore.confirm);
		map.put("created_at", skuStore.created_at);
		map.put("enabled", skuStore.enabled);
		map.put("finished", skuStore.finished);
		map.put("ordered", skuStore.ordered);
		map.put("remind", skuStore.remind);
		map.put("ship", skuStore.ship);
		map.put("returned", skuStore.returned);
		map.put("updated_at", skuStore.updated_at);
		map.put("sku_store_id", skuStore.sku_store_id);
		map.put("store", skuStore.store);

		return map;
	}

	/**
	 * 将sku_id转换成skuCode
	 */
	public static String postProcessing(String sku_id){
		String init = "00000000000000000000000";
		if(sku_id.length()<23){
			init = init.substring(0,23-sku_id.length());
			return init+sku_id;
		}else{
			return sku_id;
		}
	}

}
