package pk.shoplus.service;

import java.util.*;

import org.sql2o.Connection;

import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.CategorySkuPropertyValue;
import pk.shoplus.parameter.EnabledType;

public class CategorySkuPropertyValueService {

	/**
	 * 获取数据库连接
	 */
	private EntityDao<CategorySkuPropertyValue> categorySkuPropertyValueDao = null;

	/**
	 * @param conn
	 */
	public CategorySkuPropertyValueService(Connection conn) {
		categorySkuPropertyValueDao = new EntityDao<CategorySkuPropertyValue>(conn);
	}

	/**
	 * 根据 categoryPropertyKey 加入数据
	 * 
	 * @param categoryPropertyKey
	 * @return
	 * @throws Exception
	 */
	public CategorySkuPropertyValue createCategorySkuPropertyValue(CategorySkuPropertyValue categorySkuPropertyValue)
			throws Exception {
		try {
			long id = categorySkuPropertyValueDao.create(categorySkuPropertyValue);
			if (id > 0) {
				categorySkuPropertyValue.category_sku_property_value_id = id;
			} else {
				categorySkuPropertyValue = null;
			}
			return categorySkuPropertyValue;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 通过 batch 批量加数据
	 * @param CategorySkuPropertyValueyList
	 * @return
	 * @throws Exception
	 */
	public List<CategorySkuPropertyValue> createCategorySkuPropertyValueByBatch(List<CategorySkuPropertyValue> categorySkuPropertyValueList) throws Exception
	{
		try
		{
			//返回数据初始化
			List<CategorySkuPropertyValue> resultList = new ArrayList<CategorySkuPropertyValue>();
			//id 列表
			List<Long> idList = categorySkuPropertyValueDao.createByBatch(categorySkuPropertyValueList);
			for(int i = 0; i < categorySkuPropertyValueList.size(); i++)
			{
				CategorySkuPropertyValue categorySkuPropertyValue = categorySkuPropertyValueList.get(i);
				if (idList.get(i) > 0) {
					categorySkuPropertyValue.category_sku_property_value_id = idList.get(i);
				} else {
					categorySkuPropertyValue = null;
				}
				resultList.add(categorySkuPropertyValue);
			}
			return resultList;
		}
		catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 通过条件获取CategoryPropertyValueList
	 * 
	 * @param condition
	 * @return
	 * @throws Exception
	 */
	public List<CategorySkuPropertyValue> getCategoryPropertyValueListByCondition(Map<String, Object> condition)
			throws Exception {
		try {
			// 查询
			String fieldName = "*";
			List<CategorySkuPropertyValue> categorySkuPropertyValueList = categorySkuPropertyValueDao
					.getByCondition(CategorySkuPropertyValue.class, fieldName, condition);
			return categorySkuPropertyValueList;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据分类类目查询 ProductPropertyValue List
	 *
	 * @param categoryId
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, List<String>>> getCategoryPropertyValueListByCategory(Long categoryId)
			throws Exception {
		List<Map<String, List<String>>> valueList = null;
		try {
			StringBuffer sql = new StringBuffer("select cspk.`name`,group_concat(cspv.`value`) as `value` " +
					"from category_sku_property csp,category_sku_property_value cspv,category_sku_property_key cspk " +
					"where csp.category_id = :p1 " +
					"and csp.category_sku_property_value_id = cspv.category_sku_property_value_id " +
					"and csp.category_sku_property_key_id = cspk.category_sku_property_key_id " +
					"and csp.enabled = 1 " +
					"and cspv.enabled = 1 " +
					"and cspk.enabled = 1 ");
			Object[] params = new Object[]{categoryId};

			List<Map<String, Object>> valueMapList = categorySkuPropertyValueDao.executeBySql(sql.toString(), params);
			if (valueMapList != null && valueMapList.size() > 0) {
				valueList = new ArrayList<>(valueMapList.size());
				List<String> vList = null;
				Map<String, List<String>> vMap = null;
				for (Map<String, Object> map : valueMapList) {
					String key = map.get("name").toString();
					String value = map.get("value").toString();
					String valueArray [] = value.split(",");
					if (null != valueArray && valueArray.length > 0) {
						vList = Arrays.asList(valueArray);
					}
					vMap = new HashMap<>();
					vMap.put(key, vList);
					valueList.add(vMap);
				}
			}

		} catch (Exception e) {
			throw e;
		}

		return valueList;
	}

	/**
	 * 根据类目,属性值查询ProductPropertyValue List
	 * @param categoryId
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getCategoryPropertyValueListByCategoryAndValue(Long categoryId, String value)throws Exception {
		String sql = "select cspk.`name`,cspv.`value`\n" +
				" from category_sku_property csp,category_sku_property_value cspv,category_sku_property_key cspk\n" +
				" where csp.category_id = :p1 \n" +
				" and csp.category_sku_property_value_id = cspv.category_sku_property_value_id\n" +
				" and csp.category_sku_property_key_id = cspk.category_sku_property_key_id\n" +
				" and cspv.`value` = :p2 \n" +
				" and csp.enabled = 1 \n" +
				" and cspv.enabled = 1\n" +
				" and cspk.enabled = 1";
		Object[] params = new Object[]{categoryId, value};
		return categorySkuPropertyValueDao.executeBySql(sql.toString(), params);
	}

	/**
	 * 根据条件删除CategoryPropertyValue
	 * 
	 * @param condition
	 */
	public void deleteCategoryPropertyValueByCondition(Map<String, Object> condition) {
		try {
			// categoryProductPropertyDao.deleteByCondition(CategoryProductProperty.class,condition);
			List<CategorySkuPropertyValue> categorySkuPropertyValueList = getCategoryPropertyValueListByCondition(
					condition);
			for (int i = 0; i < categorySkuPropertyValueList.size(); i++) {
				// 每个数据有效位置为0
				CategorySkuPropertyValue categorySkuPropertyValue = categorySkuPropertyValueList.get(i);
				categorySkuPropertyValue.enabled = EnabledType.DISCARD;
				categorySkuPropertyValueDao.updateById(categorySkuPropertyValue);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	

}
