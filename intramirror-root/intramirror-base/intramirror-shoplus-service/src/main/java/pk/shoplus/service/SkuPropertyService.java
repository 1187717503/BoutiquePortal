package pk.shoplus.service;

import org.sql2o.Connection;
import pk.shoplus.common.Helper;
import pk.shoplus.dao.DaoHelper;
import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.Page;
import pk.shoplus.model.SkuProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SkuPropertyService {

	/**
	 * 获取数据库连接
	 */
	private EntityDao<SkuProperty> skuPropertyDao = null;

	/**
	 * @param conn
	 */
	public SkuPropertyService(Connection conn) {
		skuPropertyDao = new EntityDao<SkuProperty>(conn);
	}

	/**
	 * 创建 SkuProperty 数据
	 *
	 * @param skuProperty
	 * @return
	 * @throws Exception
	 */
	public SkuProperty createSkuProperty(SkuProperty skuProperty) throws Exception {
		try {
			Long sku_property_id = skuPropertyDao.create(skuProperty);
			if (sku_property_id > 0) {
				skuProperty.sku_property_id = sku_property_id;
			} else {
				skuProperty = null;
			}
			return skuProperty;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 通过condition 获取 skuProperty
	 *
	 * @param condition
	 * @return
	 * @throws Exception
	 */
	public List<SkuProperty> getSkuPropertyListByCondition(Map<String, Object> condition) throws Exception {
		try {
			// 查询
			List<SkuProperty> skuPropertyList = skuPropertyDao.getByCondition(SkuProperty.class, condition);
			return skuPropertyList;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据条件获取 skuproperty的page 和 key & value
	 *
	 * @param pageNumber
	 * @param pageSize
	 * @param whereCondition
	 * @param fieldNames
	 * @param orderBy
	 * @return
	 * @throws Exception
	 */
	public Page getSkuPropertyPageWithKeyAndValue(long pageNumber, long pageSize, Map<String, Object> whereCondition,
												  String fieldNames, String orderBy) throws Exception {
		// SELECT a.*, b.`name` as key_name, c.`value` as value_name FROM
		// sku_property a LEFT JOIN product_property_key b on
		// a.product_property_key_id =b.product_property_key_id LEFT JOIN
		// product_sku_property_value c on a.product_sku_property_value_id =
		// c.product_sku_property_value_id where a.sku_id = 3;
		try {
			// 1. condition转换成 wherestr
			StringBuilder whereSql = new StringBuilder("");
			List<Object> params = DaoHelper.appendCondition(whereSql, whereCondition, "a");

			// 先计数
			// count sql: select count(*) from category where parent_id=:p1 and
			// enabled=:p2

			StringBuilder countSql = new StringBuilder("select count(*) from sku_property a");
			countSql.append(whereSql);
			System.out.println("count sql: " + countSql.toString());

			// 1. 获取 总数
			Long totalRow = skuPropertyDao.createQuery(countSql.toString(), params.toArray()).executeScalar(Long.class);

			// System.out.println("sku_property totalRow = " + totalRow);

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

			// SELECT a.*, b.`name` as key_name, c.`value` as value_name FROM
			// sku_property a LEFT JOIN product_property_key b on
			// a.product_property_key_id =b.product_property_key_id LEFT JOIN
			// product_sku_property_value c on a.product_sku_property_value_id =
			// c.product_sku_property_value_id where a.sku_id = 3;

			sql.append("SELECT a.*, b.name as key_name, c.value as value_name FROM sku_property a ");
			sql.append(
					"LEFT JOIN product_sku_property_key b on a.product_sku_property_key_id =b.product_sku_property_key_id ");
			sql.append(
					"LEFT JOIN product_sku_property_value c on a.product_sku_property_value_id = c.product_sku_property_value_id ");
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
			List<Map<String, Object>> productMapList = skuPropertyDao.executeBySql(sql.toString(), params.toArray());
			return new Page(productMapList, pageNumber, pageSize, totalPage, totalRow);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// 根据product列表 获取sku property
	public List<Map<String, Object>> getSkuPropertyListWithKeyAndValueByProduct(String whereStringInput)
			throws Exception {

		// SELECT p.*, b.name as key_name, c.value as value_name FROM
		// sku_property p
		// LEFT JOIN product_property_key b on p.product_property_key_id
		// =b.product_property_key_id
		// LEFT JOIN product_sku_property_value c on
		// p.product_sku_property_value_id =
		// c.product_sku_property_value_id
		// WHERE EXISTS (SELECT s.sku_id from sku s WHERE p.sku_id = s.sku_id
		// and EXISTS (SELECT a.product_id FROM product a where s.product_id =
		// a.product_id and (a.product_id = 27 or a.product_id = 2)))
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try {
			StringBuilder sql = new StringBuilder("");
			sql.append("SELECT p.*, b.name as key_name, c.value as value_name FROM sku_property p ");
			sql.append(
					"LEFT JOIN product_sku_property_key b on p.product_sku_property_key_id =b.product_sku_property_key_id ");
			sql.append(
					"LEFT JOIN product_sku_property_value c on p.product_sku_property_value_id = c.product_sku_property_value_id ");
			sql.append("WHERE EXISTS (");
			sql.append("SELECT s.sku_id from sku s WHERE p.sku_id = s.sku_id ");
			sql.append("and EXISTS (");
			sql.append("SELECT a.product_id FROM product a where s.product_id = a.product_id ");
			if (whereStringInput.length() > 0) {
				sql.append(" and (" + whereStringInput + ") ");
			}
			sql.append("))");
			System.out.println("excutesql:" + sql.toString());

			result = skuPropertyDao.executeBySql(sql.toString(), new Object[0]);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return result;
	}

	public void deleteSkuPropertyByCondition(Map<String, Object> condition) throws Exception {
		try {
			// delete
			skuPropertyDao.deleteByCondition(SkuProperty.class, condition);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据skuid获取property信息
	 *
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getSkuPropertyListBySkuId(Long id) throws Exception {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try {
			String sql = "select p.*, b.name as key_name, c.value as value_name from "
					+ " sku_property p left join product_sku_property_key b on p.product_sku_property_key_id =b.product_sku_property_key_id "
					+ " left join product_sku_property_value c on p.product_sku_property_value_id = c.product_sku_property_value_id  "
					+ "where p.sku_id = :p1";
			Object[] params = { id };

			result = skuPropertyDao.executeBySql(sql, params);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return result;
	}

	public List<Map<String, Object>> getSkuPropertyListWithKeyAndValueBySkuId(String sku_id) throws Exception {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try {
			StringBuilder sql = new StringBuilder("");
			sql.append("SELECT b.name as key_name, c.value as value_name FROM sku_property p ");
			sql.append(
					"LEFT JOIN product_sku_property_key b on p.product_sku_property_key_id =b.product_sku_property_key_id ");
			sql.append(
					"LEFT JOIN product_sku_property_value c on p.product_sku_property_value_id = c.product_sku_property_value_id ");
			sql.append("WHERE p.sku_id=:p1");
			result = skuPropertyDao.executeBySql(sql.toString(), new Object[] { sku_id });
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return result;
	}

	public List<Map<String, Object>> getSkuPropertyListWithSizeAndProductCode(String size, String productCode) throws Exception {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try {
			StringBuilder sql = new StringBuilder("");
			sql.append("select ss.sku_store_id from ");
			sql.append(" product p,sku s,sku_property sp,sku_store ss,product_sku_property_key pspk,product_sku_property_value pspv ");
			sql.append(" where p.product_id = s.product_id and ");
			sql.append(" s.sku_id = sp.sku_id and ");
			sql.append(" sp.product_sku_property_value_id = pspv.product_sku_property_value_id and ");
			sql.append(" pspk.product_sku_property_key_id = sp.product_sku_property_key_id and ");
			sql.append(" pspk.`name` = 'Size' and ");
			sql.append(" s.sku_id = ss.sku_id and ");
			sql.append(" pspv.`value` = :p1 and ");
			sql.append(" p.product_code = :p2 ");
			result = skuPropertyDao.executeBySql(sql.toString(), new Object[] {size, productCode });
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return result;
	}


    // App根据product列表 获取sku property
    public List<Map<String, Object>> getAppSkuPropertyListWithKeyAndValueByProduct(String whereStringInput)
            throws Exception {

//		SELECT sp.*, b.name as key_name, c.value as value_name FROM sku_property sp
//		LEFT JOIN product_sku_property_key b on sp.product_sku_property_key_id =b.product_sku_property_key_id
//		LEFT JOIN product_sku_property_value c on sp.product_sku_property_value_id = c.product_sku_property_value_id
//		LEFT JOIN sku s ON sp.sku_id = s.sku_id
//		LEFT JOIN product a ON s.product_id = a.product_id
//		WHERE (a.product_id = 341 or a.product_id = 342 or a.product_id = 343 or a.product_id = 344
//				or a.product_id = 345 or a.product_id = 346 or a.product_id = 347 or a.product_id = 348
//				or a.product_id = 349 or a.product_id = 350) ;
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        try {
            StringBuilder sql = new StringBuilder("");
            sql.append("SELECT sp.*, b.name as key_name, c.value as value_name FROM sku_property sp ");
            sql.append(
                    "LEFT JOIN product_sku_property_key b on sp.product_sku_property_key_id = b.product_sku_property_key_id ");
            sql.append(
                    "LEFT JOIN product_sku_property_value c on sp.product_sku_property_value_id = c.product_sku_property_value_id ");
            sql.append("LEFT JOIN sku s ON sp.sku_id = s.sku_id ");
            sql.append("LEFT JOIN product a ON s.product_id = a.product_id ");
            if (whereStringInput.length() > 0) {
                sql.append(" where (" + whereStringInput + ") ");
            }
            System.out.println("excutesql:" + sql.toString());

            result = skuPropertyDao.executeBySql(sql.toString(), new Object[0]);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return result;
    }
//=======
//	// App根据product列表 获取sku property
//	public List<Map<String, Object>> getAppSkuPropertyListWithKeyAndValueByProduct(String whereStringInput)
//			throws Exception {
//
////		SELECT sp.*, b.name as key_name, c.value as value_name FROM sku_property sp
////		LEFT JOIN product_sku_property_key b on sp.product_sku_property_key_id =b.product_sku_property_key_id
////		LEFT JOIN product_sku_property_value c on sp.product_sku_property_value_id = c.product_sku_property_value_id
////		LEFT JOIN sku s ON sp.sku_id = s.sku_id
////		LEFT JOIN product a ON s.product_id = a.product_id
////		WHERE (a.product_id = 341 or a.product_id = 342 or a.product_id = 343 or a.product_id = 344
////				or a.product_id = 345 or a.product_id = 346 or a.product_id = 347 or a.product_id = 348
////				or a.product_id = 349 or a.product_id = 350) ;
//		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
//		try {
//			StringBuilder sql = new StringBuilder("");
//			sql.append("SELECT sp.*, b.name as key_name, c.value as value_name FROM sku_property sp ");
//			sql.append(
//					"LEFT JOIN product_sku_property_key b on sp.product_sku_property_key_id = b.product_sku_property_key_id ");
//			sql.append(
//					"LEFT JOIN product_sku_property_value c on sp.product_sku_property_value_id = c.product_sku_property_value_id ");
//			sql.append("LEFT JOIN sku s ON sp.sku_id = s.sku_id ");
//			sql.append("LEFT JOIN product a ON s.product_id = a.product_id ");
//			if (whereStringInput.length() > 0) {
//				sql.append(" where (" + whereStringInput + ") ");
//			}
//			System.out.println("excutesql:" + sql.toString());
//
//			result = skuPropertyDao.executeBySql(sql.toString(), new Object[0]);
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw e;
//		}
//		return result;
//	}
//>>>>>>> refs/remotes/origin/dev_product_management_suli

}
