package pk.shoplus.service;

import org.sql2o.Connection;

import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.ApiCategoryMap;

import java.util.List;
import java.util.Map;

/**
 * @author dingyifan
 *
 */
public class ApiCategoryMapService {
	/**
	 * 获取数据库连接
	 */
	private EntityDao<ApiCategoryMap> apiCategoryMapEntityDao = null;

	/**
	 * @param conn
	 */
	public ApiCategoryMapService(Connection conn) {
		apiCategoryMapEntityDao = new EntityDao<ApiCategoryMap>(conn);
	}
	
	public Long createApiBrandMap(ApiCategoryMap api_category_map) throws Exception {
		try {
			Long api_category_map_id = apiCategoryMapEntityDao.create(api_category_map);
			if (api_category_map_id != 0) {
				return api_category_map_id;
			}
		} catch (Exception e) {
			throw e;
		}

		return 0l;
	}
	
	public Long updateApiBrandMap(ApiCategoryMap api_category_map) throws Exception {
		try {
			apiCategoryMapEntityDao.updateById(api_category_map);
		} catch (Exception e) {
			throw e;
		}

		return 1l;
	}
	public List<ApiCategoryMap> queryByConditions(Map<String,Object> coditions) throws Exception{
		List<ApiCategoryMap> apiCategoryMaps = null;
		try{
			apiCategoryMaps = apiCategoryMapEntityDao.getByCondition(ApiCategoryMap.class,coditions);
		} catch (Exception e) {
			throw e;
		}
		return apiCategoryMaps;
	}

	public boolean querySaveBoutiqueCategory(String boutique_category_id,String api_configuration_id) {
		boolean flag = false;
		String sql = "select count(1) as count from api_category_map where enabled = 1 and boutique_category_id = '" + boutique_category_id + "' and api_configuration_id = " + api_configuration_id;
		try {
			List<Map<String, Object>> mapList = apiCategoryMapEntityDao.executeBySql(sql,null);
			if(mapList != null && mapList.size() > 0) {
				if(Integer.parseInt(mapList.get(0).get("count").toString()) >= 1) {
					return true;
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return flag;
	}

}
