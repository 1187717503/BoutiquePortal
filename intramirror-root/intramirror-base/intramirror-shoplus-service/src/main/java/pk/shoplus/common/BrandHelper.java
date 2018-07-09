package pk.shoplus.common;

import java.util.Date;
import java.util.Map;

import pk.shoplus.model.Brand;
import pk.shoplus.vo.BrandShow;

public class BrandHelper {

	/**
	 * 从map 转换成 Brand
	 * 
	 * @param map
	 * @return
	 */
	static public Brand convertToBrandFromMap(Map<String, Object> map) {

		Brand brand = new Brand();
		brand.brand_id = (Long.parseLong(map.get("brand_id").toString()));
		brand.english_name = (String) map.get("english_name");
		brand.chinese_name = (String) map.get("chinese_name");
		brand.logo = (String) map.get("logo");
		brand.type = (Integer)map.get("type");
		brand.description = (String) map.get("description");
		brand.remark = (String) map.get("remark");
		brand.creator = (String) map.get("creator");
		brand.created_at = (Date) map.get("created_at");
		brand.updated_at = (Date) map.get("updated_at");
		brand.enabled = (Boolean) map.get("enabled");
		brand.status = (Integer)map.get("status");
		if(map.get("vendor_application_id")!=null){
			brand.vendor_application_id = (Long.parseLong(map.get("vendor_application_id").toString()));
		}

		brand.hot_brand = (Integer)map.get("hot_brand");

		return brand;
	}
	
	
	/**
	 * 从map 转换成 BrandShow
	 * 
	 * @param map
	 * @return
	 */
	static public BrandShow convertToBrandShowFromMap(Map<String, Object> map) {

		BrandShow brand = new BrandShow();
		brand.brand_id = (Long.parseLong(map.get("brand_id").toString()));
		brand.english_name = (String) map.get("english_name");
		brand.chinese_name = (String) map.get("chinese_name");
		brand.logo = (String) map.get("logo");
		brand.type = (Integer)map.get("type");
		brand.description = (String) map.get("description");
		brand.remark = (String) map.get("remark");
		brand.creator = (String) map.get("creator");
		brand.created_at = (Date) map.get("created_at");
		brand.updated_at = (Date) map.get("updated_at");
		brand.enabled = (Boolean) map.get("enabled");
		brand.status = (Integer)map.get("status");
		if(map.get("vendor_application_id")!=null){
			brand.vendor_application_id = (Long.parseLong(map.get("vendor_application_id").toString()));
		}

		brand.hot_brand = (Integer)map.get("hot_brand");

		return brand;
	}


}
