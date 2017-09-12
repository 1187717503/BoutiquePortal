package pk.shoplus.service;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.sql2o.Connection;
import pk.shoplus.common.utils.StringUtil;
import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.ApiBrandMap;
import pk.shoplus.model.Brand;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ApiBrandMapService {
	private  static Logger logger = Logger.getLogger(ApiBrandMapService.class);
	/**
	 * 获取数据库连接
	 */
	private EntityDao<ApiBrandMap> apiBrandMapEntityDao = null;

	/**
	 * @param conn
	 */
	public ApiBrandMapService(Connection conn) {
		apiBrandMapEntityDao = new EntityDao<ApiBrandMap>(conn);
	}


	/**
	 * 通过条件获取BrandList
	 * 
	 * @param condition
	 * @return
	 * @throws Exception
	 */
	public List<ApiBrandMap> getApiBrandMapListByCondition(Map<String, Object> condition) throws Exception {
		try {
			// 查询
			String fieldName = "*";
			List<ApiBrandMap> brandList = apiBrandMapEntityDao.getByCondition(ApiBrandMap.class, fieldName, condition);
			return brandList;
		} catch (Exception e) {
			throw e;
		}
	}

	public List<Map<String, Object>> getApiBrandMapListByCondition(String brandName,String vendor_id) throws Exception {
		try {
			if(StringUtils.isNotBlank(brandName)) {
				String sql = "select * from api_brand_map abm\n" +
						"inner join api_configuration ac on(ac.api_configuration_id = abm.api_configuration_id)\n" +
						"where abm.enabled = 1 and ac.enabled = 1 and ac.vendor_id ="+vendor_id+"\n"+
						" and abm.no_img = 1 and lower(boutique_brand_name) = lower('"+pk.shoplus.util.StringUtils.escapeStr(brandName)+"')";
				return apiBrandMapEntityDao.executeBySql(sql, null);
			}
		} catch (Exception e) {
			throw e;
		}
		return null;
	}
	
	public String getApiBrandMapListByCondition(String brandName) throws Exception {
		try {
			if(StringUtils.isNotBlank(brandName)) {
//				logger.info(" .getApiBrandMapListByCondition brandName  before escape : " + brandName);
				String sql = "select boutique_brand_name as brandName from api_brand_map where lower(boutique_brand_name) = lower('" + pk.shoplus.util.StringUtils.escapeStr(brandName) + "') and enabled = 1";
//				logger.info(" .getApiBrandMapListByCondition brandName  before escape : " + sql);
				List<Map<String, Object>> list = apiBrandMapEntityDao.executeBySql(sql, null);
				if(list != null && list.size() > 0) {
					return list.get(0).get("brandName").toString();
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return null;
	}

	public String getApiBrandName(String brandName) throws Exception {
		try {
			if(StringUtils.isNotBlank(brandName)) {

				String sql  = "select * from brand b where lower(b.english_name) = lower('" + brandName + "')";

				List<Map<String, Object>> list = apiBrandMapEntityDao.executeBySql(sql, null);
				if(list != null && list.size() > 0) {
					return list.get(0).get("brandName").toString();
				}
			}

		} catch (Exception e) {
			throw e;
		}
		return null;
	}


	public String getBrandNameByBrand(String brandName) throws Exception {
		try {
			if(StringUtils.isNotBlank(brandName)) {
				logger.info(" .getBrandNameByBrand brandName  before escape : " + brandName);
				String sql  = "select b.brand_id from brand b where lower(b.english_name) = lower(\"" + pk.shoplus.util.StringUtils.escapeStr(brandName) + "\") and b.enabled = 1";
				logger.info(" .getBrandNameByBrand brandName  after escape : " + sql);

				List<Map<String, Object>> list = apiBrandMapEntityDao.executeBySql(sql, null);
				if(list != null && list.size() > 0) {
					return list.get(0).get("brand_id").toString();
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return null;
	}

	public String getBrandNameByBrandMapping(String brandName,String vendor_id) throws Exception {
		try {
			if(StringUtils.isNotBlank(brandName) && StringUtils.isNotBlank(vendor_id)) {
				String cfgIds = "";
				String sql = "select distinct api_configuration_id from api_configuration ac where ac.enabled = 1 and ac.vendor_id = "+vendor_id;

				List<Map<String,Object>> apiCfgMaps = apiBrandMapEntityDao.executeBySql(sql,null);
				if(apiCfgMaps != null && apiCfgMaps.size() > 0){
					for(Map<String,Object> map : apiCfgMaps) {
						String api_configuration_id = map.get("api_configuration_id").toString();
						cfgIds = cfgIds + api_configuration_id + ",";
					}
				}

				if(StringUtils.isNotBlank(cfgIds)) {
					cfgIds = cfgIds.substring(0,cfgIds.length()-1);
				} else {
					return null;
				}
				logger.info(" .getBrandNameByBrandMapping brandName  before escape : " + brandName);
				sql  = "select distinct abm.brand_id from api_brand_map abm where abm.brand_id is not null and lower(abm.boutique_brand_name) = lower(\"" + pk.shoplus.util.StringUtils.escapeStr(brandName) + "\") and abm.api_configuration_id in(" + cfgIds + ") and abm.enabled = 1";
				logger.info(" .getBrandNameByBrandMapping brandName  after escape : " + sql);

				List<Map<String, Object>> list = apiBrandMapEntityDao.executeBySql(sql, null);
				if(list != null && list.size() > 0) {
					return list.get(0).get("brand_id").toString();
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return null;
	}

	public List<Map<String, Object>> getBrandMapByApi(Long apiConfigurationId, List<Long> brandId) {
		ArrayList<String> idsArr = new ArrayList<String>();
		for (Long id: brandId) {
			idsArr.add(id.toString());
		}
		String ids = String.join(",", idsArr);
		String sql = "SELECT api_brand_map_id, api_configuration_id, brand_id, " +
				"brand_name, boutique_brand_name,no_img FROM api_brand_map " +
				"WHERE api_configuration_id = " + apiConfigurationId +
				" AND brand_id IN (" + ids + ") AND enabled = 1";
		return apiBrandMapEntityDao.executeBySql(sql, null);
	}


	
	public Long createApiBrandMap(ApiBrandMap api_brand_map) throws Exception {
		try {
			Long api_brand_map_id = apiBrandMapEntityDao.create(api_brand_map);
			if (api_brand_map_id != 0) {
				return api_brand_map_id;
			}
		} catch (Exception e) {
			throw e;
		}
		return 0l;
	}
	
	public void delApiBrandMap(ApiBrandMap api_brand_map) throws Exception {
		try {
			apiBrandMapEntityDao.updateById(api_brand_map);
		} catch (Exception e) {
			throw e;
		}
	}

}
