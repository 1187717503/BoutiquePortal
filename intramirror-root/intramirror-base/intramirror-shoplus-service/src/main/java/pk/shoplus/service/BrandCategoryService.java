package pk.shoplus.service;

import java.util.List;
import java.util.Map;

import org.sql2o.Connection;

import pk.shoplus.common.Helper;
import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.BrandCategory;
import pk.shoplus.parameter.EnabledType;

public class BrandCategoryService {

	/**
	 * 获取数据库连接
	 */
	private EntityDao<BrandCategory> brandCategoryDao = null;

	/**
	 * @param conn
	 */
	public BrandCategoryService(Connection conn) {
		brandCategoryDao = new EntityDao<BrandCategory>(conn);
	}

	/**
	 * 根据条件获取 brandCategoryList
	 * 
	 * @param condition
	 * @return
	 * @throws Exception
	 */
	public List<BrandCategory> getBrandCategoryListByCondition(String fieldName, Map<String, Object> condition)
			throws Exception {
		fieldName = Helper.isNullOrEmpty(fieldName) ? "*" : fieldName;
		try {
			// 查询
			List<BrandCategory> brandCategoryList = brandCategoryDao.getByCondition(BrandCategory.class, fieldName,
					condition);
			return brandCategoryList;
		} catch (Exception e) {
			throw e;
		}
	}

	public List<Map<String, Object>> getCategoryListByBrand(Long brand_id) throws Exception {
		StringBuilder sql = new StringBuilder("");
		List<Map<String, Object>> brandCategoryList = null;
		try {
			sql.append("SELECT c.category_id ,c.`name`,c.show_code_int ").append("FROM brand_category lc ")
					.append("LEFT JOIN category c ON lc.category_id = c.category_id ").append("WHERE c.enabled = :p1 ");
			if (brand_id != null && brand_id > 0) {
				sql.append("AND lc.brand_id = :p2 ");
				brandCategoryList = brandCategoryDao.executeBySql(sql.toString(),
						new Object[] { EnabledType.USED, brand_id });
			}
		} catch (Exception e) {
			throw e;
		}
		return brandCategoryList;
	}

	/**
	 * 创建brandCategory
	 * 
	 * @param brand
	 * @return
	 * @throws Exception
	 */
	public BrandCategory createBrandCategory(BrandCategory brandCategory) throws Exception {
		try {
			long brand_category_id = brandCategoryDao.create(brandCategory);
			if (brand_category_id > 0) {
				brandCategory.brand_category_id = brand_category_id;
			} else {
				brandCategory = null;
			}
			return brandCategory;
		} catch (Exception e) {
			throw e;
		}
	}
}
