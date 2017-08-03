package pk.shoplus.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sql2o.Connection;

import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.CategoryProductProperty;
import pk.shoplus.parameter.EnabledType;

public class CategoryProductPropertyService {

	/**
	 * 获取数据库连接
	 */
	private EntityDao<CategoryProductProperty> categoryProductPropertyDao = null;

	/**
	 * @param conn
	 */
	public CategoryProductPropertyService(Connection conn) {
		categoryProductPropertyDao = new EntityDao<CategoryProductProperty>(conn);
	}

	/**
	 * 创建类别
	 * 
	 * @param categoryProductProperty
	 * @return 实体/null
	 * @throws Exception
	 */
	public CategoryProductProperty createCategoryProductProperty(CategoryProductProperty categoryProductProperty)
			throws Exception {
		try {
			long category_product_property_id = categoryProductPropertyDao.create(categoryProductProperty);
			if (category_product_property_id > 0) {
				categoryProductProperty.category_product_property_id = category_product_property_id;
			} else {
				categoryProductProperty = null;
			}
			return categoryProductProperty;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 通过 batch 批量加数据
	 * @param categoryProductPropertyList
	 * @return
	 * @throws Exception
	 */
	public List<CategoryProductProperty> createCategoryProductPropertyByBatch(List<CategoryProductProperty> categoryProductPropertyList) throws Exception
	{
		try
		{
			//返回数据初始化
			List<CategoryProductProperty> resultList = new ArrayList<CategoryProductProperty>();
			//id 列表
			List<Long> idList = categoryProductPropertyDao.createByBatch(categoryProductPropertyList);
			for(int i = 0; i < categoryProductPropertyList.size(); i++)
			{
				CategoryProductProperty categoryProductProperty = categoryProductPropertyList.get(i);
				if (idList.get(i) > 0) {
					categoryProductProperty.category_product_property_id = idList.get(i);
				} else {
					categoryProductProperty = null;
				}
				resultList.add(categoryProductProperty);
			}
			return resultList;
		}
		catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 通过条件获取categoryProductInfoList
	 * 
	 * @param condition
	 * @return
	 * @throws Exception
	 */
	public List<CategoryProductProperty> getCategoryProductPropertyListByCondition(Map<String, Object> condition)
			throws Exception {
		try {
			// 查询
			String fieldName = "*";
			List<CategoryProductProperty> categoryProductPropertyList = categoryProductPropertyDao
					.getByCondition(CategoryProductProperty.class, fieldName, condition);
			return categoryProductPropertyList;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据条件删除CategoryProductProperty
	 * 
	 * @param condition
	 */
	public void deleteCategoryProductPropertyByCondition(Map<String, Object> condition) {
		try {
			// categoryProductPropertyDao.deleteByCondition(CategoryProductProperty.class,condition);
			List<CategoryProductProperty> categoryProductPropertyList = getCategoryProductPropertyListByCondition(
					condition);
			for (int i = 0; i < categoryProductPropertyList.size(); i++) {
				// 每个数据有效位置为0
				CategoryProductProperty categoryProductProperty = categoryProductPropertyList.get(i);
				categoryProductProperty.enabled = EnabledType.DISCARD;
				categoryProductPropertyDao.updateById(categoryProductProperty);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
