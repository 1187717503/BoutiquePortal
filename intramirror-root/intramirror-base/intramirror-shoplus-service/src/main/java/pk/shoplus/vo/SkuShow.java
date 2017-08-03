package pk.shoplus.vo;

import java.util.List;
import java.util.Map;

import pk.shoplus.model.Sku;

/**
 * 显示sku
 * @author Caijl
 *
 */
public class SkuShow {

	/**
	 * sku信息
	 */
	public Sku sku;

	/**
	 * sku的code信息
	 */
	public String skuCode;
	
	/**
	 * sku库存信息
	 */
	//public SkuStore skuStore;
	
	/**
	 * sku库存信息的 map
	 */
	public Map<String, Object> skuStoreMap;
	
	/**
	 * sku 属性 list
	 */
	//public List<SkuPropertyShow> skuPropertyShowList;
	
	/**
	 * sku属性 map
	 */
	public List<Map<String, Object>> skuPropertyShowMapList;
	
	public Sku getSku() {
		return sku;
	}

	/*
	public SkuStore getSkuStore() {
		return skuStore;
	}
	*/
	public Map<String, Object> getSkuStoreMap() {
		return skuStoreMap;
	}

	/*
	public List<SkuPropertyShow> getSkuPropertyShowList() {
		return skuPropertyShowList;
	}
	*/
	public List<Map<String, Object>> getSkuPropertyShowMapList() {
		return skuPropertyShowMapList;
	}


	public String getSkuCode() {
		return skuCode;
	}
}
