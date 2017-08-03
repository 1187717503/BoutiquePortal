package pk.shoplus.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.sql2o.Connection;

import pk.shoplus.common.Helper;
import pk.shoplus.dao.DaoHelper;
import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.CategorySkuProperty;
import pk.shoplus.model.Page;
import pk.shoplus.parameter.EnabledType;

public class CategorySkuPropertyService {
	/**
	 * 获取数据库连接
	 */
	private EntityDao<CategorySkuProperty> categorySkuPropertyDao = null;

	/**
	 * @param conn
	 */
	public CategorySkuPropertyService(Connection conn) {
		categorySkuPropertyDao = new EntityDao<CategorySkuProperty>(conn);
	}

	/**
	 * 根据 categoryPropertyKey 加入数据
	 * 
	 * @param categorySkuProperty
	 * @return
	 * @throws Exception
	 */
	public CategorySkuProperty createCategoryProperty(CategorySkuProperty categorySkuProperty) throws Exception {
		try {
			long id = categorySkuPropertyDao.create(categorySkuProperty);
			if (id > 0) {
				categorySkuProperty.category_sku_property_id = id;
			} else {
				categorySkuProperty = null;
			}
			return categorySkuProperty;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 通过 batch 批量加数据
	 * @param categorySkuPropertyList
	 * @return
	 * @throws Exception
	 */
	public List<CategorySkuProperty> createCategorySkuPropertyByBatch(List<CategorySkuProperty> categorySkuPropertyList) throws Exception
	{
		try
		{
			//返回数据初始化
			List<CategorySkuProperty> resultList = new ArrayList<CategorySkuProperty>();
			//id 列表
			List<Long> idList = categorySkuPropertyDao.createByBatch(categorySkuPropertyList);
			for(int i = 0; i < categorySkuPropertyList.size(); i++)
			{
				CategorySkuProperty categorySkuProperty = categorySkuPropertyList.get(i);
				if (idList.get(i) > 0) {
					categorySkuProperty.category_sku_property_id = idList.get(i);
				} else {
					categorySkuProperty = null;
				}
				resultList.add(categorySkuProperty);
			}
			return resultList;
		}
		catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 通过条件获取categoryList
	 * 
	 * @param condition
	 * @return
	 * @throws Exception
	 */
	public List<CategorySkuProperty> getCategoryPropertyListByCondition(Map<String, Object> condition)
			throws Exception {
		try {
			// 查询
			String fieldName = "*";
			List<CategorySkuProperty> categorySkuPropertyList = categorySkuPropertyDao
					.getByCondition(CategorySkuProperty.class, fieldName, condition);
			return categorySkuPropertyList;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据条件删除CategoryProperty
	 * 
	 * @param condition
	 */
	public void deleteCategoryPropertyByCondition(Map<String, Object> condition) {
		try {
			// categoryPropertyDao.deleteByCondition(CategoryProperty.class,condition);
			// 根据 condition 找到数据
			List<CategorySkuProperty> categorySkuPropertyList = getCategoryPropertyListByCondition(condition);
			for (int i = 0; i < categorySkuPropertyList.size(); i++) {
				// 每个数据有效位置为0
				CategorySkuProperty categorySkuProperty = categorySkuPropertyList.get(i);
				categorySkuProperty.enabled = EnabledType.DISCARD;
				categorySkuPropertyDao.updateById(categorySkuProperty);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据条件获取 CategoryProperty的page 和 key & value
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @param whereCondition
	 * @param fieldNames
	 * @param orderBy
	 * @return
	 * @throws Exception
	 */
	public Page getCategoryPropertyPageWithKeyAndValue(long pageNumber, long pageSize,
			Map<String, Object> whereCondition, String fieldNames, String orderBy) throws Exception {
		try
		{
			StringBuilder whereSql = new StringBuilder("");
			List<Object> params = DaoHelper.appendCondition(whereSql, whereCondition, "a");
			
			StringBuilder countSql = new StringBuilder("select count(*) from category_sku_property a");
			countSql.append(whereSql);
			System.out.println("count sql: " + countSql.toString());
			
			// 1. 获取 总数
			Long totalRow = categorySkuPropertyDao.createQuery(countSql.toString(), params.toArray()).executeScalar(Long.class);

			if (totalRow == null || totalRow <= 0) {
				return new Page(new ArrayList<Map<String, Object>>(), pageNumber, pageSize, 1l, 0l);
			}
			
			// 2. 获取 分页
			// excutesql:select * from category where parent_id=:p1 and
			// enabled=:p2 order by category_id limit 0,10
			StringBuilder sql = new StringBuilder("");
			if (Helper.isNullOrEmpty(fieldNames)) {
				fieldNames = "*";
			}
			
			sql.append("SELECT a.*, b.name as key_name, c.value as value_name FROM category_sku_property a ");
			sql.append("LEFT JOIN category_sku_property_key b on a.category_sku_property_key_id =b.category_sku_property_key_id ");
			sql.append("LEFT JOIN category_sku_property_value c on a.category_sku_property_value_id = c.category_sku_property_value_id ");
			sql.append(whereSql);
			
			// order by
			if (!Helper.isNullOrEmpty(orderBy)) {
				sql.append(" order by ").append(orderBy);
			}

			// pageSize或者pageNumber
			// 如果小于1就返回全部数据
			if (pageSize < 1) {
				return new Page();
				// return new Page(categoryDao.executeBySql(sql.toString(),
				// params.toArray()), pageNumber, pageSize, 1l, totalRow);
			}

			if (pageNumber < 1) {
				return new Page();
				// throw new Exception("pageNumber must be more than 1!!");
			}

			// 计数
			long totalPage = totalRow / pageSize;
			if (totalRow % pageSize != 0) {
				totalPage++;
			}
			if (pageNumber > totalPage) {
				return new Page();
				// throw new Exception("pageNumber must be less than
				// totalPage!!");
			}

			// 拼MySQL分页
			long offset = pageSize * (pageNumber - 1);
			sql.append(" limit ").append(offset).append(",").append(pageSize);
			System.out.println("excutesql:" + sql.toString());
			// executeSql
			List<Map<String, Object>> categoryPropertyMapList = categorySkuPropertyDao.executeBySql(sql.toString(), params.toArray());
			return new Page(categoryPropertyMapList, pageNumber, pageSize, totalPage, totalRow);			
		}
		catch (Exception e) {
			e.printStackTrace();
			throw e;
		}	
	}
}
