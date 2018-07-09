package pk.shoplus.vo;

import pk.shoplus.model.Product;
import pk.shoplus.model.ProductProperty;
import pk.shoplus.model.ProductSkuPropertyKey;
import pk.shoplus.model.VendorProductCashbackLevel;
import pk.shoplus.model.annotation.Column;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author Caijl
 *
 */
public class ProductShow {
	/**
	 *  product 实体
	 */
	public Product product;

	/**
	 * 商户名
	 */
	public String vendor_name;
	
	/**
	 * 品牌英文名字
	 */
	public String brand_english_name;
	
	/**
	 * 品牌中文名
	 */
	public String brand_chinese_name;
	
	/**
	 * 分类名
	 */
	public String category_name;
	
	/**
	 * 产品评分
	 */
	public double rate_product;
	
	/**
	 * product_property表 key_name 为 Brand 的value
	 */
	public String property_brandId;
	
	/**
	 * product product_group_name
	 */
	public String product_group_name;
	
		
	/**
	 * product对应的sku列表
	 */
	public List<SkuShow> skuShow;

	/**
	 * 每个product的库存
	 */
	public Map<String, Object> storeMap;

	/**
	 * 每个product对应的返点信息
	 */
	public List<VendorProductCashbackLevel> vendorProductCashbackLevel;
	
	/**
	 * 每个product的最大最小价格
	 */
	public Map<String, Object> priceMap;
	
	/**
	 * 每个product的key list
	 */
	public List<ProductSkuPropertyKey> keyList;
	
	/**
	 * 封面图片，经过json转换成list cover_img_list
	 */
	public List<String> cover_img_list;
	
	/**
	 * 描述图片，经过json转换成list description_img_list
	 */
	public List<String> description_img_list;

	public List<ProductProperty>  required_list;
	
	public Boolean img_modified;
	
	public Product getProduct() {
		return product;
	}

	public String getVendor_name() {
		return vendor_name;
	}

	public String getBrand_english_name() {
		return brand_english_name;
	}

	public String getBrand_chinese_name() {
		return brand_chinese_name;
	}

	public String getCategory_name() {
		return category_name;
	}

	public double getRate_product() {
		return rate_product;
	}
	
	public String getProperty_brandId() {
		return property_brandId;
	}
	
	public String getProduct_group_name() {
		return product_group_name;
	}

	public List<SkuShow> getSkuShow() {
		return skuShow;
	}

	public Map<String, Object> getStoreMap() {
		return storeMap;
	}

	public Map<String, Object> getPriceMap() {
		return priceMap;
	}

	public List<ProductSkuPropertyKey> getKeyList() {
		return keyList;
	}
	
	public List<String> getCover_img_list() {
		return cover_img_list;
	}

	public List<String> getDescription_img_list() {
		return description_img_list;
	}

	public List<ProductProperty> getRequired_list() {
		return required_list;
	}
	
	public Boolean getImg_modified() {
		return img_modified;
	}
}
