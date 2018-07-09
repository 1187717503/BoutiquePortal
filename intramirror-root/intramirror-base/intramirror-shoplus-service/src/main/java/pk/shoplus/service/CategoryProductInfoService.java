package pk.shoplus.service;

import java.util.List;
import java.util.Map;

import org.sql2o.Connection;

import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.CategoryProductInfo;

public class CategoryProductInfoService {

	/**
	 * 获取数据库连接
	 */
	private EntityDao<CategoryProductInfo> categoryProductInfoDao = null;

	/**
	 * @param conn
	 */
	public CategoryProductInfoService(Connection conn) {
		categoryProductInfoDao = new EntityDao<CategoryProductInfo>(conn);
	}

	/**
	 * 创建类别
	 * 
	 * @param categoryProductInfo
	 * @return 实体/null
	 * @throws Exception
	 */
	public CategoryProductInfo createCategoryProductInfo(CategoryProductInfo categoryProductInfo) throws Exception {
		try {
			long category_product_info_id = categoryProductInfoDao.create(categoryProductInfo);
			if (category_product_info_id > 0) {
				categoryProductInfo.category_product_info_id = category_product_info_id;
			} else {
				categoryProductInfo = null;
			}
			return categoryProductInfo;
		} catch (Exception e) {
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
	public List<CategoryProductInfo> getCategoryProductInfoListByCondition(Map<String, Object> condition)
			throws Exception {
		try {
			// 查询
			String fieldName = "*";
			List<CategoryProductInfo> categoryProductInfoList = categoryProductInfoDao
					.getByCondition(CategoryProductInfo.class, fieldName, condition);
			return categoryProductInfoList;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 更新categoryProductInfo
	 * 
	 * @param categoryProductInfo
	 * @throws Exception
	 */
	public void updateCategoryProductInfo(CategoryProductInfo categoryProductInfo) throws Exception {
		try {
			categoryProductInfoDao.updateById(categoryProductInfo);
		} catch (Exception e) {
			throw e;
		}
	}

}
