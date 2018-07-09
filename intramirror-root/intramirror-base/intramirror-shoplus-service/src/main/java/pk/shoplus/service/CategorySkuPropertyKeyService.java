package pk.shoplus.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.sql2o.Connection;

import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.CategorySkuPropertyKey;

public class CategorySkuPropertyKeyService {
	/**
	 * 获取数据库连接
	 */
	private EntityDao<CategorySkuPropertyKey> categorySkuPropertyKeyDao = null;

	/**
	 * @param conn
	 */
	public CategorySkuPropertyKeyService(Connection conn) {
		categorySkuPropertyKeyDao = new EntityDao<CategorySkuPropertyKey>(conn);
	}

	/**
	 * 根据条件查询 ProductPropertyKey List
	 * 
	 * @param condition
	 * @return
	 * @throws Exception
	 */
	public List<CategorySkuPropertyKey> getCategoryPropertyKeyListByCondition(Map<String, Object> condition)
			throws Exception {
		try {
			// 查询
			List<CategorySkuPropertyKey> categoryPropertyKeyList = categorySkuPropertyKeyDao
					.getByCondition(CategorySkuPropertyKey.class, "*", condition);
			return categoryPropertyKeyList;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据分类类目查询 ProductPropertyKey List
	 *
	 * @param categoryId
	 * @return
	 * @throws Exception
	 */
	public List<String> getCategoryPropertyKeyNameListByCategory(Long categoryId)
			throws Exception {
		List<String> keyList = null;
		try {
			StringBuffer sql = new StringBuffer("select distinct cspk.category_sku_property_key_id, cspk.`name` " +
					"from category_sku_property csp,category_sku_property_key cspk " +
					"where csp.category_id = :p1 " +
					"and csp.category_sku_property_key_id = cspk.category_sku_property_key_id " +
					"and csp.enabled = 1 " +
					"and cspk.enabled = 1");
			Object[] params = new Object[]{categoryId};

			List<Map<String, Object>> categoryMapList = categorySkuPropertyKeyDao.executeBySql(sql.toString(), params);

			if (null != categoryMapList && categoryMapList.size() > 0) {
				keyList = new ArrayList<>(categoryMapList.size());
				for (Map<String, Object> map : categoryMapList) {
					if (null != map && null != map.get("name")) {
						keyList.add(map.get("name").toString());
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}

		return keyList;
	}

	/**
	 * 根据 categoryPropertyKey 加入数据
	 * 
	 * @param categoryPropertyKey
	 * @return
	 * @throws Exception
	 */
	public CategorySkuPropertyKey createCategoryPropertyKey(CategorySkuPropertyKey categoryPropertyKey) throws Exception {
		try {
			long id = categorySkuPropertyKeyDao.create(categoryPropertyKey);
			if (id > 0) {
				categoryPropertyKey.category_sku_property_key_id = id;
			} else {
				categoryPropertyKey = null;
			}
			return categoryPropertyKey;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 通过 batch 批量加数据
	 * @param categoryPropertyKeyList
	 * @return
	 * @throws Exception
	 */
	public List<CategorySkuPropertyKey> createCategoryPropertyKeyByBatch(List<CategorySkuPropertyKey> categoryPropertyKeyList) throws Exception
	{
		try
		{
			//返回数据初始化
			List<CategorySkuPropertyKey> resultList = new ArrayList<CategorySkuPropertyKey>();
			//id 列表
			List<Long> idList = categorySkuPropertyKeyDao.createByBatch(categoryPropertyKeyList);
			for(int i = 0; i < categoryPropertyKeyList.size(); i++)
			{
				CategorySkuPropertyKey categorySkuPropertyKey = categoryPropertyKeyList.get(i);
				if (idList.get(i) > 0) {
					categorySkuPropertyKey.category_sku_property_key_id = idList.get(i);
				} else {
					categorySkuPropertyKey = null;
				}
				resultList.add(categorySkuPropertyKey);
			}
			return resultList;
		}
		catch (Exception e) {
			throw e;
		}
		
	}
}
